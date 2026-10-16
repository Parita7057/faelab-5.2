package com.example.myapplication;

import android.util.Log;
import java.util.ArrayList;

public class CheckListNote extends AbstractNote {
    public ArrayList<CheckItem> items = new ArrayList<>();

    @Override
    public String getSummary() {
        int done = 0;
        for (CheckItem item : items) {
            if (item.done) done++;
        }
        return "เสร็จแล้ว " + done + "/" + items.size() + " รายการ";
    }

    @Override
    public void getOrder() {
        Log.d("NOTE_ORDER", "CheckListNote: " + title);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getType() {
        return "Checklist Note";
    }
}
