package com.gnifrix.util;

/**
 * Created by LeeBaeng on 2018-09-10.
 */

public class HsUtil_String {
    /**
     * 지정된 문자열 길이에 맞춰 appendStr을 추가하여 반환한다.
     * @param srcStr 변경할 문자열
     * @param appendStr append할 문자
     * @param targetLength 목표 문자열 길이
     * @return
     */
    public static String getAppendedStr(String srcStr, String appendStr, int targetLength){
        if(srcStr.length() < targetLength) {
            int j = targetLength - srcStr.length();
            for(int i=0; i<j; i++) srcStr += appendStr;
        }

        return srcStr;
    }
}
