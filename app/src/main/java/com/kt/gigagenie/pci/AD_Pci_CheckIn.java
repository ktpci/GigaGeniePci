package com.kt.gigagenie.pci;

import android.content.Intent;

import com.gnifrix.debug.GLog;
import com.gnifrix.util.HsUtil_Date;
import com.kt.gigagenie.pci.data.PciSrv_PidData;
import com.kt.gigagenie.pci.data.PciSrv_VoiceChkInData;
import com.kt.gigagenie.pci.data.PidListManager;
import com.kt.gigagenie.pci.data.StbData;
import com.kt.gigagenie.pci.data.pci_db.ErrorInfo;
import com.kt.gigagenie.pci.system.PciRuntimeException;

import java.util.Calendar;

public class AD_Pci_CheckIn {



    /////// pciBroadcastReceiver 참고 - ble 캐취해서 체크인 처리 할때......
    PciSrv_VoiceChkInData newCheckInData = new PciSrv_VoiceChkInData(realVoiceId, realUserkey, checkInDate, action, gender, age_group);
    stbData.setLastVoiceChkInData(newCheckInData);
    PidListManager pidListMgr = pciManager.getDataManager().getPidManager();
    PciSrv_PidData curPidData = null;

    /**
     * 음성 발화 체크인 정보를 화자인식 App으로 부터 수신된 경우
     * 해당 데이터를 처리하고 Pci서버에 Check in 정보를 전달한다.
     * API : VID001
     */
    private void recvVoiceId(final Intent recvIntent){
        // pciService가 비 실행중일 경우 return
        if(Global.getInstance().getPciService() == null || !PciManager.isInitialized() && Pci_Service.pciReady){
            GLog.printWtf(this, "pciManager is not initialized. return recvVoiceEvent");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                PciManager pciManager = PciManager.getInstance();
                StbData stbData = pciManager.getDataManager().getStbData();

                String vid = recvIntent.getStringExtra("VID");
                String preVid = recvIntent.getStringExtra("PreVID"); // 이전 보이스ID(VoiceId가 UserKey로 변경된 경우에만 값이 넘어옴)
                String senderPkgName = recvIntent.getStringExtra("packageName");
                String gender = recvIntent.getStringExtra("GENDER_TYPE");
                String age_group = recvIntent.getStringExtra("AGE_GROUP");
                String checkInDate = HsUtil_Date.getTimeString(Calendar.getInstance().getTime(), pciManager.getPciProperty().getDate_format_pcisrv_checkin_list());

                // 빈값, null, unknown일 경우 처리하지 않음 (2019.02.13)
                if(vid != null && !vid.equals("") && !vid.equalsIgnoreCase("UNKNOWN") && !vid.equalsIgnoreCase("NULL") ){
                    GLog.printInfo(this,"recvVoiceId=" + vid + "/ preVid=" + preVid + " / " + senderPkgName + " / " + checkInDate);

                    String realVoiceId = "";
                    String realUserkey = "";

                    // VID가 UserKey로 변경된 경우 preVID에 이전에 사용하던 VID가 넘어옴.
                    // boolean isNeedUpdate = false;
                    // isNeedUpdate -> action_type으로 변경 (Platform 1.7.1_1130)
                    String action;

                    if(preVid != null && !preVid.trim().equals("") && !vid.startsWith("V")){
                        action = "U";
                        realVoiceId = preVid;
                        realUserkey = vid;
                    }else{
                        action = "I";
                        if(vid.startsWith("V")){
                            realVoiceId = vid;
                            realUserkey = "";
                        }else{
                            realVoiceId = "";
                            realUserkey = vid;
                        }
                    }

                    // 수신된 데이터를 통해 PciSrv_VoiceChkInData Object를 생성하고, 현재 체크인된 보이스 사용자(LastVoiceChkInData)로 지정
                    // Voice체크인 데이타에 있는 gender, age 정보는 불확실하므로 서버에서 받은 정보를 우선시 함
                    PciSrv_VoiceChkInData newCheckInData = new PciSrv_VoiceChkInData(realVoiceId, realUserkey, checkInDate, action, gender, age_group);
                    stbData.setLastVoiceChkInData(newCheckInData);
                    PidListManager pidListMgr = pciManager.getDataManager().getPidManager();
                    PciSrv_PidData curPidData = null;

                    if(realVoiceId != null && !realVoiceId.equals("")) curPidData = pidListMgr.getPidDataByVoiceID(realVoiceId);
                    else if(realUserkey != null && !realUserkey.equals("")) curPidData = pidListMgr.getPidDataByUserKey(realUserkey);

                    // PidList에 PidData가 없을 경우 서버통신 우선, PidData가 있을 경우 MC전송 우선 (PidList에 존재하지 않을 경우 해당 voiceID의 Pid를 알수 없음)
                    if(curPidData != null){
                        curPidData.setVoiceChkInData(newCheckInData);

                        // MC에 현재 체크인이벤트(보이스) + 체크인리스트 전송
                        recvVoiceCheckInSendToMC(curPidData, checkInDate);

                        // 서버에 보이스 체크인 이벤트 전송
                        pciManager.getNetManager().apiPci6005_VoiceAction(newCheckInData);

                    }else{
                        // 서버에 보이스 체크인 이벤트 전송
                        curPidData = pciManager.getNetManager().apiPci6005_VoiceAction(newCheckInData);

                        if(curPidData == null){
                            GLog.printExceptWithSaveToPciDB(this, "voiceId & UserKey is not found.", new PciRuntimeException("receive VoiceId Exception. recvData=vid:" + vid + "/preVid:" + preVid + "/ realVoiceId:" + realVoiceId + "/ realUserkey:"+realUserkey), ErrorInfo.CODE_INAPP_VOICE_EVENT_FAIL, ErrorInfo.CATEGORY_INAPP);
                            return;
                        }
                        // MC에 현재 체크인이벤트(보이스) + 체크인리스트 전송
                        recvVoiceCheckInSendToMC(curPidData, checkInDate);
                    }
                }
            }
        }).start();
    }

}
