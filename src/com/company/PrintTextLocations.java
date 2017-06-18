package com.company;

/**
 * Created by apple on 2017/4/25.
 */
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;
import java.io.IOException;
import java.util.HashMap;


//处理一个pdf流(一般为一页)
public class PrintTextLocations extends PDFTextStripper{

    private FontedString fontedString;

    public PrintTextLocations() throws IOException {
        super("utf-8");
        super.setSortByPosition(true);
        fontedString = new FontedString();
    }

    public String floatToString(float f){
        return String.format("%f", f);
    }

    public FontedString getFontedString(){
        return fontedString;
    }

    protected void processTextPosition(TextPosition text) {
        //PDFontDescriptor fd = text.getFont().getFontDescriptor();
        HashMap<String, String> mptext = new HashMap<>();
        mptext.put("fontsize", floatToString(text.getFontSize()));
        mptext.put("xscale", floatToString(text.getXScale()));
        mptext.put("width", floatToString(text.getWidthDirAdj()));
        mptext.put("height", floatToString(text.getHeight()));
        mptext.put("space", floatToString(text.getWidthOfSpace()));
        mptext.put("subfont", text.getFont().getSubType());
        mptext.put("basefont", text.getFont().getBaseFont());
        mptext.put("character", text.getCharacter());
        //TODO<Interface>: Add more characteristic here
        FontedChar c = new FontedChar(mptext);  //处理完pdf的每个字创建一个fontedchar
        fontedString.append(c);  //加入到String中

    }



}