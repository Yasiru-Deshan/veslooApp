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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class AdminLogin extends BaseActivity implements View.OnClickListener {
    Button loginBtn, newAccBtn;
    EditText emailET, passwordEt;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        loginBtn = findViewById(R.id.btn_admin_login);
        newAccBtn = findViewById(R.id.new_admin_account);
        emailET = findViewById(R.id.adminLoginEmail);
        passwordEt = findViewById(R.id.adminLoginPassword);
        newAccBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    public static boolean isValidPassword(String password) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}");

        return !TextUtils.isEmpty(password) && PASSWORD_PATTERN.matcher(password).matches();
    }

    private boolean validateForm(){
        boolean valid = true;
        String email = emailET.getText().toString();
        String password = passwordEt.getText().toString();


        if(TextUtils.isEmpty(email)){
            emailET.setError("Email is required");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailET.setError("This must contain a valid email address");
            valid = false;
        }else{
            emailET.setError(null);
        }

        if(!isValidPassword(password)){
            passwordEt.setError("Password must be 8 - 24 characters long and have capital letter, simple letter and a numeric");
            valid = false;
        }else{
            passwordEt.setError(null);

        }
        return valid;
    }


    private void loginAdmin(){
        if(validateForm()){
            String email = emailET.getText().toString();
            String password = passwordEt.getText().toString();
            showProgressDialog(getString(R.string.login_in_as_admin));
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();

                        if(user != null){
                            db.collection("users").document(user.getUid()).get().addOnCompleteListener(AdminLogin.this, new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot snap = task.getResult();
                                    if(snap.exists()){
                                        String role = snap.getString("role");
                                        if (role != null) {
                                            if(role.equals("admin")){

                                                Intent mainIntent = new Intent(AdminLogin.this, MainActivity.class);
                                                startActivity(mainIntent);

                                            }else{
                                                mAuth.signOut();
                                                Toast.makeText(AdminLogin.this, "Sign in failed, This account is not an admin account",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }else{
                                            mAuth.signOut();
                                            Toast.makeText(AdminLogin.this, "Sign in failed, This account is not an admin account",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }

                                }
                            });
                        }
                    }else{
                        Toast.makeText(AdminLogin.this, "Sign in failed",
                                Toast.LENGTH_LONG).show();
                    }
                    hideProgressDialog();
                }
            });
        }

    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btn_admin_login){
            loginAdmin();
        }else if(id == R.id.new_admin_account){
            Intent reg = new Intent(this, AdminRegister.class);
            startActivity(reg);
        }
    }
}