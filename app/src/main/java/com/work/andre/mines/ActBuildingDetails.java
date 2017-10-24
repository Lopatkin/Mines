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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.work.andre.mines.ActMap.USERGOOGLEEMAIL;
import static com.work.andre.mines.ActMap.etBuildingName;
import static com.work.andre.mines.database.DBase.addNewBuilding;
import static com.work.andre.mines.database.DBase.buildingTypeClay;
import static com.work.andre.mines.database.DBase.buildingTypeClayEn;
import static com.work.andre.mines.database.DBase.buildingTypeHQ;
import static com.work.andre.mines.database.DBase.buildingTypeHQEn;
import static com.work.andre.mines.database.DBase.buildingTypeStone;
import static com.work.andre.mines.database.DBase.buildingTypeStoneEn;
import static com.work.andre.mines.database.DBase.buildingTypeWood;
import static com.work.andre.mines.database.DBase.buildingTypeWoodEn;
import static com.work.andre.mines.database.DBase.fbBuildings;
import static com.work.andre.mines.database.DBase.fbUsers;
import static com.work.andre.mines.database.DBase.getBuildingCategoryByBuildingType;
import static com.work.andre.mines.database.DBase.getUserNickNameOrDisplayNameByGoogleEmailWithSnapshot;

public class ActBuildingDetails extends AppCompatActivity implements View.OnClickListener {

    public static boolean isHQAviable;

    static long userGold;
    static long userWood;
    static long userStone;
    static long userClay;

    static long costGold;
    static long costWood;
    static long costStone;
    static long costClay;

    static long newIncomeGold;
    static long newIncomeWood;
    static long newIncomeStone;
    static long newIncomeClay;

    static boolean getCost;
    static boolean getUserMoney;

    static long buildingLVLPlus1;

    static boolean more;
    static boolean payIsOk;

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

    EditText etRenameBuildingName;

    Button btnOK;
    Button btnClose;
    Button btnDeleteBuilding;
    Button btnRename;
    Button btnUpgrade;

    static String buildingName;
    static String ownerName;
    static long buildingLVL;
    static String buildingType;

    static String newBuildingName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_details);

        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setVisibility(View.INVISIBLE);

        btnDeleteBuilding = (Button) findViewById(R.id.btnDeleteBuilding);
        btnDeleteBuilding.setVisibility(View.INVISIBLE);

        btnRename = (Button) findViewById(R.id.btnRename);
        btnRename.setVisibility(View.INVISIBLE);

        btnUpgrade = (Button) findViewById(R.id.btnUpgrade);
        btnUpgrade.setVisibility(View.INVISIBLE);

        currentUserGoogleEmail = getIntent().getStringExtra(USERGOOGLEEMAIL);
        buildingID = getIntent().getStringExtra(BUILDINGID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(fbBuildings).document(buildingID);
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
                                String nickName = String.valueOf(snapshot.get("NickName"));
                                String displayName = String.valueOf(snapshot.get("DisplayName"));

                                if (nickName.length() > 0) {
                                    ownerName = nickName;
                                } else {
                                    ownerName = displayName;
                                }

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

        etRenameBuildingName = (EditText) findViewById(R.id.etRenameBuildingName);

        btnClose = (Button) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);

        if (currentUserGoogleEmail.equals(buildingOwnerGoogleEmail)) {
            btnOK.setVisibility(View.VISIBLE);
            btnOK.setOnClickListener(this);

            btnDeleteBuilding.setVisibility(View.VISIBLE);
            btnDeleteBuilding.setOnClickListener(this);

            btnRename.setVisibility(View.VISIBLE);
            btnRename.setOnClickListener(this);

            btnUpgrade.setVisibility(View.VISIBLE);
            btnUpgrade.setOnClickListener(this);
        } else {
            btnOK.setVisibility(View.INVISIBLE);
            btnDeleteBuilding.setVisibility(View.INVISIBLE);
            btnRename.setVisibility(View.INVISIBLE);
            btnUpgrade.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnOK) {

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference bRef = db.collection(fbBuildings).document(buildingID);

            bRef
                    .update("buildingName", newBuildingName)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Log.w(TAG, "Error updating document", e);
                        }
                    });

            goToMap();
        }

        if (v.getId() == R.id.btnClose) {
            goToMap();
        }

        if (v.getId() == R.id.btnDeleteBuilding) {

            context = ActBuildingDetails.this;
            ad = new AlertDialog.Builder(context);

            String title = "Внимание!";
            String message = "Вы действительно хотите снести здание? Вы получите 50% от стоимости его последнего улучшения.";
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

                                    getCost = false;
                                    getUserMoney = false;
                                    more = true;
                                    payIsOk = true;

                                    //Получаем стоимость постройки
                                    String bType = null;
                                    if (buildingType.equals(buildingTypeHQ)) {
                                        bType = buildingTypeHQEn;
                                    } else if (buildingType.equals(buildingTypeWood)) {
                                        bType = buildingTypeWoodEn;
                                    } else if (buildingType.equals(buildingTypeStone)) {
                                        bType = buildingTypeStoneEn;
                                    } else if (buildingType.equals(buildingTypeClay)) {
                                        bType = buildingTypeClayEn;
                                    }

                                    FirebaseFirestore db9 = FirebaseFirestore.getInstance();
                                    String doc = bType + buildingLVL;

                                    DocumentReference bInfoRef = db9.collection("bInfo").document(doc);
                                    bInfoRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    costGold = document.getLong("CostGold");
                                                    costWood = document.getLong("CostWood");
                                                    costStone = document.getLong("CostStone");
                                                    costClay = document.getLong("CostClay");

                                                    getCost = true;
                                                }
                                            }
                                        }
                                    });

                                    //Получаем количество денег и ресурсов у пользователя
                                    FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                                    DocumentReference userRef = db1.collection("users").document(currentUserGoogleEmail);
                                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    userGold = document.getLong("userGold");
                                                    userWood = document.getLong("userWood");
                                                    userStone = document.getLong("userStone");
                                                    userClay = document.getLong("userClay");

                                                    getUserMoney = true;
                                                }
                                            }
                                        }
                                    });

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            while (more) {
                                                if ((getCost) && (getUserMoney)) {

                                                    long returnGold = costGold / 2;
                                                    long returnWood = costWood / 2;
                                                    long returnStone = costStone / 2;
                                                    long returnClay = costClay / 2;

                                                    HashMap<String, Object> updatedData = new HashMap<>();
                                                    updatedData.put("userGold", userGold + returnGold);
                                                    updatedData.put("userWood", userWood + returnWood);
                                                    updatedData.put("userStone", userStone + returnStone);
                                                    updatedData.put("userClay", userClay + returnClay);

                                                    FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                                                    DocumentReference documentReference = db2.collection(fbUsers).document(currentUserGoogleEmail);
                                                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                                            @Nullable FirebaseFirestoreException e) {

                                                            if (snapshot != null && snapshot.exists()) {
                                                                isHQAviable = snapshot.getBoolean("isHQAviable");
                                                            }
                                                        }
                                                    });

                                                    more = false;
                                                    payIsOk = true;

                                                    for (Map.Entry entry : updatedData.entrySet()) {
                                                        documentReference.update(entry.getKey().toString(), entry.getValue());
                                                    }

                                                    if (!more) {
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }).start();

                                    if ((!isHQAviable) && (buildingType.equals(buildingTypeHQ))) {

                                        FirebaseFirestore dbU = FirebaseFirestore.getInstance();
                                        DocumentReference uRef = dbU.collection(fbUsers).document(currentUserGoogleEmail);
                                        uRef
                                                .update("isHQAviable", true)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
//                                                                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
//                                                                        Log.w(TAG, "Error updating document", e);
                                                    }
                                                });
                                    }
                                    Toast.makeText(getBaseContext(), "Постройка снесена!", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getBaseContext(), "Постройка не снесена!", Toast.LENGTH_LONG).show();
                                }
                            });

                    goToMap();
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

        if (v.getId() == R.id.btnRename) {
            renameBuilding();
        }

        if (v.getId() == R.id.btnUpgrade) {
            //Создание диалогового окна
            final LayoutInflater inflater = LayoutInflater.from(this);
            View dialogView = inflater.inflate(R.layout.update_building_dialog, null);

            //Cоздаем AlertDialog
            AlertDialog.Builder mDialogNBuilder = new AlertDialog.Builder(this);

            //Прикручиваем лейаут к алерту
            mDialogNBuilder.setView(dialogView);

            mDialogNBuilder
                    .setCancelable(true)
                    .setNegativeButton("И так сойдёт", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })

                    .setPositiveButton("Да, конечно!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            upgradeBuilding();
                        }
                    });

            AlertDialog alertDialog = mDialogNBuilder.create();
            alertDialog.show();
        }
    }

    private void upgradeBuilding() {
        if ((buildingLVL == 10) && (!buildingType.equals(buildingTypeHQ))) {
            Toast.makeText(getBaseContext(), "Достигнут максимальный уровень постройки!", Toast.LENGTH_LONG).show();
            return;
        } else if ((buildingLVL == 3) && (buildingType.equals(buildingTypeHQ))){
            Toast.makeText(getBaseContext(), "Достигнут максимальный уровень постройки!", Toast.LENGTH_LONG).show();
        }

        buildingLVLPlus1 = buildingLVL + 1;
        String bType = null;
        if (buildingType.equals(buildingTypeHQ)) {
            bType = buildingTypeHQEn;
        } else if (buildingType.equals(buildingTypeWood)) {
            bType = buildingTypeWoodEn;
        } else if (buildingType.equals(buildingTypeStone)) {
            bType = buildingTypeStoneEn;
        } else if (buildingType.equals(buildingTypeClay)) {
            bType = buildingTypeClayEn;
        }

        //Получаем стоимость постройки
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String doc = bType + buildingLVLPlus1;

        DocumentReference bInfoRef = db.collection("bInfo").document(doc);
        bInfoRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        costGold = document.getLong("CostGold");
                        costWood = document.getLong("CostWood");
                        costStone = document.getLong("CostStone");
                        costClay = document.getLong("CostClay");

                        newIncomeGold = document.getLong("IncomeGold");
                        newIncomeWood = document.getLong("IncomeWood");
                        newIncomeStone = document.getLong("IncomeStone");
                        newIncomeClay = document.getLong("IncomeClay");

                        //Получаем количество денег и ресурсов у пользователя
                        FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                        DocumentReference userRef = db1.collection(fbUsers).document(currentUserGoogleEmail);
                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        userGold = document.getLong("userGold");
                                        userWood = document.getLong("userWood");
                                        userStone = document.getLong("userStone");
                                        userClay = document.getLong("userClay");

                                        if (((costGold > 0) && (userGold >= costGold)) ||
                                                ((costWood > 0) && (userWood >= costWood)) ||
                                                ((costStone > 0) && (userStone > costStone)) ||
                                                ((costClay > 0) && (userClay > costClay))) {

                                            //Апгрейдим новое количество ресурсов пользователя
                                            HashMap<String, Object> updatedData = new HashMap<>();
                                            updatedData.put("userGold", userGold - costGold);
                                            updatedData.put("userWood", userWood - costWood);
                                            updatedData.put("userStone", userStone - costStone);
                                            updatedData.put("userClay", userClay - costClay);

                                            FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                                            DocumentReference documentReference = db2.collection(fbUsers).document(currentUserGoogleEmail);

                                            for (Map.Entry entry : updatedData.entrySet()) {
                                                documentReference.update(entry.getKey().toString(), entry.getValue());
                                            }

                                            //Апгрейдим постройку
                                            HashMap<String, Object> updatedDataBuilding = new HashMap<>();
                                            updatedDataBuilding.put("buildingLVL", buildingLVLPlus1);
                                            updatedDataBuilding.put("incomeGold", newIncomeGold);
                                            updatedDataBuilding.put("incomeWood", newIncomeWood);
                                            updatedDataBuilding.put("incomeStone", newIncomeStone);
                                            updatedDataBuilding.put("incomeClay", newIncomeClay);

                                            FirebaseFirestore db20 = FirebaseFirestore.getInstance();
                                            DocumentReference documentReference1 = db20.collection("buildings").document(buildingID);

                                            for (Map.Entry entry : updatedDataBuilding.entrySet()) {
                                                documentReference1.update(entry.getKey().toString(), entry.getValue());
                                            }

                                            Toast.makeText(getBaseContext(), "Улучшение завершено!", Toast.LENGTH_SHORT).show();

                                            Intent intentActMap = new Intent(getBaseContext(), ActMap.class);
                                            intentActMap.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
                                            startActivity(intentActMap);

//                                            documentReference1
//                                                    .update("buildingLVL", buildingLVLPlus1)
//                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                        @Override
//                                                        public void onSuccess(Void aVoid) {
//                                                            Toast.makeText(getBaseContext(), "Улучшение завершено!", Toast.LENGTH_SHORT).show();
//
//                                                            Intent intentActMap = new Intent(getBaseContext(), ActMap.class);
//                                                            intentActMap.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
//                                                            startActivity(intentActMap);
//
//                                                        }
//                                                    })
//                                                    .addOnFailureListener(new OnFailureListener() {
//                                                        @Override
//                                                        public void onFailure(@NonNull Exception e) {
////                                                            Log.w(TAG, "Error updating document", e);
//                                                        }
//                                                    });
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public void renameBuilding() {

        //Создание диалогового окна
        final LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.rename_building_dialog, null);

        //Cоздаем AlertDialog
        AlertDialog.Builder mDialogNBuilder = new AlertDialog.Builder(this);

        //Прикручиваем лейаут к алерту
        mDialogNBuilder.setView(dialogView);

        //Инициализация компонентов
        etRenameBuildingName = (EditText) dialogView.findViewById(R.id.etRenameBuildingName);
        etRenameBuildingName.setText(tvBuildingName.getText());

        mDialogNBuilder
                .setCancelable(true)
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newBuildingName = String.valueOf(etRenameBuildingName.getText());
                        tvBuildingName.setText(newBuildingName);
                    }
                });

        AlertDialog alertDialog = mDialogNBuilder.create();
        alertDialog.show();
    }

    private void goToMap(){
        Intent intentActMap = new Intent(this, ActMap.class);
        intentActMap.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
        startActivity(intentActMap);
    }
}