package com.example.gestiondecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommercialAdapter extends RecyclerView.Adapter<CommercialAdapter.commercialViewHolder> {
    List<MVT> listeClient;
    Context context;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public CommercialAdapter(List<MVT> listeClient, Context context) {
        this.listeClient = listeClient;
        this.context = context;
    }

    @NonNull
    @Override
    public commercialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iteam_commercial, parent, false);
        return new commercialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull commercialViewHolder holder, int position) {
        MVT currentItem = listeClient.get(position);

        holder.textViewClientName.setText("client Name :" + currentItem.getIdClient());
        holder.textViewAmount.setText("Montant : " + String.valueOf(currentItem.getMontant()));

        if (currentItem.isValidation_commercial()) {
            holder.imageViewValidation.setImageResource(R.drawable.valide);
            holder.imageViewValidation.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewValidation.setImageResource(R.drawable.invalid_foreground);
            holder.imageViewValidation.setVisibility(View.VISIBLE);
        }
        if (currentItem.isValidation_admin()) {
            holder.imageViewValidation1.setImageResource(R.drawable.valide);
            holder.imageViewValidation1.setVisibility(View.VISIBLE);
        } else {
            holder.imageViewValidation1.setImageResource(R.drawable.invalid_foreground);
            holder.imageViewValidation1.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return listeClient.size();
    }

    public void updateData(List<MVT> newData) {
        this.listeClient = newData;
        notifyDataSetChanged();
    }

    public class commercialViewHolder extends RecyclerView.ViewHolder {
        TextView textViewClientName;
        TextView textViewAmount;
        TextView textView6;
        TextView textView8;
        ImageView imageViewValidation;
        ImageView imageViewValidation1;

        public commercialViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewClientName = itemView.findViewById(R.id.textView3);
            textViewAmount = itemView.findViewById(R.id.textView7);
            textView6 = itemView.findViewById(R.id.textView6);
            textView8 = itemView.findViewById(R.id.textView8);
            imageViewValidation = itemView.findViewById(R.id.imageViewValidation);
            imageViewValidation1= itemView.findViewById(R.id.imageViewValidation1);


            itemView.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(listeClient.get(position));
                    }
                }
            });
        }

}

    public interface OnItemClickListener {
        void onItemClick(MVT item);
    }
}

