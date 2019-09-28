package com.pathibharatechnology.smartkishan.login.login_view;

import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;

public interface IVLogin {

    void handleButtonClick();

    void handleGoogleLogin();

    void handleSignUpButtonClick();

    void handleSendVerification();

    void handlePasswordForgetClick();

    void handleSkipClick();

    void showProgressBar();

    void hideProgressBar();

    void displayMessage(String message);

    void onSuccess();

    void snackBarMessage(String message);

    void successButEmailNotVerified();

    void disableViews();

    void enableViews();

    void parseUserDetail(UserDTO user);
}
