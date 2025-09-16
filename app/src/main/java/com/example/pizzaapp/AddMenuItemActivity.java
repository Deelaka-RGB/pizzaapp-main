package com.example.pizzaapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddMenuItemActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilDesc, tilPrice;
    private TextInputEditText etName, etDesc, etPrice;
    private ImageView imgPreview;
    private MaterialButton btnPickImage, btnSave;

    private Uri pickedImageUri;

    // System picker (no storage permissions needed)
    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    pickedImageUri = uri;
                    imgPreview.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tilName  = findViewById(R.id.tilName);
        tilDesc  = findViewById(R.id.tilDesc);
        tilPrice = findViewById(R.id.tilPrice);
        etName   = findViewById(R.id.etName);
        etDesc   = findViewById(R.id.etDesc);
        etPrice  = findViewById(R.id.etPrice);

        imgPreview   = findViewById(R.id.imgPreview);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnSave      = findViewById(R.id.btnSave);

        btnPickImage.setOnClickListener(v -> pickImage.launch("image/*"));
        btnSave.setOnClickListener(v -> trySave());
    }

    private void trySave() {
        clearErrors();

        final String name  = textOf(etName);
        final String desc  = textOf(etDesc);
        final String priceStr = textOf(etPrice);

        boolean ok = true;
        if (name.isEmpty()) { tilName.setError("Required"); ok = false; }
        if (desc.isEmpty()) { tilDesc.setError("Required"); ok = false; }
        if (priceStr.isEmpty()) { tilPrice.setError("Required"); ok = false; }

        long parsedPrice = 0L;
        if (!priceStr.isEmpty()) {
            try {
                parsedPrice = Long.parseLong(priceStr);
            } catch (NumberFormatException e) {
                tilPrice.setError("Invalid number");
                ok = false;
            }
        }

        if (pickedImageUri == null) {
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
            ok = false;
        }
        if (!ok) return;

        // Make captured values final for lambdas
        final long   finalPrice = parsedPrice;
        final String finalName  = name;
        final String finalDesc  = desc;

        btnSave.setEnabled(false);
        btnSave.setText("Saving…");

        // 1) Upload image to Firebase Storage
        String imagePath = "menu_images/" + UUID.randomUUID() + ".jpg";
        StorageReference ref = FirebaseStorage.getInstance().getReference(imagePath);

        ref.putFile(pickedImageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return ref.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    // 2) Write Firestore document
                    Map<String, Object> data = new HashMap<>();
                    data.put("title",       finalName);
                    data.put("description", finalDesc);
                    data.put("price",       finalPrice);              // Long in Firestore
                    data.put("imageUrl",    downloadUri.toString());
                    data.put("rating",      0.0);                     // defaults
                    data.put("available",   true);

                    FirebaseFirestore.getInstance()
                            .collection("menu")
                            .add(data)
                            .addOnSuccessListener(doc -> {
                                Toast.makeText(this, "Item added!", Toast.LENGTH_SHORT).show();
                                finish(); // go back to Admin
                            })
                            .addOnFailureListener(e -> {
                                btnSave.setEnabled(true);
                                btnSave.setText("Save Item");
                                Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    btnSave.setEnabled(true);
                    btnSave.setText("Save Item");
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void clearErrors() {
        tilName.setError(null);
        tilDesc.setError(null);
        tilPrice.setError(null);
    }

    private static String textOf(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
