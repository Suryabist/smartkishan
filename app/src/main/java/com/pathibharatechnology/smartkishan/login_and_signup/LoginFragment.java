package com.pathibharatechnology.smartkishan.login_and_signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pathibharatechnology.smartkishan.MainActivity;
import com.pathibharatechnology.smartkishan.MainDashboardActivity;
import com.pathibharatechnology.smartkishan.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executor;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.provider.ContactsContract.Intents.Insert.EMAIL;
import static com.facebook.AccessTokenManager.TAG;


public class LoginFragment extends Fragment {


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

    FirebaseUser mUser;

    FirebaseAuth mAuth;
    StorageReference storageReference;

    //for google signin
    private static final int RC_SIGN_IN = 234;
    private static final String TAG = "surya";
    GoogleSignInClient mGoogleSignInClient;
    Uri photoUrl;

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

        view.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Google signin button clicked=====");
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
                if (validate()){
                    sendVerification();
                }

            }
        });

        skipTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainDashboardActivity.class);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });






        forgotPasswordTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailForForgotPassword = emailEditText.getText().toString().trim();
                if (TextUtils.isEmpty(emailForForgotPassword)) {
                    emailEditText.setError("कृपया तपाईंको पासवर्ड रिसेट गर्न ईमेल भर्नुहोस्...");
                } else {

                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailForForgotPassword)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar.make(getView(), "पासवर्ड रिसेट लिंकको लागि कृपया तपाईंको ईमेल जाँच गर्नुहोस्।", Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(getView(), e.getMessage(), Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            });


                }
            }
        });



        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    loginUserToFirebase();
                    // Toast.makeText(LoginActivity.this, "validated input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction()
                        .replace(R.id.frameForFragmentID, new SignUpFragment());
                fragmentTransaction.commit();
            }
        });






        return view;
    }

    //for google signin
    @Override
    public void onStart() {
        super.onStart();

        //if the user is already signed in
        //we will close this activity
        //and take the user to profile activity
        if (mAuth.getCurrentUser() != null) {
            getActivity().finish();
            Toast.makeText(getContext(), "Logged in google.", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this, ProfileActivity.class));
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("INside activity result=====");

        //if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            System.out.println("Inside this====");

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                System.out.println("inside try=====");
                //Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                //authenticating with firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                System.out.println("inside catch=====");
                e.printStackTrace();
//                Toast.makeText(getContext(), "got this"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //for google signin
    private void signIn() {
        //getting the google signin intent
        System.out.println("inside signin=====");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        //starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //for google signin
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        progressBar.setVisibility(View.VISIBLE);

        //getting the auth credential
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Now using firebase we are signing in the user here
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

//                            Toast.makeText(getContext(), "User Signed In", Toast.LENGTH_SHORT).show();

                            checkForUserDetail();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText( getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    private void checkForUserDetail() {
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final UserDTO user = dataSnapshot.getValue(UserDTO.class);

                        if (user != null) {
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getContext(), MainDashboardActivity.class));
                            getActivity().finish();
                        } else {
//                            String userName = user.getUserName();
                                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (fUser != null) {
                                    for (UserInfo profile : fUser.getProviderData()) {
                                        // Id of the provider (ex: google.com)
                                        String providerId = profile.getProviderId();

                                        // UID specific to the provider
                                        String uid = profile.getUid();

                                        // Name, email address, and profile photo Url
                                        String name = profile.getDisplayName();
                                        String email = profile.getEmail();

                                        photoUrl = profile.getPhotoUrl();


                                        final UserDTO userDTO = new UserDTO();
                                        userDTO.setFullName(name);
                                        userDTO.setEmail(email);
                                        userDTO.setUserName(name);
                                        if (profile.getPhoneNumber() == null || profile.getPhoneNumber().equals("")) {
                                            userDTO.setMobile("");
                                        } else {
                                            userDTO.setMobile(profile.getPhoneNumber());
                                        }
                                        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                                        Date date = new Date();
                                        String strDate = dateFormat.format(date);
                                        userDTO.setJoinedTime(strDate);

                                        userDTO.setProfilePic(photoUrl.toString());
                                        uploadUserInformation(userDTO);

                                    }


                            } else {
                                    progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(getContext(), MainDashboardActivity.class));
                                getActivity().finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
                    Snackbar.make(getView(), "साइन अप सफल भयो।", Snackbar.LENGTH_LONG)
                            .show();
                    progressBar.setVisibility(View.GONE);
                    getContext().startActivity(new Intent(getContext(), MainDashboardActivity.class));
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


    /*@Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Toast.makeText(getContext(), "Hello surya. logged in to fb", Toast.LENGTH_SHORT).show();
        }
    }*/

    private boolean validate(){
        boolean isValid=false;
        email=emailEditText.getText().toString();
        password=passwordEditText.getText().toString();
        if(TextUtils.isEmpty(email)){
            emailEditText.setError("Required");
        }else if(TextUtils.isEmpty(password)){
            passwordEditText.setError("Required");
        }else if(password.length()<8){
            passwordEditText.setError("Should be of minimum 8 characters");

        }else{
            isValid=true;

        }
        return isValid;
    }


    private void sendVerification(){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){

                            mUser = FirebaseAuth.getInstance().getCurrentUser();
                            mUser.reload();

                            mUser.reload();
                            if(!mUser.isEmailVerified()) {
                                mUser.sendEmailVerification();
                                Snackbar.make(getView(), "ईमेल पठाइयो", Snackbar.LENGTH_LONG)
                                        .show();
                            }else {
                                Snackbar.make(getView(), "तपाईंको ईमेल प्रमाणित भेरिफाइ भइसकेको छ।", Snackbar.LENGTH_LONG)
                                        .show();
                                Intent intent = new Intent(getContext(), MainDashboardActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }else{
                            Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }


    private void loginUserToFirebase(){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){

                            mUser = FirebaseAuth.getInstance().getCurrentUser();
                            mUser.reload();

                            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            } else {
                                Snackbar.make(getView(), "कृपया ईमेल भेरिफाइ गर्न तपाईंको ईमेल जाँच गर्नुहोस्।", Snackbar.LENGTH_LONG)
                                        .show();
                                FirebaseAuth.getInstance().signOut();
                            }

                        }else{
                            Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
    }
}
