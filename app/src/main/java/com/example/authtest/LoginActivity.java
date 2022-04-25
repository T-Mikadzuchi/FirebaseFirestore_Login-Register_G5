package com.example.authtest;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Log.d(TAG, "onAuthStateChanged:signed_in:" + auth.getCurrentUser().getUid());
            startActivity(new Intent(LoginActivity.this, ExitActivity.class));
            finish();
            return;
        }

        EditText edtemail = findViewById(R.id.email_);
        EditText edtpass = findViewById(R.id.password_);

        Button login = findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtemail.getText().toString().trim();
                String pass = edtpass.getText().toString().trim();
                ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setTitle("Loading...");
                pd.show();
                if (email.isEmpty()) {
                    edtemail.setError("Email is required!");
                    edtemail.requestFocus();
                    pd.dismiss();
                    return;
                }
                if (pass.isEmpty()) {
                    edtpass.setError("Password is required!");
                    edtpass.requestFocus();
                    pd.dismiss();
                    return;
                }
                auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    pd.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, ExitActivity.class));
                                } else {
                                    pd.dismiss();
                                    Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}