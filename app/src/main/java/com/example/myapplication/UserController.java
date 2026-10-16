package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

/** CONTROLLER: ตัวกลางระหว่าง View (AddUserActivity, AddNoteActivity) กับ Model (User, Address) */
public class UserController {

    public enum Result { OK, EMPTY_FIELD, DUPLICATE }

    private final DataStore store = DataStore.getInstance();

    public Result addUser(String userId, String name, String address) {
        if (isBlank(userId) || isBlank(name) || isBlank(address)) return Result.EMPTY_FIELD;
        boolean ok = store.addUser(userId.trim(), name.trim(), address.trim());
        return ok ? Result.OK : Result.DUPLICATE;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(store.getUsers());
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public void addListener(DataStore.OnDataChangedListener l) {
        store.addListener(l);
    }

    public void removeListener(DataStore.OnDataChangedListener l) {
        store.removeListener(l);
    }
}
