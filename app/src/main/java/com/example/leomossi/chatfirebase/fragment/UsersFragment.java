package com.example.leomossi.chatfirebase.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.leomossi.chatfirebase.R;
import com.example.leomossi.chatfirebase.Users;
import com.example.leomossi.chatfirebase.adapter.FriendsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by nuuneoi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class UsersFragment extends Fragment {

    DatabaseReference myRef;
    FirebaseUser user;

    ListView listView;
    FriendsAdapter adapter;
    ArrayList<Users> listUsers;

    ProgressBar progressBar;


    public UsersFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static UsersFragment newInstance() {
        UsersFragment fragment = new UsersFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_users_list, container, false);
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

        listView = rootView.findViewById(R.id.lv_users);
        progressBar = rootView.findViewById(R.id.progress_bar);
        listUsers = new ArrayList<>();
        adapter = new FriendsAdapter(listUsers);
        listView.setAdapter(adapter);

        getAllUsers();
    }

    private void getAllUsers() {
        myRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean haveUsers = false;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    haveUsers = true;
                    Users users = child.getValue(Users.class);
                    listUsers.add(users);
                    adapter.notifyDataSetChanged();
                }
                if (!haveUsers) {
                    Toast.makeText(getActivity(), "No Users", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.GONE);
                }

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
