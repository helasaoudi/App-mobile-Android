package com.example.gestiondecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class interface_admin extends AppCompatActivity {

    private CommercialAdapter commercialAdapter;
    private List<MVT> mvtList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mvtCollection = db.collection("mvt");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_admin);

        mvtList = new ArrayList<>();
        commercialAdapter = new CommercialAdapter(mvtList, this);

        RecyclerView recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commercialAdapter);

        loadDataFromFirestore();  // Appeler la méthode pour charger les données depuis Firestore

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> {
            updateValidationCAdmin();
        });

        Button btnLogout = findViewById(R.id.button1);
        btnLogout.setOnClickListener(view -> {
            // Déconnexion ici (si nécessaire)

            // Redirection vers MainActivity
            Intent intent = new Intent(interface_admin.this, MainActivity.class);
            startActivity(intent);
            finish(); // Facultatif, selon le comportement que vous souhaitez
        });

    }


    // Déplacer la définition de la méthode à l'extérieur de onCreate()
    public void loadDataFromFirestore() {
        mvtCollection
                //.whereEqualTo("validation_admin", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mvtList.clear(); // Effacer la liste actuelle

                        for (DocumentSnapshot document : task.getResult()) {
                            MVT mvt = document.toObject(MVT.class);
                            mvtList.add(mvt);
                        }

                        // Mettez à jour les données de l'adaptateur
                        commercialAdapter.updateData(mvtList);
                    } else {
                        Toast.makeText(this, "Erreur lors du chargement des données Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateValidationCAdmin() {
        if (mvtList != null && !mvtList.isEmpty()) {
            for (MVT mvt : mvtList) {
                String idClient = mvt.getIdClient();

                if (idClient != null && !mvt.isValidation_admin()) {
                    mvtCollection
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    mvtCollection
                                            .document(document.getId())
                                            .update("validation_admin", true)
                                            .addOnSuccessListener(aVoid -> {
                                                mvt.setValidation_admin(true);


                                                // Charger les données après la mise à jour
                                                loadDataFromFirestore();

                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("ValidationError", "Erreur lors de la validation commerciale", e);
                                                Toast.makeText(this, "Erreur lors de la validation commerciale", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ValidationError", "Erreur lors de la récupération des documents", e);
                                Toast.makeText(this, "Erreur lors de la récupération des documents", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        } else {
            Log.e("ValidationError", "Liste mvtList est null ou vide");
            Toast.makeText(this, "Liste mvtList est null ou vide", Toast.LENGTH_SHORT).show();
        }

    }

}