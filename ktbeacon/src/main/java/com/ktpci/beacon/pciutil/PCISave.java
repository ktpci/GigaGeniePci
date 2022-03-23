package com.ktpci.beacon.pciutil;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PCISave {
    public static void save(Context context , String str) {

        final String fileName = "log_05025_0914.txt";
        File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/beacon");
        if(!saveFile.exists()){ // 폴더 없을 경우
            saveFile.mkdirs(); // 폴더 생성
            File file = new File(saveFile,fileName);
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            long now = System.currentTimeMillis(); // 현재시간 받아오기
            Date date = new Date(now); // Date 객체 생성
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            String nowTime = sdf.format(date);

            BufferedWriter buf = new BufferedWriter(new FileWriter(saveFile + "/" + fileName, true));
            buf.append(nowTime + " "); // 날짜 쓰기
            buf.append(str); // 파일 쓰기
            buf.newLine(); // 개행
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
