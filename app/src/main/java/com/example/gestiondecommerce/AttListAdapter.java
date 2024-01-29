package com.example.gestiondecommerce;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AttListAdapter extends ArrayAdapter<MVT> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context context;
    public AttListAdapter(Context context, List<MVT> list){
        super(context,0,list);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent){
        View itemView = convertView;
        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.en_attend, parent, false);
        }

        MVT currentMvt = getItem(position);

        TextView date = itemView.findViewById(R.id.attDate);
        TextView commercial = itemView.findViewById(R.id.attCommercial);
        EditText edit = itemView.findViewById(R.id.attEdit);
        Button save = itemView.findViewById(R.id.save);
        if(currentMvt != null){
            date.setText("Date: "+currentMvt.getDate());
            commercial.setText("Commercial :"+currentMvt.getCommercial());
            edit.setText(String.valueOf(currentMvt.getMontant()));
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentMvt.setMontant(Integer.parseInt(edit.getText().toString()));
                    saveMvt(currentMvt);
                }
            });
        }
        return itemView;
    }

    private void saveMvt(MVT currentMvt) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Mis A jour...");
        progressDialog.show();
        db.collection("mvt").document(currentMvt.getId()).update("montant",currentMvt.getMontant())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Succes", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    }

