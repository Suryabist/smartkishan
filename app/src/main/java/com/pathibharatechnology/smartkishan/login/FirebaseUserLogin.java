package com.pathibharatechnology.smartkishan.login;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pathibharatechnology.smartkishan.login.login_model.LoginModel;
import com.pathibharatechnology.smartkishan.login.login_view.IVLogin;

public class FirebaseUserLogin {

    IVLogin ivLogin;
    LoginModel loginModel;
    FirebaseUser mUser;

    public FirebaseUserLogin(IVLogin ivLogin, LoginModel loginModel) {
        this.ivLogin = ivLogin;
        this.loginModel = loginModel;
    }

    public void loginUserToFirebase(){
        ivLogin.showProgressBar();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(loginModel.getEmail(),loginModel.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            mUser = FirebaseAuth.getInstance().getCurrentUser();
                            mUser.reload();

                            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                ivLogin.hideProgressBar();
                                ivLogin.enableViews();
                                ivLogin.onSuccess();
                            } else {
                                ivLogin.hideProgressBar();
                                ivLogin.enableViews();
                                ivLogin.successButEmailNotVerified();
                            }
                        }else{
                            ivLogin.enableViews();
                            ivLogin.hideProgressBar();
                            ivLogin.snackBarMessage(task.getException().getMessage());
                        }
                    }
                });
    }

}
