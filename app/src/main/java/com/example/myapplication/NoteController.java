package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * CONTROLLER: ตัวกลางระหว่าง View (MainActivity, AddNoteActivity)
 * กับ Model (TextNote, CheckListNote, User ผ่าน DataStore)
 *
 * - View ส่งข้อมูลที่ผู้ใช้กรอกมาให้ Controller
 * - Controller ตรวจสอบข้อมูล สร้าง/แก้ไข Model แล้วส่งผลลัพธ์กลับ
 */
public class NoteController {

    public enum Result { OK, NO_USER, TITLE_EMPTY, CONTENT_EMPTY, ITEMS_EMPTY }

    private final DataStore store = DataStore.getInstance();

    // ---------- เพิ่มโน๊ต ----------
    public Result addTextNote(String userId, String title, String content) {
        Result check = validateCommon(userId, title);
        if (check != Result.OK) return check;
        if (content == null || content.trim().isEmpty()) return Result.CONTENT_EMPTY;

        String now = DataStore.now();
        TextNote note = new TextNote();
        note.field = "text_type";
        note.userId = userId;
        note.title = title.trim();
        note.content = content.trim();
        note.createdAt = now;
        note.updatedAt = now;
        store.addNote(note);          // Model แจ้ง View ให้รีเฟรชเอง
        return Result.OK;
    }

    public Result addCheckListNote(String userId, String title, List<String> items) {
        Result check = validateCommon(userId, title);
        if (check != Result.OK) return check;
        if (items == null || items.isEmpty()) return Result.ITEMS_EMPTY;

        String now = DataStore.now();
        CheckListNote note = new CheckListNote();
        note.field = "checklist_type";
        note.userId = userId;
        note.title = title.trim();
        for (String text : items) note.items.add(new CheckItem(text));
        note.createdAt = now;
        note.updatedAt = now;
        store.addNote(note);
        return Result.OK;
    }

    private Result validateCommon(String userId, String title) {
        if (userId == null || store.findUser(userId) == null) return Result.NO_USER;
        if (title == null || title.trim().isEmpty()) return Result.TITLE_EMPTY;
        return Result.OK;
    }

    // ---------- แสดงโน๊ต ----------
    public List<AbstractNote> getAllNotes() {
        return new ArrayList<>(store.getNotes());
    }

    public int countNotesOf(String userId) {
        return store.countNotesOf(userId);
    }

    // ---------- เชื่อม Note กับ User ----------
    /** @return ผู้ใช้เจ้าของโน๊ต (null ถ้าไม่พบ) */
    public User getOwnerOf(AbstractNote note) {
        return store.findUser(note.userId);
    }

    /** ข้อความบอกเจ้าของโน๊ต พร้อมใช้แสดงบน View */
    public String getOwnerLabel(AbstractNote note) {
        User owner = getOwnerOf(note);
        return owner == null ? "ไม่ทราบเจ้าของ" : owner.getShortInfo();
    }

    // ---------- แก้ไขสถานะเช็คลิสต์ ----------
    /** @return ข้อความสรุปใหม่ของเช็คลิสต์ เพื่อให้ View อัปเดตข้อความ */
    public String setItemDone(CheckListNote note, CheckItem item, boolean done) {
        item.done = done;
        note.updatedAt = DataStore.now();
        return note.getSummary();
    }

    // ---------- Listener (ให้ View ลงทะเบียนผ่าน Controller) ----------
    public void addListener(DataStore.OnDataChangedListener l) {
        store.addListener(l);
    }

    public void removeListener(DataStore.OnDataChangedListener l) {
        store.removeListener(l);
    }
}
