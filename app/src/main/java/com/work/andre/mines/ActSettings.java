package com.work.andre.mines;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.work.andre.mines.ActMain.email;
import static com.work.andre.mines.ActMap.USERGOOGLEEMAIL;
import static com.work.andre.mines.database.DBase.allowedEmail;
import static com.work.andre.mines.database.DBase.fbUsers;
import static com.work.andre.mines.database.DBase.getUserNickNameOrDisplayNameByGoogleEmailWithSnapshot;

public class ActSettings extends FragmentActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    DatabaseReference myRef;

    private GoogleApiClient mGoogleApiClient;


    TextView tvUserMail;
    TextView tvUserFirstName;
    EditText etUserFirstName;
    EditText etUserNickName;
    TextView tvUserSecondName;
    EditText etUserSecondName;
    TextView tvUserAge;
    EditText etUserAge;
    TextView tvUserSex;
    EditText etUserSex;
    TextView tvRegistrationDateText;
    TextView tvRegistrationDate;
    Button btnSaveSettings;
    Button btnCancelSettings;

    private Button btnSignOut, btnRevokeAccess;
    private ImageView imgProfilePic;
    private TextView txtName, txtEmail;

    private String userDisplayName;
    String currentUserGoogleEmail;
    String currentUserNickName;
    String registrationDate;

    String userNickName;
    String userFirstName;
    String userSecondName;
    long userAge;
    String userSex;
    public static String personPhotoUrl;
    String allowedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.initUI();

        currentUserGoogleEmail = getIntent().getStringExtra(USERGOOGLEEMAIL);

        //......................................FIRESTORE......................................
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection(fbUsers).document(currentUserGoogleEmail);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    userDisplayName = (String) snapshot.get("DisplayName");
                    userNickName = (String) snapshot.get("NickName");
                    userFirstName = (String) snapshot.get("GivenName");
                    userSecondName = (String) snapshot.get("FamilyName");
                    userAge = (long) snapshot.get("Age");
                    userSex = (String) snapshot.get("Sex");
                    registrationDate = (String) snapshot.get("registrationDate");
                    updateData();
                }
            }
        });
        //.....................................................................................

//        //......................................FIRESTORE......................................
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference docRef = db.collection(fbUsers).document(currentUserGoogleEmail);
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//
//                    if (!document.exists()) {
//
//                        userDisplayName = (String) document.get("DisplayName");
//                        userNickName = (String) document.get("NickName");
//                        userFirstName = (String) document.get("GivenName");
//                        userSecondName = (String) document.get("FamilyName");
//                        userAge = (int) document.get("Age");
//                        userSex = (String) document.get("Sex");
//                        registrationDate = (String) document.get("registrationDate");
//                        updateData();
//                    }
//
//                } else {
////                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
        //.....................................................................................


//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        myRef = database.getReference(fbUsers);
//
//
//        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//
//                allowedEmail = allowedEmail(currentUserGoogleEmail);
//
//                userDisplayName = dataSnapshot.child(allowedEmail).child("DisplayName").getValue(String.class);
//                userNickName = dataSnapshot.child(allowedEmail).child("NickName").getValue(String.class);
//                userFirstName = dataSnapshot.child(allowedEmail).child("GivenName").getValue(String.class);
//                userSecondName = dataSnapshot.child(allowedEmail).child("FamilyName").getValue(String.class);
//                userAge = dataSnapshot.child(allowedEmail).child("Age").getValue(Integer.class);
//                userSex = dataSnapshot.child(allowedEmail).child("Sex").getValue(String.class);
//                registrationDate = dataSnapshot.child(allowedEmail).child("registrationDate").getValue(String.class);
//
//                updateData();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//            }
//        });

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();


//        currentUserNickName = MyApp.getMyDBase().getUserDisplayNameByGoogleEmail(currentUserGoogleEmail);


        personPhotoUrl = mAuth.getCurrentUser().getPhotoUrl().toString();

        Glide.with(getApplicationContext()).load(personPhotoUrl)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfilePic);

//
    }

    public void initUI() {
        tvUserMail = (TextView) findViewById(R.id.tvUserMail);
        tvUserFirstName = (TextView) findViewById(R.id.tvUserFirstName);
        etUserFirstName = (EditText) findViewById(R.id.etUserFirstName);
        etUserNickName = (EditText) findViewById(R.id.etUserNickName);
        tvUserSecondName = (TextView) findViewById(R.id.tvUserSecondName);
        etUserSecondName = (EditText) findViewById(R.id.etUserSecondName);
        tvUserAge = (TextView) findViewById(R.id.tvUserAge);
        etUserAge = (EditText) findViewById(R.id.etUserAge);
        tvUserSex = (TextView) findViewById(R.id.tvUserSex);
        etUserSex = (EditText) findViewById(R.id.etUserSex);
        tvRegistrationDateText = (TextView) findViewById(R.id.tvRegistrationDateText);
        tvRegistrationDate = (TextView) findViewById(R.id.tvRegistrationDate);
        btnSaveSettings = (Button) findViewById(R.id.btnSaveSettings);
        btnSaveSettings.setOnClickListener(this);
        btnCancelSettings = (Button) findViewById(R.id.btnCancelSettings);
        btnCancelSettings.setOnClickListener(this);

        btnSignOut = (Button) findViewById(R.id.btn_sign_out);
        btnRevokeAccess = (Button) findViewById(R.id.btn_revoke_access);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);

        btnSignOut.setOnClickListener(this);
        btnRevokeAccess.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnSaveSettings) {

            userNickName = etUserNickName.getText().toString();
            userFirstName = etUserFirstName.getText().toString();
            userSecondName = etUserSecondName.getText().toString();
            userSex = etUserSex.getText().toString();

            if (etUserAge.length() == 0) {
                userAge = 0;
            } else {
                userAge = Integer.valueOf(etUserAge.getText().toString());
            }

            //......................................FIRESTORE......................................
            HashMap<String, Object> updatedData = new HashMap<>();
            updatedData.put("NickName", userNickName);
            updatedData.put("GivenName", userFirstName);
            updatedData.put("FamilyName", userSecondName);
            updatedData.put("Age", userAge);
            updatedData.put("Sex", userSex);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference documentReference = db.collection(fbUsers).document(currentUserGoogleEmail);

            for (Map.Entry entry : updatedData.entrySet()) {
                documentReference
                        .update(entry.getKey().toString(), entry.getValue())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Log.w(TAG, "Error updating document", e);
                            }
                        });
            }
            //.....................................................................................

            Intent intentActMap = new Intent(this, ActMap.class);
            intentActMap.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
            startActivity(intentActMap);
        }

        if (v.getId() == R.id.btnCancelSettings) {
            Intent intentActMap = new Intent(this, ActMap.class);
            intentActMap.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
            startActivity(intentActMap);
        }

        if (v.getId() == R.id.btn_sign_out) {
            signOut();
        }

        if (v.getId() == R.id.btn_revoke_access) {
            revokeAccess();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void updateData() {
        txtName.setText(userDisplayName);
        txtEmail.setText(currentUserGoogleEmail);
        etUserNickName.setText(userNickName);
        etUserFirstName.setText(userFirstName);
        etUserSecondName.setText(userSecondName);
        etUserAge.setText(String.valueOf(userAge));
        etUserSex.setText(userSex);
        tvRegistrationDate.setText(registrationDate);
    }

    private void updateUI(FirebaseUser user) {
//        hideProgressDialog();
        if (user != null) {
//            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

//            findViewById(R.id.btn_sign_in).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);

//            findViewById(R.id.btn_sign_in).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                        Intent intentActMap = new Intent(getBaseContext(), ActMain.class);
                        startActivity(intentActMap);
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                        Intent intentActMap = new Intent(getBaseContext(), ActMain.class);
                        startActivity(intentActMap);
                    }
                });
    }


}
