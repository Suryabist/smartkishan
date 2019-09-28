package com.pathibharatechnology.smartkishan.login;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pathibharatechnology.smartkishan.login.login_view.IVLogin;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;

public class UploadUserDataToFirebase {

    private String fullName;
    private String email;
    private String userName;
    private String joinedDate;
    private String phone;
    private String photoUrl;

    UserDTO userDTO;
    FirebaseAuth mAuth;
    IVLogin ivLogin;

    public UploadUserDataToFirebase(String fullName, String email, String userName, String joinedDate, String phone, String photoUrl, IVLogin ivLogin) {
        this.fullName = fullName;
        this.email = email;
        this.userName = userName;
        this.joinedDate = joinedDate;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.ivLogin = ivLogin;
        mAuth = FirebaseAuth.getInstance();
    }

    public void uploadUserData() {
        userDTO = new UserDTO();
        userDTO.setFullName(fullName);
        userDTO.setEmail(email);
        userDTO.setUserName(userName);
        userDTO.setJoinedTime(joinedDate);
        userDTO.setMobile(phone);
        userDTO.setProfilePic(photoUrl);

        uploadToFireBase(userDTO);
    }

    private void uploadToFireBase(UserDTO userDTO) {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(mAuth.getUid())
                .setValue(userDTO).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ivLogin.hideProgressBar();
                    ivLogin.enableViews();
                    ivLogin.snackBarMessage("साइन अप सफल भयो।");
                    ivLogin.onSuccess();

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ivLogin.enableViews();
                        ivLogin.hideProgressBar();
                        ivLogin.snackBarMessage(e.getMessage());
                    }
                })
        ;
    }
}
