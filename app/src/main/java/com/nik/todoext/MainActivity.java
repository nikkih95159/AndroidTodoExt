package com.nik.todoext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements ItemRowListener {
    final long MS24HOURS = 86400000;
    private DatabaseReference mDatabase;
    ArrayList<TodoItem> todoItemList;
    ArrayList<TodoItem> doneTodoList;
    TodoItemAdapter adapter;
    TodoItemAdapter doneAdapter;
    ListView listViewItems;
    ListView listDoneItems;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        listViewItems = findViewById(R.id.items_list);
        listDoneItems = findViewById(R.id.done_items_list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewItemDialog();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        todoItemList = new ArrayList<TodoItem>();
        doneTodoList = new ArrayList<TodoItem>();
        adapter = new TodoItemAdapter(this, todoItemList);
        doneAdapter = new TodoItemAdapter(this, doneTodoList);
        listViewItems.setAdapter(adapter);
        listDoneItems.setAdapter(doneAdapter);

        username = getIntent().getStringExtra("USERNAME");
        password = getIntent().getStringExtra("PASSWORD");

//        Log.d("USERNAME MAIN", username);

//        FirebaseDatabase.getInstance().getReference("users/" + username + "/todo").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                addDataToList(snapshot);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
////                Log.w("MainActivity", "loadItem:onCancelled", error.toException());
//            }
//        });

        FirebaseDatabase.getInstance().getReference("users/" + username + "/todo").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("CHILD ADDED", snapshot.child("itemText").getValue().toString());
                TodoItem todoItem = new TodoItem();
                todoItem.itemText = snapshot.child("itemText").getValue().toString();
                todoItem.done = Boolean.valueOf(snapshot.child("done").getValue().toString());
                todoItem.objectId = snapshot.child("objectId").getValue().toString();
                int index = todoItemList.indexOf(todoItem);
                if (index == -1) {
                    todoItemList.add(todoItem);
//                    Toast.makeText(getApplicationContext(), "New todo added '" + todoItem.itemText + "'", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
                for (int i = 0; i < todoItemList.size(); i++) {
                    Log.d("LIst: ", todoItemList.get(i).itemText + ", " + todoItemList.get(i).done);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("CHILD CHANGED", snapshot.getValue().toString());
                TodoItem todoItem = new TodoItem();
                todoItem.itemText = snapshot.child("itemText").getValue().toString();
                todoItem.done = !Boolean.valueOf(snapshot.child("done").getValue().toString());
                todoItem.objectId = snapshot.child("objectId").getValue().toString();
                int index = todoItemList.indexOf(todoItem);
                if (index != -1 && todoItem.done == todoItemList.get(index).done) {
                    todoItemList.get(index).done = !todoItem.done;
//                    Toast.makeText(getApplicationContext(), "Modified todo '" + todoItem.itemText + "'", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d("CHILD REMOVED", snapshot.getValue().toString());
                TodoItem todoItem = new TodoItem();
                todoItem.itemText = snapshot.child("itemText").getValue().toString();
                todoItem.done = Boolean.valueOf(snapshot.child("done").getValue().toString());
                todoItem.objectId = snapshot.child("objectId").getValue().toString();
                int index = todoItemList.indexOf(todoItem);
                if (index != -1) {
                    todoItemList.remove(index);
//                    Toast.makeText(getApplicationContext(), "Todo removed '" + todoItem.itemText + "'", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseDatabase.getInstance().getReference("users/" + username + "/todo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Calendar c1 = Calendar.getInstance();
                long date = c1.getTimeInMillis();
                for (DataSnapshot sp: snapshot.getChildren()) {
                    if ((long)sp.child("date").getValue() + 10 < date && (boolean)sp.child("done").getValue() == true) {
                        TodoItem item = new TodoItem();
                        item.done = (boolean) sp.child("done").getValue();
                        item.itemText = sp.child("itemText").getValue().toString();
                        item.objectId = sp.child("objectId").getValue().toString();
                        item.date = (long)sp.child("date").getValue();
                        int index = todoItemList.indexOf(item);
                        if (index != -1) {
                            todoItemList.remove(index);
                            doneTodoList.add(item);
                            doneAdapter.notifyDataSetChanged();
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        alert.setPositiveButton("Add Todo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TodoItem todoItem = new TodoItem();
                todoItem.itemText = itemEditText.getText().toString();
                todoItem.done = false;
                todoItem.date = 0;
//                String key = mDatabase.child("users/001/todo").push().getKey();
                DatabaseReference key = mDatabase.child("users/" + username + "/todo").push();
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
        Calendar c1 = Calendar.getInstance();
        long date = c1.getTimeInMillis();
        DatabaseReference itemReference = mDatabase.child("users/" + username + "/todo").child(itemObjectId);
        itemReference.child("done").setValue(isDone);
        TodoItem temp = new TodoItem();
        temp.itemText = itemText;
        temp.objectId = itemObjectId;
        if (isDone == true) {
            Toast.makeText(getApplicationContext(), "Completed todo '" + itemText + "'", Toast.LENGTH_SHORT).show();
            itemReference.child("date").setValue(date);
            int index = todoItemList.indexOf(temp);
            if (index != -1) {
                todoItemList.get(index).done = isDone;
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Undo todo '" + itemText + "'", Toast.LENGTH_SHORT).show();
            itemReference.child("date").setValue(0);
            int index = doneTodoList.indexOf(temp);
            if (index != -1) {
                temp.done = isDone;
                doneTodoList.remove(index);
                todoItemList.add(temp);
                doneAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
            } else {
                index = todoItemList.indexOf(temp);
                if (index != -1) {
                    todoItemList.get(index).done = isDone;
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onItemDelete(String itemObjectId, String itemText, boolean isDone) {
        DatabaseReference itemReference = mDatabase.child("users/" + username + "/todo").child(itemObjectId);
        itemReference.removeValue();
        TodoItem temp = new TodoItem();
        temp.itemText = itemText;
        temp.objectId = itemObjectId;
        temp.done = isDone;
        int index = todoItemList.indexOf(temp);
        if (index != -1) {
            todoItemList.remove(index);
            adapter.notifyDataSetChanged();
        }
        else {
            index = doneTodoList.indexOf(temp);
            if (index != -1) {
                doneTodoList.remove(index);
                doneAdapter.notifyDataSetChanged();
            }
        }
        Toast.makeText(getApplicationContext(), "Deleted todo '" + itemText + "'", Toast.LENGTH_SHORT).show();
    }

    public void goToDoneTodos(View view) {
        Button doneButton = (Button) findViewById(R.id.doneTodos);
        Button todoListButton = (Button) findViewById(R.id.todoList);
        ListView doneList = (ListView) findViewById(R.id.done_items_list);
        ListView todoList = (ListView) findViewById(R.id.items_list);
        TextView todoListHeader = (TextView) findViewById(R.id.todoListHeader);
        TextView doneTodoHeader = (TextView) findViewById(R.id.doneTodoHeader);

        doneButton.setVisibility(View.GONE);
        doneList.setVisibility(View.VISIBLE);
        todoListButton.setVisibility(View.VISIBLE);
        todoList.setVisibility(View.GONE);
        todoListHeader.setVisibility(View.GONE);
        doneTodoHeader.setVisibility(View.VISIBLE);
    }

    public void goToList(View view) {
        Button doneButton = (Button) findViewById(R.id.doneTodos);
        Button todoListButton = (Button) findViewById(R.id.todoList);
        ListView doneList = (ListView) findViewById(R.id.done_items_list);
        ListView todoList = (ListView) findViewById(R.id.items_list);
        TextView todoListHeader = (TextView) findViewById(R.id.todoListHeader);
        TextView doneTodoHeader = (TextView) findViewById(R.id.doneTodoHeader);

        doneButton.setVisibility(View.VISIBLE);
        doneList.setVisibility(View.GONE);
        todoListButton.setVisibility(View.GONE);
        todoList.setVisibility(View.VISIBLE);
        todoListHeader.setVisibility(View.VISIBLE);
        doneTodoHeader.setVisibility(View.GONE);
    }
}