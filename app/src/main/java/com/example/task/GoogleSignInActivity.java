package com.example.task;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.annotations.Nullable;

public class GoogleSignInActivity extends MainActivity {
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private static final int RC_SIGN_IN = 100;
    private final boolean showOneTapUI = true;
    FirebaseAuth mAuth;

    GoogleSignInClient googleSignInClient;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("GoogleSignIn....");
        progressDialog.show();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mAuth= FirebaseAuth.getInstance();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent intent = googleSignInClient.getSignInIntent();
        // Start activity for result
        startActivityForResult(intent, RC_SIGN_IN);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check condition

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> signInAccountTask;
            signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Initialize sign in account
                GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    firebaseAuthWithGoogle(googleSignInAccount.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "hello" + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        }
    }

    private void firebaseAuthWithGoogle(String id) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(id, null);
        // Check credential
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Check condition
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    // When task is successful redirect to profile activity display Toast
                    FirebaseUser user = mAuth.getCurrentUser();
                    upDateUI(user);
                } else {
                    // When task is unsuccessful display Toast
                    progressDialog.dismiss();
                    Toast.makeText(GoogleSignInActivity.this, "Eroor"+task.getException(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }


        });
    }
    private void upDateUI(FirebaseUser user) {
        Intent intent=new Intent(GoogleSignInActivity.this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}


