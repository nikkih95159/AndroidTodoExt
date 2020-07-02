package com.example.todoext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemRowListener {
    private DatabaseReference mDatabase;
    List<TodoItem> todoItemList;
    TodoItemAdapter adapter;
    ListView listViewItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        listViewItems = findViewById(R.id.items_list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewItemDialog();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        todoItemList = new ArrayList<TodoItem>();
        adapter = new TodoItemAdapter(this, todoItemList);
        listViewItems.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference("users/001/todo").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addDataToList(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("MainActivity", "loadItem:onCancelled", error.toException());
            }
        });
    }

    private void addDataToList(DataSnapshot snapshot) {
        Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
        while (items.hasNext()) {
            DataSnapshot todoListIndex = items.next();
            HashMap<String, Object> map = (HashMap<String, Object>) todoListIndex.getValue();
//            Iterator<DataSnapshot> itemsIterator = todoListIndex.getChildren().iterator();
//            todoItemList.add(todoListIndex.getValue().toString());
//            while (itemsIterator.hasNext()) {
//                DataSnapshot currentItem = itemsIterator.next();
                TodoItem todoItem = new TodoItem();
                todoItem.itemText = map.get("itemText").toString();
                todoItem.objectId = todoListIndex.getKey();
                todoItem.done = (boolean)map.get("done");
                todoItemList.add(todoItem);
//                Log.d("items iterator", ;
//            todoItem.itemText = currentItem.getValue();
//            todoItemList.add(todoItem);
//            todoItem.objectId = ;
//                todoItem.itemText = currentItem.getValue;
//                Log.d("CURRENT ITEM", currentItem.getValue().toString());
//                Object map = currentItem.getValue();
//                todoItemList.add(currentItem.getValue().toString());
//            }
        }
        adapter.notifyDataSetChanged();
    }

    private void addNewItemDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText itemEditText = new EditText(this);
        alert.setMessage("Add new item");
        alert.setTitle("Enter todo item");
        alert.setView(itemEditText);
        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TodoItem todoItem = new TodoItem();
                todoItem.itemText = itemEditText.getText().toString();
                todoItem.done = false;
//                String key = mDatabase.child("users/001/todo").push().getKey();
                DatabaseReference key = mDatabase.child("users/001/todo").push();
                todoItem.objectId = key.getKey();
                key.setValue(todoItem);
//                Map<String, Object> childUpdates = new HashMap<>();
//                childUpdates.put("users/001/todo/" + key, itemEditText.getText().toString());
//                mDatabase.updateChildren(childUpdates);
                dialogInterface.dismiss();
//                todoItemList.add(itemEditText.getText().toString());
                todoItemList.add(todoItem);
                Toast.makeText(getApplicationContext(), "Added todo '" + todoItem.itemText + "'", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });
        alert.show();
    }

    @Override
    public void modifyItemState(String itemObjectId, String itemText, boolean isDone) {
        DatabaseReference itemReference = mDatabase.child("users/001/todo").child(itemObjectId);
        itemReference.child("done").setValue(isDone);
        TodoItem temp = new TodoItem();
        temp.itemText = itemText;
        temp.objectId = itemObjectId;
        temp.done = !isDone;
        int index = todoItemList.indexOf(temp);
        todoItemList.get(index).done = isDone;
        if (isDone == true)
            Toast.makeText(getApplicationContext(), "Completed todo '" + itemText + "'", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Undo todo '" + itemText + "'", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemDelete(String itemObjectId, String itemText, boolean isDone) {
        DatabaseReference itemReference = mDatabase.child("users/001/todo").child(itemObjectId);
        itemReference.removeValue();
        TodoItem temp = new TodoItem();
        temp.itemText = itemText;
        temp.objectId = itemObjectId;
        temp.done = isDone;
        int index = todoItemList.indexOf(temp);
        todoItemList.remove(index);
        Toast.makeText(getApplicationContext(), "Deleted todo '" + itemText + "'", Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    /** called when user taps adds a todo */
    public void addTodo(View view) {
        // do something in response to the button
//        Intent intent = new Intent(this, DisplayMessageActivity.class);
//        EditText editText = (EditText) findViewById(R.id.entertodotext);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
    }
}