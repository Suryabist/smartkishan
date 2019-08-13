package com.pathibharatechnology.smartkishan.user_profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.SupportActionBarInitializer;
import com.pathibharatechnology.smartkishan.login_and_signup.LoginFragment;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileActivity extends AppCompatActivity {

    TextInputEditText fullnameEdittext, emailEdittext, mobileEdittext;
    TextView userNameTextview, changePasswordTextview;
    CircleImageView userImageCircleImageView;
    Button updateButton;

    TextInputLayout emailTextlayout;

    String userFullName, userEmail, userMobile, userName, imageUrl, joinedTime;

    Uri uri;
    Bitmap bitmap;
    StorageReference storageReference;
    UserDTO user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportActionBarInitializer.setUpSupportActionBar(getSupportActionBar(), "Update profile", true);


        fullnameEdittext = findViewById(R.id.fullNameEditTextID);
        emailEdittext = findViewById(R.id.emailEditTextID);
        mobileEdittext = findViewById(R.id.mobileEditTextID);
        userNameTextview = findViewById(R.id.userNameTextViewID);
        userImageCircleImageView = findViewById(R.id.userProfileImageID);
        updateButton = findViewById(R.id.updateUserProfileID);
        changePasswordTextview = findViewById(R.id.changePasswordTextViewID);

        emailTextlayout = findViewById(R.id.emailID);

        userImageCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        getUserDetails();

        changePasswordTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Snackbar.make(getWindow().getDecorView().getRootView(), "कृपया पासवर्ड रिसेट लिंकको लागि तपाईंको ईमेल जाँच गर्नुहोस्।", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });


        emailEdittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(getWindow().getDecorView().getRootView(), "कृपया पूर्वनिर्धारित ईमेल परिवर्तन गर्न एड्मिन सम्पर्क गर्नुहोस्।", Snackbar.LENGTH_SHORT).show();
            }
        });


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    user=new UserDTO();
                    user.setJoinedTime(joinedTime);
                    user.setEmail(userEmail);
                    user.setFullName(userFullName);
                    user.setMobile(userMobile);
                    user.setUserName(userName);

                    if (bitmap!=null){
                        storageReference = FirebaseStorage.getInstance().getReference().child("profile_pictures")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child(String.valueOf(System.currentTimeMillis()));
                        storageReference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return storageReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri uri = task.getResult();
                                    imageUrl = uri.toString();
                                }

                            }
                        });
                    }
                    user.setProfilePic(imageUrl);
                    uploadUserInformation(user);



                }

            }
        });


    }


    private void uploadUserInformation(UserDTO user)
    {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    finish();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        ;

    }

    private boolean validate() {
        boolean isValid = false;

        userFullName = fullnameEdittext.getText().toString();
        userMobile = mobileEdittext.getText().toString();

        if (TextUtils.isEmpty(userFullName)) {
            fullnameEdittext.setError("Required");
        } else if (TextUtils.isEmpty(userMobile)) {
            mobileEdittext.setError("Required");
        } else {
            isValid = true;
        }
        return isValid;
    }

    private void getUserDetails(){
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserDTO user=dataSnapshot.getValue(UserDTO.class);
                        userName = user.getUserName();
                        userFullName = user.getFullName();
                        userEmail = user.getEmail();
                        userMobile = user.getMobile();
                        imageUrl = user.getProfilePic();
                        joinedTime = user.getJoinedTime();

                        fullnameEdittext.setText(userFullName);
                        emailEdittext.setText(userEmail);
                        mobileEdittext.setText(userMobile);
                        userNameTextview.setText(userName);

                        Glide.with(UpdateProfileActivity.this)
                                .load(imageUrl)
                                .into(userImageCircleImageView);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }




    private void selectImage() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(3, 3)
                .setMaxCropResultSize(4096, 4096)
                .start(this);
    }

    private void cropRequest() {
        CropImage.activity()
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(3, 3)
                .setMaxCropResultSize(4096, 4096)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Snackbar.make(getWindow().getDecorView().getRootView(),"आवश्यक अनुमतिहरू प्रदान गरिएको छैन", Snackbar.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                cropRequest();
            } else {
                Snackbar.make(getWindow().getDecorView().getRootView(),"आवश्यक अनुमतिहरू प्रदान गरिएको छैन", Snackbar.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                uri = resultUri;

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    userImageCircleImageView.setImageBitmap(bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }


        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
