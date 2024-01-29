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

        holder.client.setText("client Name :" + currentItem.getIdClient());
        holder.montant.setText("Montant : " + String.valueOf(currentItem.getMontant()));
        holder.date.setText("Date : " + String.valueOf(currentItem.getDate()));


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
        TextView client;
        TextView montant;
        TextView date;
        TextView valCom;
        TextView valAdm;
        ImageView imageViewValidation;
        ImageView imageViewValidation1;

        public commercialViewHolder(@NonNull View itemView) {
            super(itemView);
            client = itemView.findViewById(R.id.client);
            montant = itemView.findViewById(R.id.montant);
            date = itemView.findViewById(R.id.date);
            valAdm = itemView.findViewById(R.id.valAdm);
            valCom = itemView.findViewById(R.id.valCom);

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
