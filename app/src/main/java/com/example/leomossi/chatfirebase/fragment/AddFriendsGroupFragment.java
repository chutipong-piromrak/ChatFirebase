package com.example.leomossi.chatfirebase.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.leomossi.chatfirebase.Group;
import com.example.leomossi.chatfirebase.R;
import com.example.leomossi.chatfirebase.Users;
import com.example.leomossi.chatfirebase.activity.Main2Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by nuuneoi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class AddFriendsGroupFragment extends Fragment {
    EditText inputEmailFriend;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    FirebaseUser myUser;
    ImageView imgFriend;
    TextView emailFriend;
    FrameLayout btnSearch;
    String email;

    String idGroup;
    String nameGroup;
    String photoUrlGroup;

    LinearLayout contentAddFriends;


    public AddFriendsGroupFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static AddFriendsGroupFragment newInstance() {
        AddFriendsGroupFragment fragment = new AddFriendsGroupFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_friends_group, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here

        myRef = FirebaseDatabase.getInstance().getReference();
        myUser = FirebaseAuth.getInstance().getCurrentUser();

        nameGroup = getActivity().getIntent().getExtras().get("nameGroup").toString();
        idGroup = getActivity().getIntent().getExtras().get("idGroup").toString();
        photoUrlGroup = getActivity().getIntent().getExtras().get("photoUrlGroup").toString();

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        inputEmailFriend = rootView.findViewById(R.id.search_email);
        btnSearch = rootView.findViewById(R.id.btn_search);
        emailFriend = rootView.findViewById(R.id.email_friend);
        contentAddFriends = rootView.findViewById(R.id.content_add_friends);
        imgFriend = rootView.findViewById(R.id.img_friend);



        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
//                    searchFriends();
                    testSearch();
                }
            }
        });

    }

    public boolean validate() {
        boolean valid = true;
        email = inputEmailFriend.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmailFriend.setError("อีเมลไม่ถูกต้อง");
            valid = false;
        } else {
            inputEmailFriend.setError(null);
        }
        return valid;
    }

    public  void testSearch() {
        myRef.child("Users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final Users users = child.getValue(Users.class);

                    if (users.getEmail().equals(myUser.getEmail())) {
                        Toast.makeText(getActivity(), "คุณอยู่ในกลุ่มแล้ว", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    else if (users.getEmail().equals(email)) {
                        contentAddFriends.setVisibility(View.VISIBLE);
                        emailFriend.setText(users.getEmail());
                        if (users.getPhotoUrl().equals("")) {
                            Glide.with(getActivity())
                                    .load(R.mipmap.ic_launcher_round)
                                    .into(imgFriend);
                        } else {
                            Glide.with(getActivity())
                                    .load(users.getPhotoUrl())
                                    .into(imgFriend);
                        }

                        contentAddFriends.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addFriends(users);
                            }
                        });

                        Log.d("testSearch", "true");
                        return;
                    }
                }
                contentAddFriends.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "ไม่พบผู้ใช้อีเมล " + email, Toast.LENGTH_SHORT).show();
                Log.d("testSearch", "false");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    public void searchFriends() {
//        myRef.child("Users").orderByChild("email").equalTo(email).addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                final Users users = dataSnapshot.getValue(Users.class);
//
//                if (users.getEmail().equals(myUser.getEmail())) {
//                    Toast.makeText(getActivity(), "คุณอยู่ในกลุ่มแล้ว", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                contentAddFriends.setVisibility(View.VISIBLE);
//                emailFriend.setText(users.getEmail());
//
//                if (!users.getPhotoUrl().equals("")) {
//                    Glide.with(getActivity())
//                            .load(users.getPhotoUrl())
//                            .into(imgFriend);
//                } else {
//                    Glide.with(getActivity())
//                            .load(R.mipmap.ic_launcher_round)
//                            .into(imgFriend);
//                }
//
//                contentAddFriends.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        addFriends(users);
//                    }
//                });
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }

    private void addFriends(final Users users) {
        AlertDialog.Builder builderAddFriends = new AlertDialog.Builder(getActivity());
        builderAddFriends.setMessage("ต้องการเพิ่ม " + users.getEmail() + " เข้ากลุ่มหรือไม่");
        builderAddFriends.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Group group = new Group();
                group.setNameGroup(nameGroup);
                group.setIdGroup(idGroup);
                group.setPhotoUrl(photoUrlGroup);

                myRef.child("Groups").child(users.getUid()).child(idGroup).setValue(group);
                getActivity().finish();
                Toast.makeText(getActivity(), "Add " + users.getEmail() + "to group success!", Toast.LENGTH_SHORT).show();
            }
        });

        builderAddFriends.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderAddFriends.show();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }
}
