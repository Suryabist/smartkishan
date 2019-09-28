package com.pathibharatechnology.smartkishan.login;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pathibharatechnology.smartkishan.login.login_model.LoginModel;
import com.pathibharatechnology.smartkishan.login.login_view.IVLogin;

public class VerificationLinkResendTask {

    IVLogin ivLogin;
    LoginModel loginModel;
    FirebaseUser mUser;

    public VerificationLinkResendTask(IVLogin ivLogin, LoginModel loginModel) {
        this.ivLogin = ivLogin;
        this.loginModel = loginModel;
    }

    public void resendVerificationLink() {
        ivLogin.showProgressBar();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(loginModel.getEmail(),loginModel.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            mUser = FirebaseAuth.getInstance().getCurrentUser();
                            mUser.reload();

                            mUser.reload();
                            if(!mUser.isEmailVerified()) {
                                ivLogin.hideProgressBar();
                                ivLogin.enableViews();
                                mUser.sendEmailVerification();
                                ivLogin.snackBarMessage("ईमेल पठाइयो");
                            }else {
                                ivLogin.hideProgressBar();
                                ivLogin.enableViews();
                                ivLogin.displayMessage("तपाईंको ईमेल प्रमाणित भइसकेको छ।");
                                ivLogin.onSuccess();
                            }
                        }else{
                            ivLogin.hideProgressBar();
                            ivLogin.enableViews();
                            ivLogin.snackBarMessage(task.getException().getMessage());
                        }
                    }
                });
    }

}
