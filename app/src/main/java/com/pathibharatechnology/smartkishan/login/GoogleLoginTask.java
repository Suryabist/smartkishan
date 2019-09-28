package com.pathibharatechnology.smartkishan.login;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.pathibharatechnology.smartkishan.login.login_view.IVLogin;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GoogleLoginTask {
    IVLogin ivLogin;
    Context mContext;
    FirebaseAuth mAuth;
    GetUserDetailFromFirebase getUserDetailFromFirebase;

    public GoogleLoginTask(IVLogin ivLogin, Context mContext) {
        this.ivLogin = ivLogin;
        this.mContext = mContext;
    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        ivLogin.disableViews();
        ivLogin.showProgressBar();
        mAuth = FirebaseAuth.getInstance();

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext,
                        new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getUserDetailFromFirebase = new GetUserDetailFromFirebase(ivLogin);
                            getUserDetailFromFirebase.checkForUserDetail(FirebaseAuth.getInstance().getUid());

                        } else {
                            // If sign in fails, display a message to the user.
                            ivLogin.hideProgressBar();
                            ivLogin.enableViews();
                            ivLogin.displayMessage("Authentication failed.");

                        }
                    }
                });
    }

    public void pushUser() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser != null) {

            for (UserInfo profile : fUser.getProviderData()) {
                String fullName = profile.getDisplayName();
                String email = profile.getEmail();
                String userName = profile.getEmail();
                String photoUrl = profile.getPhotoUrl().toString();
                String phone = profile.getPhoneNumber();
                if (phone == null || phone.equals("")) {
                    phone = "";
                }
                DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                Date date = new Date();
                String strDate = dateFormat.format(date);

                UploadUserDataToFirebase uploadUserDataToFirebase = new UploadUserDataToFirebase(fullName, email, userName, strDate, phone, photoUrl, ivLogin);
                uploadUserDataToFirebase.uploadUserData();
            }

        }

    }
}
