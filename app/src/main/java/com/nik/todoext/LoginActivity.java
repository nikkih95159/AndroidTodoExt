package com.nik.todoext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    Query userQuery = mDatabase.child("users");
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
        EditText usernameInput = (EditText) findViewById(R.id.usernameLogin);
        EditText passwordInput = (EditText) findViewById(R.id.passwordLogin);
        final String username = usernameInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (username.equals("") || password.equals("")) {
            if (username.equals(""))
                Toast.makeText(getApplicationContext(), "Username is empty.", Toast.LENGTH_SHORT).show();
            if (password.equals(""))
                Toast.makeText(getApplicationContext(), "Password is empty.", Toast.LENGTH_SHORT).show();
        }
        else {
            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                boolean userExists = false, passwordExists = false;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String userFB = postSnapshot.getKey();
                        String pswdFB = postSnapshot.child("password").getValue().toString();
                        if (username.equals(userFB) && password.equals(pswdFB)) {
                            userExists = true;
                            passwordExists = true;
                        } else if (username.equals(userFB) && !password.equals(pswdFB)) {
                            userExists = true;
                            passwordExists = false;
                        }
                    }
                    if (userExists && passwordExists) {
                        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
                        pref.edit().putString("USERNAME", username).apply();
                        pref.edit().putString("PASSWORD", password).apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("USERNAME", username);
                        intent.putExtra("PASSWORD", password);
                        Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    } else if (!passwordExists && userExists) {
                        Toast.makeText(getApplicationContext(), "Incorrect password.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Username does not exist.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Getting Post failed, log a message
                    Log.w("loadPost:onCancelled", error.toException());
                }
            });
        }
    }

    public void signup(View view) {
        EditText usernameInput = (EditText) findViewById(R.id.usernameSignup);
        EditText passwordInput = (EditText) findViewById(R.id.passwordSignup);
        EditText passwordVerify = (EditText) findViewById(R.id.passwordVerify);
        final String username = usernameInput.getText().toString();
        final String password = passwordInput.getText().toString();
        final String pswdVerify = passwordVerify.getText().toString();

        if (username.equals("") || password.equals("") || pswdVerify.equals("")) {
            if (username.equals(""))
                Toast.makeText(getApplicationContext(), "Username is empty.", Toast.LENGTH_SHORT).show();
            if (password.equals("") || pswdVerify.equals(""))
                Toast.makeText(getApplicationContext(), "Password is empty.", Toast.LENGTH_SHORT).show();
        } else if (!pswdVerify.equals(password)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
        }
        else {
            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                boolean userExists = false;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String userFB = postSnapshot.getKey();
                        if (username.equals(userFB)) {
                            userExists = true;
                            break;
                        }
                    }
                    if (userExists) {
                        Toast.makeText(getApplicationContext(), "Username already exists.", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
                        pref.edit().putString("USERNAME", username).apply();
                        pref.edit().putString("PASSWORD", password).apply();

                        mDatabase.child("users").child(username).child("password").setValue(password);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("USERNAME", username);
                        intent.putExtra("PASSWORD", password);
                        Toast.makeText(getApplicationContext(), "Successfully signed up.", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Getting Post failed, log a message
                    Log.w("loadPost:onCancelled", error.toException());
                }
            });
        }
    }

    public void toggleSignup(View view) {
        TextView loginHeader = (TextView) findViewById(R.id.loginHeader);
        TextView signupHeader = (TextView) findViewById(R.id.signupHeader);
        Button toggleSignup = (Button) findViewById(R.id.toggleSignup);
        Button toggleLogin = (Button) findViewById(R.id.toggleLogin);
        Button loginButton = (Button) findViewById(R.id.login_button);
        Button signupButton = (Button) findViewById(R.id.signup_button);
        EditText usernameLogin = (EditText) findViewById(R.id.usernameLogin);
        EditText passwordLogin = (EditText) findViewById(R.id.passwordLogin);
        EditText usernameSignup = (EditText) findViewById(R.id.usernameSignup);
        EditText passwordSignup = (EditText) findViewById(R.id.passwordSignup);
        EditText passwordVerify = (EditText) findViewById(R.id.passwordVerify);

        loginHeader.setVisibility(View.GONE);
        loginButton.setVisibility(View.GONE);
        usernameLogin.setVisibility(View.GONE);
        passwordLogin.setVisibility(View.GONE);

        signupHeader.setVisibility(View.VISIBLE);
        signupButton.setVisibility(View.VISIBLE);
        usernameSignup.setVisibility(View.VISIBLE);
        passwordSignup.setVisibility(View.VISIBLE);
        passwordVerify.setVisibility(View.VISIBLE);

        toggleSignup.setBackgroundColor(Color.rgb(169,169,169));
        toggleLogin.setBackgroundResource(android.R.drawable.btn_default);
    }

    public void toggleLogin(View view) {
        TextView loginHeader = (TextView) findViewById(R.id.loginHeader);
        TextView signupHeader = (TextView) findViewById((R.id.signupHeader));
        Button toggleSignup = (Button) findViewById((R.id.toggleSignup));
        Button toggleLogin = (Button) findViewById(R.id.toggleLogin);
        Button loginButton = (Button) findViewById(R.id.login_button);
        Button signupButton = (Button) findViewById(R.id.signup_button);
        EditText usernameLogin = (EditText) findViewById(R.id.usernameLogin);
        EditText passwordLogin = (EditText) findViewById(R.id.passwordLogin);
        EditText usernameSignup = (EditText) findViewById(R.id.usernameSignup);
        EditText passwordSignup = (EditText) findViewById(R.id.passwordSignup);
        EditText passwordVerify = (EditText) findViewById(R.id.passwordVerify);

        loginHeader.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        usernameLogin.setVisibility(View.VISIBLE);
        passwordLogin.setVisibility(View.VISIBLE);

        signupHeader.setVisibility(View.GONE);
        signupButton.setVisibility(View.GONE);
        usernameSignup.setVisibility(View.GONE);
        passwordSignup.setVisibility(View.GONE);
        passwordVerify.setVisibility(View.GONE);

        toggleLogin.setBackgroundColor(Color.rgb(169,169,169));
        toggleSignup.setBackgroundResource(android.R.drawable.btn_default);
    }
}