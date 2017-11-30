package com.example.leomossi.chatfirebase.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leomossi.chatfirebase.R;
import com.example.leomossi.chatfirebase.Users;
import com.example.leomossi.chatfirebase.fragment.FriendsListFragment;
import com.example.leomossi.chatfirebase.fragment.GroupsListFragment;
import com.example.leomossi.chatfirebase.fragment.SettingFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    DatabaseReference myRef;
    FirebaseUser user;

    TextView nameProfile;
    ImageView imgProfile;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_friends:
                    setTitle("Friends");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_container, FriendsListFragment.newInstance())
                            .commit();
                    return true;

                case R.id.navigation_group:
                    setTitle("Groups");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_container, GroupsListFragment.newInstance())
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init(){
        myRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_friends);

        nameProfile = (TextView) findViewById(R.id.name_profile);
        imgProfile = (ImageView) findViewById(R.id.img_profile);

        if (user != null) {
            nameProfile.setText(user.getDisplayName());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.add_friends:
                intent = new Intent(this, AddFriendsActivity.class);
                startActivity(intent);
                return true;

            case R.id.create_groups:
                intent = new Intent(this, CreateGroupsActivity.class);
                startActivity(intent);
                return true;

//            case R.id.log_out:
//
//                AlertDialog.Builder builderLogOut = new AlertDialog.Builder(this);
//                builderLogOut.setMessage("คุณต้องการออกจากระบบใช่หรือไม่");
//                builderLogOut.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        SharedPreferences prefs = getBaseContext()
//                                .getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.clear();
//                        editor.apply();
//                        FirebaseAuth.getInstance().signOut();
//                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                        Toast.makeText(MainActivity.this, "คุณได้ออกจากระบบ", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                builderLogOut.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builderLogOut.show();
//                return true;
//
//            case R.id.delete_account:
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage("คุณต้องการลบบัญชีถาวรใช่หรือไม่");
//                builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        myRef.child("Relationship").child(user.getUid()).addChildEventListener(new ChildEventListener() {
//                            @Override
//                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                                Users friends = dataSnapshot.getValue(Users.class);
//                                myRef.child("Relationship").child(friends.getUid()).child(user.getUid()).removeValue();
//                            }
//
//                            @Override
//                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                            }
//
//                            @Override
//                            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                            }
//
//                            @Override
//                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//
//                        myRef.child("Relationship").child(user.getUid()).removeValue();
//                        myRef.child("Users").child(user.getUid()).removeValue();
//
//                        user.delete()
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            SharedPreferences prefs = getBaseContext()
//                                                    .getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
//                                            SharedPreferences.Editor editor = prefs.edit();
//                                            editor.clear();
//                                            editor.apply();
//                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                                            startActivity(intent);
//                                            finish();
//                                            Toast.makeText(MainActivity.this, "ลบขัญชีสำเร็จ", Toast.LENGTH_SHORT).show();
//                                            Log.d("deleteUser", "User account deleted.");
//                                        }
//                                    }
//                                });
//                    }
//                });
//                builder.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                builder.show();
//
//                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
