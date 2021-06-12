package com.fuzailkhan.spectator_findmissingpeople;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.renderscript.RenderScript;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import static android.app.Activity.RESULT_OK;

public class Profile_Fragment_1 extends Fragment {
    TextView tvName, tvCountry, tvGender, tvAge, tvEmail;
    Button myPostsBtn,logOutBtn;
    ImageView imageView;
    ProgressBar progressBar;
    Uri imageUri;
    UploadTask uploadTask;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    public static final int PICK_IMAGE = 1;
    Profile_pic_model member;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment1,container,false);

        member = new Profile_pic_model();
        logOutBtn = view.findViewById(R.id.logoutBtn);
        myPostsBtn = view.findViewById(R.id.mypostsBtn);
        tvName = view.findViewById(R.id.tv_name);
        tvCountry = view.findViewById(R.id.tv_country);
        tvGender = view.findViewById(R.id.tv_gender);
        tvAge = view.findViewById(R.id.tv_age);
        tvEmail = view.findViewById(R.id.tv_email);
        imageView = view.findViewById(R.id.imgProfile);
        progressBar = view.findViewById(R.id.progressBar_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentid = user.getUid();
        documentReference = db.collection("user").document(currentid);
        storageReference = FirebaseStorage.getInstance().getReference("Profile images");
        databaseReference = database.getReference("All Users").child(currentid);



        imageView.setOnClickListener(view1 -> {
            Intent gallery = new Intent();
            gallery.setType("image/*");
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(gallery, "Select your profile"), PICK_IMAGE);


        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(),LoginActivity.class));
            }
        });

        myPostsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePostsFragment profilePostsFragment = new profilePostsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.profile_container, profilePostsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //String currentid = user.getUid();
        //DocumentReference reference;
        //FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        //reference = firestore.collection("user").document(currentid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String newName = snapshot.child("name").getValue().toString();
                String newCountry = snapshot.child("ccp").getValue().toString();
                String newAge = snapshot.child("age").getValue().toString();
                String newGender = snapshot.child("gender").getValue().toString();
                String newEmail = snapshot.child("email").getValue().toString();
                String profileImage = snapshot.child("url").getValue().toString();

                tvName.setText(newName);
                tvCountry.setText(newCountry);
                tvAge.setText(newAge);
                tvGender.setText(newGender);
                tvEmail.setText(newEmail);
                Picasso.get().load(profileImage).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
              /*  .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String newName = task.getResult().getString("name");
                            String newCountry = task.getResult().getString("ccp");
                            String newAge = task.getResult().getString("age");
                            String newGender = task.getResult().getString("gender");
                            String newEmail = task.getResult().getString("email");
                            String profileImage = task.getResult().getString("imageUrl");

                            tvName.setText(newName);
                            tvCountry.setText(newCountry);
                            tvAge.setText(newAge);
                            tvGender.setText(newGender);
                            tvEmail.setText(newEmail);
                            Picasso.get().load(profileImage).into(imageView);
                        }else {
                        }
                    }
                });*/
    }



    /*    reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Profile_pic_model profile_pic_model = new Profile_pic_model(uri.toString());
                        String modelId = reference.push().getKey();
                        reference.child("profile").setValue(profile_pic_model);
                        Toast.makeText(getContext(),"Uploaded Successfully",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressDialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),"Uploading Failed !!",Toast.LENGTH_SHORT).show();
            }
        });*/

    private  String getFileExt(Uri muri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(muri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_IMAGE || resultCode == RESULT_OK || data.getData() != null) {
                imageUri = data.getData();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentid = user.getUid();

                    progressBar.setVisibility(View.VISIBLE);
                    final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));
                    uploadTask = reference.putFile(imageUri);

                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return reference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();

                                Map<String, Object> profile = new HashMap<>();
                                profile.put("url", downloadUrl.toString());

                                member.setUrl(downloadUrl.toString());

                                databaseReference.updateChildren(profile)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressBar.setVisibility(View.INVISIBLE);
                                                Toast.makeText(getContext(), "Picture Uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                    });
            }


    }
}