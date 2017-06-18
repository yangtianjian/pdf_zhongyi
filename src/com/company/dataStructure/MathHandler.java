package com.company.dataStructure;

import com.itextpdf.layout.renderer.LineRenderer;

import java.util.ArrayList;

/**
 * Created by apple on 2017/4/28.
 */
public class MathHandler {
    public static boolean compareValueString(String s1, String s2, float tolerant){
        boolean res = false;
        try{
            Float f1 = Float.parseFloat(s1);
            Float f2 = Float.parseFloat(s2);
            res = Math.abs(f1-f2) / Math.max(f1, f2) <= tolerant;
        }catch(NumberFormatException e){
            res = s1.equals(s2);
        }
        return res;
    }

    /**
     * 从from索引开始，寻找s1到s2之间的字符串，如果不存在s1或s2，返回空字符串
     * @param s
     * @param s1
     * @param s2
     * @param from
     * @return
     */
    public static String stringBetweenTwoStrings(String s, String s1, String s2, int from, boolean should_trim){
        int p1 = s.indexOf(s1, from);
        if(p1 == -1) return "";
        int p2 = s.indexOf(s2, p1 + s1.length());
        if(p2 == -1) return "";
        String ret = s.substring(p1 + s1.length(), p2);
        return should_trim? ret.trim(): ret;
    }

    public static String stringToEnd(String s, String s1, int from, boolean should_trim){
        int p1 = s.indexOf(s1, from);
        if(p1 == -1) return "";
        String ret = s.substring(p1 + s1.length(), s.length());
        return should_trim? ret.trim(): ret;
    }

    private static String add(String s, int k){
        Integer a = Integer.parseInt(s);
        a += k;
        return a.toString();
    }

    /**
     * 小字串与大字串匹配当且仅当小字串在大字串中出现且大字串出现的位置之间只能用空格连接
     * @param s
     * @param s1
     * @param from 从大字串from开始匹配
     * @return 匹配后大字串的匹配末端的下一个位置的索引, -1表示无匹配
     */
    public static int matchIgnoreSpace(String s, String s1, int from){
        int startPoint = from - 1;
        int len1 = s1.length();
        ArrayList<Integer> matchPoint = new ArrayList<>();
        for(int i = 0; i < len1; i++){
            Character c = s1.charAt(i);
            int curMatch = s.indexOf(c.toString(), startPoint + 1);
            matchPoint.add(curMatch);
            startPoint = curMatch;
        }
        for(int i = 0; i < matchPoint.size() - 1; i++){
            String middle = s.substring(matchPoint.get(i) + 1, matchPoint.get(i + 1));
            if(middle.trim().length() != 0) return -1;
        }
        return matchPoint.get(matchPoint.size() - 1) + 1;
    }

    public static Tuple<String, Integer> stringBetweenTwoStringsWithoutSpace(String s, String s1, String s2, int from){
        int p1 = matchIgnoreSpace(s, s1, from);
        if(p1 == -1) return new Tuple<>("", from);
        int p2 = matchIgnoreSpace(s, s2, p1);
        if(p2 == -1) return new Tuple<>("", from);
        return new Tuple<>(s.substring(p1, p2 - s2.length()), p2 + s2.length());
    }

    public static String stringToEndWithoutSpace(String s, String s1, int from){
        int p1 = matchIgnoreSpace(s, s1, from);
        if(p1 == -1) return "";
        return s.substring(p1, s.length());
    }

    /**
     * 根据现在的一个标号来计算下一个可能的标号。如给出1，要看1.1, 2,...目前只负责三级标题
     * @param curLabel 当前的label
     * @return 下一个可能的label
     */
    public static String[] nextLabels(String curLabel){  //输入永远要用半角点
        ArrayList<String> ret = new ArrayList<>();
        String[] lbNum;
        if(curLabel.length() > 1){
            lbNum = curLabel.split("\\.");
        }else{
            lbNum = new String[1];
            lbNum[0] = curLabel;
        }

        int len = lbNum.length;
        if(len == 1){
            ret.add(lbNum[0] + ".1");
            ret.add(lbNum[0] + "． 1");
            ret.add(add(lbNum[0], 1));
        }else if(len == 2){
            ret.add(lbNum[0] + ".1");
            ret.add(lbNum[0] + "． 1");
            ret.add(lbNum[0] + "." + add(lbNum[1], 1));
            ret.add(lbNum[0] + "． " + add(lbNum[1], 1));
            ret.add(add(lbNum[0], 1));
        }else if(len == 3){
            ret.add(lbNum[0] + "." + lbNum[1] + "." + add(lbNum[2], 1));
            ret.add(lbNum[0] + "． " + lbNum[1] + "." + add(lbNum[2], 1));
            ret.add(lbNum[0] + "." + add(lbNum[1], 1));
            ret.add(lbNum[0] + "． " + add(lbNum[1], 1));
            ret.add(add(lbNum[0], 1));
        }

        String[] arr = new String[ret.size()];
        return ret.toArray(arr);
    }

    /**
     *
     * @param lb
     * @return 几级标题，从1开始
     */
    public static int titleLevel(String lb){
        String[] s = lb.split("\\.");
        if(s.length == 0 || s.length == 1) return 1;
        return s.length;
    }


}
