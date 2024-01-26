package com.example.gestiondecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

interface UsersCallback {
    void onCallback(List<User> users);
}
interface MvtsCallBack{
    void onCallBack(List<MVT> mvtList);
}
public class interface_admin extends AppCompatActivity {

    private CommercialAdapter commercialAdapter;
    private List<MVT> mvtList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mvtCollection = db.collection("mvt");
    Spinner spinner;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_admin);
        mvtList = new ArrayList<>();
        spinner =  findViewById(R.id.spinner);

        getUsers(users -> {
            ArrayAdapter<User> adapter = new ArrayAdapter<>(interface_admin.this, android.R.layout.simple_spinner_item,users);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        });

        recyclerView = findViewById(R.id.rv);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadDataFromFirestore(mvtList -> {
                    commercialAdapter = new CommercialAdapter(mvtList, interface_admin.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(interface_admin.this));
                    recyclerView.setAdapter(commercialAdapter);
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> updateValidationCAdmin());

        Button btnLogout = findViewById(R.id.button1);
        btnLogout.setOnClickListener(view -> {
            Intent intent = new Intent(interface_admin.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



    public void loadDataFromFirestore( MvtsCallBack callBack ) {
        User user = (User) spinner.getSelectedItem();
        mvtCollection
                .whereEqualTo("commercial", user.getName())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mvtList.clear(); // Effacer la liste actuelle
                        for (DocumentSnapshot document : task.getResult()) {
                            MVT mvt = document.toObject(MVT.class);
                            mvtList.add(mvt);
                        }
                        callBack.onCallBack(mvtList);
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
                                                loadDataFromFirestore(mvtList -> {
                                                    commercialAdapter = new CommercialAdapter(mvtList, interface_admin.this);
                                                    recyclerView.setLayoutManager(new LinearLayoutManager(interface_admin.this));
                                                    recyclerView.setAdapter(commercialAdapter);
                                                });
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

    private void getUsers(UsersCallback callback){
        List<User> lst = new ArrayList<>();
        db.collection("User")
                .whereEqualTo("role", "commercial")
                .get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful()){
                       for(QueryDocumentSnapshot document :task.getResult()){
                           User u = document.toObject(User.class);
                           u.setId(document.getId());
                           lst.add(u);
                       }
                       callback.onCallback(lst);
                   }
                });
    }
}