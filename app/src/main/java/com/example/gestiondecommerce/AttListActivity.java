package com.example.gestiondecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

interface AttCallback{
    void onCallback(List<MVT> mvtList);
}
public class AttListActivity extends AppCompatActivity{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String clientId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvt_list);
        Intent intent = getIntent();
        clientId = intent.getStringExtra("id");

        List<MVT> mvtList = getAttFromDatabase(new AttCallback() {
            @Override
            public void onCallback(List<MVT> mvtList) {
            AttListAdapter mvtAdapter = new AttListAdapter(AttListActivity.this, mvtList);
                ListView mvtListView = findViewById(R.id.mvtListView);
                mvtListView.setAdapter(mvtAdapter);
            }
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("En Attend");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public List<MVT> getAttFromDatabase(AttCallback callback) {
        List<MVT> mouvmnts = new ArrayList<>();
        db.collection("mvt")
                .whereEqualTo("idClient", clientId)
                .whereEqualTo("validation_commercial", false)
                .get()
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()){
                        if(task.getResult().isEmpty()){
                            Toast.makeText(this, "empty list", Toast.LENGTH_LONG).show();
                        }else {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MVT mvt = new MVT();
                                mvt.setId(document.getId());
                                mvt.setCommercial(document.getString("commercial"));
                                mvt.setDate(document.getString("date"));
                                mvt.setMontant(Integer.parseInt(String.valueOf(document.get("montant"))));
                                mouvmnts.add(mvt);
                            }
                        }
                        callback.onCallback(mouvmnts);
                    }
                });
        return mouvmnts;
    }
}
