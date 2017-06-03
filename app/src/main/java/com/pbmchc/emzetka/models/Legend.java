package com.pbmchc.emzetka.models;

/**
 * Created by Piotrek on 2016-12-16.
 */
public class Legend {

    private String symbol;
    private String content;

    public String getSymbol() {
        return symbol;
    }

    public String getContent() {
        return content;
    }

    public String toString(){
        return symbol + " - " + content + "\n";
    }

}
