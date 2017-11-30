package com.example.leomossi.chatfirebase.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.leomossi.chatfirebase.R;
import com.example.leomossi.chatfirebase.Users;
import com.example.leomossi.chatfirebase.activity.Main2Activity;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by nuuneoi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class ProfileFragment extends Fragment {

    DatabaseReference myRef;
    FirebaseUser user;

    LinearLayout changeName;
    LinearLayout changePassword;
    AppCompatTextView nameProfile;
    AppCompatTextView emailProfile;
    ImageView changeImgeProfile;
    Bitmap bitmap;
    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference mountainsRef;
    ProgressDialog progress;

    public static final int REQUEST_GALLERY = 1;


    public ProfileFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        initInstances(rootView, savedInstanceState);
        return rootView;
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
        user = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mountainsRef = storageRef.child("imagesProfile/" + user.getUid() + "/photoUrl");
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        changeName = rootView.findViewById(R.id.change_name);
        changePassword = rootView.findViewById(R.id.change_password);
        nameProfile = rootView.findViewById(R.id.name_profile);
        emailProfile = rootView.findViewById(R.id.email_profile);
        changeImgeProfile = rootView.findViewById(R.id.change_img_profile);

        if (user != null) {
            nameProfile.setText(user.getDisplayName());
            emailProfile.setText(user.getEmail());

            try {
                Glide.with(getActivity())
                        .load(user.getPhotoUrl().toString())
                        .into(changeImgeProfile);
            } catch (NullPointerException ex) {
            }

        }

        changeImgeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent
                        , "Select Picture"), REQUEST_GALLERY);
            }
        });

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chaneName();
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode
            , Intent data) {
        if (requestCode == REQUEST_GALLERY && resultCode == getActivity().RESULT_OK) {
            Uri uri = data.getData();
            try {
                progress = ProgressDialog.show(getActivity(), "Please wait",
                        "กำลังอัพเดทรูปภาพประจำตัว", true);
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                changeImgeProfile.setImageBitmap(bitmap);

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
                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(downloadUrl.toString()))
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Users users = new Users();
                                            users.setName(user.getDisplayName());
                                            users.setEmail(user.getEmail());
                                            users.setIdRoom("");
                                            users.setPhotoUrl(downloadUrl.toString());
                                            users.setUid(user.getUid());
                                            myRef.child(user.getUid()).setValue(users);
                                            progress.dismiss();
                                            Toast.makeText(getActivity(), "เปลี่ยนรูปภาพประจำตัวสำเร็จ", Toast.LENGTH_SHORT).show();
                                            Log.d("uploadimg", "success");
//
                                        }
                                    }
                                });
                    }
                });


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void changePassword() {
        final EditText input = new EditText(getActivity());
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        AlertDialog.Builder builderChangePassword = new AlertDialog.Builder(getActivity());
        builderChangePassword.setTitle("Type your new password");
        builderChangePassword.setView(input);
        builderChangePassword.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();
                if (!password.isEmpty()) {
                    user.updatePassword(password)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "เปลี่ยนรหัสผ่านสำเร็จ", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "กรุณาใส่รหัสผ่าน", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builderChangePassword.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderChangePassword.show();
    }

    private void chaneName() {
        final EditText input = new EditText(getActivity());
        input.setText(user.getDisplayName());
        input.setSelection(user.getDisplayName().length());
        AlertDialog.Builder builderChangeName = new AlertDialog.Builder(getActivity());
        builderChangeName.setTitle("Type your name");
        builderChangeName.setView(input);
        builderChangeName.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = input.getText().toString();

                if (!name.isEmpty()) {
                    if (user != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Users users = new Users();
                                            users.setName(name);
                                            users.setEmail(user.getEmail());
                                            users.setUid(user.getUid());
                                            users.setIdRoom("null");
                                            try {
                                                users.setPhotoUrl(user.getPhotoUrl().toString());
                                            } catch (NullPointerException ex) {
                                                users.setPhotoUrl("");
                                            }

                                            myRef.child(user.getUid()).setValue(users);
                                            nameProfile.setText(name);
                                            Toast.makeText(getActivity(), "เปลี่ยนชื่อสำเร็จ", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                } else {
                    Toast.makeText(getActivity(), "กรุณาใส่ชื่อ", Toast.LENGTH_SHORT).show();
                }

            }
        });

        builderChangeName.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderChangeName.show();

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
