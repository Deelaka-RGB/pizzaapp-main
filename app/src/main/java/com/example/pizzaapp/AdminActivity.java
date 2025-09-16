package com.example.pizzaapp; // keep in sync with your manifest & folder

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerMenuItems;
    private MenuAdminAdapter adapter;
    private final List<FoodItem> menuList = new ArrayList<>();

    // If you want a callback after the add form closes (optional)
    private final ActivityResultLauncher<Intent> addItemLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // If you return something from AddMenuItemActivity you can handle it here.
                        // For now, we just refresh from Firestore (if you’re using it).
                        loadFromFirestore(); // safe no-op if collection is empty
                    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // ----- Admin tiles / actions -----
        MaterialCardView cardAddItem    = findViewById(R.id.cardAddItem);
        MaterialCardView cardEditItem   = findViewById(R.id.cardEditItem);
        MaterialCardView cardDeleteItem = findViewById(R.id.cardDeleteItem);
        MaterialCardView cardViewOrders = findViewById(R.id.cardViewOrders);
        MaterialButton  btnLogout       = findViewById(R.id.btnLogout);

        // Open the "Add Menu Item" form Activity
        cardAddItem.setOnClickListener(v -> {
            Intent i = new Intent(this, AddMenuItemActivity.class);
            addItemLauncher.launch(i);
        });

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
        recyclerMenuItems = findViewById(R.id.recyclerMenuItems);
        recyclerMenuItems.setLayoutManager(new LinearLayoutManager(this));

        seedLocalData(); // keep your existing demo items
        adapter = new MenuAdminAdapter(menuList);
        recyclerMenuItems.setAdapter(adapter);

        // Optional: also try to load from Firestore to reflect newly added items
        loadFromFirestore();
    }

    /** keep your existing demo list so the screen isn’t empty */
    private void seedLocalData() {
        menuList.clear();
        menuList.add(new FoodItem("Margherita",   "Classic Pizza", 4.8f, R.drawable.pizza_10,       1590));
        menuList.add(new FoodItem("Pepperoni",    "Spicy delight", 4.7f, R.drawable.chickenpizza_1, 1890));
        menuList.add(new FoodItem("Veggie",       "Fresh Garden",  4.6f, R.drawable.pizza_9,        1700));
        menuList.add(new FoodItem("BBQ Chicken",  "Smoky & Sweet", 4.5f, R.drawable.pizza_13,       2000));
        menuList.add(new FoodItem("Hawaiian",     "Pineapple Hit", 4.2f, R.drawable.pizza_9,        1800));
        menuList.add(new FoodItem("Meat Lovers",  "Loaded Feast",  4.9f, R.drawable.pizza_10,       2200));
    }

    /**
     * OPTIONAL: Pull items from Firestore “menu” collection.
     * This lets the new item from AddMenuItemActivity show up automatically.
     * If you don’t want Firestore yet, you can remove this method + its calls.
     */
    private void loadFromFirestore() {
        FirebaseFirestore.getInstance()
                .collection("menu")
                .get()
                .addOnSuccessListener(this::applyFirestoreData)
                .addOnFailureListener(e -> {
                    // Not fatal—keep local list
                });
    }

    private void applyFirestoreData(QuerySnapshot snap) {
        if (snap == null || snap.isEmpty()) return;

        // Replace the local list with Firestore data (use a placeholder image res)
        menuList.clear();
        snap.getDocuments().forEach(doc -> {
            String title = doc.getString("title");
            String desc  = doc.getString("description");
            Number priceNum = doc.getLong("price");
            int price = priceNum == null ? 0 : priceNum.intValue();

            // If you also want real images, switch MenuAdminAdapter to load URLs with Glide.
            menuList.add(new FoodItem(
                    title == null ? "" : title,
                    desc == null ? "" : desc,
                    0f,
                    R.drawable.pizza_1, // placeholder thumbnail in admin list
                    price
            ));
        });
        adapter.notifyDataSetChanged();
    }
}
