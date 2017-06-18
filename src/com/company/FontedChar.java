package com.company;

/**
 * Created by apple on 2017/4/27.
 */

import com.company.dataStructure.MathHandler;

import java.awt.*;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Set;

public class FontedChar{
    private HashMap<String, String> characteristic;

    public FontedChar(HashMap<String, String> characteristic){
        this.characteristic = characteristic;
    }

    public float getFontSize(){
        return Float.parseFloat(characteristic.get("fontsize"));
    }

    public float getXscale(){
        return Float.parseFloat(characteristic.get("xscale"));
    }

    public float getWidth(){
        return Float.parseFloat(characteristic.get("width"));
    }

    public float getHeight(){
        return Float.parseFloat(characteristic.get("height"));
    }

    public float getSpace(){
        return Float.parseFloat(characteristic.get("space"));
    }

    public String getSubFont(){
        return characteristic.get("subfont");
    }

    public String getBaseFont(){
        return characteristic.get("basefont");
    }

    public String getCharacteristic(String characteristic){
        return this.characteristic.get(characteristic);
    }

    public HashMap<String, String> getAllCharacteristics(){
        return characteristic;
    }

    public char toCharacter(){
        return characteristic.get("character").charAt(0);
    }

    public String toString(){
        return String.format("%c", toCharacter());
    }

    /**
     * 两个字符串当指定的property一样，则返回true。
     * 注意：给定的properties在another字符里面都要有
     * 如果指定的属性在本实例中没有，返回比较不相等
     * @param another
     * @return
     */
    public boolean compareTo(FontedChar another, Set<String> properties, float tolerant){
        for(String p: properties){
            //如果这个字符根本没有这个属性，那么比较不等，否则数值与字符串区别对待，用MathHandler里面的算法考虑
            if(!characteristic.containsKey(p) ||
                    !MathHandler.compareValueString(characteristic.get(p), another.getAllCharacteristics().get(p), tolerant)){
                return false;
            }
        }
        return true;
    }

    public void outputInfo(PrintStream ps){
        ps.print("{");
        for(String key: characteristic.keySet()){
            ps.print(key + ":" + characteristic.get(key) + " ");
        }
        ps.println("}");
    }

//    mptext.put("fontsize", floatToString(text.getFontSize()));
//        mptext.put("xscale", floatToString(text.getXScale()));
//        mptext.put("width", floatToString(text.getWidthDirAdj()));
//        mptext.put("height", floatToString(text.getHeight()));
//        mptext.put("space", floatToString(text.getWidthOfSpace()));
//        mptext.put("subfont", text.getFont().getSubType());
//        mptext.put("basefont", text.getFont().getBaseFont());
//        mptext.put("character", text.getCharacter());
}
