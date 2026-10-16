package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

/** VIEW: ฟอร์มเพิ่มโน๊ต รับข้อมูลแล้วส่งให้ NoteController (ไม่สร้าง TextNote/CheckListNote เอง) */
public class AddNoteActivity extends AppCompatActivity {

    private NoteController noteController;
    private UserController userController;
    private Spinner spinnerUser;
    private TextView tvOwnerInfo;
    private EditText etTitle, etContent, etItem;
    private RadioGroup rgType;
    private LinearLayout layoutText, layoutChecklist, layoutItems;

    private final ArrayList<String> pendingItems = new ArrayList<>();
    private ArrayList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_note);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        noteController = new NoteController();
        userController = new UserController();
        spinnerUser = findViewById(R.id.spinnerUser);
        tvOwnerInfo = findViewById(R.id.tvOwnerInfo);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        etItem = findViewById(R.id.etItem);
        rgType = findViewById(R.id.rgType);
        layoutText = findViewById(R.id.layoutText);
        layoutChecklist = findViewById(R.id.layoutChecklist);
        layoutItems = findViewById(R.id.layoutItems);
        Button btnNewUser = findViewById(R.id.btnNewUser);
        Button btnAddItem = findViewById(R.id.btnAddItem);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        // Event: เลือก User จาก Spinner -> แสดงข้อมูล User (จากคลาส User) ทันที
        spinnerUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showOwnerInfo(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvOwnerInfo.setText(R.string.msg_no_user);
            }
        });

        // Event: เปลี่ยนชนิดโน๊ต -> สลับฟอร์ม TextNote / CheckListNote
        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isText = checkedId == R.id.rbText;
            layoutText.setVisibility(isText ? View.VISIBLE : View.GONE);
            layoutChecklist.setVisibility(isText ? View.GONE : View.VISIBLE);
        });

        // Event: ปุ่มต่างๆ
        btnNewUser.setOnClickListener(v ->
                startActivity(new Intent(AddNoteActivity.this, AddUserActivity.class)));
        btnAddItem.setOnClickListener(v -> addPendingItem());
        btnSave.setOnClickListener(v -> saveNote());
        btnCancel.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();   // โหลดใหม่ เผื่อเพิ่ม User ใหม่กลับมาจากหน้า AddUserActivity
    }

    private void loadUsers() {
        int previous = spinnerUser.getSelectedItemPosition();
        users = new ArrayList<>(userController.getAllUsers());
        ArrayList<String> names = new ArrayList<>();
        for (User u : users) names.add(u.getDisplayName());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, names);
        spinnerUser.setAdapter(adapter);
        if (previous >= 0 && previous < names.size()) spinnerUser.setSelection(previous);
        if (names.isEmpty()) tvOwnerInfo.setText(R.string.msg_no_user);
    }

    private void showOwnerInfo(int position) {
        if (position < 0 || position >= users.size()) return;
        User u = users.get(position);
        tvOwnerInfo.setText(u.getFullInfo());
    }

    private void addPendingItem() {
        String text = etItem.getText().toString().trim();
        if (text.isEmpty()) return;
        pendingItems.add(text);
        etItem.setText("");
        renderPendingItems();
    }

    private void renderPendingItems() {
        layoutItems.removeAllViews();
        for (int i = 0; i < pendingItems.size(); i++) {
            final int index = i;
            TextView tv = new TextView(this);
            tv.setText("☐  " + pendingItems.get(i) + "   (แตะเพื่อลบ)");
            tv.setTextSize(15);
            int pad = Math.round(8 * getResources().getDisplayMetrics().density);
            tv.setPadding(0, pad, 0, pad);
            // Event: แตะรายการ -> ลบออกจากเช็คลิสต์
            tv.setOnClickListener(v -> {
                pendingItems.remove(index);
                renderPendingItems();
            });
            layoutItems.addView(tv);
        }
    }

    private void saveNote() {
        if (users == null || users.isEmpty()) {
            Toast.makeText(this, R.string.msg_no_user, Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = users.get(spinnerUser.getSelectedItemPosition()).userId;
        String title = etTitle.getText().toString();

        NoteController.Result result;
        if (rgType.getCheckedRadioButtonId() == R.id.rbText) {
            result = noteController.addTextNote(userId, title, etContent.getText().toString());
        } else {
            addPendingItem();   // รวมข้อความที่พิมพ์ค้างไว้
            result = noteController.addCheckListNote(userId, title, pendingItems);
        }

        // View แปลผลลัพธ์จาก Controller เป็นข้อความบนหน้าจอ
        switch (result) {
            case OK:
                Toast.makeText(this, R.string.msg_note_saved, Toast.LENGTH_SHORT).show();
                finish();   // กลับหน้า MainActivity
                break;
            case NO_USER:
                Toast.makeText(this, R.string.msg_no_user, Toast.LENGTH_SHORT).show();
                break;
            case TITLE_EMPTY:
                etTitle.setError(getString(R.string.msg_title_required));
                break;
            case CONTENT_EMPTY:
                etContent.setError(getString(R.string.msg_content_required));
                break;
            case ITEMS_EMPTY:
                etItem.setError(getString(R.string.msg_item_required));
                break;
        }
    }
}
