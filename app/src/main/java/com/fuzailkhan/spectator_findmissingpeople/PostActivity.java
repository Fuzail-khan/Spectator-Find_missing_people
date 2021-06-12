package com.fuzailkhan.spectator_findmissingpeople;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    ImageButton imageButton;
    ProgressBar progressBar;
    private Uri selectedUri;
    private static final int PICK_FILE = 1;
    UploadTask uploadTask;
    TextView tvPostName,tvPostGender,tvPostAge,tvPostEyesColor,tvPostSkinTone,tvPostHeight,tvPostWeight,tvPostMissingSince,tvPostNationality;
    TextInputLayout postName,postAge,postEyesColor,postSkinTone,postHeight,postWeight;
    DatePicker dpPostMissingSince;
    RadioGroup rbPostGender;
    CountryCodePicker countryCodePicker;
    Button postBtn;
    String type,name,gender,missingSince,url;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db1,db2;
    MediaController mediaController;
    DocumentReference documentReference;
    Postmember postmember;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postmember = new Postmember();
        mediaController = new MediaController(this);
        progressBar = findViewById(R.id.pbPost);
        imageButton = findViewById(R.id.ibPost);
        postBtn = findViewById(R.id.btnPost);
        postName = findViewById(R.id.fieldPostName);
        postAge = findViewById(R.id.fieldPostAge);
        postEyesColor = findViewById(R.id.fieldPostEyesColor);
        postSkinTone = findViewById(R.id.fieldPostSkinTone);
        postHeight = findViewById(R.id.fieldPostHeight);
        postWeight = findViewById(R.id.fieldPostWeight);
        dpPostMissingSince = findViewById(R.id.datePickerMissingSince);
        rbPostGender = findViewById(R.id.radioGroupPostGender);
        countryCodePicker = findViewById(R.id.countryPicker);



        storageReference = FirebaseStorage.getInstance().getReference("User posts");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All posts");

        postBtn.setOnClickListener((view) -> {Dopost();});

        imageButton.setOnClickListener((view) -> {chooseImage();});

    }
    private boolean validateName() {
        String val = Objects.requireNonNull(postName.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            postName.setError("Field can not be empty");
            return false;
        } else {
            postName.setError(null);
            postName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateAge() {
        String val = postAge.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            postAge.setError("Field can not be empty");
            return false;
        }else {
            postAge.setError(null);
            postAge.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEyesColor() {
        String val = Objects.requireNonNull(postEyesColor.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            postEyesColor.setError("Field can not be empty");
            return false;
        } else {
            postEyesColor.setError(null);
            postEyesColor.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateSkinTone() {
        String val = Objects.requireNonNull(postSkinTone.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            postSkinTone.setError("Field can not be empty");
            return false;
        } else {
            postSkinTone.setError(null);
            postSkinTone.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateHeight() {
        String val = postHeight.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            postHeight.setError("Field can not be empty");
            return false;
        }else {
            postHeight.setError(null);
            postHeight.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateWeight() {
        String val = postWeight.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            postWeight.setError("Field can not be empty");
            return false;
        }else {
            postWeight.setError(null);
            postWeight.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateGender() {
        if (rbPostGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateMissingSince(){
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = dpPostMissingSince.getYear();
        int isAgeValid = currentYear - userAge;

        if (isAgeValid > 120){
            Toast.makeText(this,"Enter Valid Age",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    private boolean validateImageUri() {
        if (selectedUri == null) {
            Toast.makeText(this, "Please Select a Picture", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }



    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE || resultCode == RESULT_OK ||
        data != null || data.getData() != null){

            selectedUri = data.getData();
            if (selectedUri.toString().contains("image")){
                Picasso.get().load(selectedUri).into(imageButton);
                imageButton.setVisibility(View.VISIBLE);
                type = "iv";
            }else{
                Toast.makeText(this,"Please select a picture",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart(){
        super.onStart();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("All Users");
        reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 name = dataSnapshot.child("name").getValue().toString();
                 url = dataSnapshot.child("url").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

}

        void Dopost(){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String currentuid = user.getUid();

            String postname = postName.getEditText().getText().toString();
            String age = postAge.getEditText().getText().toString();
            String eyescolor = postEyesColor.getEditText().getText().toString();
            String skintone = postSkinTone.getEditText().getText().toString();
            String height = postHeight.getEditText().getText().toString();
            String weight = postWeight.getEditText().getText().toString();
            String ccp = countryCodePicker.getSelectedCountryName();
            String gender = rbPostGender.toString();


            rbPostGender = (RadioGroup) findViewById(R.id.radioGroupPostGender);
            int checkedId = rbPostGender.getCheckedRadioButtonId();

            if(checkedId == R.id.radioBtnMale){
                gender = "Male";
            }
            else if(checkedId == R.id.radioBtnFemale){
                gender = "Female";
            }
            else if(checkedId == R.id.radioBtnOther){
                gender = "Other";
            }


            int day = dpPostMissingSince.getDayOfMonth();
            int month = dpPostMissingSince.getMonth();
            int year = dpPostMissingSince.getYear();
            missingSince = day+"/"+(month+1)+"/"+year;

            Calendar cdate  = Calendar.getInstance();
            SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String savedate = currentdate.format(cdate.getTime());

            Calendar ctime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String savetime = currentTime.format(ctime.getTime());

            String time = savedate + " at " + savetime;

            if (!validateName() | !validateAge() | !validateEyesColor() |
                    !validateSkinTone() | !validateHeight() | !validateWeight() |
                    !validateGender() | !validateMissingSince() | !validateImageUri()){
            }
            else {
                progressBar.setVisibility(View.VISIBLE);

                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                String currentUid = user.getUid();
                documentReference = db.collection("userPost").document(currentUid);
                final StorageReference reference = storageReference.child(System.currentTimeMillis() +".");
                uploadTask = reference.putFile(selectedUri);

                String finalGender = gender;
                Task<Uri> uriTask = uploadTask.continueWithTask((task) -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        if (type.equals("iv")) {
                            postmember.setName(name);
                            postmember.setPosturi(downloadUri.toString());
                            postmember.setTime(time);
                            postmember.setUid(currentuid);
                            postmember.setUrl(url);
                            postmember.setPostname(postname);
                            postmember.setAge(age);
                            postmember.setEyescolor(eyescolor);
                            postmember.setSkintone(skintone);
                            postmember.setHeight(height);
                            postmember.setWeight(weight);
                            postmember.setCcp(ccp);
                            postmember.setRbPostGender(finalGender);
                            postmember.setMissingSince(missingSince);
                            postmember.setType("iv");

                            //for image
                            String id = db1.push().getKey();
                            db1.child(id).setValue(postmember);

                            //for both
                            String id1 = db2.push().getKey();
                            db2.child(id1).setValue(postmember);

                            documentReference.set(postmember);

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }
                           }
                });
            }
                }
}