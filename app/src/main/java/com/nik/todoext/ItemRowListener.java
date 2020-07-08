package com.nik.todoext;

public interface ItemRowListener {
    void modifyItemState(String itemObjectId, String itemText, boolean isDone);
    void onItemDelete(String itemObjectId, String itemText, boolean isDone);
}
