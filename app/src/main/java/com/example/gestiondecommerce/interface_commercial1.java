package com.example.gestiondecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class interface_commercial1 extends AppCompatActivity {

    private List<MVT> mvtList;
    private FirebaseFirestore db;
    private CollectionReference mvtCollection;
    private int currentMVTIndex = 0;
    Date currentDate = new Date(); // pour travailler avec l'ancienne API de date
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // Convertissez la date en chaîne de caractères
    String formattedDate = dateFormat.format(currentDate);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_commercial1);

        mvtList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mvtCollection = db.collection("mvt");

        loadDataFromFirestore();

        Button buttonValider = findViewById(R.id.button1);
        buttonValider.setOnClickListener(view -> {
            // Mettez à jour le champ "validation_commercial" en true
            updateValidationCommercial();
        });
        Button btnLogout = findViewById(R.id.button2);
        btnLogout.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // Facultatif, selon le comportement que vous souhaitez
        });
    }

    public void loadDataFromFirestore() {
        mvtCollection.whereEqualTo("validation_commercial", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mvtList.clear(); // Effacer la liste actuelle

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            MVT mvt = document.toObject(MVT.class);
                            mvtList.add(mvt);
                        }

                        if (!mvtList.isEmpty()) {
                            updateTextViews(mvtList);
                        } else {
                            TextView nameTextView = findViewById(R.id.textView3);
                            TextView amountTextView = findViewById(R.id.textView7);
                            nameTextView.setText("Tout les montants sont valider ");
                            amountTextView.setText("");
                        }
                    } else {
                        Toast.makeText(this, "Erreur lors du chargement des données", Toast.LENGTH_SHORT).show();
                    }
                });
    }





    private void updateTextViews(List<MVT> filteredList) {
        TextView nameTextView = findViewById(R.id.textView3);
        TextView amountTextView = findViewById(R.id.textView7);


        // Assurez-vous que l'index est valide par rapport à la nouvelle liste filtrée
        if (currentMVTIndex < filteredList.size()) {
            MVT currentItem = filteredList.get(currentMVTIndex);

            nameTextView.setText("Nom du Client : " + currentItem.getIdClient());
            amountTextView.setText("Montant : " + String.valueOf(currentItem.getMontant()));

            Log.d("FilteredMVT",  "la listeeeeeeeeeeeeee  " +filteredList.get(currentMVTIndex).getIdClient());

        }
    }
    private void updateValidationCommercial() {
        if (currentMVTIndex < mvtList.size()) {
            MVT currentItem = mvtList.get(currentMVTIndex);

            mvtCollection
                    .whereEqualTo("idClient", currentItem.getIdClient())
                    .whereEqualTo("validation_commercial", false)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentId = document.getId();

                            // Mettez à jour le champ "validation_commercial" en true
                            mvtCollection.document(documentId)
                                    .update("validation_commercial", true)
                                    .addOnSuccessListener(aVoid -> {
                                        // Mettez à jour le champ "date"
                                        mvtCollection.document(documentId)
                                                .update("date", formattedDate)
                                                .addOnSuccessListener(aVoid1 -> {
                                                    currentItem.setValidation_commercial(true);
                                                    currentItem.setDate(formattedDate);
                                                    mvtList.remove(currentItem);

                                                    // Charger les données après la mise à jour
                                                    loadDataFromFirestore();
                                                    updateTextViews(mvtList);
                                                    currentMVTIndex++;

                                                    if (currentMVTIndex < mvtList.size()) {
                                                        updateTextViews(mvtList);
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(this, "Erreur lors de la mise à jour de la date", Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Erreur lors de la validation commerciale", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Erreur lors de la récupération des documents", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Handle the case when all items in the list are processed
            // For example, show a message or perform appropriate actions
        }
    }}
