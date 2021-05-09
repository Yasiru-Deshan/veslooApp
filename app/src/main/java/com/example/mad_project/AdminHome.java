package com.example.vesloo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminHome extends AppCompatActivity implements View.OnClickListener{

    Button logoutBtn, addProductBtn,viewProduct, viewFeedback, viewAds;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        logoutBtn = findViewById(R.id.admin_logout_btn);
        addProductBtn = findViewById(R.id.go_to_add_product_btn);
        viewProduct = findViewById(R.id.go_to_view_product_btn);
        viewProduct.setOnClickListener(this);
        viewFeedback = findViewById(R.id.view_feedback_btn);
        viewFeedback.setOnClickListener(this);
        addProductBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        viewAds = findViewById(R.id.goto_ad_btn);
        viewAds.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.admin_logout_btn){
            mAuth.signOut();
            startActivity(new Intent(this, AuthSelection.class));
        }else if(id == R.id.go_to_add_product_btn){
            startActivity(new Intent(this, AddProduct.class));
        }else if(id == R.id.go_to_view_product_btn){
            startActivity(new Intent(this, ViewProducts.class));
        }else if(id == R.id.view_feedback_btn){
            startActivity(new Intent(this, ViewFeedbacks.class));
        }else if(id == R.id.goto_ad_btn){
            startActivity(new Intent(this, ViewAdvertisements.class));
        }
    }
}