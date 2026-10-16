package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** VIEW: ฟอร์มเพิ่ม/แสดงผู้ใช้ ติดต่อ Model ผ่าน UserController และ NoteController */
public class AddUserActivity extends AppCompatActivity implements DataStore.OnDataChangedListener {

    private EditText etUserId, etName, etAddress;
    private LinearLayout usersContainer;
    private UserController userController;
    private NoteController noteController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_user);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userController = new UserController();
        noteController = new NoteController();
        etUserId = findViewById(R.id.etUserId);
        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        usersContainer = findViewById(R.id.usersContainer);
        Button btnSaveUser = findViewById(R.id.btnSaveUser);
        Button btnBack = findViewById(R.id.btnBack);

        // Event: กดปุ่มบันทึกผู้ใช้
        btnSaveUser.setOnClickListener(v -> saveUser());

        // Event: กดปุ่มกลับ
        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        userController.addListener(this);
        renderUsers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userController.removeListener(this);
    }

    @Override
    public void onDataChanged() {
        renderUsers();
    }

    private void saveUser() {
        String userId = etUserId.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        UserController.Result result = userController.addUser(userId, name, address);
        if (result == UserController.Result.EMPTY_FIELD) {
            Toast.makeText(this, R.string.msg_fill_all, Toast.LENGTH_SHORT).show();
            return;
        }
        if (result == UserController.Result.DUPLICATE) {
            etUserId.setError(getString(R.string.msg_user_duplicate));
            return;
        }
        etUserId.setText("");
        etName.setText("");
        etAddress.setText("");
        etUserId.requestFocus();
        Toast.makeText(this, R.string.msg_user_saved, Toast.LENGTH_SHORT).show();
    }

    private void renderUsers() {
        usersContainer.removeAllViews();
        for (User u : userController.getAllUsers()) {
            TextView tv = new TextView(this);
            tv.setText(u.getDisplayName()
                    + "\nที่อยู่: " + u.address.address
                    + "\nสร้างเมื่อ: " + u.createdAt
                    + "  |  โน๊ต: " + noteController.countNotesOf(u.userId) + " รายการ");
            tv.setBackgroundResource(R.drawable.bg_card);
            int pad = Math.round(12 * getResources().getDisplayMetrics().density);
            tv.setPadding(pad, pad, pad, pad);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = pad;
            tv.setLayoutParams(lp);
            usersContainer.addView(tv);
        }
    }
}
