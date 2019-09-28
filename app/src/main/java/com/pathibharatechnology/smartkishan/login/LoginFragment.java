package com.pathibharatechnology.smartkishan.login;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.pathibharatechnology.smartkishan.MainActivity;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.R;
import com.pathibharatechnology.smartkishan.login.login_model.LoginModel;
import com.pathibharatechnology.smartkishan.login.login_presenter.LoginPresenter;
import com.pathibharatechnology.smartkishan.login.login_view.IVLogin;
import com.pathibharatechnology.smartkishan.login_and_signup.SignUpFragment;
import com.pathibharatechnology.smartkishan.login_and_signup.UserDTO;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class LoginFragment extends Fragment implements IVLogin {


    EditText emailEditText, passwordEditText;
    Button signInButton;
    TextView signUpTextView;
    String email, password;
    ProgressBar progressBar;
//    LoginButton fbLoginButton;
    TextView forgotPasswordTextview;
    TextView resendVerificationTextview;
    TextView skipTextview;

    CallbackManager callbackManager;
    private static final String EMAIL = "email";

    FirebaseAuth mAuth;
    StorageReference storageReference;

    //for google signin
    private static final int RC_SIGN_IN = 234;
    private static final String TAG = "surya";
    GoogleSignInClient mGoogleSignInClient;
    Uri photoUrl;

    SignInButton googleSignIn;
    GoogleLoginTask googleLoginTask;

    LoginPresenter loginPresenter;
    LoginModel loginModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailEditText = view.findViewById(R.id.emailEditTextID);
        passwordEditText = view.findViewById(R.id.passwordEditTextID);
        signInButton = view.findViewById(R.id.signInButtonId);
        signUpTextView = view.findViewById(R.id.signUpTextviewId);
        progressBar = view.findViewById(R.id.progressBarID);

        googleSignIn = view.findViewById(R.id.sign_in_button);

        loginPresenter = new LoginPresenter(this);

//        fbLoginButton = (LoginButton) view.findViewById(R.id.fb_login_button);
//        fbLoginButton.setReadPermissions(Arrays.asList(EMAIL));

        forgotPasswordTextview = view.findViewById(R.id.forgotPasswordTextviewID);
        resendVerificationTextview = view.findViewById(R.id.emailVerificationSendId);
        skipTextview = view.findViewById(R.id.skipTextviewId);

//        fbLoginButton.setFragment(this);

        mAuth = FirebaseAuth.getInstance();


        //for google signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient((Activity) getContext(), gso);
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        // Callback registration

        /*fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());

                // App code
            }

            @Override
            public void onCancel() {
                Toast.makeText(getContext(), "Text cancel", Toast.LENGTH_SHORT).show();
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(getContext(), "Text error: "+exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/


        resendVerificationTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSendVerification();

            }
        });

        skipTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSkipClick();
            }
        });

        forgotPasswordTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePasswordForgetClick();
            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleButtonClick();
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSignUpButtonClick();
            }
        });

        return view;
    }

    private void handleFacebookAccessToken(AccessToken token) {
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            uploadUserInformation();

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(getView(), "प्रमाणीकरण असफल भयो।", Snackbar.LENGTH_LONG)
                                    .show();
                            updateUI();
                        }

                    }
                });
    }

    private void uploadUserInformation()
    {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        UserDTO user =new UserDTO();
        user.setFullName(currentUser.getDisplayName());
        String email;
        if (currentUser.getEmail() != null){
            email = currentUser.getEmail();
        } else {
            email = "";
        }
        user.setEmail(email);
        user.setUserName(currentUser.getDisplayName());
        String phone;
        if (currentUser.getPhoneNumber() != null){
            phone = currentUser.getPhoneNumber();
        } else {
            phone = "";
        }
        user.setMobile(phone);
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        Date date = new Date();
        String strDate = dateFormat.format(date);
        user.setJoinedTime(strDate);
        user.setProfilePic(currentUser.getPhotoUrl().toString());



        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(mAuth.getUid())
                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){
                    Snackbar.make(getView(), "Facebook login successful.", Snackbar.LENGTH_LONG)
                            .show();
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
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



    public void updateUI() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
        }
    }

    @Override
    public void handleButtonClick() {
        loginModel = new LoginModel();
        loginModel.setEmail(emailEditText.getText().toString());
        loginModel.setPassword(passwordEditText.getText().toString());
        loginPresenter.onLogin(loginModel);
    }

    @Override
    public void handleGoogleLogin() {

    }



    @Override
    public void handleSendVerification() {

        loginModel = new LoginModel();
        loginModel.setEmail(emailEditText.getText().toString());
        loginModel.setPassword(passwordEditText.getText().toString());
        loginPresenter.onSendVerification(loginModel);

    }

    @Override
    public void handlePasswordForgetClick() {
        loginModel = new LoginModel();
        loginModel.setEmail(emailEditText.getText().toString());
        loginPresenter.onForgetPassword(loginModel);
    }

    @Override
    public void handleSkipClick() {
        onSuccess();
    }

    @Override
    public void onSuccess() {
        Intent intent = new Intent(getContext(), MainDashboardActivity.class);
        startActivity(intent);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().finish();
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void displayMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void snackBarMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void handleSignUpButtonClick() {
        getFragmentManager().beginTransaction().replace(R.id.frameForFragmentID, new SignUpFragment()).commit();
    }

    @Override
    public void successButEmailNotVerified() {
        snackBarMessage("कृपया ईमेल भेरिफाइ गर्न तपाईंको ईमेल जाँच गर्नुहोस्।");
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void disableViews() {
        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        forgotPasswordTextview.setEnabled(false);
        signInButton.setEnabled(false);
        googleSignIn.setEnabled(false);
        signUpTextView.setEnabled(false);
        resendVerificationTextview.setEnabled(false);
        skipTextview.setEnabled(false);
    }

    @Override
    public void enableViews() {
        emailEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
        forgotPasswordTextview.setEnabled(true);
        signInButton.setEnabled(true);
        googleSignIn.setEnabled(true);
        signUpTextView.setEnabled(true);
        resendVerificationTextview.setEnabled(true);
        skipTextview.setEnabled(true);
    }

    @Override
    public void parseUserDetail(UserDTO user) {
        if (user == null) {
            googleLoginTask.pushUser();
        } else {
            hideProgressBar();
            enableViews();
            displayMessage("Welcome back.");
            onSuccess();
        }
    }


    //for google signin
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase

                googleLoginTask= new GoogleLoginTask(this, getContext());
                googleLoginTask.firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
}
