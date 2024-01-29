package com.example.gestiondecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class interface_admin_principal extends AppCompatActivity {
    private FirebaseFirestore firestore; // Déclaration de firestore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_admin_principal);
        Button btn1 = findViewById(R.id.button3);
        Button btn2 = findViewById(R.id.button6);
        firestore = FirebaseFirestore.getInstance();

        btn1.setOnClickListener(view -> {
            Intent intent = new Intent(this, interface_admin.class);
            startActivity(intent);
            finish();
        });
        btn2.setOnClickListener(view -> {
            Intent intent = new Intent(this, Sign_up.class);
            startActivity(intent);
            finish(); //
        });

        Button btnShowUsers = findViewById(R.id.btnShowUsers);
        btnShowUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

        Button quit = findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(interface_admin_principal.this,MainActivity.class);
                startActivity(i);
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_show_users) {
                    showAllUsers();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void showAllUsers() {
        firestore.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> userList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User user = document.toObject(User.class);
                                userList.add(user);
                            }

                            // Afficher le menu contextuel avec les noms des utilisateurs
                            showUsersContextMenu(userList);
                        } else {
                            Toast.makeText(interface_admin_principal.this, "Erreur lors de la récupération des utilisateurs.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void showUsersContextMenu(List<User> userList) {
        PopupMenu userMenu = new PopupMenu(this, findViewById(R.id.btnShowUsers));
        Menu menu = userMenu.getMenu();

        for (User user : userList) {
            menu.add(user.getName() + " - " + user.getRole());
        }

        userMenu.show();
    }
}
