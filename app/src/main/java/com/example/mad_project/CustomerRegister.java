package com.example.vesloo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

public class CustomerRegister extends BaseActivity implements View.OnClickListener {

    Button registerBtn, gotoLoginBtn;
    EditText fullNameET, addressET, phoneET, emailET, passwordET, confirmPassET;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fullNameET = findViewById(R.id.registerUserFullName);
        addressET = findViewById(R.id.registerUserAddress);
        phoneET = findViewById(R.id.registerUserPhone);
        passwordET = findViewById(R.id.registerPasswordcustomer);
        confirmPassET = findViewById(R.id.confirmPasswordcustomer);
        emailET = findViewById(R.id.registerEmailcustomer);


        registerBtn = findViewById(R.id.btn_register_customer);
        gotoLoginBtn = findViewById(R.id.btn_cus_goto_login);

        gotoLoginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }



    private boolean validateForm(){
        boolean valid = true;
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String fullname = fullNameET.getText().toString();
        String phone = phoneET.getText().toString();
        String passwordConfirmation = confirmPassET.getText().toString();
        String address = addressET.getText().toString();
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
            passwordET.setError("Password must be 8 - 24 characters long and have capital letter, simple letter and a numeric");
            valid = false;
        }else if(!password.equals(passwordConfirmation)){
            confirmPassET.setError("Password confirmation must match");
            valid = false;
        }else{
            confirmPassET.setError(null);
            passwordET.setError(null);
        }

        if(TextUtils.isEmpty(fullname)){
            fullNameET.setError("Full name cannot be empty");
            valid = false;
        }else if(fullname.length() < 6){
            fullNameET.setError("Full name must contain more than 6 characters");
            valid = false;
        }else{
            fullNameET.setError(null);
        }

        if(TextUtils.isEmpty(phone)){
            phoneET.setError("Phone number cannot be empty");
            valid = false;
        }else if(phone.length() < 6){
            phoneET.setError("Phone number must contain more than 10 characters");
            valid = false;
        }else{
            phoneET.setError(null);
        }

        if(TextUtils.isEmpty(address)){
            addressET.setError("Address cannot be empty");
            valid = false;
        }else if(address.length() < 6){
            addressET.setError("Address must contain more than 10 characters");
            valid = false;
        }else{
            addressET.setError(null);
        }
        return valid;
    }
    public static boolean isValidPassword(String password) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "[a-zA-Z0-9\\!\\@\\#\\$]{8,24}");

        return !TextUtils.isEmpty(password) && PASSWORD_PATTERN.matcher(password).matches();
    }
    private void registerCustomer() {
        if(validateForm()){
            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();
            final String fullname = fullNameET.getText().toString();
            final String phone = phoneET.getText().toString();
            final String address = addressET.getText().toString();
            showProgressDialog(getString(R.string.creating_cus_acc));
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("fullname", fullname);
                        userData.put("address", address);
                        userData.put("phone", phone);
                        userData.put("role","user");

                        if(user != null){
                            db.collection("users").document(user.getUid()).set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(CustomerRegister.this, "User created Successfully", Toast.LENGTH_LONG).show();
                                    hideProgressDialog();
                                    Intent mainIntent = new Intent(CustomerRegister.this, MainActivity.class);
                                    startActivity(mainIntent);
                                }
                            });
                        }
                    }else{
                        Log.w("Register", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(CustomerRegister.this, "Cannot register user at the moment", Toast.LENGTH_LONG).show();
                    }
                    hideProgressDialog();
                }
            });
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btn_cus_goto_login){
            startActivity(new Intent(this, CustomerLogin.class));
        }else if(id == R.id.btn_register_customer){
            System.out.println("register cusss");
            registerCustomer();
        }
    }
}