package com.fuzailkhan.spectator_findmissingpeople;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout emailEt;
    Button loginButton;
    FirebaseAuth mAuth;
    TextInputLayout passwordEt;
    ProgressBar progressBar;
    Button registerButton;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_login);
        this.emailEt = (TextInputLayout) findViewById(R.id.login_email_et);
        this.passwordEt = (TextInputLayout) findViewById(R.id.login_password_et);
        this.registerButton = (Button) findViewById(R.id.btnRegister);
        this.loginButton = (Button) findViewById(R.id.btnlogin);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar_login);
        this.mAuth = FirebaseAuth.getInstance();
        this.registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        this.loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = emailEt.getEditText().getText().toString();
                String password = passwordEt.getEditText().getText().toString();
                if (validateEmail() || validatePassword()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                LoginActivity.this.startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                LoginActivity.this.finish();
                                return;
                            }
                            Toast.makeText(LoginActivity.this, "Error:" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* access modifiers changed from: private */
    public boolean validateEmail() {
        if (((EditText) Objects.requireNonNull(this.emailEt.getEditText())).getText().toString().trim().isEmpty()) {
            this.emailEt.setError("Field can not be empty");
            return false;
        }
        this.emailEt.setError((CharSequence) null);
        this.emailEt.setErrorEnabled(false);
        return true;
    }

    /* access modifiers changed from: private */
    public boolean validatePassword() {
        if (((EditText) Objects.requireNonNull(this.passwordEt.getEditText())).getText().toString().trim().isEmpty()) {
            this.passwordEt.setError("Field can not be empty");
            return false;
        }
        this.passwordEt.setError((CharSequence) null);
        this.passwordEt.setErrorEnabled(false);
        return true;
    }

   /* private boolean isConnected(LoginActivity loginActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) loginActivity.getSystemService("connectivity");
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(1);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(0);
        if ((wifiConn == null || !wifiConn.isConnected()) && (mobileConn == null || !mobileConn.isConnected())) {
            return false;
        }
        return true;
    }*/

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please connect to the Internet").setCancelable(false).setPositiveButton("Connect", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LoginActivity.this.startActivity(new Intent("android.settings.WIFI_SETTINGS"));
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this.getApplicationContext(), LoginActivity.class));
                LoginActivity.this.finish();
            }
        });
        builder.create().show();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        this.emailEt.setTranslationX(300.0f);
        this.emailEt.animate().translationX(0.0f).alpha(1.0f).setDuration(700).setStartDelay(0).start();
        this.passwordEt.setTranslationX(300.0f);
        this.passwordEt.animate().translationX(0.0f).alpha(1.0f).setDuration(750).setStartDelay(0).start();
        this.loginButton.setTranslationX(300.0f);
        this.loginButton.animate().translationX(0.0f).alpha(1.0f).setDuration(800).setStartDelay(0).start();
        this.registerButton.setTranslationX(-300.0f);
        this.registerButton.animate().translationX(0.0f).alpha(1.0f).setDuration(600).setStartDelay(0).start();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }
    }
}
