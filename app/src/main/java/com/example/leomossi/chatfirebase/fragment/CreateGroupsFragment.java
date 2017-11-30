package com.example.leomossi.chatfirebase.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.leomossi.chatfirebase.Group;
import com.example.leomossi.chatfirebase.R;
import com.example.leomossi.chatfirebase.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by nuuneoi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class CreateGroupsFragment extends Fragment {

    DatabaseReference myRef;
    FirebaseUser user;

    EditText inputCreateGroups;
    FrameLayout btnCreateGroups;
    ImageView imgCreateGroups;

    Bitmap bitmap;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference mountainsRef;
    String idGroup;
    String photoUrl="";

    public static final int REQUEST_GALLERY = 1;


    public CreateGroupsFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static CreateGroupsFragment newInstance() {
        CreateGroupsFragment fragment = new CreateGroupsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_create_groups, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        idGroup = myRef.push().getKey();
        mountainsRef = storageRef.child("imagesGroup/" + idGroup + "/photoUrl");

    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        inputCreateGroups = rootView.findViewById(R.id.input_create_groups);
        btnCreateGroups = rootView.findViewById(R.id.btn_create_groups);
        imgCreateGroups = rootView.findViewById(R.id.img_create_groups);

        imgCreateGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent
                        , "Select Picture"), REQUEST_GALLERY);
            }
        });


        btnCreateGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroups();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode
            , Intent data) {
        if (requestCode == REQUEST_GALLERY && resultCode == getActivity().RESULT_OK) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                imgCreateGroups.setImageBitmap(bitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] dataA = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(dataA);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d("uploadimg", "fail" + exception.getMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        photoUrl = downloadUrl.toString();
                    }
                });


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createGroups() {
        final String nameGroup = inputCreateGroups.getText().toString();
        if (nameGroup.isEmpty()) {
            inputCreateGroups.setError("กรุณาใส่ชื่อกลุ่ม");
            return;
        }

        AlertDialog.Builder builderCreateGroups = new AlertDialog.Builder(getActivity());
        builderCreateGroups.setMessage("สร้างกลุ่มชื่อ " + nameGroup + " หรือไม่?");
        builderCreateGroups.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Group group = new Group();
                group.setNameGroup(nameGroup);
                group.setIdGroup(idGroup);
                group.setPhotoUrl(photoUrl);
                myRef.child("Groups").child(user.getUid()).child(idGroup).setValue(group);
                Toast.makeText(getActivity(), "Create group success!!", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });

        builderCreateGroups.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderCreateGroups.show();


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
