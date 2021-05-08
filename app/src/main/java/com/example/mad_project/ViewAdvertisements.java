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

public class ViewAdvertisements extends BaseActivity implements View.OnClickListener {

    private RecyclerView adRv;
    private RecyclerView.Adapter adAdapter;
    private RecyclerView.LayoutManager adLayoutManager;
    ArrayList<Advertisement> ads = new ArrayList<>();
    FirebaseFirestore db;
    Button searchBtn;
    EditText searchET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_advertisements);
        adRv = findViewById(R.id.ad_recyclerView);
        adRv.setHasFixedSize(true);
        adLayoutManager = new LinearLayoutManager(this);
        db= FirebaseFirestore.getInstance();
        searchBtn  =findViewById(R.id.ad_search_btn);
        searchBtn.setOnClickListener(this);
        searchET = findViewById(R.id.ad_search_tv);
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadAds();
    }

    private void loadAds() {
        ads.clear();
        showProgressDialog("Loading ads");
        db.collection("ads").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().size() > 0){
                        for(QueryDocumentSnapshot doc: task.getResult()){
                            Advertisement ad = new Advertisement(doc.getId(),doc.getString("productId"),doc.getString("productName"),doc.getString("title"), doc.getString("description"),Double.parseDouble(doc.get("discount").toString()), doc.getString("image"));
                            ads.add(ad);
                        }
                        adAdapter = new AdvertisementAdapter(ads);
                        adRv.setLayoutManager(adLayoutManager);
                        adRv.setAdapter(adAdapter);
                    }else{
                        Toast.makeText(ViewAdvertisements.this, "No ads yet", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(ViewAdvertisements.this, "Cannot load ads, check your internet", Toast.LENGTH_LONG).show();
                }
                hideProgressDialog();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.ad_search_btn){
            searchAds();
        }
    }

    private void searchAds() {
        final String searchT = searchET.getText().toString();
        View view = this.getCurrentFocus();
        this.searchET.clearFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if(!searchT.isEmpty()){
            ads.clear();
            showProgressDialog("Loading ads");
            db.collection("ads").orderBy("title").startAt(searchT).endAt(searchT + "\uf8ff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        if(task.getResult().size() > 0){
                            for(QueryDocumentSnapshot doc: task.getResult()){
                                Advertisement ad = new Advertisement(doc.getId(),doc.getString("productId"),doc.getString("productName"),doc.getString("title"), doc.getString("description"),Double.parseDouble(doc.get("discount").toString()), doc.getString("image"));
                                ads.add(ad);
                            }
                            adAdapter = new AdvertisementAdapter(ads);
                            adRv.setLayoutManager(adLayoutManager);
                            adRv.setAdapter(adAdapter);
                        }else{
                            Toast.makeText(ViewAdvertisements.this, "No ads named"+searchT, Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(ViewAdvertisements.this, "Cannot load ads, check your internet", Toast.LENGTH_LONG).show();
                    }
                    hideProgressDialog();
                }
            });
        }else{
            loadAds();
        }
    }
}