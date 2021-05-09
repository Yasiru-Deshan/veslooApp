package com.example.vesloo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddFeedback extends BaseActivity implements View.OnClickListener{

    TextView prdName;
    String id, name;
    Button add, cancel;
    RatingBar ratingBar;
    EditText commentET;
    FirebaseFirestore db;
    String fullname, userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feedback);
        add = findViewById(R.id.btn_add_feedback);
        cancel = findViewById(R.id.btn_cancel);
        ratingBar = findViewById(R.id.rating);
        commentET = findViewById(R.id.feedback_comment);
        db = FirebaseFirestore.getInstance();
        add.setOnClickListener(this);
        cancel.setOnClickListener(this);
        if(getIntent().getExtras() != null){
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
        }
        prdName = findViewById(R.id.feedback_prd_name_tv);

    }

    @Override
    protected void onStart() {
        super.onStart();
        prdName.setText(name);
        User user = User.getUser();
        userId = user.getId();
        db.collection("users").document(user.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snap = task.getResult();
                    if(snap != null){
                        fullname = snap.getString("fullname");
                    }
                }
            }
        });
    }

    private String getName(){
        User user = User.getUser();
        final String[] mName = new String[1];
        db.collection("users").document(user.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snap = task.getResult();
                    if(snap != null){
                        mName[0] = snap.getString("fullname");
                    }
                }
            }
        });

        return mName[0];
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btn_add_feedback){
            addFeedback();
        }else{
            this.finish();
        }
    }

    private boolean validateFeedback(){
        boolean valid = true;
        int val = Math.round(ratingBar.getRating());
        String comment = commentET.getText().toString();

        if(TextUtils.isEmpty(comment)){
            commentET.setError("Comment cannot be empty");
            valid = false;
        }else if(comment.length() < 10){
            commentET.setError("Comment must contain more than 10 characters");
            valid = false;
        }else{
            commentET.setError(null);
        }

        if(val <= 0){
            Toast.makeText(this, "Please give a rating", Toast.LENGTH_LONG).show();
            valid = false;
        }else if(name == null){
            Toast.makeText(this, "Please select a product", Toast.LENGTH_LONG).show();
            valid = false;
        }else if(id == null){
            Toast.makeText(this, "Please select a product", Toast.LENGTH_LONG).show();
            valid = false;
        }else if(fullname == null || TextUtils.isEmpty(fullname)){
            fullname = getName();
            if(fullname == null || TextUtils.isEmpty(fullname)){
                Toast.makeText(this, "Cannot find the user's name", Toast.LENGTH_LONG).show();
                valid = false;
            }
        }else if(userId == null){
            Toast.makeText(this, "User id cannot be null", Toast.LENGTH_LONG).show();
            valid = false;
        }
        return  valid;
    }
    private void addFeedback() {
        if(validateFeedback()){
            showProgressDialog("Adding feedback");
            int val = Math.round(ratingBar.getRating());
            String comment = commentET.getText().toString();
            Map<String, Object> feedback = new HashMap<>();
            feedback.put("rating", val);
            feedback.put("comment", comment);
            feedback.put("productId", id);
            feedback.put("productName", name);
            feedback.put("userId", userId);
            feedback.put("userName", fullname);

            db.collection("feedbacks").document().set(feedback).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddFeedback.this, "Feedback added successfully", Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                    AddFeedback.this.finish();
//                    Intent mainIntent = new Intent(AddFeedback.this, ViewProduct.class);
//                    mainIntent.putExtra("id", id);
//                    startActivity(mainIntent);
                }
            });

        }else{
            Toast.makeText(this, "Unable to post the feedback", Toast.LENGTH_LONG).show();
        }
    }
}