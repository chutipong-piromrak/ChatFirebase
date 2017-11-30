package com.example.leomossi.chatfirebase.activity;

import android.support.v7.app.ActionBar;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.example.leomossi.chatfirebase.R;
import com.example.leomossi.chatfirebase.fragment.ChatRoomFragment;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChatRoomActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.chat_room_container, ChatRoomFragment.newInstance())
                .commit();

        String nameFriend = getIntent().getExtras().get("nameFriend").toString();
        String photoUrlFriend = getIntent().getExtras().get("photoUrlFriend").toString();

        CircleImageView imageView = new CircleImageView(this);
        Glide.with(this).load(photoUrlFriend).into(imageView);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME |
//                ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_USE_LOGO);
//        getSupportActionBar().setIcon(R.mipmap.ic_delete_sweep_black_24dp);
        
        setTitle(nameFriend);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
