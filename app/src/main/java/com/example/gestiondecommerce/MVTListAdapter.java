package com.example.gestiondecommerce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class MVTListAdapter extends ArrayAdapter<MVT> {
    private List<MVT> mvtList;
    private LayoutInflater inflater;

    public MVTListAdapter(Context context, List<MVT> mvtList) {
        super(context, 0, mvtList);
        this.mvtList = mvtList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.att_list, parent, false);
        }

        MVT currentMVT = mvtList.get(position);

        TextView dateTextView = itemView.findViewById(R.id.dateTextView);
        EditText montantEditText = itemView.findViewById(R.id.montantEditText);

        dateTextView.setText(currentMVT.getCommercial());
        montantEditText.setText(String.valueOf(currentMVT.getMontant()));

        montantEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // Save the edited amount when the EditText loses focus
                    currentMVT.setMontant(Integer.parseInt(montantEditText.getText().toString()));
                }
            }
        });

        return itemView;
    }

    public List<MVT> getMvtList() {
        return mvtList;
    }
}