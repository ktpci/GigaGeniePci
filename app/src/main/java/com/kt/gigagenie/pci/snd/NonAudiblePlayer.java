package com.kt.gigagenie.pci.snd;

/**
 * Created by LeeBaeng on 2018-09-27.
 */


import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;


import com.gnifrix.debug.GLog;
import com.gnifrix.system.GThread;
import com.kt.gigagenie.pci.PciManager;
import com.kt.gigagenie.pci.R;
import com.kt.gigagenie.pci.data.DataManager;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.system.PciProperty;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class NonAudiblePlayer {
    private static final String logHeader = "NonAudiblePlayer";

    // 음원 다운로드 실패 시 콜드부팅 시까지 재시도 없음.
    private int downloadState = DOWNLOAD_STATE_READY;

    public static final int DOWNLOAD_STATE_READY 		    = 0;
    public static final int DOWNLOAD_STATE_DOWNLOADING      = 1;
    public static final int DOWNLOAD_STATE_COMPLETE	        = 2;
    public static final int DOWNLOAD_STATE_FAILED = -1;

    private PciManager pciManager;
    private DataManager dataManager;
    private String fileName;
    private File _rootDir;
    private File _pciSndFile;
    private String mediaSource;

    private MediaPlayer mediaPlayer;
    private boolean _reqPlayed = false;

    public NonAudiblePlayer(PciManager _pciManager, Context ctx) {
        pciManager = _pciManager;
        dataManager = pciManager.getDataManager();
        downloadState = DOWNLOAD_STATE_READY;

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        _rootDir = ctx.getFilesDir();
        fileName = dataManager.getStbData().getMacAddress().replace(":", "")  + ".mp3";

        // 맥주소 하나만 사용 : 2019_01_21
//        if(dataManager.getStbData().getSaId() != null && !dataManager.getStbData().getSaId().trim().equals("null"))
//            fileName = dataManager.getStbData().getSaId() + ".mp3";
//        else if(dataManager.getStbData().getDeviceId() != null && !dataManager.getStbData().getDeviceId().trim().equals("null"))
//            fileName = dataManager.getStbData().getDeviceId().replace(":", "") + ".mp3";
//        else fileName = dataManager.getStbData().getMacAddress().replace(":", "")  + ".mp3";

        if (!_rootDir.exists()) {
            boolean b = _rootDir.mkdir();
            GLog.printInfo(this, "<NonAudiblePlayer> make directory --> " + b);
        }


        // 음원파일 객체 생성
        File[] fileList = _rootDir.listFiles();
        if (fileList != null) {
            if (fileList.length > 0) {
                for (int i = 0; i < fileList.length; i++) {
                    String name = fileList[i].getName();
                    if (name != null && name.equals(fileName)) {
                        _pciSndFile = fileList[i];
                        GLog.printInfo(this, "get pci sound file: " + _pciSndFile.getAbsolutePath());
                        try {
                            setMediaSource(_pciSndFile.getAbsolutePath());
                            //GLog.printInfo(this, "load pci sound: " + mediaSource);
                        } catch (SecurityException e) {
                            //GLog.printExceptWithSaveToPciDB(this, "load pci sound(" + _pciSndFile.getAbsolutePath() + ") fail(mediaPlayer.setDataSource fail): " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_INITIALIZE_FAIL, ErrorInfo.CATEGORY_NONAUDIBLE);
                        } catch (IOException e) {
                            //GLog.printExceptWithSaveToPciDB(this, "load pci sound(" + _pciSndFile.getAbsolutePath() + ") fail: " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_INITIALIZE_FAIL, ErrorInfo.CATEGORY_NONAUDIBLE);
                        }
                    } else {
                        boolean b = fileList[i].delete();
                        GLog.printInfo(this, "delete file: " + fileList[i].getAbsolutePath() + " --> " + b);
                    }
                }
            } else {
                GLog.printInfo(this, "get file list: 0");
            }
        } else {
            GLog.printInfo(this, "get file list: null");
        }

        if (_pciSndFile == null) {
            _pciSndFile = new File(_rootDir, fileName);
        }
    }

    public void setMediaSource(String filePath) throws IOException{
        if(pciManager.getPciProperty().getPci_sndfile_useAudibleTest()) {
            AssetFileDescriptor afd = pciManager.getMainService().getResources().openRawResourceFd(R.raw.loop_audible);
            if (afd != null) {
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mediaSource = "R.raw.loop_audible";
                GLog.printInfo(this,"setMediaSource(UseAudibleTest) >>> " + mediaSource);
                return;
            }
        }
        mediaPlayer.setDataSource(filePath);
        mediaSource = filePath;
        GLog.printInfo(this,"setMediaSource >>> " + mediaSource);
    }

    public void destroy() {
        stopSound();

        downloadState = DOWNLOAD_STATE_READY;
        _rootDir = null;
        _pciSndFile = null;

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void update(final String url) {
        if (downloadState == DOWNLOAD_STATE_FAILED) {
            GLog.printInfo(this, "already download pci sound file failed: NonAudiblePlayer.update() returned");
            GLog.printInfo(this, "sndDownload satate is invalid, current state : " + downloadState + " // NonAudiblePlayer.update() returned");
            return;
        }

        boolean b = _pciSndFile.exists();
        GLog.printInfo(this, "\n<pci sound file update requested(file exist : " + b + ")> : " + url + "\n");

        downloadState = DOWNLOAD_STATE_READY;
        if (b) {
            b = _pciSndFile.delete();
            GLog.printInfo(this, "pci sound file deleted: " + b);
            mediaPlayer.release();
            mediaPlayer = null;
            mediaPlayer = new MediaPlayer();
        }

        try {
            downloadState = DOWNLOAD_STATE_DOWNLOADING;
            if (_pciSndFile.createNewFile()) {
                saveFile(_pciSndFile, url);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                setMediaSource(_pciSndFile.getAbsolutePath());
                GLog.printInfo(this, "load pci sound: " + mediaSource);
                downloadState = DOWNLOAD_STATE_COMPLETE;
            } else {
                downloadState = DOWNLOAD_STATE_FAILED;
                GLog.printInfo(this, "create pci sound file fail");
            }
        } catch (IOException e) {
            downloadState = DOWNLOAD_STATE_FAILED;
            //GLog.printExceptWithSaveToPciDB(this, "download or load pci sound file fail: " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_MEDIA_PLAYER_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
        } catch (Exception e) {
            //GLog.printExceptWithSaveToPciDB(this, "Non-Audible sound update() failed. " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_UNKNOWN_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
        }
    }

    public boolean isExists() {
        return _pciSndFile.exists();
    }

    private void saveFile(File file, String url) {
        byte[] fileBytes = download(url);

        if (fileBytes != null && fileBytes.length == 0) {
            GLog.printInfo(this,"retry download pci sound file after 500 msec");
            fileBytes = null;
            try {
                Thread.sleep(500);
                fileBytes = download(url);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (fileBytes == null) {
            downloadState = DOWNLOAD_STATE_FAILED;
            //GLog.printExceptWithSaveToPciDB(this, "save pci sound file fail", new PciRuntimeException("fileBytes is null. download failed."), ErrorInfo.CODE_NONAUDIBLE_DOWNLOAD_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
            return;
        }

        BufferedInputStream bufIn = null;
        BufferedOutputStream bufOut = null;
        try {
            bufIn = new BufferedInputStream(new ByteArrayInputStream(fileBytes));
            bufOut = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buf = new byte[4096];
            int size;
            while ((size = bufIn.read(buf)) > 0) {
                bufOut.write(buf, 0, size);
            }
            //GLog.printInfo(this,"pci sound file saved: " + file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            //GLog.printExceptWithSaveToPciDB(this,"open pci sound file(" + file.getAbsolutePath() + ") fail: " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_FILEIO_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
        } catch (IOException e) {
            //GLog.printExceptWithSaveToPciDB(this,"open/write pci sound file(" + file.getAbsolutePath() + ") fail: " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_FILEIO_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
        } finally {
            try {
                if (bufIn != null) {
                    bufIn.close();
                }
            } catch (IOException e) {
               // GLog.printExceptWithSaveToPciDB(this, "save pci sound file finalize fail(bufIn Close failed) : " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_FILEIO_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
            }

            try {
                if (bufOut != null) {
                    bufOut.close();
                }
            }catch (IOException e){
                //GLog.printExceptWithSaveToPciDB(this, "save pci sound file finalize fail(bufOut Close failed) : " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_FILEIO_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
            }

        }
    }

    private byte[] download(String url) {
        GLog.printInfo(this,"\n<download pci sound> : " + url + "\n");
        HttpURLConnection httpCon = null;
        BufferedInputStream bufIn = null;
        ByteArrayOutputStream byteOut = null;
        byte[] returnBytes = null;

        try {
            httpCon = (HttpURLConnection) new URL(url).openConnection();
            httpCon.setRequestMethod("GET");
            int resCode = httpCon.getResponseCode();
            if (resCode == HttpURLConnection.HTTP_OK) {
                bufIn = new BufferedInputStream(httpCon.getInputStream());
                byteOut = new ByteArrayOutputStream();
                byte[] buf = new byte[4096];
                int size;
                while ((size = bufIn.read(buf)) > 0) {
                    byteOut.write(buf, 0, size);
                }
                returnBytes = byteOut.toByteArray();
            } else if (resCode == 500) {
                //GLog.printExceptWithSaveToPciDB(this, "download pci sound file fail: resCode is 500", new PciRuntimeException("http repose is 500"), ErrorInfo.CODE_NONAUDIBLE_DOWNLOAD_HTTPRES_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
                return new byte[0];
            } else {
                //GLog.printExceptWithSaveToPciDB(this, "download pci sound file fail: resCode is " + resCode, new PciRuntimeException("http repose is " + resCode), ErrorInfo.CODE_NONAUDIBLE_DOWNLOAD_HTTPRES_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
            }
        } catch (MalformedURLException e) {
            //GLog.printExceptWithSaveToPciDB(this, "download pci sound file fail: " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_DOWNLOAD_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
        } catch (IOException e) {
            //GLog.printExceptWithSaveToPciDB(this, "download pci sound file fail: " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_DOWNLOAD_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
        } finally {
            try {
                if (httpCon != null) {
                    httpCon.disconnect();
                }
                if (bufIn != null) {
                    bufIn.close();
                }
                if (byteOut != null) {
                    byteOut.close();
                }
            } catch (Exception e) {
                //GLog.printExceptWithSaveToPciDB(this, "download pci sound file finalize fail : " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_DOWNLOAD_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
            }
        }
        return returnBytes;
    }

    public void playSound(final int delaySec) {
        if ((downloadState == DOWNLOAD_STATE_FAILED || downloadState == DOWNLOAD_STATE_DOWNLOADING)) {
            GLog.printInfo(this,"sndDownload state is invalid, current state : " + downloadState + " // NonAudiblePlayer.playSound() returned (step 1)");
            return;
        }
        if (downloadState == DOWNLOAD_STATE_READY && !isExists()) {
            GLog.printInfo(this,"file is not download but file is not exist try to update Policy, current state : " + downloadState + " // NonAudiblePlayer.playSound() returned (step 2)");
            return;
        }

        try{
            if(mediaPlayer == null) throw new PciRuntimeException("Media Player is not initialized....");
            if(_pciSndFile == null) throw new PciRuntimeException("_pciSndFile is not initialized....");
            if(!isExists()) throw new PciRuntimeException("_pciSndFile is not exist. please check download state(if you set pci_sndfile_ignorePolicy to 2(Ignore Policy For Non-Download State), maybe didn't download file yet)...");
        }catch (PciRuntimeException e){
            //GLog.printExceptWithSaveToPciDB(this, "playSound Failed !!", e, ErrorInfo.CODE_NONAUDIBLE_MEDIA_PLAYER_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
        }

        try{
            stopSound();
        }catch (Exception e){
            GLog.printExcept(this, "SndPlayer", e);
        }
        try{
            Thread.sleep(1000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(!getDolbySetting()){
            try {
                if(pciManager.getPciProperty().getPci_sndfile_ignorePolicy() == PciProperty.PCI_SND_TEST_USEPOLICY){
                    try {
                        GLog.printDbg(this,"wait for playing pci sound(policy.WaitingTimeForUpdate). delaySec : " + delaySec);
                        if(delaySec > 0) Thread.sleep(delaySec * 1000);
                    } catch (InterruptedException e) {
                        GLog.printInfo(this,"wait playing pci sound interrupted");
                        _reqPlayed = false;
                        return;
                    }
                }

                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();


                _reqPlayed = true;
                GLog.printInfo(this,"Start Play Non-Audible Sound >> " + mediaSource);
            } catch (Exception e) {
                //GLog.printExceptWithSaveToPciDB(this,"Non-Audible Sound Play failed: " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_MEDIA_PLAYER_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
                _reqPlayed = false;
            }
        } else {
            _reqPlayed = false;
            // 사운드 설정이 돌비로 되어 있어 재생 못하는 상태일 경우 서버에 보고 :: 돌비설정 제거(기가지니 Only) - 2018/10/23
//                pciManager.getNetManager().apiPci_cantPlaySound();
        }
    }


    public boolean getDolbySetting(){
        if(true) return false; // 돌비설정 제거(기가지니 Only) - 2018/10/23

        int digitAudioOut = 0;
//        digitAudioOut = UserPreference.getUserPreference().getDigitalAudioOutput();
        GLog.printDbg(this,"UserPreference.getDigitalAudioOutput() --> " + digitAudioOut);

        // 1=돌비 설정 상태
        if (digitAudioOut == 1) return true;
        return false;
    }

    public void stopSound() {
        if (_reqPlayed) {
            try {

                _reqPlayed = false;
                GLog.printInfo(this,"stop sound");
            } catch (Exception e) {
                //GLog.printExceptWithSaveToPciDB(this,"stop sound fail: " + e.getMessage(), e, ErrorInfo.CODE_NONAUDIBLE_MEDIA_PLAYER_ERR, ErrorInfo.CATEGORY_NONAUDIBLE);
            }
        }
    }

    public MediaPlayer getMediaPlayer() { return mediaPlayer; }
    public boolean getIsReqPlayed() { return _reqPlayed; }
    public int getDownloadState() { return downloadState; }
    public void setDownloadState( int _downloadState ) { this.downloadState = _downloadState; }

    public String toString(){
        return logHeader;
    }


}