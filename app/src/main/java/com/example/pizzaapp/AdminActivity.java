package com.example.pizzaapp; // make sure this matches your folder & manifest

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Match the IDs in activity_admin.xml
        MaterialCardView cardAddItem    = findViewById(R.id.cardAddItem);
        MaterialCardView cardEditItem   = findViewById(R.id.cardEditItem);
        MaterialCardView cardDeleteItem = findViewById(R.id.cardDeleteItem);
        MaterialCardView cardViewOrders = findViewById(R.id.cardViewOrders);
        MaterialButton btnLogout        = findViewById(R.id.btnLogout);

        // Click listeners
        cardAddItem.setOnClickListener(v ->
                Toast.makeText(this, "Add Menu Item clicked", Toast.LENGTH_SHORT).show());

        cardEditItem.setOnClickListener(v ->
                Toast.makeText(this, "Edit Menu Item clicked", Toast.LENGTH_SHORT).show());

        cardDeleteItem.setOnClickListener(v ->
                Toast.makeText(this, "Delete Menu Item clicked", Toast.LENGTH_SHORT).show());

        cardViewOrders.setOnClickListener(v ->
                Toast.makeText(this, "View Orders clicked", Toast.LENGTH_SHORT).show());

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
