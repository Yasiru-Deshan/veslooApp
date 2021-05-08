package com.example.vesloo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class CustomerHome extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    Button logoutBtn, viewProductsBtn, viewMyFeedback, viewAds, viewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        logoutBtn = findViewById(R.id.logout);
        viewProductsBtn = findViewById(R.id.go_to_add_product_btn);
        viewMyFeedback = findViewById(R.id.view_my_feedback);
        viewMyFeedback.setOnClickListener(this);
        viewProductsBtn.setOnClickListener(this);
        viewAds = findViewById(R.id.goto_ad_btn);
        viewAds.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        viewProfile = findViewById(R.id.my_profile_btn);
        viewProfile.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
       if( mAuth.getCurrentUser() == null){
           startActivity(new Intent(this, AuthSelection.class));
       }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.logout){
            mAuth.signOut();
            startActivity(new Intent(this, AuthSelection.class));
        }else if(id == R.id.go_to_add_product_btn){
            startActivity(new Intent(this, ViewProducts.class));
        }else if(R.id.view_my_feedback == id){
            startActivity(new Intent(this, MyFeedbacks.class));
        }else if(id == R.id.goto_ad_btn){
            startActivity(new Intent(this, ViewAdvertisements.class));
        }else if(id == R.id.my_profile_btn){
            startActivity(new Intent(this, MyProfile.class));
        }
    }
}