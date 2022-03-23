package com.gnifrix.util;

/**
 * Created by LeeBaeng on 2018-09-04.
 */

public class HsUtil_Calculates {
    /**
     * 백분율을 계산하여 반환 한다.(반올림)
     * @return curValue가 maxVlaue에 몇% 인가?
     */
    public static double getPercentage(double curValue, double maxValue){
        return getPercentage(curValue, maxValue, 100);
    }

    /**
     * maxPercentage분율을 계산하여 반환 한다.
     * @return
     */
    public static double getPercentage(double curValue, double maxValue, double maxPercentage){
        return curValue/maxValue * maxPercentage;
    }

    /**
     * curValue값에서 p% 만큼의 값을 반환 한다.
     */
    public static double getPercentageValue(double curValue, double p){
        return curValue * p / 100;
    }

    /**
     * curValue값에서 a%만큼 더한 값을 반환 한다.
     */
    public static double getAddPercentageValue(double curValue, double a){
        return curValue *(1+a/100);
    }

    /**
     * curValue값에서 m%만큼 뺀 값을 반환 한다.
     */
    public static double getMinusPercentageValue(double curValue, double m){
        return curValue *(1-m/100);
    }

}
