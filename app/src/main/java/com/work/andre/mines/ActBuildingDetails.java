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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import static com.work.andre.mines.ActMap.USERGOOGLEEMAIL;
import static com.work.andre.mines.database.DBase.buildingTypeClay;
import static com.work.andre.mines.database.DBase.buildingTypeHQ;
import static com.work.andre.mines.database.DBase.buildingTypeStone;
import static com.work.andre.mines.database.DBase.buildingTypeWood;
import static com.work.andre.mines.database.DBase.fbBuildings;
import static com.work.andre.mines.database.DBase.fbInfo;
import static com.work.andre.mines.database.DBase.fbUsers;
import static com.work.andre.mines.database.DBase.getEnBuildingType;

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

    static long buildingLVLPlus1;

    public static final String BUILDINGID = "buildingID";

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
    TextView tvBuildingIncomeText;
    TextView tvBuildingIncomeInfo;

    TextView tvCurrentData;
    TextView tvAfterUpdateInfo;
    TextView tvCostInfo;

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

    static long buildingIncomeGold;
    static long buildingIncomeWood;
    static long buildingIncomeStone;
    static long buildingIncomeClay;

    static String newBuildingName;
    static String printStr;
    static String printStrUpdate;

    FirebaseFirestore dbMines;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_details);

        dbMines = FirebaseFirestore.getInstance();

        btnOK = (Button) findViewById(R.id.btnOK);
        btnRename = (Button) findViewById(R.id.btnRename);
        btnUpgrade = (Button) findViewById(R.id.btnUpgrade);
        btnDeleteBuilding = (Button) findViewById(R.id.btnDeleteBuilding);

        btnOK.setVisibility(View.INVISIBLE);
        btnRename.setVisibility(View.INVISIBLE);
        btnUpgrade.setVisibility(View.INVISIBLE);
        btnDeleteBuilding.setVisibility(View.INVISIBLE);

        currentUserGoogleEmail = getIntent().getStringExtra(USERGOOGLEEMAIL);
        buildingID = getIntent().getStringExtra(BUILDINGID);

        DocumentReference docRef = dbMines.collection(fbBuildings).document(buildingID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    buildingName = (String) snapshot.get("buildingName");
                    buildingLVL = (long) snapshot.get("buildingLVL");
                    buildingType = (String) snapshot.get("buildingType");

                    buildingIncomeGold = (long) snapshot.get("incomeGold");
                    buildingIncomeWood = (long) snapshot.get("incomeWood");
                    buildingIncomeStone = (long) snapshot.get("incomeStone");
                    buildingIncomeClay = (long) snapshot.get("incomeClay");

                    printStr = "";

                    if (buildingIncomeGold > 0) {
                        printStr = "Золота: " + buildingIncomeGold + "\n";
                    }
                    if (buildingIncomeWood > 0) {
                        printStr = printStr + "Дерева: " + buildingIncomeWood + "\n";
                    }
                    if (buildingIncomeStone > 0) {
                        printStr = printStr + "Камня: " + buildingIncomeStone + "\n";
                    }
                    if (buildingIncomeClay > 0) {
                        printStr = printStr + "Глины: " + buildingIncomeClay + "\n";
                    }
                    tvBuildingIncomeInfo.setText(printStr);

                    buildingOwnerGoogleEmail = (String) snapshot.get("userGoogleEmail");

                    DocumentReference docRef = dbMines.collection(fbUsers).document(buildingOwnerGoogleEmail);
                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
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
        tvBuildingTypeInfo.setText(buildingType);
        tvBuildingLVLInfo.setText(String.valueOf(buildingLVL));

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
        } catch (Exception ignored) {
        }
    }

    private void initUI() {
        btnClose = (Button) findViewById(R.id.btnClose);
        tvBuildingName = (TextView) findViewById(R.id.tvBuildingName);
        tvBuildingLVLText = (TextView) findViewById(R.id.tvBuildingLVLText);
        tvBuildingLVLInfo = (TextView) findViewById(R.id.tvBuildingLVLInfo);
        ivBuildingPicture = (ImageView) findViewById(R.id.ivBuildingPicture);
        tvBuildingTypeText = (TextView) findViewById(R.id.tvBuildingTypeText);
        tvBuildingTypeInfo = (TextView) findViewById(R.id.tvBuildingTypeInfo);
        tvBuildingOwnerText = (TextView) findViewById(R.id.tvBuildingOwnerText);
        tvBuildingOwnerInfo = (TextView) findViewById(R.id.tvBuildingOwnerInfo);
        tvBuildingIncomeText = (TextView) findViewById(R.id.tvBuildingIncomeText);
        tvBuildingIncomeInfo = (TextView) findViewById(R.id.tvBuildingIncomeInfo);

        etRenameBuildingName = (EditText) findViewById(R.id.etRenameBuildingName);

        btnClose.setOnClickListener(this);

        if (currentUserGoogleEmail.equals(buildingOwnerGoogleEmail)) {
            btnOK.setVisibility(View.VISIBLE);
            btnRename.setVisibility(View.VISIBLE);
            btnUpgrade.setVisibility(View.VISIBLE);
            btnDeleteBuilding.setVisibility(View.VISIBLE);

            btnOK.setOnClickListener(this);
            btnRename.setOnClickListener(this);
            btnUpgrade.setOnClickListener(this);
            btnDeleteBuilding.setOnClickListener(this);

        } else {
            btnOK.setVisibility(View.INVISIBLE);
            btnRename.setVisibility(View.INVISIBLE);
            btnUpgrade.setVisibility(View.INVISIBLE);
            btnDeleteBuilding.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        //Нажатие кнопки "ОК"
        if (v.getId() == R.id.btnOK) {
            if (newBuildingName != null) {
                DocumentReference buildingRef = dbMines.collection(fbBuildings).document(buildingID);
                buildingRef.update("buildingName", newBuildingName)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                goToMap();
                            }
                        });
            }
            goToMap();
        }

        //Нажатие кнопки "Закрыть"
        if (v.getId() == R.id.btnClose) {
            goToMap();
        }

        //Нажатие кнопки "Удалить"
        if (v.getId() == R.id.btnDeleteBuilding) {
            deleteBuilding();
        }

        //Нажатие кнопки "Переименовать"
        if (v.getId() == R.id.btnRename) {
            renameBuilding();
        }

        //Нажатие кнопки "Улучшить"
        if (v.getId() == R.id.btnUpgrade) {
            upgradeBuilding();
        }
    }

    private void upgradeBuilding() {
        //Создание диалогового окна
        final LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.update_building_dialog, null);

        //Cоздаем AlertDialog
        AlertDialog.Builder mDialogNBuilder = new AlertDialog.Builder(this);

        //Прикручиваем лейаут к алерту
        mDialogNBuilder.setView(dialogView);

        tvCurrentData = (TextView) dialogView.findViewById(R.id.tvCurrentData);
        tvAfterUpdateInfo = (TextView) dialogView.findViewById(R.id.tvAfterUpdateInfo);
        tvCostInfo = (TextView) dialogView.findViewById(R.id.tvCostInfo);


        printStrUpdate = "Текущий уровень: " + buildingLVL + "\n";

        if (buildingIncomeGold > 0) {
            printStrUpdate = "Приход золота: " + buildingIncomeGold + "\n";
        }
        if (buildingIncomeWood > 0) {
            printStrUpdate = printStrUpdate + "Приход дерева: " + buildingIncomeWood + "\n";
        }
        if (buildingIncomeStone > 0) {
            printStrUpdate = printStrUpdate + "Приход камня: " + buildingIncomeStone + "\n";
        }
        if (buildingIncomeClay > 0) {
            printStrUpdate = printStrUpdate + "Приход глины: " + buildingIncomeClay + "\n";
        }

        tvCurrentData.setText(printStrUpdate);

        //Получаем информацию о постройки после улучшения
        buildingLVLPlus1 = buildingLVL + 1;
        String bType = getEnBuildingType(buildingType);
        String doc = bType + buildingLVLPlus1;

        DocumentReference bInfoRef = dbMines.collection(fbInfo).document(doc);
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

                        String printStrAfterUpdate = "";
                        printStrAfterUpdate = "Уровень: " + buildingLVLPlus1 + "\n";
                        if (newIncomeGold > 0) {
                            printStrAfterUpdate = "Приход золота: " + newIncomeGold + "\n";
                        }
                        if (newIncomeWood > 0) {
                            printStrAfterUpdate = printStrAfterUpdate + "Приход дерева: " + newIncomeWood + "\n";
                        }
                        if (newIncomeStone > 0) {
                            printStrAfterUpdate = printStrAfterUpdate + "Приход камня: " + newIncomeStone + "\n";
                        }
                        if (newIncomeClay > 0) {
                            printStrAfterUpdate = printStrAfterUpdate + "Приход глины: " + newIncomeClay + "\n";
                        }

                        tvAfterUpdateInfo.setText(printStrAfterUpdate);


                        String printStrCostUpdate = "";
                        if (costGold > 0) {
                            printStrCostUpdate = "Золота: " + costGold + "\n";
                        }
                        if (costWood > 0) {
                            printStrCostUpdate = printStrCostUpdate + "Дерева: " + costWood + "\n";
                        }
                        if (costStone > 0) {
                            printStrCostUpdate = printStrCostUpdate + "Камня: " + costStone + "\n";
                        }
                        if (costClay > 0) {
                            printStrCostUpdate = printStrCostUpdate + "Глины: " + costClay + "\n";
                        }

                        tvCostInfo.setText(printStrCostUpdate);

                    }
                }
            }
        });


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
                        if ((buildingLVL == 10) && (!buildingType.equals(buildingTypeHQ))) {
                            Toast.makeText(getBaseContext(), "Достигнут максимальный уровень постройки!", Toast.LENGTH_LONG).show();
                            return;
                        } else if ((buildingLVL == 3) && (buildingType.equals(buildingTypeHQ))) {
                            Toast.makeText(getBaseContext(), "Достигнут максимальный уровень постройки!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        buildingLVLPlus1 = buildingLVL + 1;

                        //Получаем стоимость постройки
                        String bType = getEnBuildingType(buildingType);
                        String doc = bType + buildingLVLPlus1;

                        DocumentReference bInfoRef = dbMines.collection(fbInfo).document(doc);
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

                                        //Получаем количество ресурсов у пользователя
                                        DocumentReference userRef = dbMines.collection(fbUsers).document(currentUserGoogleEmail);
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

                                                        if ((costGold > 0) && (userGold < costGold)) {
                                                            Toast.makeText(getBaseContext(), "Недостаточно средств!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        if ((costWood > 0) && (userWood < costWood)) {
                                                            Toast.makeText(getBaseContext(), "Недостаточно средств!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        if ((costStone > 0) && (userStone < costStone)) {
                                                            Toast.makeText(getBaseContext(), "Недостаточно средств!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        if ((costClay > 0) && (userClay < costClay)) {
                                                            Toast.makeText(getBaseContext(), "Недостаточно средств!", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }

                                                        //Апгрейдим новое количество ресурсов пользователя
                                                        HashMap<String, Object> updatedData = new HashMap<>();
                                                        updatedData.put("userGold", userGold - costGold);
                                                        updatedData.put("userWood", userWood - costWood);
                                                        updatedData.put("userStone", userStone - costStone);
                                                        updatedData.put("userClay", userClay - costClay);

                                                        DocumentReference documentReference = dbMines.collection(fbUsers).document(currentUserGoogleEmail);

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

                                                        DocumentReference documentBuilding = dbMines.collection(fbBuildings).document(buildingID);

                                                        for (Map.Entry entry : updatedDataBuilding.entrySet()) {
                                                            documentBuilding.update(entry.getKey().toString(), entry.getValue());
                                                        }
                                                        Toast.makeText(getBaseContext(), "Улучшение завершено!", Toast.LENGTH_SHORT).show();
                                                        goToMap();

                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                });

        AlertDialog alertDialog = mDialogNBuilder.create();
        alertDialog.show();
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
        etRenameBuildingName = dialogView.findViewById(R.id.etRenameBuildingName);
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

    private void deleteBuilding() {
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
                dbMines.collection(fbBuildings).document(buildingID)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                //Получаем стоимость постройки
                                String bType = getEnBuildingType(buildingType);
                                String doc = bType + buildingLVL;

                                DocumentReference deletedBuildingRef = dbMines.collection(fbInfo).document(doc);
                                deletedBuildingRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                costGold = document.getLong("CostGold");
                                                costWood = document.getLong("CostWood");
                                                costStone = document.getLong("CostStone");
                                                costClay = document.getLong("CostClay");

                                                //Получаем количество ресурсов у пользователя
                                                DocumentReference userRef = dbMines.collection(fbUsers).document(currentUserGoogleEmail);
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

                                                                //Возвращаем пользователю половину ресурсов
                                                                long returnGold = costGold / 2;
                                                                long returnWood = costWood / 2;
                                                                long returnStone = costStone / 2;
                                                                long returnClay = costClay / 2;

                                                                HashMap<String, Object> updatedData = new HashMap<>();
                                                                updatedData.put("userGold", userGold + returnGold);
                                                                updatedData.put("userWood", userWood + returnWood);
                                                                updatedData.put("userStone", userStone + returnStone);
                                                                updatedData.put("userClay", userClay + returnClay);

                                                                DocumentReference userRefUpdate = dbMines.collection(fbUsers).document(currentUserGoogleEmail);
                                                                for (Map.Entry entry : updatedData.entrySet()) {
                                                                    userRefUpdate.update(entry.getKey().toString(), entry.getValue());
                                                                }

                                                                userRefUpdate.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            DocumentSnapshot document = task.getResult();
                                                                            if (document.exists()) {
                                                                                isHQAviable = document.getBoolean("isHQAviable");

                                                                                if ((!isHQAviable) && (buildingType.equals(buildingTypeHQ))) {
                                                                                    DocumentReference uRef = dbMines.collection(fbUsers).document(currentUserGoogleEmail);
                                                                                    uRef.update("isHQAviable", true)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void aVoid) {
                                                                                                    //OK
                                                                                                }
                                                                                            });
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
                                        }
                                    }
                                });
                                Toast.makeText(getBaseContext(), "Постройка снесена!", Toast.LENGTH_LONG).show();
                                goToMap();
                            }
                        });
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

    private void goToMap() {
        Intent intentActMap = new Intent(this, ActMap.class);
        intentActMap.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
        startActivity(intentActMap);
    }
}