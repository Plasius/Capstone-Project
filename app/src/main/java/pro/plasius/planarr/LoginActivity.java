package pro.plasius.planarr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.plasius.planarr.utils.NetworkSetup;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAnalytics mFirebaseAnalytics;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    public static final int CODE_SIGN_IN = 320;
    private static final String TAG_AUTH = "AUTH";

    @BindView(R.id.login_et_email) TextView emailView;
    @BindView(R.id.login_et_password) TextView passwordView;
    @BindView(R.id.sign_in_button) SignInButton signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // Set the dimensions of the sign-in button.
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if(!NetworkSetup.isNetworkAvailable(this))
            Toast.makeText(this, "Please check network connection.", Toast.LENGTH_SHORT).show();
        //Auth
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(user != null) {
            Log.d("AUTH", "User Already signed in to Firebase. Launching app.");
            launchApp();
        }else if(account!=null){
            Log.d("AUTH", "User Already signed in to Google. Signing in to Firebase.");
            firebaseAuthWithGoogle(account);
        }
    }

    public void onLoginClicked(View v){
        mAuth.signInWithEmailAndPassword(emailView.getText().toString(), passwordView.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_AUTH, "signInWithEmail:success");
                            mFirebaseAnalytics.logEvent("event_login_email", null);
                            launchApp();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_AUTH, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void onRegisterClicked(View v){
        mAuth.createUserWithEmailAndPassword(emailView.getText().toString(), passwordView.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG_AUTH, "createUserWithEmail:success");
                            mFirebaseAnalytics.logEvent("event_register_email", null);
                            launchApp();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG_AUTH, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Account creation failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v){
        Log.d(TAG_AUTH, "Signing in to Google.");
        signIn();
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, CODE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("AUTH", "Signing in to Firebase.");
                mFirebaseAnalytics.logEvent("event_login_gmail", null);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG_AUTH, "Google sign in failed.", e);
                Toast.makeText(this, "Sign in failed, please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Success
                            Log.d("AUTH", "Signed in to Firebase. Launching App.");
                            launchApp();
                        } else {
                            // Fail
                            Log.w("AUTH", "Signing in to Firebase failed.", task.getException());
                        }
                    }
                });
    }

    private void launchApp(){
        Intent intent = new Intent(this, TaskListActivity.class);
        startActivity(intent);
        finish();
    }
}
