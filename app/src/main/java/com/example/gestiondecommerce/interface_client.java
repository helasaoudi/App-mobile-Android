package com.example.gestiondecommerce;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

interface UserCallback {
    void onCallback(List<User> users);
}
interface MvtCallback {
    void onCallback(List<MVT> mvtList);
}

public class interface_client extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText montantInput;
    private Spinner commercialesSpinner;
    private FirebaseFirestore db;
    Intent intent ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_client);
        intent=getIntent();
        montantInput = findViewById(R.id.montantInput);
        commercialesSpinner = findViewById(R.id.commercialesSpinner);
        Button submitBtn = findViewById(R.id.submitBtn);
        db = FirebaseFirestore.getInstance();

        getCommercialesFromDatabase(users -> {
            ArrayAdapter<User> spinnerAdapter = new ArrayAdapter<>(interface_client.this, android.R.layout.simple_spinner_item, users);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            commercialesSpinner.setAdapter(spinnerAdapter);
        });
        commercialesSpinner.setOnItemSelectedListener(this);

        submitBtn.setOnClickListener(view -> {
            if (montantInput.getText().toString().equals("")) {
                Toast.makeText(interface_client.this, "Montant Vide", Toast.LENGTH_SHORT).show();
            } else {
                MVT mvt = new MVT();
                mvt.setMontant(Integer.parseInt(montantInput.getText().toString()));
                User selectedUser = (User) commercialesSpinner.getSelectedItem();
                if (selectedUser != null) {
                    mvt.setCommercial(selectedUser.getName());
                } else {
                    Toast.makeText(interface_client.this, "Selected user is null", Toast.LENGTH_SHORT).show();
                    Log.d("OnClick", "Selected user is null");
                    return;
                }
                mvt.setIdClient(intent.getStringExtra("id"));
                mvt.setNomClient(intent.getStringExtra("nom"));
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate currentDate = LocalDate.now();
                String formattedDate = currentDate.format(dateFormatter);
                mvt.setDate(formattedDate.substring(0, 10));
                db.collection("mvt").add(mvt)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(interface_client.this, "Mouvement ajouter avec succes", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(interface_client.this, "Erreur", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        Button showMVTsBtn = findViewById(R.id.showMVTsBtn);
        showMVTsBtn.setOnClickListener(view -> {
            Intent i = new Intent(interface_client.this,MvtListActivity.class);
            i.putExtra("id", intent.getStringExtra("id"));
            startActivity(i);
        });
        Button showAttButton = findViewById(R.id.enAttendBtn);
        showAttButton.setOnClickListener(view -> {
            Intent i = new Intent(interface_client.this,AttListActivity.class);
            i.putExtra("id", intent.getStringExtra("id"));
            startActivity(i);
        });

        Button quit = findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(interface_client.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
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
                        }
                        callback.onCallback(users);
                    }
                });
        return users;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
