package com.example.vesloo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdvertisementAdapter extends RecyclerView.Adapter<AdvertisementAdapter.AdvertisementViewHolder> {
    public ArrayList<Advertisement> ads;

    public class AdvertisementViewHolder extends RecyclerView.ViewHolder{
        public ImageView img;
        public TextView title, desc, discount;
        public ImageButton editBtn, dltBtn;

        public AdvertisementViewHolder(@NonNull final View itemView) {
            super(itemView);
            dltBtn = itemView.findViewById(R.id.ad_delete_btn);
            editBtn = itemView.findViewById(R.id.ad_update_btn);

            img = itemView.findViewById(R.id.list_ad_image_view);
            title = itemView.findViewById(R.id.ad_list_ad_title);
            desc = itemView.findViewById(R.id.ad_list_ad_desc);
            discount = itemView.findViewById(R.id.ad_list_discount);

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent updateAd = new Intent(itemView.getContext(), UpdateAdvertisement.class);
                    updateAd.putExtra("id", ads.get(getBindingAdapterPosition()).getId());
                    itemView.getContext().startActivity(updateAd);
                }
            });

            dltBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(itemView.getContext()).setTitle("Delete Advertisement")
                            .setMessage("Are you sure, You need to delete this advertisement? This action cannot be undone")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("ads").document(ads.get(getBindingAdapterPosition()).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(itemView.getContext(),"Advertisement deleted successfully", Toast.LENGTH_LONG).show();
                                            ((Activity) itemView.getContext()).finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(itemView.getContext(),"Cannot delete ad right now, Try again", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                }
                            }).setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_menu_delete).show();
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent viewAd = new Intent(itemView.getContext(), ViewAdvertisement.class);
                    viewAd.putExtra("id", ads.get(getBindingAdapterPosition()).getId());
                    itemView.getContext().startActivity(viewAd);
                }
            });
        }
    }

    public AdvertisementAdapter(ArrayList<Advertisement> ads) {
        this.ads = ads;
    }

    @NonNull
    @Override
    public AdvertisementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_cardview,parent,false);
        AdvertisementViewHolder avHolder = new AdvertisementViewHolder(view);

        return avHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdvertisementViewHolder holder, int position) {
        Advertisement ad = ads.get(position);

        Picasso.get().load(ad.getImage()).into(holder.img);
        holder.title.setText(ad.getTitle());
        holder.desc.setText(ad.getDesc());
        holder.discount.setText(String.valueOf(ad.getDiscount()));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().getString("role").equals("admin")){
                        holder.dltBtn.setVisibility(View.VISIBLE);
                        holder.editBtn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ads.size();
    }




}
