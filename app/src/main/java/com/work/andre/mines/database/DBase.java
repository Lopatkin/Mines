package com.work.andre.mines.database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;

import java.util.HashMap;
import java.util.Map;

import static com.work.andre.mines.ActBuildingDetails.tvAfterUpdateInfo;
import static com.work.andre.mines.ActBuildingDetails.tvCostInfo;

public class DBase {

    public static FirebaseFirestore dbMines = FirebaseFirestore.getInstance();

    static boolean isContinue;
    public static String printStrAfterUpdate;
    public static String printStrCostUpdate;

    static long costGold;
    static long costWood;
    static long costStone;
    static long costClay;

    static long userGold;
    static long userWood;
    static long userStone;
    static long userClay;

    static long newIncomeGold;
    static long newIncomeWood;
    static long newIncomeStone;
    static long newIncomeClay;

    public static LatLng myTarget;

    public static boolean canIputMyBuildHere;

    //FIREBASE
    public static DatabaseReference myRef;

    public static int resValue;
    public static String myResType;

    public static String myNick;
    public static String fbUsers = "users";
    public static String fbBuildings = "buildings";
    public static String fbInfo = "bInfo";
    public static String area_ = "area_";
    public static String loc_ = "loc_";

    public static double minDistanceBetweenTwoBuildings = 100.0; //Минимальное расстояние между шахтами

    public static String buildingTypeHQ = "Штаб";
    public static String buildingTypeWood = "Лесопилка";
    public static String buildingTypeStone = "Рудник";
    public static String buildingTypeClay = "Карьер";

    public static String buildingTypeHQEn = "HQ";
    public static String buildingTypeWoodEn = "WOOD";
    public static String buildingTypeStoneEn = "STONE";
    public static String buildingTypeClayEn = "CLAY";

    public static String buildingCategoryMining = "Добывающая";
    public static String buildingCategoryOffice = "Офисная";

    public static String resGold = "Gold";
    public static String resWood = "Wood";
    public static String resStone = "Stone";
    public static String resClay = "Clay";

    public static String getEnBuildingType(String buildingType) {
        if (buildingType.equals(buildingTypeHQ)) {
            return buildingTypeHQEn;
        } else if (buildingType.equals(buildingTypeWood)) {
            return buildingTypeWoodEn;
        } else if (buildingType.equals(buildingTypeStone)) {
            return buildingTypeStoneEn;
        } else if (buildingType.equals(buildingTypeClay)) {
            return buildingTypeClayEn;
        }
        return null;
    }

    public static String getBuildingCategoryByBuildingType(String buildingType) {
        if ((buildingType.equals(buildingTypeWood)) || (buildingType.equals(buildingTypeStone)) || (buildingType.equals(buildingTypeClay))) {
            return buildingCategoryMining;
        } else return buildingCategoryOffice;
    }

    public static String getBuildingID(double buildingLat, double buildingLng) {
        return String.valueOf(buildingLat) + " " + String.valueOf(buildingLng);
    }

    public static String getBuildingID(String buildingLat, String buildingLng) {
        return String.valueOf(buildingLat) + " " + String.valueOf(buildingLng);
    }

    public static String getDocArea(String strLat, String strLng) {
        String docAreaLat = strLat.substring(0, 2);
        String docAreaLng = strLng.substring(0, 2);
        return area_ + docAreaLat + "_" + docAreaLng;
    }

    public static String getColLoc(String strLat, String strLng) {
        String colLocLat = strLat.substring(0, 5);
        String colLocLng = strLng.substring(0, 5);
        return loc_ + colLocLat + "_" + colLocLng;
    }

    public static String getDocAreaByBuildingID(String buildingID) {

        //55.755534068756944 37.51492425799369  вход
        //area_55_37                            выход

        String[] splitted = buildingID.split(" ");
        String preDocAreaLat = splitted[0];
        String preDocAreaLng = splitted[1];

        String docAreaLat = preDocAreaLat.substring(0, 2);
        String docAreaLng = preDocAreaLng.substring(0, 2);

        return area_ + docAreaLat + "_" + docAreaLng;
    }

    public static String getColLocByBuildingID(String buildingID) {

        //55.755534068756944 37.51492425799369  вход
        //loc_55.75_37.51                       выход

        String[] splitted = buildingID.split(" ");
        String preColLocLat = splitted[0];
        String preColLocLng = splitted[1];

        String colLocLat = preColLocLat.substring(0, 5);
        String colLocLng = preColLocLng.substring(0, 5);

        return loc_ + colLocLat + "_" + colLocLng;
    }

    public static void getBuildingInfoAfterUpdate(String buildingType, final long buildingLVL) {

        //Получаем информацию о постройке после улучшения
        String doc = getEnBuildingType(buildingType) + (buildingLVL + 1);

        printStrAfterUpdate = "загрузка...";

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

                        printStrAfterUpdate = "Уровень: " + (buildingLVL + 1) + "\n";
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
    }

    public static void upgradeBuilding(final Context context, String buildingType, final long buildingLVL, final String userGoogleEmail, final String buildingID) {

        //Получаем стоимость постройки
        String doc = getEnBuildingType(buildingType) + (buildingLVL + 1);

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
                        DocumentReference userRef = dbMines.collection(fbUsers).document(userGoogleEmail);
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
                                            Toast.makeText(context, "Недостаточно средств!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        if ((costWood > 0) && (userWood < costWood)) {
                                            Toast.makeText(context, "Недостаточно средств!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        if ((costStone > 0) && (userStone < costStone)) {
                                            Toast.makeText(context, "Недостаточно средств!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        if ((costClay > 0) && (userClay < costClay)) {
                                            Toast.makeText(context, "Недостаточно средств!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        //Апгрейдим новое количество ресурсов пользователя
                                        HashMap<String, Object> updatedData = new HashMap<>();
                                        updatedData.put("userGold", userGold - costGold);
                                        updatedData.put("userWood", userWood - costWood);
                                        updatedData.put("userStone", userStone - costStone);
                                        updatedData.put("userClay", userClay - costClay);

                                        DocumentReference documentReference = dbMines.collection(fbUsers).document(userGoogleEmail);

                                        for (Map.Entry entry : updatedData.entrySet()) {
                                            documentReference.update(entry.getKey().toString(), entry.getValue());
                                        }

                                        //Апгрейдим постройку
                                        HashMap<String, Object> updatedDataBuilding = new HashMap<>();
                                        updatedDataBuilding.put("buildingLVL", (buildingLVL + 1));
                                        updatedDataBuilding.put("incomeGold", newIncomeGold);
                                        updatedDataBuilding.put("incomeWood", newIncomeWood);
                                        updatedDataBuilding.put("incomeStone", newIncomeStone);
                                        updatedDataBuilding.put("incomeClay", newIncomeClay);

                                        //Апгрейдим постройку в общей таблице
                                        DocumentReference documentBuilding = dbMines.collection(fbBuildings).document(getDocAreaByBuildingID(buildingID)).collection(getColLocByBuildingID(buildingID)).document(buildingID);
                                        for (Map.Entry entry : updatedDataBuilding.entrySet()) {
                                            documentBuilding.update(entry.getKey().toString(), entry.getValue());
                                        }

                                        //Апгрейдим постройку в таблице пользователя
                                        DocumentReference documentUserBuilding = dbMines.collection(fbUsers).document(userGoogleEmail).collection(fbBuildings).document(buildingID);
                                        for (Map.Entry entry : updatedDataBuilding.entrySet()) {
                                            documentUserBuilding.update(entry.getKey().toString(), entry.getValue());
                                        }
                                        Toast.makeText(context, "Улучшение завершено!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public static void renameBuilding(String buildingID, String userGoogleEmail, String newBuildingName) {
        //Меняем название в списке баз пользователя
        DocumentReference userBuildingRef = dbMines.collection(fbUsers).document(userGoogleEmail).collection(fbBuildings).document(buildingID);
        userBuildingRef.update("buildingName", newBuildingName);

        //Меняем название в общем списке построек
        String docArea = getDocAreaByBuildingID(buildingID);
        String colLoc = getColLocByBuildingID(buildingID);
        DocumentReference buildingRef = dbMines.collection(fbBuildings).document(docArea).collection(colLoc).document(buildingID);
        buildingRef.update("buildingName", newBuildingName);
    }

    public static boolean deleteBuilding(final String buildingID, final String userGoogleEmail, final String buildingType, final long buildingLVL) {

        String docArea = getDocAreaByBuildingID(buildingID);
        String colLoc = getColLocByBuildingID(buildingID);

        //Удаляем из общей таблицы
        dbMines.collection(fbBuildings).document(docArea).collection(colLoc).document(buildingID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void aVoid) {

                                              //Удаляем из таблицы пользователя
                                              dbMines.collection(fbUsers).document(userGoogleEmail).collection(fbBuildings).document(buildingID)
                                                      .delete()
                                                      .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                          @Override
                                                          public void onSuccess(Void aVoid) {

                                                              //Получаем стоимость постройки
                                                              String doc = getEnBuildingType(buildingType) + buildingLVL;
                                                              DocumentReference deletedBuildingInfoRef = dbMines.collection(fbInfo).document(doc);
                                                              deletedBuildingInfoRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                                                              DocumentReference userRef = dbMines.collection(fbUsers).document(userGoogleEmail);
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

                                                                                              DocumentReference userRefUpdate = dbMines.collection(fbUsers).document(userGoogleEmail);
                                                                                              for (Map.Entry entry : updatedData.entrySet()) {
                                                                                                  userRefUpdate.update(entry.getKey().toString(), entry.getValue());
                                                                                              }

                                                                                              //Если удалили штаб, то разрешаем построить его снова
                                                                                              if (buildingType.equals(buildingTypeHQ)) {
                                                                                                  userRefUpdate.update("isHQAviable", true);
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
                                                      });
                                          }
                                      }
                );
        return true;
    }

    public static void addNewBuilding(String userNickName, String userGoogleEmail, String
            buildingType, String buildingCategory, String buildingName, long buildingLVL,
                                      long incomeGold, long incomeWood, long incomeStone, long incomeClay, double buildingLat,
                                      double buildingLng, String buildingBuildDate) {
        String strLat = String.valueOf(buildingLat);
        String strLng = String.valueOf(buildingLng);

        String buildingID = getBuildingID(buildingLat, buildingLng);
        String docArea = getDocArea(strLat, strLng);
        String colLoc = getColLoc(strLat, strLng);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> newBuilding = new HashMap<>();
        newBuilding.put("buildingID", buildingID);
        newBuilding.put("userGoogleEmail", userGoogleEmail);
        newBuilding.put("userNickName", userNickName);
        newBuilding.put("buildingType", buildingType);
        newBuilding.put("buildingCategory", buildingCategory);
        newBuilding.put("buildingName", buildingName);
        newBuilding.put("buildingLVL", buildingLVL);
        newBuilding.put("incomeGold", incomeGold);
        newBuilding.put("incomeWood", incomeWood);
        newBuilding.put("incomeStone", incomeStone);
        newBuilding.put("incomeClay", incomeClay);
        newBuilding.put("buildingLat", strLat);
        newBuilding.put("buildingLng", strLng);
        newBuilding.put("buildingBuildDate", buildingBuildDate);

        db.collection(fbBuildings).document(docArea).collection(colLoc).document(buildingID).set(newBuilding);
        db.collection(fbUsers).document(userGoogleEmail).collection(fbBuildings).document(buildingID).set(newBuilding);
    }

//    //Добавить новую постройку
//    public static void addNewBuilding(String userNickName, String userGoogleEmail, String buildingType, String buildingCategory, String buildingName, long buildingLVL, long incomeGold, long incomeWood, long incomeStone, long incomeClay, double buildingLat, double buildingLng, String buildingBuildDate) {
//        String strLat = String.valueOf(buildingLat);
//        String strLng = String.valueOf(buildingLng);
//        String buildingID = getBuildingID(buildingLat, buildingLng);
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        Map<String, Object> newBuilding = new HashMap<>();
//        newBuilding.put("buildingID", buildingID);
//        newBuilding.put("userGoogleEmail", userGoogleEmail);
//        newBuilding.put("userNickName", userNickName);
//        newBuilding.put("buildingType", buildingType);
//        newBuilding.put("buildingCategory", buildingCategory);
//        newBuilding.put("buildingName", buildingName);
//        newBuilding.put("buildingLVL", buildingLVL);
//        newBuilding.put("incomeGold", incomeGold);
//        newBuilding.put("incomeWood", incomeWood);
//        newBuilding.put("incomeStone", incomeStone);
//        newBuilding.put("incomeClay", incomeClay);
//        newBuilding.put("buildingLat", strLat);
//        newBuilding.put("buildingLng", strLng);
//        newBuilding.put("buildingBuildDate", buildingBuildDate);
//
//        db.collection(fbBuildings).document(buildingID).set(newBuilding);
//    }

    //Проверка можно ли поставить здесь свою постройку, допустимо ли расстояние от соседних построек
    public static boolean canIPutMyNewBuildingHere(LatLng target) {

        canIputMyBuildHere = false;

        myTarget = target;

        FirebaseFirestore dbLoc = FirebaseFirestore.getInstance();
        dbLoc.collection(fbBuildings)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {

                                String buildingLatStr;
                                String buildingLngStr;

                                buildingLatStr = (String) document.get("buildingLat");
                                buildingLngStr = (String) document.get("buildingLng");

                                double buildingLat;
                                double buildingLng;

                                buildingLat = Double.parseDouble(buildingLatStr);
                                buildingLng = Double.parseDouble(buildingLngStr);

                                LatLng myBuilding = new LatLng(buildingLat, buildingLng);

                                if (SphericalUtil.computeDistanceBetween(myTarget, myBuilding) > minDistanceBetweenTwoBuildings) {
                                    canIputMyBuildHere = true;
                                }


//                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return canIputMyBuildHere;
    }

    //Получить ресурсы пользователя по типу
    public static int getUserResources(final String userGoogleEmail, String resType) {

        myResType = resType;

        //FIREBASE
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference(fbUsers);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                String allowedEmail = allowedEmail(userGoogleEmail);

                if (myResType.equals(resGold)) {
                    resValue = dataSnapshot.child(allowedEmail).child("userGold").getValue(Integer.class);
                } else if (myResType.equals(resWood)) {
                    resValue = dataSnapshot.child(allowedEmail).child("userWood").getValue(Integer.class);
                } else if (myResType.equals(resStone)) {
                    resValue = dataSnapshot.child(allowedEmail).child("userStone").getValue(Integer.class);
                } else if (myResType.equals(resClay)) {
                    resValue = dataSnapshot.child(allowedEmail).child("userClay").getValue(Integer.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        return resValue;

//        Cursor cr = MyApp.getMyDBase().getReadableCursor(UsersTable.TABLE_USERS);
//        if (cr.moveToFirst()) {
//            do {
//
//                int col_userGoogleEmail = 0;
//                int col_res = 0;
//
//                if (resType.equals(resGold)) {
//                    col_userGoogleEmail = cr.getColumnIndex(UsersTable.COLUMN_USERGOOGLEEMAIL);
//                    col_res = cr.getColumnIndex(UsersTable.COLUMN_USER_GOLD);
//                } else if (resType.equals(resWood)) {
//                    col_userGoogleEmail = cr.getColumnIndex(UsersTable.COLUMN_USERGOOGLEEMAIL);
//                    col_res = cr.getColumnIndex(UsersTable.COLUMN_USER_WOOD);
//                } else if (resType.equals(resStone)) {
//                    col_userGoogleEmail = cr.getColumnIndex(UsersTable.COLUMN_USERGOOGLEEMAIL);
//                    col_res = cr.getColumnIndex(UsersTable.COLUMN_USER_STONE);
//                } else if (resType.equals(resClay)) {
//                    col_userGoogleEmail = cr.getColumnIndex(UsersTable.COLUMN_USERGOOGLEEMAIL);
//                    col_res = cr.getColumnIndex(UsersTable.COLUMN_USER_CLAY);
//                }
//
//                if (cr.getString(col_userGoogleEmail).equals(userGoogleEmail)) {
//                    return cr.getInt(col_res);
//                }
//
//            } while (cr.moveToNext());
//        }
//        cr.close();
//        return 0;
    }

    //Получить ресурсы пользователя по типу
    public static long getUserResourcesWithSnapshot(DocumentSnapshot snapshot, String resType) {
        try {
            if (resType.equals(resGold)) {
                return (long) snapshot.get("userGold");
            } else if (resType.equals(resWood)) {
                return (long) snapshot.get("userWood");
            } else if (resType.equals(resStone)) {
                return (long) snapshot.get("userStone");
            } else if (resType.equals(resClay)) {
                return (long) snapshot.get("userClay");
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public static String getUserNickNameOrDisplayNameByGoogleEmail(String userGoogleEmail) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(fbUsers).document(userGoogleEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        String MyUserNickName = (String) document.get("NickName");
                        if (MyUserNickName.length() > 0) {
                            myNick = MyUserNickName;
                        } else {
                            myNick = (String) document.get("DisplayName");
                        }
                    }
                }
            }
        });
        return myNick;
    }

    public static String getUserNickNameOrDisplayNameByGoogleEmailWithSnapshot(DocumentSnapshot
                                                                                       snapshot) {
        String MyUserNickName = (String) snapshot.get("NickName");
        if (MyUserNickName.length() > 0) {
            return MyUserNickName;
        } else {
            return (String) snapshot.get("DisplayName");
        }
    }

    public static String allowedEmail(String email) {
        return email.replace(".", "@");
    }

    public static String allowedLatLng(double Lat, double Lng) {
        String allLat = String.valueOf(Lat).replace(".", "@");
        String allLng = String.valueOf(Lng).replace(".", "@");
        return allLat + " " + allLng;
    }

    public static String allowedKey(String myKey) {
        String mKey = String.valueOf(myKey).replace(".", "@");
        return mKey;
    }
}