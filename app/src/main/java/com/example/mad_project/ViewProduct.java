package com.example.vesloo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewProduct extends AppCompatActivity implements View.OnClickListener{

    TextView prdName,prdDetails,price, supplier, qty;
    ImageView imgView;
    FirebaseFirestore db;
    String id;
    Product product;
    Button gotoFeedbackBtn, addAdBtn, viewFeedbackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        prdName = findViewById(R.id.view_product_name);
        prdDetails = findViewById(R.id.view_product_details);
        price = findViewById(R.id.view_product_price);
        supplier = findViewById(R.id.prd_supplier_tv);
        qty = findViewById(R.id.prd_qty_tv);
        imgView = findViewById(R.id.product_image);

        addAdBtn = findViewById(R.id.add_ad_btn);
        addAdBtn.setOnClickListener(this);
        gotoFeedbackBtn = findViewById(R.id.goto_feedback);
        viewFeedbackBtn = findViewById(R.id.view_item_feedback);
        viewFeedbackBtn.setOnClickListener(this);
        gotoFeedbackBtn.setOnClickListener(this);

        id = getIntent().getStringExtra("id");
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference ref = db.collection("products").document(id);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snap = task.getResult();
                if (snap != null && snap.exists()) {
                    product = new Product(id, snap.getString("name"), snap.getString("description"), snap.getDouble("price"), snap.getString("image"), Integer.parseInt(snap.get("qty").toString()),snap.getString("code"), snap.getString("supplier"));
                    prdName.setText(product.getName());
                    prdDetails.setText(product.getDescription());
                    Picasso.get().load(product.getImage()).into(imgView);
                    supplier.setText(product.getSupplier());
                    qty.setText(String.valueOf(product.getQty()));
                    price.setText(product.getPrice()+"");
                    DocumentReference reff = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reff.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot snap = task.getResult();
                            if(snap != null){
                                if(snap.getString("role").equals("admin")){
                                    gotoFeedbackBtn.setVisibility(View.GONE);
                                    addAdBtn.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
                }else{
                    Toast.makeText(ViewProduct.this, "cannot find the product", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.goto_feedback){
           Intent newI  =new Intent(this,AddFeedback.class);
           newI.putExtra("id", product.getId());
           newI.putExtra("name", product.getName());
           startActivity(newI);
        }else if(id == R.id.add_ad_btn){
            Intent newI  =new Intent(this, AddAdvertisement.class);
            newI.putExtra("id", product.getId());
            newI.putExtra("name", product.getName());
            startActivity(newI);
        }else if(id == R.id.view_item_feedback){
            Intent newI  =new Intent(this, ViewProductFeedback.class);
            newI.putExtra("id", product.getId());
            newI.putExtra("name", product.getName());
            startActivity(newI);
        }
    }
}