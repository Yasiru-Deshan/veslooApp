package com.example.vesloo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    public ArrayList<Feedback> feedbacks;

    public class FeedbackViewHolder extends RecyclerView.ViewHolder {
        public TextView productName, comment, user;
        public RatingBar ratingBar;
        public ImageButton dltBtn, edtBtn;

        public FeedbackViewHolder(@NonNull final View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.rating_prd_name);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            comment = itemView.findViewById(R.id.rating_comment);
            user = itemView.findViewById(R.id.rating_user);
            dltBtn = itemView.findViewById(R.id.rating_delete_btn);
            edtBtn = itemView.findViewById(R.id.rating_update_btn);

            edtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent feedbackIntent = new Intent(view.getContext(), UpdateFeedback.class);
                    feedbackIntent.putExtra("id", feedbacks.get(getBindingAdapterPosition()).getId());
                    view.getContext().startActivity(feedbackIntent);
                }
            });

            dltBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(view.getContext()).setTitle("Delete feedback")
                            .setMessage("Are you sure you want to delete this feedback?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("feedbacks").document(feedbacks.get(getBindingAdapterPosition()).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(itemView.getContext(),"Feedback deleted successfully", Toast.LENGTH_LONG).show();
                                            ((Activity) itemView.getContext()).finish();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(itemView.getContext(),"Cannot delete the feedback right now", Toast.LENGTH_LONG).show();

                                        }
                                    });
                                }
                            }).setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_menu_delete)
                            .show();
                }
            });
        }

    }

    public FeedbackAdapter(ArrayList<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedback_item, parent, false);
        FeedbackViewHolder fvHolder = new FeedbackViewHolder(view);
        return fvHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbacks.get(position);

        holder.productName.setText(feedback.getProductName());
        holder.ratingBar.setRating(feedback.getRate());
        holder.comment.setText(feedback.getComment());
        holder.user.setText(feedback.getUserName());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(feedback.getUserId().equals(user.getUid())){
            holder.dltBtn.setVisibility(View.VISIBLE);
            holder.edtBtn.setVisibility(View.VISIBLE);
        }else{
            System.out.println("bot");
        }
    }


    @Override
    public int getItemCount() {
        return feedbacks.size();
    }


}
