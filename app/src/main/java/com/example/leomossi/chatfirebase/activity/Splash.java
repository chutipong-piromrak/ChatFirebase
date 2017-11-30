package com.example.leomossi.chatfirebase.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.leomossi.chatfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Splash extends AppCompatActivity {

    Handler handler;
    Runnable runnable;
    long delay_time;
    long time = 3000L;

    SharedPreferences prefs;
    FirebaseAuth mAuth;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();

        prefs = getBaseContext().getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
        final String emailRemember = prefs.getString("email", null);
        final String passwordRemember = prefs.getString("password", null);
        Log.d("emailpassword", emailRemember+""+passwordRemember);
        if (emailRemember != null && passwordRemember != null) {
            handler = new Handler();
            runnable = new Runnable() {
                public void run() {
                    signInWithEmailAndPassword(emailRemember, passwordRemember);
                }
            };
        } else {
            handler = new Handler();
            runnable = new Runnable() {
                public void run() {
                    Intent intent = new Intent(Splash.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
        }
    }

    public void signInWithEmailAndPassword(final String email, final String password) {
        Log.d("email", email+""+password);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Toast.makeText(LoginActivity.this, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Splash.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

                        } else {
                            // If sign in fails, display a message to the user.
//                            Toast.makeText(Splash.this, "เข้าสู่ระบบไม่สำเร็จ" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onResume() {
        super.onResume();
        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
    }
}