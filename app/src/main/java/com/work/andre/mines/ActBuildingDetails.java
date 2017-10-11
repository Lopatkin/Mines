package com.work.andre.mines;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static com.work.andre.mines.ActMap.USERGOOGLEEMAIL;
import static com.work.andre.mines.database.DBase.buildingTypeClay;
import static com.work.andre.mines.database.DBase.buildingTypeHQ;
import static com.work.andre.mines.database.DBase.buildingTypeStone;
import static com.work.andre.mines.database.DBase.buildingTypeWood;
import static com.work.andre.mines.database.DBase.fbBuildings;
import static com.work.andre.mines.database.DBase.fbUsers;

public class ActBuildingDetails extends AppCompatActivity implements View.OnClickListener {

    public static final String BUILDINGID = "buildingID";
    public static final String BUILDINGOWNERNICKNAME = "buildingOwnerNickName";

    public static String buildingID;
    public static String buildingOwnerGoogleEmail;

    public static String currentUserGoogleEmail;

    AlertDialog.Builder ad;
    Context context;

    TextView tvBuildingName;
    ImageView ivBuildingPicture;
    TextView tvBuildingOwnerText;
    TextView tvBuildingOwnerInfo;
    TextView tvBuildingLVLText;
    TextView tvBuildingLVLInfo;
    TextView tvBuildingTypeText;
    TextView tvBuildingTypeInfo;

    Button btnOK;
    Button btnClose;
    Button btnDeleteBuilding;

    static String buildingName;
    static String ownerName;
    static long buildingLVL;
    static String buildingType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_details);

        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setVisibility(View.INVISIBLE);
        btnDeleteBuilding = (Button) findViewById(R.id.btnDeleteBuilding);
        btnDeleteBuilding.setVisibility(View.INVISIBLE);

        currentUserGoogleEmail = getIntent().getStringExtra(USERGOOGLEEMAIL);
        buildingID = getIntent().getStringExtra(BUILDINGID);

        //......................................FIRESTORE......................................
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection(fbBuildings).document(buildingID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    //Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    buildingName = (String) snapshot.get("buildingName");
                    buildingLVL = (long) snapshot.get("buildingLVL");
                    buildingType = (String) snapshot.get("buildingType");
                    buildingOwnerGoogleEmail = (String) snapshot.get("userGoogleEmail");

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference docRef = db.collection(fbUsers).document(buildingOwnerGoogleEmail);
                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                //Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            if (snapshot != null && snapshot.exists()) {
                                ownerName = String.valueOf(snapshot.get("NickName"));
                                updateData();
                                initUI();

                            }
                        }
                    });
                }
            }
        });

        initUI();
    }

    private void updateData() {
        tvBuildingName.setText(buildingName);
        tvBuildingOwnerInfo.setText(ownerName);
        tvBuildingLVLInfo.setText(String.valueOf(buildingLVL));
        tvBuildingTypeInfo.setText(buildingType);

        try {
            if (buildingType.equals(buildingTypeHQ)) {
                ivBuildingPicture.setImageResource(R.drawable.hq2d);
            } else if (buildingType.equals(buildingTypeWood)) {
                ivBuildingPicture.setImageResource(R.drawable.mine_forest_picture);
            } else if (buildingType.equals(buildingTypeStone)) {
                ivBuildingPicture.setImageResource(R.drawable.mine_stone_picture);
            } else if (buildingType.equals(buildingTypeClay)) {
                ivBuildingPicture.setImageResource(R.drawable.mine_sand_picture);
            }
        } catch (Exception e) {
        }

    }

    private void initUI() {
        tvBuildingName = (TextView) findViewById(R.id.tvBuildingName);
        ivBuildingPicture = (ImageView) findViewById(R.id.ivBuildingPicture);
        tvBuildingOwnerText = (TextView) findViewById(R.id.tvBuildingOwnerText);
        tvBuildingOwnerInfo = (TextView) findViewById(R.id.tvBuildingOwnerInfo);
        tvBuildingLVLText = (TextView) findViewById(R.id.tvBuildingLVLText);
        tvBuildingLVLInfo = (TextView) findViewById(R.id.tvBuildingLVLInfo);
        tvBuildingTypeText = (TextView) findViewById(R.id.tvBuildingTypeText);
        tvBuildingTypeInfo = (TextView) findViewById(R.id.tvBuildingTypeInfo);

        btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);

        if (currentUserGoogleEmail.equals(buildingOwnerGoogleEmail)) {
            btnOK.setVisibility(View.VISIBLE);
            btnOK.setOnClickListener(this);

            btnDeleteBuilding.setVisibility(View.VISIBLE);
            btnDeleteBuilding.setOnClickListener(this);
        } else {
            btnOK.setVisibility(View.INVISIBLE);
            btnDeleteBuilding.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOK) {
            Intent intentActMap = new Intent(this, ActMap.class);
            intentActMap.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
            startActivity(intentActMap);
        }

        if (v.getId() == R.id.btnClose) {
            Intent intentActMap = new Intent(this, ActMap.class);
            intentActMap.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
            startActivity(intentActMap);
        }

        if (v.getId() == R.id.btnDeleteBuilding) {

            context = ActBuildingDetails.this;
            ad = new AlertDialog.Builder(context);

            String title = "Внимание!";
            String message = "Вы действительно хотите удалить здание?";
            String button1String = "Да!";
            String button2String = "Нет, я передумал";

            ad.setTitle(title);  // заголовок
            ad.setMessage(message); // сообщение
            ad.setPositiveButton(button1String, new OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {


                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(fbBuildings).document(buildingID)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    Toast.makeText(getBaseContext(), "Постройка удалена!", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getBaseContext(), "Постройка не удалена!", Toast.LENGTH_LONG).show();

                                }
                            });


                    Intent intentActMap = new Intent(ActBuildingDetails.this, ActMap.class);
                    intentActMap.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
                    startActivity(intentActMap);
                }
            });
            ad.setNegativeButton(button2String, new OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    //передумали удялять здание
                }
            });
            ad.setCancelable(true);
            ad.setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    //передумали удялть здание
                }
            });
            ad.show();
        }
    }
}