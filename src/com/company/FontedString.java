package com.company;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import com.company.dataStructure.Tuple;
import com.company.dataStructure.MathHandler;

/**
 * Created by apple on 2017/4/27.
 */
public class FontedString{
    private ArrayList<FontedChar> chars;

    public FontedString(){
        chars = new ArrayList<>();
    }
    public FontedString(ArrayList<FontedChar> chars){
        this.chars = chars;
    }

    public int length(){
        return chars.size();
    }

    public FontedChar getFontedCharAt(int index){
        return chars.get(index);
    }

    public char getCharAt(int index){
        return chars.get(index).toCharacter();
    }

    /**
     * 给出一个样式字符c，从索引index开始找出当前实例为样式c的连续子串，
     * @param c 给定样式
     * @param tolerant 容纳率。对于值属性，我们考虑与sample相差率小于tolerant时视为相同
     * @return 字符串提取完字符串后第一个不是样式c的字符的在本实例的索引
     */
    public Tuple<String, Integer> getAllWithFont(FontedChar c, int index, float tolerant){
        String ret = "";
        int i;
        int len = this.length();
        Set<String> properties = c.getAllCharacteristics().keySet();
        properties.remove("character");  //检索字体相同，特点不能包含字符本身
        for(i = index; i < len; i++){
            FontedChar cur = chars.get(i);
            if(!cur.compareTo(c, properties, tolerant)){
                if(ret.isEmpty()) continue;  //比较不成功一种情况是尚未找到匹配
                else break;  //另一种情况是已经到了匹配的结尾
            }
            ret += cur.toString();
        }
        return new Tuple<>(ret, i);
    }



    /**
     * 提取从[start,end)索引之间的子串
     * @param start
     * @param end
     * @return
     */
    public FontedString substring(int start, int end){
        FontedString ret = new FontedString();
        for(int i = start; i < end; i++){
            ret.append(chars.get(i));
        }
        return ret;
    }

    public void append(FontedChar c){
        chars.add(c);
    }

    public void clear(){
        chars.clear();
    }

    public String toString(){
        String ret = "";
        for(FontedChar c: chars){
            ret += c.toString();
        }
        return ret;
    }

    /**
     * 输出每个字的字体、字号
     */
    public void outputInfo(PrintStream ps){
        for(FontedChar c: chars){
            c.outputInfo(ps);
        }
    }

}
