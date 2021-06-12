package com.fuzailkhan.spectator_findmissingpeople;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    TextInputLayout emailEt, passwordEt,confirm_pass_Et,nameEt;
    ImageView imageView;
    Uri imageUri;
    UploadTask uploadTask;
    private static final int PICK_IMAGE = 1;
    RadioGroup reg_radioGroup;
    DatePicker reg_datePicker;
    CountryCodePicker countryCodePicker;
    Button regBtn;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;
    UserHelperClass member;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        member = new UserHelperClass();
        emailEt = findViewById(R.id.register_email_et);
        passwordEt = findViewById(R.id.register_password_et);
        confirm_pass_Et = findViewById(R.id.register_ConfirmPassword_et);
        nameEt = findViewById(R.id.Register_Name_et);
        reg_datePicker = findViewById(R.id.register_datePicker);
        reg_radioGroup = findViewById(R.id.register_radioGroup);
        countryCodePicker = findViewById(R.id.Register_countryPicker);
        imageView = findViewById(R.id.register_imgProfile);
        regBtn = findViewById(R.id.btnGetRegister);
        progressBar = findViewById(R.id.progressBar_register);
        mAuth = FirebaseAuth.getInstance();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE);

            }
        });



        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String email = emailEt.getEditText().getText().toString();
                String password = passwordEt.getEditText().getText().toString();
                String confirm_password = confirm_pass_Et.getEditText().getText().toString();
                String name = nameEt.getEditText().getText().toString();
                String ccp = countryCodePicker.getSelectedCountryName();
                String gender = reg_radioGroup.toString();


                //RadioGroup
                reg_radioGroup = (RadioGroup) findViewById(R.id.register_radioGroup);
                int checkedId = reg_radioGroup.getCheckedRadioButtonId();

                if(checkedId == R.id.radioBtnMale){
                    gender = "Male";
                }
                else if(checkedId == R.id.radioBtnFemale){
                    gender = "Female";
                }
                else if(checkedId == R.id.radioBtnOther){
                    gender = "Other";
                }
                else {
                    Toast.makeText(getBaseContext(),"Select Gender",Toast.LENGTH_LONG).show();
                }



                int day = reg_datePicker.getDayOfMonth();
                int month = reg_datePicker.getMonth();
                int year = reg_datePicker.getYear();

                String age = day+"/"+(month+1)+"/"+year;


                if(validateEmail() | validatePassword() | validateConfirmPass() | validateName() | validateGender() | validateAge() | imageUri != null ) {

                    if (password.equals(confirm_password)){
                        progressBar.setVisibility(View.VISIBLE);


                        String finalGender = gender;
                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()){
                                    progressBar.setVisibility(View.INVISIBLE);

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    assert user != null;
                                    String uid = user.getUid();

                                    documentReference = db.collection("user").document(uid);
                                    storageReference = FirebaseStorage.getInstance().getReference("Profile images");
                                    databaseReference = database.getReference("All Users");

                                    uploadTask = storageReference.putFile(imageUri);
                                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if (!task.isSuccessful()){
                                                throw task.getException();
                                            }
                                            return storageReference.getDownloadUrl();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()){
                                                Uri downloadUri = task.getResult();
                                                Map<String,String> profile = new HashMap<>();
                                                profile.put("name",name);
                                                profile.put("email",email);
                                                profile.put("ccp",ccp);
                                                profile.put("age",age);
                                                profile.put("gender", finalGender);
                                                profile.put("uid",uid);
                                                profile.put("imageUrl",downloadUri.toString());

                                                member.setName(name);
                                                member.setEmail(email);
                                                member.setCcp(ccp);
                                                member.setAge(age);
                                                member.setGender(finalGender);
                                                member.setUid(uid);
                                                member.setUrl(downloadUri.toString());

                                                databaseReference.child(uid).setValue(member);

                                                documentReference.set(profile)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                progressBar.setVisibility(View.INVISIBLE);

                                                            }
                                                        });


                                                Intent intent = new Intent(RegisterActivity.this,DashboardActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,"Error:"+error,Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                    else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this,"Password & Confirm Password doesn't match",Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(RegisterActivity.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private boolean validateEmail() {
        String val = Objects.requireNonNull(emailEt.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            emailEt.setError("Field can not be empty");
            return false;
        } else {
            emailEt.setError(null);
            emailEt.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = Objects.requireNonNull(passwordEt.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            passwordEt.setError("Field can not be empty");
            return false;
        } else {
            passwordEt.setError(null);
            passwordEt.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateConfirmPass() {
        String val = Objects.requireNonNull(confirm_pass_Et.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            confirm_pass_Et.setError("Field can not be empty");
            return false;
        } else {
            confirm_pass_Et.setError(null);
            confirm_pass_Et.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateName() {
        String val = Objects.requireNonNull(nameEt.getEditText()).getText().toString().trim();
        if (val.isEmpty()) {
            nameEt.setError("Field can not be empty");
            return false;
        } else {
            nameEt.setError(null);
            nameEt.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateAge(){
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = reg_datePicker.getYear();
        int isAgeValid = currentYear - userAge;

        if (isAgeValid > 120){
            Toast.makeText(this,"Enter Valid Age",Toast.LENGTH_SHORT).show();
            return false;
        }
        else
            return true;
    }

    private boolean validateGender() {
        if (reg_radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Intent intent = new Intent(RegisterActivity.this,DashboardActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private  String getFileExt(Uri muri){
        ContentResolver contentResolver = RegisterActivity.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(muri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == PICK_IMAGE || resultCode == RESULT_OK||
                    data != null ||data.getData() != null){
                imageUri = data.getData();

                Picasso.get().load(imageUri).into(imageView);
            }
        }catch (Exception e){
            Toast.makeText(RegisterActivity.this,"Error"+e,Toast.LENGTH_SHORT).show();

        }

    }
}