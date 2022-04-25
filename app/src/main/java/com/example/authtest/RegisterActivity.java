package com.example.authtest;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            Log.d(TAG, "onAuthStateChanged:signed_in:" + auth.getCurrentUser().getUid());
            startActivity(new Intent(RegisterActivity.this, ExitActivity.class));
            finish();
            return;
        }

        EditText name = (EditText) findViewById(R.id.name);
        EditText email = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);
        Button register = (Button) findViewById(R.id.btnRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog pd = new ProgressDialog(RegisterActivity.this);
                pd.setTitle("Registering...");
                pd.show();
                String nametxt = name.getText().toString().trim();
                String emailtxt = email.getText().toString().trim();
                String passtxt = password.getText().toString().trim();

                if (nametxt.isEmpty()) {
                    name.setError("Name is required!");
                    name.requestFocus();
                    pd.dismiss();
                    return;
                }
                if (emailtxt.isEmpty()) {
                    email.setError("Email is required!");
                    email.requestFocus();
                    pd.dismiss();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailtxt).matches()) {
                    email.setError("Please provide valid email!");
                    email.requestFocus();
                    pd.dismiss();
                    return;
                }
                if (passtxt.isEmpty()) {
                    password.setError("Password is required!");
                    password.requestFocus();
                    pd.dismiss();
                    return;
                }
                if (passtxt.length() < 6) {
                    password.setError("Password length must be longer than 6!");
                    password.requestFocus();
                    pd.dismiss();
                    return;
                }
                auth.createUserWithEmailAndPassword(emailtxt, passtxt)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User(nametxt, emailtxt);
                                    db.collection("users").document(auth.getCurrentUser().getUid().toString()).set(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                   if (task.isSuccessful()) {
                                                       pd.dismiss();
                                                       Toast.makeText(RegisterActivity.this, "Register successfully!", Toast.LENGTH_SHORT).show();
                                                       name.setText("");
                                                       email.setText("");
                                                       password.setText("");
                                                       startActivity(new Intent(RegisterActivity.this, ExitActivity.class));
                                                   }
                                                }
                                            });
/*                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                    pd.dismiss();
                                                    Toast.makeText(RegisterActivity.this, "Register successfully!", Toast.LENGTH_SHORT).show();
                                                    name.setText("");
                                                    email.setText("");
                                                    password.setText("");
                                                    startActivity(new Intent(RegisterActivity.this, ExitActivity.class));
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                }
                                            });*/

                            } else {
                                    pd.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Register failed!", Toast.LENGTH_SHORT).show();
                                }
                        }
                });
            }
        });
    }
}