package com.pathibharatechnology.smartkishan.login.login_presenter;

import android.util.Log;
import android.util.Patterns;
import com.pathibharatechnology.smartkishan.login.FirebaseUserLogin;
import com.pathibharatechnology.smartkishan.login.PasswordResetTask;
import com.pathibharatechnology.smartkishan.login.VerificationLinkResendTask;
import com.pathibharatechnology.smartkishan.login.login_model.LoginModel;
import com.pathibharatechnology.smartkishan.login.login_view.IVLogin;

public class LoginPresenter implements IPLogin {

    IVLogin ivLogin;
    FirebaseUserLogin userLogin;
    VerificationLinkResendTask verificationLinkResendTask;

    public LoginPresenter(IVLogin ivLogin) {
        this.ivLogin = ivLogin;
    }

    @Override
    public void onLogin(LoginModel loginModel) {
        if (validate(loginModel)) {
            userLogin = new FirebaseUserLogin(ivLogin, loginModel);
            ivLogin.disableViews();
            userLogin.loginUserToFirebase();
        }
    }

    @Override
    public void onGoogleLogin() {


    }

    @Override
    public void onSendVerification(LoginModel loginModel) {
        if (validate(loginModel)) {
            verificationLinkResendTask = new VerificationLinkResendTask(ivLogin, loginModel);
            ivLogin.disableViews();
            verificationLinkResendTask.resendVerificationLink();
        }
    }

    @Override
    public void onForgetPassword(LoginModel loginModel) {
        if (verifyEmail(loginModel)){
            ivLogin.showProgressBar();
            ivLogin.disableViews();
            PasswordResetTask passwordResetTask = new PasswordResetTask(ivLogin, loginModel);
            passwordResetTask.resetPasswordFromFirebase();
        }
    }


    @Override
    public void checkEmailVerification() {

    }

    private boolean verifyEmail(LoginModel loginModel) {
        boolean isValid = false;
        if (loginModel.getEmail()==null || loginModel.getEmail().equals("")) {
            ivLogin.snackBarMessage("Enter your email for password reset link.");
        } else if(!Patterns.EMAIL_ADDRESS.matcher(loginModel.getEmail()).matches()) {
            ivLogin.snackBarMessage("Enter a valid email.");
        } else {
            isValid = true;
        }
        return isValid;
    }

    private boolean validate(LoginModel loginModel){
        boolean isValid=false;
        String email= loginModel.getEmail();
        String password=loginModel.getPassword();
        if(email == null || email.equals("")){
            ivLogin.displayMessage("Email is empty.");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ivLogin.displayMessage("Please enter a valid email.");
        }else if(password.equals("")) {
            ivLogin.displayMessage("Password cannot be empty.");
        }else if(password.length() <8){
            ivLogin.displayMessage("Password length is shorter.");
        }else{
            isValid=true;
        }
        return isValid;
    }
}
