package com.nik.todoext;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        if (pref.contains("USERNAME") && pref.contains("PASSWORD")) {
            Intent intent = new Intent(this, MainActivity.class);
            String username = pref.getString("USERNAME", null);
            String password = pref.getString("PASSWORD", null);
            intent.putExtra("USERNAME", username);
            intent.putExtra("PASSWORD", password);
            Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }
    }

    public void login(View view) {
        EditText usernameInput = (EditText) findViewById(R.id.username);
        EditText passwordInput = (EditText) findViewById(R.id.password);
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
//        Log.d("USERNAME", username);
//        Log.d("PASSWORD", password);

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        pref.edit().putString("USERNAME", username).apply();
        pref.edit().putString("PASSWORD", password).apply();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("PASSWORD", password);
        Toast.makeText(getApplicationContext(), "Signed Up", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        finish();
    }
}