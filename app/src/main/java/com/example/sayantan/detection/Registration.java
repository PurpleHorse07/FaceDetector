package com.example.sayantan.detection;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class Registration extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener stateListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO set a splash screen
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();

        Toast.makeText(this, "oncreate", Toast.LENGTH_SHORT).show();
        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=auth.getCurrentUser();
                if(user==null){
                    if(isOnline()){
                        Toast.makeText(Registration.this, "Came here", Toast.LENGTH_SHORT).show();
                        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                                .setTheme(R.style.AppTheme)
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                                .build(), RC_SIGN_IN);
                    }else{
                        Toast.makeText(Registration.this, "nointernet", Toast.LENGTH_SHORT).show();
                        noInternet();
                    }

                }else {
                    Toast.makeText(Registration.this, "register", Toast.LENGTH_SHORT).show();
                    userRegister();
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        auth.addAuthStateListener(stateListener);
    }

    private void userRegister() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    private void noInternet() {
        setContentView(R.layout.no_internet);
        findViewById(R.id.trial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
    }

    private boolean isOnline() {
        ConnectivityManager conn = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert conn != null;
        NetworkInfo info = conn.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN) {
            if (resultCode==RESULT_OK)
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
            else
                finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
