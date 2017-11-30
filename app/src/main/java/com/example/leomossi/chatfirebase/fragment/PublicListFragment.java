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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.leomossi.chatfirebase.Group;
import com.example.leomossi.chatfirebase.R;
import com.example.leomossi.chatfirebase.activity.ChatGroupRoomActivity;
import com.example.leomossi.chatfirebase.activity.PublicRoomActivity;
import com.example.leomossi.chatfirebase.adapter.GroupAdapter;
import com.example.leomossi.chatfirebase.adapter.PublicAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

/**
 * Created by nuuneoi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class PublicListFragment extends Fragment {

    DatabaseReference myRef;
    FirebaseUser user;
    PublicAdapter adapter;
    ListView listView;
    ArrayList<String> listPublic;

    ProgressBar progressBar;



    public PublicListFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static PublicListFragment newInstance() {
        PublicListFragment fragment = new PublicListFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_public_list, container, false);
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
        listView = rootView.findViewById(R.id.lv_public_list);
        progressBar = rootView.findViewById(R.id.progress_bar);
        listPublic = new ArrayList<>();
        adapter = new PublicAdapter(listPublic);
        listView.setAdapter(adapter);
//        progressBar.setVisibility(View.GONE);
        testGetAllPublicRoom();
//        getAllPublicRoom();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PublicRoomActivity.class);
                intent.putExtra("nameGroup", listPublic.get(position));
                startActivity(intent);
            }
        });

    }

    private void testGetAllPublicRoom() {
        myRef.child("Public").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listPublic.clear();
                boolean havePublicRoom = false;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    havePublicRoom = true;
                    String room = child.getValue(String.class);
                    listPublic.add(room);
                    adapter.notifyDataSetChanged();
                }
                if (!havePublicRoom) {
                    Toast.makeText(getActivity(), "No Public Room", Toast.LENGTH_SHORT).show();
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

    private void getAllPublicRoom() {
        myRef.child("Public").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Group group = dataSnapshot.getValue(Group.class);
                String room = dataSnapshot.getValue(String.class);
                listPublic.add(room);
                adapter.notifyDataSetChanged();
                Log.d("groups", dataSnapshot.getValue(String.class));

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
