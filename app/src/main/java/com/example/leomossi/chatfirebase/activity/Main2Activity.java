package com.example.leomossi.chatfirebase.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.leomossi.chatfirebase.R;
import com.example.leomossi.chatfirebase.Users;
import com.example.leomossi.chatfirebase.fragment.FriendsListFragment;
import com.example.leomossi.chatfirebase.fragment.GroupsListFragment;
import com.example.leomossi.chatfirebase.fragment.PublicListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,BottomNavigationView.OnNavigationItemSelectedListener {

    TextView nameProfile;
    TextView emailProfile;
    ImageView imgProfile;
    DatabaseReference myRef;
    FirebaseUser user;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initBottomNavigationView();
        initNavigationView();
        updateImgProfile();

    }

    private void updateImgProfile() {
        if (user.getPhotoUrl() != null) {
            Glide.with(Main2Activity.this)
                    .load(user.getPhotoUrl())
                    .into(imgProfile);
        } else {
            Glide.with(Main2Activity.this)
                    .load(R.mipmap.ic_launcher_round)
                    .into(imgProfile);
        }

        myRef.child("Users").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Glide.with(Main2Activity.this)
                        .load(user.getPhotoUrl())
                        .into(imgProfile);

                nameProfile.setText(user.getDisplayName());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        nameProfile = header.findViewById(R.id.name_profile);
        emailProfile = header.findViewById(R.id.email_profile);
        imgProfile = header.findViewById(R.id.img_profile);
        nameProfile.setText(user.getDisplayName());
        emailProfile.setText(user.getEmail());
    }

    private void initBottomNavigationView() {
        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
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

                    case R.id.navigation_public:
                        setTitle("Public");
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content_container, PublicListFragment.newInstance())
                                .commit();
                        return true;
                }
                return false;
            }
        };

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_friends);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_users) {
            Intent intent = new Intent(this, UsersActivity.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_profile) {
            Intent intent = new Intent(Main2Activity.this, ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builderLogOut = new AlertDialog.Builder(this);
            builderLogOut.setMessage("คุณต้องการออกจากระบบใช่หรือไม่");
            builderLogOut.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences prefs = getBaseContext()
                            .getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(Main2Activity.this, "คุณได้ออกจากระบบ", Toast.LENGTH_SHORT).show();
                }
            });
            builderLogOut.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builderLogOut.show();

        } else if (id == R.id.nav_del_account) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("คุณต้องการลบบัญชีถาวรใช่หรือไม่");
            builder.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    myRef.child("Relationship").child(user.getUid()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Users friends = dataSnapshot.getValue(Users.class);
                            myRef.child("Relationship").child(friends.getUid()).child(user.getUid()).removeValue();
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    myRef.child("Relationship").child(user.getUid()).removeValue();
                    myRef.child("Users").child(user.getUid()).removeValue();

                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        SharedPreferences prefs = getBaseContext()
                                                .getSharedPreferences("RememberMe", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.clear();
                                        editor.apply();
                                        Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                        Toast.makeText(Main2Activity.this, "ลบขัญชีสำเร็จ", Toast.LENGTH_SHORT).show();
                                        Log.d("deleteUser", "User account deleted.");
                                    }
                                }
                            });
                }
            });
            builder.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
