package com.company;

/**
 * Created by apple on 2017/4/25.
 */

import com.company.dataStructure.MathHandler;
import com.company.dataStructure.Tuple;
import java.util.function.Function;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;


//处理对象是严格的一片论文
public class Paper {
    private String allRawContent;
    private String title;
    private String abstract_str;
    private String[] keyword_str;
    private String classify_no;
    private String code;
    private String id_no;

    private ArrayList<Paragraph> paragraphs;

    //private Map<String, String> contentFont; //正文字体

    //各个开头信息的标志
    private final String[] abstract_kw = {"【摘要】", "[摘要]", "摘要:", "摘要：", "摘要", "Abstract", "abstract"};
    private final String[] keyword_kw = {"【关键词】", "[关键词]", "关键词:", "关键词：", "关键词", "Keywords", "Keyword", "keywords", "keyword", "Key words", "Key word"};
    private final String[] classify_kw = {"【中图分类号】", "[中图分类号]", "中图分类号:", "中图分类号：", "中图分类号"};
    private final String[] code_kw = {"【文献标志码】", "[文献标志码]", "文献标志码:", "文献标志码：", "文献标志码"};
    private final String[] id_kw = {"【文章编号】", "[文章编号]", "文章编号:", "文章编号：", "文章编号"};

    public Paper(String title, String allRawContent){
        this.title = title;
        this.allRawContent = allRawContent;
        this.abstract_str = "";
        paragraphs = new ArrayList<>();
        keyword_str = new String[1];
        keyword_str[0] = "";
    }

    public void setAllRawContent(String allRawContent){
        this.allRawContent = allRawContent;
    }

    public String getAllRawContent(){
        return allRawContent;
    }

    public String getTitle(){
        return title;
    }

    public void parse(){
        //extractContentFont();
        int pt = extractAbstract(0);
        pt = extractKeyword(pt);
        extractSubArea(pt);
    }


    private void writeTitle(PrintStream ps){
        ps.println("<title>");
        ps.println(title);
        ps.println("</title>");
    }

    private void writeAbstract(PrintStream ps){
        ps.println("<abstract>");
        ps.println(abstract_str);
        ps.println("</abstract>");
    }

    private void writeKeyword(PrintStream ps){
        ps.println("<keywords>");
        ps.println(String.join("$", keyword_str));
        ps.println("</keywords>");
    }

    private void printTabs(int k, PrintStream ps){
        if(k < 0) return;
        for(int i = 0; i < k; i++) {ps.print("\t");}
    }

    private void writeContent(PrintStream ps){
        Function<String, Integer> points = (s) -> {
            int p = 0; for(int i = 0; i < s.length(); i++) { if(s.charAt(i) == '.') p++; } return p;
        };
        Function<Tuple<String, String>, Integer> compareTitle = (t) -> points.apply(t.second) - points.apply(t.first);
        Stack<Integer> s = new Stack<>();
        for(int i = 0; i < paragraphs.size(); i++){
            if(i == 0 || compareTitle.apply(new Tuple<>(paragraphs.get(i).getNumLabel(), paragraphs.get(i - 1).getNumLabel())) < 0 ){
                ps.println("<" + paragraphs.get(i).getNumLabel() + ">");
                ps.println(paragraphs.get(i).getContent_str());
                s.push(i);
            }else{
                while(!s.isEmpty()){
                    int k = s.peek();
                    int cmp = compareTitle.apply(new Tuple<>(paragraphs.get(k).getNumLabel(), paragraphs.get(i).getNumLabel()));
                    if(cmp <= 0){
                        ps.println("</" + paragraphs.get(k).getNumLabel() + ">");
                        s.pop();
                    }else{
                        break;
                    }
                }
                ps.println("<" + paragraphs.get(i).getNumLabel() + ">");
                ps.println(paragraphs.get(i).getContent_str());
                s.push(i);
            }
        }
        while(!s.isEmpty()){
            int k = s.pop();
            ps.println(paragraphs.get(k).getContent_str());
            ps.println("</" + paragraphs.get(k).getNumLabel() + ">");
        }
    }

    public void writeXML(PrintStream ps){
        writeTitle(ps);
        writeAbstract(ps);
        writeKeyword(ps);
        writeContent(ps);
    }


    private int extractAbstract(int lastPointer){
        for(int i = 0; i < abstract_kw.length; i++){
            for(int j = 0; j < keyword_kw.length; j++){
                String sub = MathHandler.stringBetweenTwoStrings(allRawContent, abstract_kw[i], keyword_kw[j], lastPointer, false);
                if(!sub.isEmpty()){
                    abstract_str = sub.trim();
                    return lastPointer + abstract_str.length();
                }
            }
        }
        return lastPointer;
    }

    private int extractKeyword(int lastPointer){
        String keywords_str = "";
        String[] spliters = {";", "；", ",", " "};
        int maxKeyWordSize = 0;
        for(int i = 0; i < keyword_kw.length; i++){
            for(int j = 0; j < classify_kw.length; j++){
                keywords_str = MathHandler.stringBetweenTwoStrings(allRawContent, keyword_kw[i], classify_kw[j], lastPointer, false);
                if(!keywords_str.isEmpty()){
                    int nextPointer = lastPointer + keywords_str.length();
                    keywords_str = keywords_str.trim();
                    for(String spliter: spliters){  //把每一种spliter尝试一遍，取使得分出词中关键词数最大的spliter
                        String[] keywords = keywords_str.split(spliter);
                        if(keywords.length > maxKeyWordSize){  //如果当前分词数最大
                            maxKeyWordSize = keywords.length;  //更新最大分词数
                            this.keyword_str = keywords.clone();  //更新关键词表
                        }
                    }
                    return nextPointer;
                }
            }
        }
        return lastPointer;
    }


    private void extractSubArea(int lastPointer){
        String contentArea = allRawContent.substring(lastPointer, allRawContent.length());  //正文区域的字符串
        Scanner scanner = new Scanner(contentArea);
        String[] curLabels = {"1"};
        Paragraph curParagraph = null;

        while(scanner.hasNextLine()){ //一行一行读
            String line = scanner.nextLine().trim();
            boolean found = false;
            for(String label: curLabels){
                if(line.indexOf(label) == 0){
                    found = true;  //找到标号了
                    Paragraph newParagraph = new Paragraph(label.replace("． ", "."), "");
                    curParagraph = newParagraph;
                    paragraphs.add(newParagraph);
                    curLabels = MathHandler.nextLabels(curParagraph.getNumLabel());
                    break;
                }
            }
            if(curParagraph != null) curParagraph.append(line);
        }
    }
}
