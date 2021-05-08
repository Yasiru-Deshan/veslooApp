package com.example.vesloo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddAdvertisement extends BaseActivity implements View.OnClickListener {

    EditText titleET, commentET, discountET;
    String prodId, prodName;
    Uri image;
    ImageView prdImage;
    Button btnImage, btnAdd, btnCancel;
    TextView setProdNameTV;
    private static final int PICK_IMAGE_REQUEST = 234;
    FirebaseFirestore db;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addadvertisement);
        prodId = getIntent().getStringExtra("id");
        prodName = getIntent().getStringExtra("name");

        setProdNameTV = findViewById(R.id.prd_name);
        titleET = findViewById(R.id.ad_title_et);
        commentET = findViewById(R.id.ad_desc_et);
        discountET = findViewById(R.id.ad_discount_et);
        prdImage = findViewById(R.id.ad_image);

        btnAdd = findViewById(R.id.btn_add_ad);
        btnImage =findViewById(R.id.btn_img_select);
        btnCancel = findViewById(R.id.cancel_ad);

        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnImage.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setProdNameTV.setText(prodName);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            image = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                prdImage.setImageBitmap(bitmap);
                prdImage.setVisibility(View.VISIBLE);
                System.out.println(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btn_img_select){
            showFileChooser();
        }else if(id == R.id.cancel_ad){
            this.finish();
        }else if(id == R.id.btn_add_ad){
            addAdvertisement();
        }
    }

    private boolean validateAd(String desc, String title, double discount){
        boolean valid = true;

        if(prodName == null){
            Toast.makeText(this, "Can't find the product name, Go back and select a product", Toast.LENGTH_LONG).show();
            valid = false;
        }
        if(prodId == null){
            Toast.makeText(this, "Can't find the product Id, Go back and select a product", Toast.LENGTH_LONG).show();
            valid = false;
        }

        if(desc.isEmpty()){
            commentET.setError("Description cannot be empty");
            valid = false;
        }else if(desc.length() < 10){
            commentET.setError("Description cannot be less than 10 characters");
            valid = false;
        }else{
            commentET.setError(null);
        }

        if(title.isEmpty()){
            titleET.setError("title cannot be empty");
            valid = false;
        }else if(title.length() < 6){
            titleET.setError("title cannot be less than 6 characters");
            valid = false;
        }else{
            titleET.setError(null);
        }
        if(discount <=0){
            discountET.setError("Discount must be larger than 0%");
            valid = false;
        }
        if (image == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_LONG).show();
            valid = false;
        }


        return valid;
    }

    private void addAdvertisement() {
        final String title = titleET.getText().toString();
        final String desc = commentET.getText().toString();
        final double discount = !discountET.getText().toString().isEmpty() ? Double.parseDouble(discountET.getText().toString()) : 0.0;


        if(validateAd(desc,title,discount)){
            showProgressDialog("Creating advertisement");
            final StorageReference stRef = storage.getReference().child("advertisements/ad_"+System.currentTimeMillis());

            stRef.putFile(image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    stRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map<String, Object> ad = new HashMap<>();
                            ad.put("productId", prodId);
                            ad.put("productName", prodName);
                            ad.put("title", title);
                            ad.put("description", desc);
                            ad.put("discount", discount);
                            ad.put("image", uri.toString());

                            db.collection("ads").document().set(ad).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddAdvertisement.this, "Advertisement added successfully", Toast.LENGTH_LONG).show();
                                    hideProgressDialog();
                                    AddAdvertisement.this.finish();
                                }
                            });
                        }
                    });
                    hideProgressDialog();
                }
            });
        }else{
            Toast.makeText(this, "Something went wrong, cannot create advertisement", Toast.LENGTH_LONG).show();

        }
    }
}