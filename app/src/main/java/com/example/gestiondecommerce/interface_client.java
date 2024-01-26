package com.example.gestiondecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

interface UserCallback {
    void onCallback(List<User> users);
}
interface MvtCallback {
    void onCallback(List<MVT> mvtList);
}

public class interface_client extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText montantInput;
    private Spinner commercialesSpinner;
    private Button submitBtn;
    private FirebaseFirestore db;
    Intent intent ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_client);
        intent=getIntent();
        montantInput = findViewById(R.id.montantInput);
        commercialesSpinner = (Spinner) findViewById(R.id.commercialesSpinner);
        submitBtn = findViewById(R.id.submitBtn);
        db = FirebaseFirestore.getInstance();

        getCommercialesFromDatabase(new UserCallback() {
            @Override
            public void onCallback(List<User> users) {
                ArrayAdapter<User> spinnerAdapter = new ArrayAdapter<>(interface_client.this, android.R.layout.simple_spinner_item, users);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                commercialesSpinner.setAdapter(spinnerAdapter);
            }
        });
        commercialesSpinner.setOnItemSelectedListener(this);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (montantInput.getText().toString().equals("")) {
                    Toast.makeText(interface_client.this, "Montant Vide", Toast.LENGTH_SHORT).show();
                } else {
                    MVT mvt = new MVT();
                    mvt.setMontant(Integer.parseInt(montantInput.getText().toString()));
                    User selectedUser = (User) commercialesSpinner.getSelectedItem();
                    Log.d("OnClick", "Selected user: " + (selectedUser != null ? selectedUser.getName() : "null"));

                    // Check if the selectedUser is not null before accessing its properties
                    if (selectedUser != null) {
                        mvt.setCommercial(selectedUser.getName()); // Set the name of the selected user
                    } else {
                        // Handle the case where selectedUser is null (optional)
                        Toast.makeText(interface_client.this, "Selected user is null", Toast.LENGTH_SHORT).show();
                        Log.d("OnClick", "Selected user is null");
                        return; // Exit the onClick method to prevent further execution
                    }

                    mvt.setIdClient(intent.getStringExtra("id"));
                    mvt.setDate(new Date().toString());

                    db.collection("mvt").add(mvt)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        // Document added successfully
                                        Toast.makeText(interface_client.this, "Mouvement ajouter avec succes", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Error adding document
                                        Toast.makeText(interface_client.this, "Erreur", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        Button showMVTsBtn = findViewById(R.id.showMVTsBtn);
        showMVTsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(interface_client.this,MvtListActivity.class);
                i.putExtra("id", intent.getStringExtra("id"));
                //showMVTsDialog();
                startActivity(i);
            }
        });

        Button showAttButton = findViewById(R.id.enAttendBtn);
        showAttButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showAttDialog();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String nom= ((User) parent.getItemAtPosition(position)).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void showMVTsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Historique");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_mvt_list, null);
        builder.setView(dialogView);

        ListView mvtListView = dialogView.findViewById(R.id.mvtListView);

         getMVTsFromDatabase(new MvtCallback() {
            @Override
            public void onCallback(List<MVT> mvtList) {
                ArrayAdapter<MVT> mvtAdapter = new ArrayAdapter<>(interface_client.this, android.R.layout.simple_list_item_2, android.R.id.text1, mvtList);
                mvtListView.setAdapter(mvtAdapter);

            }
        });
        // Set a button to dismiss the dialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Dismiss the dialog
                dialogInterface.dismiss();
            }
        });

        // Show the AlertDialog
        builder.create().show();
    }
    private MVTListAdapter1 mvtAdapter;

    private void showAttDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("En attent");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_mvt_list, null);
        builder.setView(dialogView);

        ListView mvtListView = dialogView.findViewById(R.id.mvtListView);

        getAttFromDatabase(new MvtCallback() {
            @Override
            public void onCallback(List<MVT> mvtList) {
                mvtAdapter = new MVTListAdapter1(interface_client.this, mvtList);
                mvtListView.setAdapter(mvtAdapter);

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!mvtAdapter.getMvtList().isEmpty()){
                    updateDatabaseWithEditedAmounts(mvtAdapter.getMvtList());
                }
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }


    private void updateDatabaseWithEditedAmounts(List<MVT> editedMVTs) {
        for (MVT editedMVT : editedMVTs) {
            // Update the corresponding document in the database
            db.collection("mvt").document(editedMVT.getId())
                    .update("montant", editedMVT.getMontant())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Update", "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Update", "Error updating document", e);
                        }
                    });
        }
    }


    public List<MVT> getMVTsFromDatabase(MvtCallback callback) {
        List<MVT> mouvmnts = new ArrayList<>();
        db.collection("mvt")
                .whereEqualTo("idClient", intent.getStringExtra("id"))
                .whereEqualTo("validation_admin",true)
                .whereEqualTo("validation_commercial", true)
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
                               mvt.setValidation_admin(true);
                               mvt.setValidation_commercial(true);
                               mouvmnts.add(mvt);
                           }
                       }
                       callback.onCallback(mouvmnts);
                   }
                });
        return mouvmnts;
    }

    public List<MVT> getAttFromDatabase(MvtCallback callback) {
        List<MVT> mouvmnts = new ArrayList<>();
        db.collection("mvt")
                .whereEqualTo("idClient", intent.getStringExtra("id"))
                .whereEqualTo("validation_admin",false)
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

    public List<User> getCommercialesFromDatabase(UserCallback callback) {
        List<User> users = new ArrayList<>();
        db.collection("User")
                .whereEqualTo("role", "commercial")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            user.setId(document.getId());
                            users.add(user);
                            Log.i("users", user.getName());
                        }
                        callback.onCallback(users);
                    }
                });
        return users;
    }
}
