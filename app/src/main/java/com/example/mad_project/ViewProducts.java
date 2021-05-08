package com.example.vesloo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewProducts extends BaseActivity implements View.OnClickListener {
    private RecyclerView productRV;
    private RecyclerView.Adapter pAdapter;
    private RecyclerView.LayoutManager pLayoutManager;
    ArrayList<Product> products = new ArrayList<>();
    private FirebaseFirestore db;
    private Button searchBtn;
    EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_products);
        db = FirebaseFirestore.getInstance();
        productRV = findViewById(R.id.product_recyclerView);
        productRV.setHasFixedSize(true);
        pLayoutManager = new LinearLayoutManager(this);
        searchBtn = findViewById(R.id.product_search_btn);
        searchBtn.setOnClickListener(this);
        searchText = findViewById(R.id.searchText);


    }


    @Override
    protected void onStart() {
        super.onStart();
        loadProducts();


    }

    private void loadProducts() {
        products.clear();
        productRV.setVisibility(View.VISIBLE);
        showProgressDialog("loading products");
        db.collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product temPd = new Product(document.getId(), document.getString("name"), document.getString("description"), document.getDouble("price"), document.getString("image"));
                            products.add(temPd);


                        }
                        pAdapter = new ProductAdapter(products);
                        productRV.setLayoutManager(pLayoutManager);
                        productRV.setAdapter(pAdapter);
                    } else {
                        Toast.makeText(ViewProducts.this, "No products yet", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(ViewProducts.this, "Caannot load products, check your internet", Toast.LENGTH_LONG).show();
                }
                hideProgressDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.product_search_btn) {
            searchProduct();
        }
    }

    private void searchProduct() {
        View view = this.getCurrentFocus();
        this.searchText.clearFocus();
        productRV.setVisibility(View.VISIBLE);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        final String searchText = this.searchText.getText().toString();
        if (searchText.isEmpty()) {
            loadProducts();
        } else {
            products.clear();
            showProgressDialog("loading products");
            db.collection("products").orderBy("name").startAt(searchText).endAt(searchText + "\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() > 0) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product temPd = new Product(document.getId(), document.getString("name"), document.getString("description"), document.getDouble("price"), document.getString("image"));
                                products.add(temPd);


                            }
                            pAdapter = new ProductAdapter(products);
                            productRV.setLayoutManager(pLayoutManager);
                            productRV.setAdapter(pAdapter);
                        } else {
                            Toast.makeText(ViewProducts.this, "No products for " + searchText, Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(ViewProducts.this, "Cannot load Products, check your internet", Toast.LENGTH_LONG).show();
                    }
                    hideProgressDialog();
                }
            });
        }
    }
}