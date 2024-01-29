package com.example.gestiondecommerce;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase. firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    private Button btnDatePicker;
    private Calendar selectedDate;
    private String date;

    Button chercher;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_admin);
        mvtList = new ArrayList<>();
        spinner =  findViewById(R.id.spinner);
        chercher = findViewById(R.id.cher);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        btnDatePicker.setOnClickListener(view -> showDatePickerDialog());


        getUsers(users -> {
            ArrayAdapter<User> adapter = new ArrayAdapter<>(interface_admin.this, android.R.layout.simple_spinner_item,users);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        });

        recyclerView = findViewById(R.id.rv);


        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(view -> updateValidationCAdmin());

        Button btnLogout = findViewById(R.id.button1);
        btnLogout.setOnClickListener(view -> {
            Intent intent = new Intent(interface_admin.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        chercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataFromFirestore(mvtList -> {
                    commercialAdapter = new CommercialAdapter(mvtList, interface_admin.this);
                    recyclerView.setLayoutManager(new LinearLayoutManager(interface_admin.this));
                    recyclerView.setAdapter(commercialAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                });
            }
        });

    }



    public void loadDataFromFirestore( MvtsCallBack callBack ) {
        User user = (User) spinner.getSelectedItem();
        if(date != null && user != null) {
            mvtCollection
                    .whereEqualTo("commercial", user.getName())
                    .whereEqualTo("validation_admin", false)
                    .whereEqualTo("date", date)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            mvtList.clear(); // Effacer la liste actuelle
                            for (DocumentSnapshot document : task.getResult()) {
                                MVT mvt = document.toObject(MVT.class);
                                mvtList.add(mvt);
                            }
                            if(mvtList.isEmpty()) Toast.makeText(this,"Liste vide",Toast.LENGTH_SHORT).show();
                            else Toast.makeText(this,"List Non vide",Toast.LENGTH_SHORT).show();
                            calculateAndSetTotal();
                            callBack.onCallBack(mvtList);
                        } else {
                            Toast.makeText(this, "Erreur lors du chargement des données Firestore", Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(this,"La liste est vide", Toast.LENGTH_SHORT).show();
        }
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

    private void calculateAndSetTotal() {
        int sum = 0;
        for (MVT value : mvtList) {
            sum = sum + value.getMontant();
        }
        TextView t = findViewById(R.id.textView9);
        t.setText("Total: " + String.valueOf(sum));
    }

    private void showDatePickerDialog() {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(yearSelected, monthOfYear, dayOfMonth);
                    handleDateSelection();
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }
    private void handleDateSelection() {
        if (selectedDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(selectedDate.getTime());
            Toast.makeText(this, "Selected Date: " + formattedDate, Toast.LENGTH_SHORT).show();
           this.date = formattedDate;
        }
    }
}