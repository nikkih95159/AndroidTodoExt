package com.nik.todoext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class TodoItemAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    List<TodoItem> itemList;
    ItemRowListener rowListener;
    public TodoItemAdapter(Context context, List<TodoItem> todoItemList) {
        mInflater = LayoutInflater.from(context);
        itemList = todoItemList;
        rowListener = (ItemRowListener) context;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        final String objectId = itemList.get(i).objectId;
        final String itemText = itemList.get(i).itemText;
        final boolean done = itemList.get(i).done;
        View view;
        ListRowHolder vh;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.row_items, parent, false);
            vh = new ListRowHolder(view);
            view.setTag(vh);
        }
        else {
            view = convertView;
            vh = (ListRowHolder)view.getTag();
        }
        vh.label.setText(itemText);
        vh.isDone.setChecked(done);

        vh.isDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rowListener.modifyItemState(objectId, itemText, !done);
            }
        });

        vh.isDeleteObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rowListener.onItemDelete(objectId, itemText, done);
            }
        });

        return view;
    }

    @Override
    public Object getItem(int index) {
        return itemList.get(index);
    }

    @Override
    public long getItemId(int index) {
        return (long)index;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    private class ListRowHolder {
        TextView label;
        CheckBox isDone;
        ImageButton isDeleteObject;
        private ListRowHolder(View row) {
            label = row.findViewById(R.id.textView2);
            isDone = row.findViewById(R.id.checkBox);
            isDeleteObject = row.findViewById(R.id.imageButton);
        }
    }
}
