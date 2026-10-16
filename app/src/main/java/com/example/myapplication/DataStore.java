package com.example.myapplication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * MODEL (data holder): ที่เก็บข้อมูลกลาง (Singleton) เก็บ User และ Note ไว้ในหน่วยความจำ
 * เมื่อข้อมูลเปลี่ยน Model จะแจ้ง Listener (View) ให้อัปเดตตัวเอง
 * View ไม่ควรเรียกคลาสนี้โดยตรง ให้ผ่าน Controller เท่านั้น
 */
public class DataStore {

    public interface OnDataChangedListener {
        void onDataChanged();
    }

    private static DataStore instance;

    private final ArrayList<User> users = new ArrayList<>();
    private final ArrayList<AbstractNote> notes = new ArrayList<>();
    private final ArrayList<OnDataChangedListener> listeners = new ArrayList<>();
    private int nextUserId = 1;
    private int nextAddressId = 101;

    private DataStore() {
        // ข้อมูลผู้ใช้ตั้งต้น (จากโค้ดเดิมใน MainActivity)
        addUser("kafae_057", "Parita", "123/45 Bangkok, Thailand");
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    public static String now() {
        return new SimpleDateFormat("d MMM yyyy HH:mm", new Locale("th", "TH")).format(new Date());
    }

    // ---------- Listener ----------
    public void addListener(OnDataChangedListener l) {
        if (!listeners.contains(l)) listeners.add(l);
    }

    public void removeListener(OnDataChangedListener l) {
        listeners.remove(l);
    }

    private void fireChanged() {
        for (OnDataChangedListener l : new ArrayList<>(listeners)) l.onDataChanged();
    }

    // ---------- User ----------
    /** @return false ถ้า userId ซ้ำ */
    public boolean addUser(String userId, String name, String addressText) {
        if (findUser(userId) != null) return false;

        Address address = new Address();
        address.addressId = nextAddressId++;
        address.address = addressText;

        User user = new User();
        user.id = nextUserId++;
        user.userId = userId;
        user.title = name;
        user.createdAt = now();
        user.address = address;

        users.add(user);
        fireChanged();
        return true;
    }

    public User findUser(String userId) {
        for (User u : users) {
            if (u.userId.equals(userId)) return u;
        }
        return null;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public int countNotesOf(String userId) {
        int n = 0;
        for (AbstractNote note : notes) {
            if (note.userId.equals(userId)) n++;
        }
        return n;
    }

    // ---------- Note ----------
    public void addNote(AbstractNote note) {
        notes.add(0, note);   // โน๊ตใหม่อยู่บนสุด
        fireChanged();
    }

    public ArrayList<AbstractNote> getNotes() {
        return notes;
    }
}
