package com.example.leomossi.chatfirebase.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.leomossi.chatfirebase.R;
import com.example.leomossi.chatfirebase.fragment.CreateGroupsFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CreateGroupsActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_groups);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_create_groups, CreateGroupsFragment.newInstance())
                .commit();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("Create Groups");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
