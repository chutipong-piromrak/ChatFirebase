package com.example.leomossi.chatfirebase.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.leomossi.chatfirebase.R;
import com.example.leomossi.chatfirebase.fragment.ChatGroupRoomFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChatGroupRoomActivity extends AppCompatActivity {

    String idGroup;
    String nameGroup;
    String photoUrlGroup;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group_room);

        idGroup = getIntent().getExtras().get("idGroup").toString();
        nameGroup = getIntent().getExtras().get("nameGroup").toString();
        photoUrlGroup = getIntent().getExtras().get("photoUrlGroup").toString();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_chat_group_room, ChatGroupRoomFragment.newInstance())
                .commit();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(nameGroup);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_friends_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.add_friends_groups:
                intent = new Intent(this, AddFriendsGroupActivity.class);
                intent.putExtra("idGroup", idGroup);
                intent.putExtra("nameGroup", nameGroup);
                intent.putExtra("photoUrlGroup", photoUrlGroup);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
