package com.example.vesloo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    public ArrayList<Product> productsList;

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        public ImageView prodImageView;
        public TextView name, desc, price;
        public ImageButton dltBtn, edtBtn;
        public  Button adBtn;
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        public ProductViewHolder(@NonNull final View itemView) {
            super(itemView);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            dltBtn = itemView.findViewById(R.id.product_delete_btn);
            edtBtn = itemView.findViewById(R.id.product_update_btn);
            adBtn = itemView.findViewById(R.id.product_add_ad_btn);
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        String role = document.getString("role");
                        if(role.equals("admin")){
                            dltBtn.setVisibility(View.VISIBLE);
                            edtBtn.setVisibility(View.VISIBLE);
                            adBtn.setVisibility(View.VISIBLE);
                            adBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent productIntent = new Intent(itemView.getContext(),AddAdvertisement.class);
                                    productIntent.putExtra("id", productsList.get(getBindingAdapterPosition()).getId());
                                    productIntent.putExtra("name",productsList.get(getBindingAdapterPosition()).getName());
                                    itemView.getContext().startActivity(productIntent);
                                }
                            });
                            edtBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent productIntent = new Intent(itemView.getContext(),UpdateProduct.class);
                                    productIntent.putExtra("id", productsList.get(getBindingAdapterPosition()).getId());
                                    itemView.getContext().startActivity(productIntent);
                                }
                            });

                            dltBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new AlertDialog.Builder(itemView.getContext())
                                            .setTitle("Delete Product")
                                            .setMessage("Are you sure you want to delete this Product? All details (feedback, Advertisements) will be deleted")

                                            // Specifying a listener allows you to take an action before dismissing the dialog.
                                            // The dialog is automatically dismissed when a dialog button is clicked.
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    db.collection("products").document(productsList.get(getBindingAdapterPosition()).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            db.collection("feedbacks").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    for (QueryDocumentSnapshot snap :task.getResult()){
                                                                        if(snap.getString("productId").equals(productsList.get(getBindingAdapterPosition()).getId())){
                                                                            snap.getReference().delete();
                                                                        }
                                                                    }
                                                                    Toast.makeText(itemView.getContext(), "Feedbacks deleted", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                            db.collection("ads").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    for (QueryDocumentSnapshot snap :task.getResult()){
                                                                        if(snap.getString("productId").equals(productsList.get(getBindingAdapterPosition()).getId())){
                                                                            snap.getReference().delete();
                                                                        }
                                                                    }
                                                                    Toast.makeText(itemView.getContext(), "Advertisements deleted", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                            ((Activity) itemView.getContext()).finish();
                                                        }
                                                    });


                                                }
                                            })

                                            // A null listener allows the button to dismiss the dialog and take no further action.
                                            .setNegativeButton(android.R.string.no, null)
                                            .setIcon(android.R.drawable.ic_menu_delete)
                                            .show();

                                }
                            });
                        }
                    }
                }
            });

            prodImageView = itemView.findViewById(R.id.list_product_image_view);

            name = itemView.findViewById(R.id.product_list_prd_name);
            desc = itemView.findViewById(R.id.product_list_prd_details);
            price = itemView.findViewById(R.id.product_list_prd_price);
            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Intent productIntent = new Intent(itemView.getContext(),ViewProduct.class);
                    productIntent.putExtra("id", productsList.get(getBindingAdapterPosition()).getId());
                    itemView.getContext().startActivity(productIntent);
                }
            });
        }
    }

    public ProductAdapter(ArrayList<Product> productList) {
        this.productsList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
        ProductViewHolder pvholder = new ProductViewHolder(view);
        return pvholder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productsList.get(position);

        Picasso.get().load(product.getImage()).into(holder.prodImageView);
        holder.name.setText(product.getName());
        holder.desc.setText(product.getDescription());
        holder.price.setText(String.format("%.2f",product.getPrice()));

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }
}
