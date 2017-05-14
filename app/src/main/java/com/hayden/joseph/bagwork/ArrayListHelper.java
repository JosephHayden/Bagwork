package com.hayden.joseph.bagwork;

import java.util.ArrayList;

/**
 * Created by Joseph on 7/17/2016.
 */
public class ArrayListHelper {
    public static String ArrayListToString(ArrayList<Integer> aList){
        String retStr = "";
        if(aList.size() > 0) {
            for (int i = 0; i < aList.size() - 1; i++) {
                retStr = retStr.concat(Integer.toString(aList.get(i)) + ",");
            }
            retStr = retStr.concat(Integer.toString(aList.get(aList.size() - 1)));
        }
        return retStr;
    }

    public static ArrayList<Integer> StringToArrayList(String str){
        String arr[] = str.split(",");
        ArrayList<Integer> ret = new ArrayList<>();
        for(int i=0; i < arr.length; i++){
            ret.add(Integer.parseInt(arr[i]));
        }
        return ret;
    }
}
