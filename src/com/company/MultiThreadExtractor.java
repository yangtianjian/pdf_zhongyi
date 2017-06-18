package com.company;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by apple on 2017/6/18.
 */
public class MultiThreadExtractor extends Thread{
    private ArrayList<File> pdfList;

    public MultiThreadExtractor(){
        pdfList = new ArrayList<>();
    }

    public void addFile(File f){
        pdfList.add(f);
    }

    @Override
    public void run() {
        PDFExtractor pe = new PDFExtractor();
        PrintStream ps = null;
        try {
            ps = new PrintStream(System.out);
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

    public static void main(String[] args){
        File path = new File("/Users/apple/IdeaProjects/pdf2txt/pdfs");
        File[] pdfs = path.listFiles((dir, name) -> name.endsWith("pdf"));
        int nThreads = 4;
        MultiThreadExtractor[] t = new MultiThreadExtractor[nThreads];   //四个线程
        for(int i = 0; i < t.length; i++){
            t[i] = new MultiThreadExtractor();
        }
        for(int i = 0; i < pdfs.length; i++){
            t[i % nThreads].addFile(pdfs[i]);
        }
        for(int i = 0; i < nThreads; i++){
            t[i].start();
        }
        for(int i = 0; i < nThreads; i++){
            try {
                t[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
