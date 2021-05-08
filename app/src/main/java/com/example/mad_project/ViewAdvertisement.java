package com.example.vesloo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ViewAdvertisement extends BaseActivity implements View.OnClickListener {

    FirebaseFirestore db;
    String id;
    String prdId;
    ImageView imageView;
    TextView titleTV, descTV, discountTV;
    Button goBtn;
    ImageButton editBtn, dltBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_advertisement);
        id = getIntent().getStringExtra("id");
        db = FirebaseFirestore.getInstance();
        imageView = findViewById(R.id.ad_image_view);
        titleTV = findViewById(R.id.ad_title);
        descTV =  findViewById(R.id.ad_desc);
        discountTV =  findViewById(R.id.ad_discount);

        goBtn=  findViewById(R.id.goto_prd_btn);
        editBtn = findViewById(R.id.ad_update_btn);
        dltBtn =  findViewById(R.id.ad_delete_btn);

        goBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);
        dltBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgressDialog("loading ad");
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getString("role").equals("admin")){
                        editBtn.setVisibility(View.VISIBLE);
                        dltBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        db.collection("ads").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc != null){
                        Picasso.get().load(doc.getString("image")).into(imageView);
                        titleTV.setText(doc.getString("title"));
                        prdId = doc.getString("productId");
                        descTV.setText(doc.getString("description"));
                        discountTV.setText(doc.get("discount").toString());

                    }else{
                        Toast.makeText(ViewAdvertisement.this, "cannot find the advertisement",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ViewAdvertisement.this, "cannot find the advertisement",Toast.LENGTH_LONG).show();
                }
                hideProgressDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int iid = view.getId();
        if(iid == R.id.goto_prd_btn){
            Intent prd = new Intent(this, ViewProduct.class);
            prd.putExtra("id",prdId);
            startActivity(prd);
        }else if(iid == R.id.ad_update_btn){
            Intent prd = new Intent(this, UpdateAdvertisement.class);
            prd.putExtra("id", id);
            startActivity(prd);
        }else if(iid == R.id.ad_delete_btn){
            deleteAd();
        }
    }

    private void deleteAd() {
        new AlertDialog.Builder(this).setTitle("Delete advertisement")
                .setMessage("Are you sure you need to delete this advertisement?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.collection("ads").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ViewAdvertisement.this, "Advertisement deleted", Toast.LENGTH_LONG).show();
                            ViewAdvertisement.this.finish();
                        }else{
                            Toast.makeText(ViewAdvertisement.this, "Cannot Delete the Advertisement, Try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_menu_delete).show();

    }
}