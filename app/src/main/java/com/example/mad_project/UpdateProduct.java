package com.example.vesloo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProduct extends BaseActivity implements View.OnClickListener {
    EditText codeET, nameET, descET, qtyET, priceET, supplierET;
    ImageButton imgBtn;
    Button addBtn, closeBtn;
    private Uri filePath;
    private static final int PICK_IMAGE_REQUEST = 234;
    String productId;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        codeET = findViewById(R.id.etCode);
        nameET = findViewById(R.id.etProductName);
        descET = findViewById(R.id.etDescription);
        qtyET = findViewById(R.id.etQty);
        priceET = findViewById(R.id.etPrice);
        supplierET = findViewById(R.id.etSupplier);

        imgBtn = findViewById(R.id.img_btn);
        addBtn = findViewById(R.id.add_product_btn);
        closeBtn = findViewById(R.id.btn_cancel);
        productId = getIntent().getStringExtra("id");
        addBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        imgBtn.setOnClickListener(this);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference ref = db.collection("products").document(productId);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snap = task.getResult();
                if(snap!=null){
                    codeET.setText(snap.getString("code"));
                    nameET.setText(snap.getString("name"));
                    descET.setText(snap.getString("description"));
                    qtyET.setText(snap.get("qty").toString());
                    priceET.setText(snap.get("price").toString());
                    supplierET.setText(snap.getString("supplier"));

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgBtn.setImageBitmap(bitmap);
                System.out.println(filePath);
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

    private boolean validateInput(String name,
                                  String code,
                                  double price,
                                  int qty,
                                  String supplier,
                                  String desc) {
        boolean val = true;

        if (name.isEmpty()) {
            nameET.setError("name cannot be empty");
            val = false;
        } else if (name.length() < 4) {
            nameET.setError("name must contain more than 4 characters");
            val = false;
        } else {
            nameET.setError(null);
        }

        if (code.isEmpty()) {
            codeET.setError("code cannot be empty");
            val = false;
        } else if (code.length() < 4) {
            codeET.setError("code must contain more than 4 characters");
            val = false;
        } else {
            codeET.setError(null);
        }

        if (supplier.isEmpty()) {
            supplierET.setError("supplier cannot be empty");
            val = false;
        } else if (supplier.length() < 4) {
            supplierET.setError("supplier must contain more than 4 characters");
            val = false;
        } else {
            supplierET.setError(null);

        }
        if (desc.isEmpty()) {
            descET.setError("desc cannot be empty");
            val = false;
        } else if (desc.length() < 20) {
            descET.setError("desc must contain more than 20 characters");
            val = false;
        } else {
            descET.setError(null);
        }

        if (qty <= 0) {
            qtyET.setError("Quantity must be larger than 0");
            val = false;
        } else {
            qtyET.setError(null);
        }
        if (price <= 0) {
            priceET.setError("price must be larger than 0");
            val = false;
        } else {
            priceET.setError(null);
        }
        if(productId == null){
            Toast.makeText(this, "No product selected", Toast.LENGTH_LONG).show();
            this.finish();
            val = false;
        }
        if (filePath == null) {
            Toast.makeText(this, "No new image selected", Toast.LENGTH_LONG).show();

        }

        return val;
    }

    private void addProduct() {
        final String name = nameET.getText().toString();
        final String code = codeET.getText().toString();
        final double price = Double.parseDouble(priceET.getText().toString());
        final int qty = Integer.parseInt(qtyET.getText().toString());
        final String supplier = supplierET.getText().toString();
        final String desc = descET.getText().toString();
        if (validateInput(name, code, price, qty, supplier, desc)) {
            showProgressDialog("Updating product");
            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference ref = storage.getReference().child("products/" + System.currentTimeMillis());
            if (filePath != null) {
                ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map<String, Object> product = new HashMap<>();
                                product.put("name", name);
                                product.put("code", code);
                                product.put("price", price);
                                product.put("qty", qty);
                                product.put("supplier", supplier);
                                product.put("description", desc);
                                product.put("image", uri.toString());
                                db.collection("products").document(productId).update(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(UpdateProduct.this, "Product Updated successfully", Toast.LENGTH_LONG).show();
                                        UpdateProduct.this.finish();
                                    }
                                });

                            }
                        });
                        hideProgressDialog();
                    }
                });
            }else{
                Map<String, Object> product = new HashMap<>();
                product.put("name", name);
                product.put("code", code);
                product.put("price", price);
                product.put("qty", qty);
                product.put("supplier", supplier);
                product.put("description", desc);
                db.collection("products").document(productId).update(product).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateProduct.this, "Product Updated successfully", Toast.LENGTH_LONG).show();
                        hideProgressDialog();
                        UpdateProduct.this.finish();
                    }
                });

            }
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_cancel) {
            this.finish();
        } else if (id == R.id.add_product_btn) {
            addProduct();

        } else if (id == R.id.img_btn) {
            showFileChooser();
        }
    }
}