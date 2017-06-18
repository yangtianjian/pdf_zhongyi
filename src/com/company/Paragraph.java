package com.company;

/**
 * Created by apple on 2017/4/25.
 */

import java.util.*;

public class Paragraph {
    private String numLabel = "";  //标题编号，形如1, 1.1, 1.2, 1.1.1, ...
    private String content_str = "";

    public Paragraph(String numLabel, String content_str){
        this.numLabel = numLabel;
        this.content_str = content_str;
    }

    public void append(String str){
        content_str += str;
    }

    public String getContent_str(){
        return content_str;
    }

    public String getNumLabel(){
        return numLabel;
    }
}
