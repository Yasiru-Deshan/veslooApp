package com.example.vesloo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateFeedback extends BaseActivity implements View.OnClickListener {

    TextView prdName;
    String feedbackId;
    Button updateBtn, cancel;
    RatingBar ratingBar;
    EditText commentET;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_feedback);
        updateBtn = findViewById(R.id.btn_update_feedback);
        cancel = findViewById(R.id.btn_cancel);
        ratingBar = findViewById(R.id.rating);
        commentET = findViewById(R.id.feedback_comment);
        db = FirebaseFirestore.getInstance();
        updateBtn.setOnClickListener(this);
        cancel.setOnClickListener(this);
        if(getIntent().getExtras() != null){
            feedbackId = getIntent().getStringExtra("id");
        }
        prdName = findViewById(R.id.feedback_prd_name_tv);
    }

    @Override
    protected void onStart() {
        super.onStart();

        showProgressDialog("loading feedback");
        db.collection("feedbacks").document(feedbackId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot snap = task.getResult();
                    if(snap != null){
                        ratingBar.setRating(Integer.parseInt(snap.get("rating").toString()));
                        commentET.setText(snap.getString("comment"));
                        prdName.setText(snap.getString("productName"));
                    }
                }
                hideProgressDialog();
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btn_update_feedback){
            updateFeedback();
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
        }else if(feedbackId == null){
            Toast.makeText(this, "Please select a feedback to update", Toast.LENGTH_LONG).show();
            valid = false;
        }

        return  valid;
    }

    private void updateFeedback() {
        if(validateFeedback()){
            showProgressDialog("Adding feedback");
            int val = Math.round(ratingBar.getRating());
            String comment = commentET.getText().toString();
            Map<String, Object> feedback = new HashMap<>();
            feedback.put("rating", val);
            feedback.put("comment", comment);

            db.collection("feedbacks").document(feedbackId).update(feedback).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(UpdateFeedback.this, "Feedback updated successfully", Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                    UpdateFeedback.this.finish();

                }
            });
        }else{
            Toast.makeText(this, "Unable to update the feedback", Toast.LENGTH_LONG).show();
        }
    }
}