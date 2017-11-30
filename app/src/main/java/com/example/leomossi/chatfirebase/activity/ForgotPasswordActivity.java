package com.example.leomossi.chatfirebase.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.leomossi.chatfirebase.R;
import com.example.leomossi.chatfirebase.fragment.ForgotPasswordFragment;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();

    }

    public void init() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_forgot_password, ForgotPasswordFragment.newInstance())
                .commit();

//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
