package com.example.leomossi.chatfirebase.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.leomossi.chatfirebase.Group;
import com.example.leomossi.chatfirebase.R;
import com.example.leomossi.chatfirebase.activity.ChatGroupRoomActivity;
import com.example.leomossi.chatfirebase.adapter.GroupAdapter;
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
public class GroupsListFragment extends Fragment {

    DatabaseReference myRef;
    FirebaseUser user;
    GroupAdapter adapter;
    ListView listView;
    ArrayList<Group> listGroups;
    ProgressBar progressBar;


    public GroupsListFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static GroupsListFragment newInstance() {
        GroupsListFragment fragment = new GroupsListFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_groups_list, container, false);
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
        listView = rootView.findViewById(R.id.lv_groups);
        progressBar = rootView.findViewById(R.id.progress_bar);
        listGroups = new ArrayList<>();
        adapter = new GroupAdapter(listGroups);
        listView.setAdapter(adapter);
//        progressBar.setVisibility(View.GONE);

        testGetAllGroups();
//        getAllGroups();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatGroupRoomActivity.class);
                intent.putExtra("idGroup", listGroups.get(position).getIdGroup());
                intent.putExtra("nameGroup", listGroups.get(position).getNameGroup());
                intent.putExtra("photoUrlGroup", listGroups.get(position).getPhotoUrl());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String nameGroup = listGroups.get(position).getNameGroup();
                myRef.child("Groups").child(user.getUid()).child(listGroups.get(position).getIdGroup()).removeValue();
                listGroups.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "remove " + nameGroup + " success!!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void testGetAllGroups() {
        myRef.child("Groups").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listGroups.clear();
                boolean haveGroup = false;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    haveGroup = true;
                    Group group = child.getValue(Group.class);
                    listGroups.add(group);
                    adapter.notifyDataSetChanged();
                    Log.d("groups", dataSnapshot.getKey());
                }
                if (!haveGroup) {
                    Toast.makeText(getActivity(), "No Groups", Toast.LENGTH_SHORT).show();
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


    private void getAllGroups() {
        myRef.child("Groups").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Group group = dataSnapshot.getValue(Group.class);
                listGroups.add(group);
                adapter.notifyDataSetChanged();
                Log.d("groups", dataSnapshot.getKey());

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
