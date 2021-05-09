package com.example.vesloo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfile extends BaseActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    TextView nameView, phoneView, addressView;
    ImageView proPic;
    Uri filepath;
    Button selectImgBtn, cancelBtn, updateBtn;
    private static final int PICK_IMAGE_REQUEST = 234;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        nameView = findViewById(R.id.fullname);
        phoneView = findViewById(R.id.phone);
        addressView = findViewById(R.id.address);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        proPic = findViewById(R.id.proPic);
        selectImgBtn = findViewById(R.id.select_image);
        cancelBtn = findViewById(R.id.cancel_btn);
        updateBtn = findViewById(R.id.btn_update_profile);

        selectImgBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snap = task.getResult();
                    if (snap != null) {
                        nameView.setText(snap.getString("fullname"));
                        phoneView.setText(snap.getString("phone"));
                        addressView.setText(snap.getString("address"));

                        if (snap.getString("image") != null && !snap.getString("image").isEmpty()) {
                            proPic.setVisibility(View.VISIBLE);
                            Picasso.get().load(snap.getString("image")).into(proPic);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                proPic.setImageBitmap(bitmap);
                proPic.setVisibility(View.VISIBLE);
                System.out.println(filepath);
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
        if (id == R.id.select_image) {
            showFileChooser();
        } else if (id == R.id.cancel_btn) {
            this.finish();
        } else if (id == R.id.btn_update_profile) {
            updateProfile();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String fullname = nameView.getText().toString();
        String phone = phoneView.getText().toString();

        String address = addressView.getText().toString();


        if (TextUtils.isEmpty(fullname)) {
            nameView.setError("Full name cannot be empty");
            valid = false;
        } else if (fullname.length() < 6) {
            nameView.setError("Full name must contain more than 6 characters");
            valid = false;
        } else {
            nameView.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            phoneView.setError("Phone number cannot be empty");
            valid = false;
        } else if (phone.length() < 6) {
            phoneView.setError("Phone number must contain more than 10 characters");
            valid = false;
        } else {
            phoneView.setError(null);
        }

        if (TextUtils.isEmpty(address)) {
            addressView.setError("Address cannot be empty");
            valid = false;
        } else if (address.length() < 6) {
            addressView.setError("Address must contain more than 10 characters");
            valid = false;
        } else {
            addressView.setError(null);
        }
        return valid;
    }

    private void updateProfile() {
        if (validateForm()) {
            String fullname = nameView.getText().toString();
            String phone = phoneView.getText().toString();
            String address = addressView.getText().toString();

            showProgressDialog("updating user");
            final Map<String, Object> userData = new HashMap<>();
            userData.put("fullname", fullname);
            userData.put("phone", phone);
            userData.put("address", address);
            if (filepath != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                final StorageReference ref = storage.getReference().child("profilepic/user" + user.getUid());
                ref.putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    userData.put("image", uri.toString());
                                    db.collection("users").document(user.getUid()).update(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(UpdateProfile.this, "user updated", Toast.LENGTH_LONG).show();
                                            hideProgressDialog();
                                            UpdateProfile.this.finish();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            } else {
                db.collection("users").document(user.getUid()).update(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateProfile.this, "user updated", Toast.LENGTH_LONG).show();
                        hideProgressDialog();
                        UpdateProfile.this.finish();
                    }
                });
            }

        } else {
            Toast.makeText(this, "Cannot update the user", Toast.LENGTH_LONG).show();
        }
    }
}