package com.example.pizzaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS = "pm_session";
    private static final String KEY_EMAIL = "email";
    private static final String ADMIN_EMAIL = "gamardeneth@gmail.com"; // fallback admin

    private TextInputLayout tilEmailLogin, tilPasswordLogin;
    private TextInputEditText etEmailLogin, etPasswordLogin;
    private Button btnLoginNow;
    private TextView tvToSignup, tvForgot;
    private CheckBox cbRemember;

    private SharedPreferences prefs;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tilEmailLogin   = findViewById(R.id.tilEmailLogin);
        tilPasswordLogin= findViewById(R.id.tilPasswordLogin);
        etEmailLogin    = findViewById(R.id.etEmailLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLoginNow     = findViewById(R.id.btnLoginNow);
        tvToSignup      = findViewById(R.id.tvToSignup);
        tvForgot        = findViewById(R.id.tvForgot);
        cbRemember      = findViewById(R.id.cbRemember);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        auth  = FirebaseAuth.getInstance();
        db    = FirebaseFirestore.getInstance();

        // Prefill remembered email
        String rememberedEmail = prefs.getString(KEY_EMAIL, "");
        if (!rememberedEmail.isEmpty()) {
            etEmailLogin.setText(rememberedEmail);
            cbRemember.setChecked(true);
        }

        addClearErrorWatcher(etEmailLogin, tilEmailLogin);
        addClearErrorWatcher(etPasswordLogin, tilPasswordLogin);

        btnLoginNow.setOnClickListener(v -> attemptLogin());
        tvToSignup.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
        tvForgot.setOnClickListener(v -> sendResetEmail());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser current = auth.getCurrentUser();
        if (current != null) {
            if (!current.isEmailVerified()) {
                Toast.makeText(this, "Please verify your email first.", Toast.LENGTH_LONG).show();
                auth.signOut();
            } else {
                fetchRoleAndRoute(current); // <- route admin or customer on app start
            }
        }
    }

    private void attemptLogin() {
        String email = safeText(etEmailLogin);
        String pw    = safeText(etPasswordLogin);

        boolean valid = true;
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmailLogin.setError("Enter a valid email");
            valid = false;
        }
        if (pw.isEmpty()) {
            tilPasswordLogin.setError("Enter your password");
            valid = false;
        }
        if (!valid) return;

        btnLoginNow.setEnabled(false);

        auth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, task -> {
                    btnLoginNow.setEnabled(true);

                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();

                        if (user != null && !user.isEmailVerified()) {
                            auth.signOut();
                            Toast.makeText(this, "Verify your email, then try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Remember email toggle
                        if (cbRemember.isChecked()) {
                            prefs.edit().putString(KEY_EMAIL, email).apply();
                        } else {
                            prefs.edit().remove(KEY_EMAIL).apply();
                        }

                        Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();

                        if (user != null) {
                            fetchRoleAndRoute(user);
                        } else {
                            // Very unlikely path
                            routeAfterLogin(false);
                        }
                    } else {
                        String msg = readableAuthError(task.getException());
                        tilPasswordLogin.setError("Invalid email or password");
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void fetchRoleAndRoute(FirebaseUser user) {
        // First try Firestore users/{uid}.isAdmin
        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> routeAfterLogin(resolveIsAdmin(doc, user)))
                .addOnFailureListener(e -> {
                    // On failure, still allow fallback by email
                    boolean isAdmin = isFallbackAdmin(user);
                    routeAfterLogin(isAdmin);
                });
    }

    private boolean resolveIsAdmin(DocumentSnapshot doc, FirebaseUser user) {
        boolean isAdmin = false;
        if (doc != null && doc.exists()) {
            Boolean flag = doc.getBoolean("isAdmin");
            isAdmin = (flag != null && flag);
        }
        // Fallback: special email treated as admin if Firestore missing
        if (!isAdmin && isFallbackAdmin(user)) {
            isAdmin = true;
        }
        return isAdmin;
    }

    private boolean isFallbackAdmin(FirebaseUser user) {
        String email = user.getEmail();
        return email != null && email.equalsIgnoreCase(ADMIN_EMAIL);
    }

    private void routeAfterLogin(boolean isAdmin) {
        Intent i = new Intent(this, isAdmin ? AdminActivity.class : MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void sendResetEmail() {
        String email = safeText(etEmailLogin);
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmailLogin.setError("Enter your email to reset");
            return;
        }
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, readableAuthError(e), Toast.LENGTH_LONG).show());
    }

    private String readableAuthError(Exception e) {
        if (e instanceof FirebaseAuthInvalidUserException) {
            return "No account found for that email.";
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            return "Incorrect email or password.";
        } else if (e != null && e.getLocalizedMessage() != null) {
            return e.getLocalizedMessage();
        } else {
            return "Authentication failed. Please try again.";
        }
    }

    private static String safeText(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void addClearErrorWatcher(TextInputEditText et, TextInputLayout til) {
        et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { til.setError(null); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }
}
