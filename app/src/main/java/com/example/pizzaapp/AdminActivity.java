package com.example.pizzaapp; // make sure this matches AndroidManifest + folder

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // ----- Admin tiles / actions -----
        MaterialCardView cardAddItem    = findViewById(R.id.cardAddItem);
        MaterialCardView cardEditItem   = findViewById(R.id.cardEditItem);
        MaterialCardView cardDeleteItem = findViewById(R.id.cardDeleteItem);
        MaterialCardView cardViewOrders = findViewById(R.id.cardViewOrders);
        MaterialButton  btnLogout       = findViewById(R.id.btnLogout);

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

        // ----- Menu list under the 4 tiles -----
        RecyclerView recyclerMenuItems = findViewById(R.id.recyclerMenuItems);
        recyclerMenuItems.setLayoutManager(new LinearLayoutManager(this));

        List<FoodItem> menuList = new ArrayList<>();
        menuList.add(new FoodItem("Margherita",   "Classic Pizza", 4.8f, R.drawable.pizza_10,       1590));
        menuList.add(new FoodItem("Pepperoni",    "Spicy delight", 4.7f, R.drawable.chickenpizza_1, 1890));
        menuList.add(new FoodItem("Veggie",       "Fresh Garden",  4.6f, R.drawable.pizza_9,        1700));
        menuList.add(new FoodItem("BBQ Chicken",  "Smoky & Sweet", 4.5f, R.drawable.pizza_13,       2000));
        menuList.add(new FoodItem("Hawaiian",     "Pineapple Hit", 4.2f, R.drawable.pizza_9,        1800));
        menuList.add(new FoodItem("Meat Lovers",  "Loaded Feast",  4.9f, R.drawable.pizza_10,       2200));

        // Adapter that accepts List<FoodItem>
        MenuAdminAdapter adapter = new MenuAdminAdapter(menuList);
        recyclerMenuItems.setAdapter(adapter);
    }
}
