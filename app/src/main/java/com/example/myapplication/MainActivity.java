package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** VIEW: แสดงรายการโน๊ต + เจ้าของโน๊ต ข้อมูลทั้งหมดมาจาก NoteController */
public class MainActivity extends AppCompatActivity implements DataStore.OnDataChangedListener {

    private LinearLayout notesContainer;
    private TextView tvEmpty;
    private NoteController noteController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        noteController = new NoteController();
        notesContainer = findViewById(R.id.notesContainer);
        tvEmpty = findViewById(R.id.tvEmpty);

        Button btnAddNote = findViewById(R.id.btnAddNote);
        Button btnUsers = findViewById(R.id.btnUsers);

        // Event: กดปุ่ม -> เปลี่ยนหน้าไป AddNoteActivity
        btnAddNote.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddNoteActivity.class)));

        // Event: กดปุ่ม -> เปลี่ยนหน้าไป AddUserActivity
        btnUsers.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, AddUserActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteController.addListener(this);
        renderNotes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        noteController.removeListener(this);
    }

    @Override
    public void onDataChanged() {
        renderNotes();
    }

    private void renderNotes() {
        notesContainer.removeAllViews();
        java.util.List<AbstractNote> notes = noteController.getAllNotes();
        tvEmpty.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);

        for (AbstractNote note : notes) {
            notesContainer.addView(buildNoteCard(note));
        }
    }

    private View buildNoteCard(AbstractNote note) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card);
        card.setPadding(dp(16), dp(12), dp(16), dp(12));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(12);
        card.setLayoutParams(lp);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(note.getTitle());
        tvTitle.setTextSize(18);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(tvTitle);

        // แสดงเจ้าของโน๊ต: Controller ดึงข้อมูลจาก User ให้
        TextView tvOwner = new TextView(this);
        tvOwner.setText("เจ้าของ: " + noteController.getOwnerLabel(note));
        tvOwner.setTextSize(13);
        card.addView(tvOwner);

        TextView tvMeta = new TextView(this);
        tvMeta.setText(note.getType() + "  •  " + note.createdAt);
        tvMeta.setTextSize(12);
        tvMeta.setAlpha(0.6f);
        card.addView(tvMeta);

        if (note instanceof TextNote) {
            TextView tvContent = new TextView(this);
            tvContent.setText(((TextNote) note).content);
            tvContent.setTextSize(15);
            tvContent.setPadding(0, dp(8), 0, 0);
            card.addView(tvContent);
        } else if (note instanceof CheckListNote) {
            final CheckListNote cl = (CheckListNote) note;
            final TextView tvSummary = new TextView(this);
            tvSummary.setText(cl.getSummary());
            tvSummary.setTextSize(12);
            tvSummary.setAlpha(0.6f);

            for (final CheckItem item : cl.items) {
                CheckBox cb = new CheckBox(this);
                cb.setText(item.text);
                cb.setChecked(item.done);
                // Event: ติ๊ก/ยกเลิกติ๊ก -> อัปเดตสถานะรายการ
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    // ส่ง event ให้ Controller แก้ Model แล้วนำข้อความสรุปใหม่มาแสดง
                    tvSummary.setText(noteController.setItemDone(cl, item, isChecked));
                });
                card.addView(cb);
            }
            card.addView(tvSummary);
        }
        return card;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
