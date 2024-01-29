package com.example.gestiondecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class CommercialListAdapter extends ArrayAdapter<MVT> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;

    public CommercialListAdapter(Context context, List<MVT> list){
        super(context,0,list);
        this.context= context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if(itemView == null){
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.commercial_list_item, parent,false);
        }
        MVT mvt = getItem(position);
        TextView client = itemView.findViewById(R.id.client);
        TextView montant = itemView.findViewById(R.id.montant);
        Button valid =itemView.findViewById(R.id.valid);

        if (mvt != null){
            client.setText(mvt.getNomClient());
            montant.setText(String.valueOf(mvt.getMontant()));
            valid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!montant.getText().equals("")) {
                        mvt.setMontant(Integer.parseInt((String) montant.getText()));
                        updateMvt(mvt);
                    }else {
                        Toast.makeText(context, "Champ vide", Toast.LENGTH_SHORT);
                    }

                }
            });
        }
        return itemView;
    }
    private void updateMvt(MVT mvt) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Mis A jour...");
        progressDialog.show();
        db.collection("mvt").document(mvt.getId()).update("validation_commercial",true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Succes", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
