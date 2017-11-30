package com.example.leomossi.chatfirebase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leomossi.chatfirebase.Group;
import com.example.leomossi.chatfirebase.R;
import com.example.leomossi.chatfirebase.Users;
import com.example.leomossi.chatfirebase.activity.ChatGroupRoomActivity;
import com.example.leomossi.chatfirebase.activity.ChatRoomActivity;
import com.example.leomossi.chatfirebase.adapter.FriendsAdapter;
import com.example.leomossi.chatfirebase.adapter.GroupAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by nuuneoi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class FriendsListFragment extends Fragment {

    DatabaseReference myRef;
    ArrayList<Users> listFriends;
    ListView listViewFriends;
    FriendsAdapter friendsAdapter;

    FirebaseUser user;
    String myId;

    ProgressBar progressBar;


    public FriendsListFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static FriendsListFragment newInstance() {
        FriendsListFragment fragment = new FriendsListFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        //Friends
        listViewFriends = rootView.findViewById(R.id.lv_friends_list);
        progressBar = rootView.findViewById(R.id.progress_bar);
        listFriends = new ArrayList<>();
        friendsAdapter = new FriendsAdapter(listFriends);
        listViewFriends.setAdapter(friendsAdapter);
//        progressBar.setVisibility(View.GONE);

        testGetAllFriends();
//        getAllFriends();
        //Friends
        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)    {
                Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
                intent.putExtra("keyRoom", listFriends.get(position).getIdRoom());
                intent.putExtra("nameFriend", listFriends.get(position).getName());
                intent.putExtra("photoUrlFriend", listFriends.get(position).getPhotoUrl());
                Log.d("keyRoom",listFriends.get(position).getIdRoom()+"@@@");
                startActivity(intent);
            }
        });

        listViewFriends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String email = listFriends.get(position).getEmail();
                myRef.child("Relationship").child(user.getUid()).child(listFriends.get(position).getUid()).removeValue();
                listFriends.remove(position);
                friendsAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "remove "+ email + " success!!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void testGetAllFriends() {

        myRef.child("Relationship").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("checkFriends","have friends");
                listFriends.clear();
                boolean haveFriends = false;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    haveFriends = true;
                    final Users relUsers = child.getValue(Users.class);
                    Log.d("checkFriends","have friends " +relUsers.getEmail());
                    Query query = myRef.child("Users").orderByChild("uid").equalTo(relUsers.getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Users uerUser = child.getValue(Users.class);
                                relUsers.setPhotoUrl(uerUser.getPhotoUrl());
                                relUsers.setName(uerUser.getName());
                                listFriends.add(relUsers);
                                friendsAdapter.notifyDataSetChanged();
                            }
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                if (!haveFriends) {
                    Toast.makeText(getActivity(), "No Friends", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void getAllFriends() {
        myRef.child("Relationship").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final Users reUsers = dataSnapshot.getValue(Users.class);

                Query query = myRef.child("Users").orderByChild("uid").equalTo(reUsers.getUid());
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Users uUser = dataSnapshot.getValue(Users.class);
                        reUsers.setName(uUser.getName());
                        reUsers.setPhotoUrl(uUser.getPhotoUrl());
                        listFriends.add(reUsers);
                        friendsAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
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
