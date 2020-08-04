package com.nik.todoext;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class TodoItem {
    public String objectId = "";
    public String itemText = "";
    public boolean done = false;
    public long date = 0;
    public TodoItem() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoItem todoItem = (TodoItem) o;
//        return done == todoItem.done &&
        return
                objectId.equals(todoItem.objectId) &&
                itemText.equals(todoItem.itemText);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(objectId, itemText, done);
    }
}
