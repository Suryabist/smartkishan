package com.pathibharatechnology.smartkishan.login.login_presenter;

import com.pathibharatechnology.smartkishan.login.login_model.LoginModel;

public interface IPLogin {

    void onLogin(LoginModel loginModel);

    void onGoogleLogin();

    void onSendVerification(LoginModel loginModel);

    void onForgetPassword(LoginModel loginModel);

    void checkEmailVerification();

}
