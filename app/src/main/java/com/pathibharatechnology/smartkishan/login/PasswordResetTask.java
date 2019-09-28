package com.pathibharatechnology.smartkishan.login;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.pathibharatechnology.smartkishan.login.login_model.LoginModel;
import com.pathibharatechnology.smartkishan.login.login_view.IVLogin;

public class PasswordResetTask {

    IVLogin ivLogin;
    LoginModel loginModel;

    public PasswordResetTask(IVLogin ivLogin, LoginModel loginModel) {
        this.ivLogin = ivLogin;
        this.loginModel = loginModel;
    }

    public void resetPasswordFromFirebase() {
        FirebaseAuth.getInstance().sendPasswordResetEmail(loginModel.getEmail())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ivLogin.hideProgressBar();
                        ivLogin.enableViews();
                        ivLogin.snackBarMessage("पासवर्ड रिसेट लिंकको लागि कृपया तपाईंको ईमेल जाँच गर्नुहोस्।");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ivLogin.hideProgressBar();
                        ivLogin.enableViews();
                        ivLogin.snackBarMessage(e.getMessage());
                    }
                });
    }
}
