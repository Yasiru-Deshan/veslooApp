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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AdminRegister extends BaseActivity implements View.OnClickListener {
    Button registerBtn, gotoLoginBtn;
    EditText emailET, passwordEt, confirmPassET;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        registerBtn = findViewById(R.id.btn_register_admin);
        gotoLoginBtn = findViewById(R.id.btn_admin_acc);

        emailET = findViewById(R.id.registerEmailAdmin);
        passwordEt = findViewById(R.id.registerPasswordAdmin);
        confirmPassET = findViewById(R.id.confirmPasswordAdmin);

        gotoLoginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }
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
        String confirmPass = confirmPassET.getText().toString();

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
        }else if(!password.equals(confirmPass)){
            confirmPassET.setError("Password confirmation must match");
        }else{
            passwordEt.setError(null);
            confirmPassET.setError(null);
        }
        return valid;
    }


    private void createAdminAccount(){
        if(validateForm()){
            String email = emailET.getText().toString();
            String password = passwordEt.getText().toString();

            showProgressDialog(getString(R.string.creating_admin));

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                        FirebaseUser admin = mAuth.getCurrentUser();
                        Map<String,Object> userDetails = new HashMap<>();
                        userDetails.put("role", "admin");

                        if(admin != null){
                            db.collection("users").document(admin.getUid()).set(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AdminRegister.this, "Admin created Successfully", Toast.LENGTH_LONG).show();
                                    hideProgressDialog();

                                    Intent mainIntent = new Intent(AdminRegister.this, MainActivity.class);
                                    startActivity(mainIntent);
                                }
                            });
                        }
                    hideProgressDialog();
                }
            });
        }

    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btn_admin_acc){
            Intent loginIntern = new Intent(this, AdminLogin.class);
            startActivity(loginIntern);
        }else if(id == R.id.btn_register_admin){
            System.out.println("Registering account");
            createAdminAccount();
        }
    }
}