package com.example.vesloo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class CustomerLogin extends BaseActivity implements View.OnClickListener {
    private EditText emailField, passwordField;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        Button gotoReg = findViewById(R.id.new_customer_account);
        Button login = findViewById(R.id.btn_customer_login);
        login.setOnClickListener(this);
        gotoReg.setOnClickListener(this);
        emailField = findViewById(R.id.customerLoginEmail);
        passwordField = findViewById(R.id.customerLoginPassword);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }
    }

    private boolean validateForm(){
        boolean valid = true;
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if(TextUtils.isEmpty(email)){
            emailField.setError("Email is required");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("This must contain a valid email address");
            valid = false;
        }else{
            emailField.setError(null);
        }

        if(!isValidPassword(password)){
            passwordField.setError("Password must be 8 - 24 characters long and have capital letter, simple letter and a numeric");
            valid = false;
        }else{
            passwordField.setError(null);
        }


        return valid;
    }


    public static boolean isValidPassword(String password) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}");

        return !TextUtils.isEmpty(password) && PASSWORD_PATTERN.matcher(password).matches();
    }
    private void signIn(){
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        if(validateForm()){
            showProgressDialog(getString(R.string.login_in));
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null){

                                db.collection("users").document(user.getUid()).get().addOnCompleteListener(CustomerLogin.this, new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        DocumentSnapshot snap = task.getResult();
                                        if(snap.exists()){
                                            String role = snap.getString("role");
                                            if (role != null) {
                                                if(role.equals("user")){
                                                    hideProgressDialog();

                                                    Toast.makeText(CustomerLogin.this, "Logged in",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent mainIntent = new Intent(CustomerLogin.this, MainActivity.class);
                                                    startActivity(mainIntent);
                                                }else{
                                                    mAuth.signOut();
                                                    Toast.makeText(CustomerLogin.this, "Sign in failed, This account is not an user account",
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }else{
                                                mAuth.signOut();
                                                Toast.makeText(CustomerLogin.this, "Sign in failed, This account is not an user account",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                });
                        }
                    }else{
                        Toast.makeText(CustomerLogin.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                    hideProgressDialog();
                }
            });
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.new_customer_account){
            Intent my = new Intent(this,CustomerRegister.class);
            startActivity(my);
        }else if(id == R.id.btn_customer_login){
            signIn();
        }
    }
}