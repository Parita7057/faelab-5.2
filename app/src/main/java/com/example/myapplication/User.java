package com.example.myapplication;

/** MODEL: ข้อมูลผู้ใช้ */
public class User {
    public int id;
    public String userId;
    public String title;      // ชื่อผู้ใช้
    public String createdAt;
    public Address address;

    public User() {}

    public String getDisplayName() {
        return title + " (" + userId + ")";
    }

    /** ข้อมูลผู้ใช้แบบเต็ม (ใช้แสดงว่าโน๊ตเป็นของใคร) */
    public String getFullInfo() {
        return "ชื่อ: " + title
                + "\nรหัสผู้ใช้: " + userId
                + "\nที่อยู่: " + (address == null ? "-" : address.address);
    }

    /** ข้อมูลผู้ใช้แบบย่อ 1 บรรทัด */
    public String getShortInfo() {
        return getDisplayName() + "  |  " + (address == null ? "-" : address.address);
    }
}
