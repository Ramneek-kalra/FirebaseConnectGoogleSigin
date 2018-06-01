package com.ramneek.firebaseconnect;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String TAG="LOG MESS:", degree;
    EditText name,className,address;
    private GoogleSignInClient mGoogleSignInClient;
    Button submit,search;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.uName);
        className = findViewById(R.id.uClass);
        address = findViewById(R.id.uAddress);
        submit = findViewById(R.id.buttonCloud);
        search = findViewById(R.id.buttonSearch);

        final Spinner spinner = findViewById(R.id.uDegree);
        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList <String>();
        categories.add("Select Degree");
        categories.add("BE/B.Tech");
        categories.add("BBA");
        categories.add("MBA");
        categories.add("B.Sc");
        categories.add("BA");
        categories.add("B.Pharmacy");
        ArrayAdapter <String> dataAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
        mAuth = FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Create a new user with a first and last name
                final Map<String, Object> user = new HashMap<>();
                user.put("Name", name.getText().toString());
                user.put("Degree",degree);
                user.put("Class", className.getText().toString());
                user.put("Address", address.getText().toString());

                int i = signIn();
                if(i == 0){
                    Toast.makeText(MainActivity.this, "You are Successfully Logged In!", Toast.LENGTH_SHORT).show();

                    DocumentReference reference = db.collection("users").document(name.getText().toString()+"");
                    reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot snapshot = task.getResult();
                                if(snapshot.exists()){
                                    Toast.makeText(MainActivity.this, "Data Already Exists!", Toast.LENGTH_SHORT).show();
                                }else{
                                    db.collection("users").document(name.getText().toString()+"").set(user);
                                    Toast.makeText(MainActivity.this, "Data Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                                    name.setText("");
                                    className.setText("");
                                    spinner.setId(0);
                                    address.setText("");
                                }
                            }
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this, "Sign in Problem! Please sure that Internet is Active!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SearchActivity.class));
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        degree = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Please do Select Valid Degree!", Toast.LENGTH_SHORT).show();
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]
    private int signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        return 0;
    }

    @Override
    public void onBackPressed() {
            mAuth.signOut();
            mGoogleSignInClient.signOut();
            Toast.makeText(this, "You are successfully Logged out!", Toast.LENGTH_SHORT).show();
            finish();
    }
}
// [END auth_with_google]
