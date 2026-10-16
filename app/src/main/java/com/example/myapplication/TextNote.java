package com.example.myapplication;

import android.util.Log;

public class TextNote extends AbstractNote {
    public String content;

    @Override
    public String getSummary() {
        if (content == null) return "";
        return content.length() > 40 ? content.substring(0, 40) + "..." : content;
    }

    @Override
    public void getOrder() {
        Log.d("NOTE_ORDER", "TextNote: " + title);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getType() {
        return "Text Note";
    }
}
