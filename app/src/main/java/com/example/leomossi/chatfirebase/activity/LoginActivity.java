package com.example.leomossi.chatfirebase.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leomossi.chatfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.input_email) EditText emailText;
    @BindView(R.id.input_password) EditText passwordText;
    @BindView(R.id.btn_login) Button loginButton;
    @BindView(R.id.link_signup) TextView signLink;
    @BindView(R.id.check_remember) CheckBox rememberMe;
    @BindView(R.id.forgot_password) TextView forgotPassword;

    private String email;
    private String password;
    private SpotsDialog dialog;

    private FirebaseAuth mAuth;
    FirebaseUser user;
    String emailIntent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        try {
            emailIntent = getIntent().getExtras().get("email").toString();
            emailText.setText(emailIntent);
            passwordText.requestFocus();
        } catch (NullPointerException n){

        }

    }

    @OnClick(R.id.btn_login)
    void onClickLogin() {
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
        login();
    }

    @OnClick(R.id.link_signup)
    void onClickSignup() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @OnClick(R.id.forgot_password)
    void onClickForgotPassword() {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);

    }

    public void login() {

        if (!validate()) {
            return;
        }

        dialog = new SpotsDialog(this, "กำลังเข้าสู่ระบบ");
        dialog.show();

        signInWithEmailAndPassword(email, password);


    }

    public boolean validate() {
        boolean valid = true;

        email = emailText.getText().toString();
        password = passwordText.getText().toString();

        if ((email.isEmpty()) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("อีเมลไม่ถูกต้อง");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 16) {
            passwordText.setError("รหัสควรมีความยาว 6 - 16 ตัวอักษร");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    public void signInWithEmailAndPassword(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);

                            if (rememberMe.isChecked()) {
                                SharedPreferences prefs = getBaseContext().getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.apply();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
//                            if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted")) {
//                                Toast.makeText(LoginActivity.this, "ไม่มีบัญชีนี้อยู่ในระบบ", Toast.LENGTH_SHORT).show();
//                            } else {
//
//                            }
                            Toast.makeText(LoginActivity.this, "อีเมลล์หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
                            passwordText.getText().clear();
                            rememberMe.setChecked(false);
                        }
                        dialog.dismiss();
                    }

                });
    }
}