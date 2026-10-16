package com.example.myapplication;

public abstract class AbstractNote {
    public String field;
    public String userId;      // อ้างอิงไปยัง User.userId (เจ้าของโน๊ต)
    public String title;
    public String createdAt;
    public String updatedAt;

    public abstract String getSummary();   // เปลี่ยนจาก void -> String เพื่อนำไปแสดงบน UI
    public abstract void getOrder();
    public abstract String getTitle();
    public abstract String getType();      // ชนิดของโน๊ต (ใช้แสดงผล)
}
