package com.pathibharatechnology.smartkishan.login_and_signup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pathibharatechnology.smartkishan.MainActivity;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.products_list.ProductListDTO;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import static android.app.Activity.RESULT_OK;


public class SignUpFragment extends Fragment {

    EditText fullNameEditText, emailEditText, userNameEditText, mobileEditText, passwordEditText, confirmPasswordEditText;
    String fullName, email, userName, password, confirmPassword;
    String mobile;
    Button signUpButton;
    TextView login;
    CircleImageView userProfilePic;
    final int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;
    Uri uri;
    StorageReference storageReference;
    ProgressBar progressBar;
    int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        fullNameEditText = view.findViewById(R.id.fullNameEditTextID);
        emailEditText = view.findViewById(R.id.emailEditTextID);
        userNameEditText = view.findViewById(R.id.userNameEditTextID);
        mobileEditText = view.findViewById(R.id.mobileEditTextID);
        passwordEditText = view.findViewById(R.id.passwordEditTextID);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditTextID);
        signUpButton = view.findViewById(R.id.signUpButtonID);
        login = view.findViewById(R.id.loginTextViewID);
        userProfilePic = view.findViewById(R.id.imageUploadCircleImageViewID);
        progressBar = view.findViewById(R.id.progressBarID);



        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    Query query = FirebaseDatabase.getInstance().getReference().child("users")
                            .orderByChild("userName").equalTo(userName);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                userNameEditText.setError("This username has already been taken.");
                            } else {
                                UserDTO user=new UserDTO();
                                user.setFullName(fullName);
                                user.setEmail(email);
                                user.setUserName(userName);
                                user.setMobile(mobile);
                                DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                                Date date = new Date();
                                String strDate = dateFormat.format(date);
                                user.setJoinedTime(strDate);
                                regiterUserToFirebase(user);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }
        });

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction()
                        .replace(R.id.frameForFragmentID, new LoginFragment());
                fragmentTransaction.commit();
            }
        });


        return view;
    }


    private boolean validate() {
        boolean isValid = false;

        fullName = fullNameEditText.getText().toString().trim();
        email = emailEditText.getText().toString().trim();
        userName = userNameEditText.getText().toString().toLowerCase().trim();
        mobile = mobileEditText.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            fullNameEditText.setError("Required");
        } else if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required");
        } else if (TextUtils.isEmpty(userName)) {
            userNameEditText.setError("Required.");
        } else if (password.length() < 8) {
            passwordEditText.setError("Should be of minimum 8 characters");

        } else if (confirmPassword.length() < 8) {
            passwordEditText.setError("Should be of minimum 8 characters");

        } else if (bitmap == null) {
            Snackbar.make(getView(), "Please select profile picture.", Snackbar.LENGTH_LONG)
                    .show();
        } else if (!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Password doesnot match.");
        }else {
            isValid = true;
        }
        return isValid;
    }


    private void regiterUserToFirebase(final UserDTO user) {

        progressBar.setVisibility(View.VISIBLE);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (bitmap != null) {

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
                                                        String downloadurl = uri.toString();
                                                        user.setProfilePic(downloadurl);
                                                        uploadUserInformation(user);
                                                    }

                                                }
                                            });
                                        } else {
                                            user.setProfilePic("");
                                            uploadUserInformation(user);

                                        }
                                    }else {
                                        Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });





                        } else {
                            System.out.println("Error occured. Couldnot complete the task...");
                            task.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    System.out.println("Failure due to::: "+e.getMessage());
                                    Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            });
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
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Snackbar.make(getView(), "साइन अप सफल भयो। कृपया ईमेल भेरिफाइ गर्न तपाईंको ईमेल जाँच गर्नुहोस्।", Snackbar.LENGTH_LONG)
                            .show();
                    FirebaseAuth.getInstance().signOut();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction()
                            .replace(R.id.frameForFragmentID, new LoginFragment());
                    fragmentTransaction.commit();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG)
                        .show();
            }
        })
        ;

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                userProfilePic.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}
