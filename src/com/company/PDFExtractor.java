package com.company;


//关于pdfbox的doc: https://pdfbox.apache.org/docs/1.8.13/javadocs/
import com.company.dataStructure.MathHandler;
import org.apache.pdfbox.PDFBox;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.util.HashMap;

import com.company.dataStructure.Tuple;
import org.apache.pdfbox.util.PDFText2HTML;
import org.apache.pdfbox.util.PDFTextStripper;

//处理一个pdf，得到每个字的信息

public class PDFExtractor {
    private String file = null;

    private FontedString fontedString;
    private String rawString;

    public PDFExtractor(){

    }
    /**
     * 构建一个PDF提取机
     * @param file PDF文件
     */
    public PDFExtractor(String file){
        this.file = file;
    }

    /**
     * 设置需要解析的文件
     * @param file 设置的pdf文件
     */
    public void setFile(String file){
        this.file = file;
    }

    /**
     * 解析PDF。更新lmtext, text
     */
    public void parse(){
        if(file == null){
            System.err.println("please choose file.");
            System.exit(1);
        }
        PDDocument document = null;
        try{
            document = PDDocument.load(file,true);
            if(document.isEncrypted()){
                throw new Exception(file + ": requires password.");
            }
            PrintTextLocations ptl = new PrintTextLocations();  //创建一个解释流处理器
            List allPages = document.getDocumentCatalog().getAllPages();  //对于每一页
            for(int i = 0; i < allPages.size(); i++) {
                PDPage page = (PDPage)allPages.get(i);
                PDStream contents = page.getContents();
                if(contents != null) {
                    ptl.processStream(page, page.findResources(), page.getContents().getStream());
                }
            }
            fontedString = ptl.getFontedString();  //获取带字体的文章
            PDFTextStripper ps = new PDFTextStripper("UTF-8");

            rawString = ps.getText(document);
        } catch(Exception e){
            System.out.println(e.toString());
        }
        finally{
            if (document != null){
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public FontedString getFontedString(){
        return fontedString;
    }

    public String getRawString(){
        return rawString;
    }

    /**
     *以最大字号分离标题，从而分离论文。同一pdf只来自同一个期刊，字号先看做相同
     * @param content 带字体的pdf中的子串
     * @return  论文组，里面有带字体的某个论文的信息
     */
    public ArrayList<Paper> splitPaper(FontedString content, String rawContent){
        ArrayList<Paper> result = new ArrayList<>();
        int len = content.length();
        float maxXscale = 0.0f;
        for(int i = 0; i < len; i++){   //找到最大的字号
            float curXscale = content.getFontedCharAt(i).getXscale();
            maxXscale = Math.max(maxXscale, curXscale);
        }

        //第一遍根据字号扫描，分离出各个论文的标题
        int p = 0;  //开始分离文章，扫第二遍
        HashMap<String, String> sampleFont = new HashMap<>();  //建立需要找到的线索：字号最大
        sampleFont.put("xscale", Float.valueOf(maxXscale).toString());
        FontedChar sample = new FontedChar(sampleFont);  //构建字样，尝试匹配

        while(p < len){
            Tuple<String, Integer> match = content.getAllWithFont(sample, p, 0.12f);  //得到一个匹配点返回字符
            String title = match.first;
            int matchPoint = match.second;  //标题结束后的后一个字符的索引
            if(title.isEmpty()) break;  //如果没有匹配到标题，那么说明没有其他论文了
            Paper newPaper = new Paper(title, "");
            result.add(newPaper);
            p = matchPoint;
        }

        if(result.isEmpty()) return result;

        //第二遍通过标题到rawText里面找
        len = rawContent.length();
        int curTitle = 0, limTitle = result.size();
        String last_target = result.get(curTitle).getTitle();
        int lastp = rawContent.indexOf(last_target, 0);
        while(lastp < len){
            curTitle++;
            int newp;
            String new_target = null;
            if(curTitle >= limTitle) newp = len;
            else{
                new_target = result.get(curTitle).getTitle();
                newp = rawContent.indexOf(new_target, lastp + last_target.length());
            }
            if(newp != -1)
                result.get(curTitle - 1).setAllRawContent(rawContent.substring(lastp + last_target.length(), newp));
            lastp = newp;
            if(new_target != null) last_target = new_target;
        }

        return result;
    }


    public static void main(String[] args){
        PDFExtractor pe = new PDFExtractor();

        File path = new File("/Users/apple/IdeaProjects/pdf2txt/pdfs");
        PrintStream ps = null;
        try {
            ps = new PrintStream(System.out);
            File[] pdfList = path.listFiles((dir, name) -> name.endsWith("pdf"));
            if(pdfList != null){
                for(File pdf: pdfList){
                    if(pdf.length() < 1024) continue;
                    System.out.println(pdf.getName());  //打印状态
                    pe.setFile(pdf.getAbsolutePath());
                    ps.println(pdf.getAbsolutePath());
                    pe.parse();
                    FontedString content = pe.getFontedString(); //获取带字体的字符串
                    String rawContent = pe.getRawString();
                    ArrayList<Paper> papers = pe.splitPaper(content, rawContent);  //分成不同的论文
                    if(papers.size() == 0) continue;
                    for(Paper p: papers){
                        p.parse();
                        PrintStream psm = new PrintStream("./xmls/" + p.getTitle() + ".xml");
                        p.writeXML(psm);
                        psm.close();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally{
            if(ps != null) ps.close();
        }

    }

}