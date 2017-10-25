package com.work.andre.mines.database;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class DBase {

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

    //Получить категорию постройки по её типу
    public static String getBuildingCategoryByBuildingType(String buildingType) {
        if ((buildingType.equals(buildingTypeWood)) || (buildingType.equals(buildingTypeStone)) || (buildingType.equals(buildingTypeClay))) {
            return buildingCategoryMining;
        } else return buildingCategoryOffice;
    }

    //Получить ID постройки
    public static String getBuildingID(double buildingLat, double buildingLng) {
        return String.valueOf(buildingLat) + " " + String.valueOf(buildingLng);
    }

    //Получить ID постройки
    public static String getBuildingID(String buildingLat, String buildingLng) {
        return String.valueOf(buildingLat) + " " + String.valueOf(buildingLng);
    }

    //Добавить новую постройку
    public static void addNewBuilding(String userNickName, String userGoogleEmail, String buildingType, String buildingCategory, String buildingName, long buildingLVL, long incomeGold, long incomeWood, long incomeStone, long incomeClay, double buildingLat, double buildingLng, String buildingBuildDate) {
        String strLat = String.valueOf(buildingLat);
        String strLng = String.valueOf(buildingLng);
        String buildingID = getBuildingID(buildingLat, buildingLng);

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

        db.collection(fbBuildings).document(buildingID).set(newBuilding);
    }

//    public boolean payPriceForBuilding(long id, String buildingType, int buildingLVL) {
//
//        int userID = (int) id;
//        ContentValues cvPrice = new ContentValues();
//
//        List<Integer> buildingCost = MyApp.getMyDBase().getBuildingCostByLVLandType(buildingType, buildingLVL);
//        int priceGold = buildingCost.get(0);
//        int priceWood = buildingCost.get(1);
//        int priceStone = buildingCost.get(2);
//        int priceClay = buildingCost.get(3);
//
//        List<Integer> userResources = MyApp.getMyDBase().getUserResources(userID);
//        int userGold = userResources.get(0);
//        int userWood = userResources.get(1);
//        int userStone = userResources.get(2);
//        int userClay = userResources.get(3);
//
//        int newUserGold = userGold - priceGold;
//        int newUserWood = userWood - priceWood;
//        int newUserStone = userStone - priceStone;
//        int newUserClay = userClay - priceClay;
//
//        cvPrice.put(UsersTable.COLUMN_USER_GOLD, newUserGold);
//        cvPrice.put(UsersTable.COLUMN_USER_WOOD, newUserWood);
//        cvPrice.put(UsersTable.COLUMN_USER_STONE, newUserStone);
//        cvPrice.put(UsersTable.COLUMN_USER_CLAY, newUserClay);
//
//        return 1 == this.getWritableDatabase().update(UsersTable.TABLE_USERS, cvPrice,
//                SQL_WHERE_BY_ID, new String[]{String.valueOf(id)});
//    }

//    public long payPriceForBuilding(String userGoogleEmail, String buildingType, int buildingLVL) {
//
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        // Create a reference to the cities collection
//        String doc = buildingType + buildingLVL;
//        DocumentReference bInfoRef = db.collection("bInfo").document(doc);
//        bInfoRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document != null) {
//                        myPrice = Long.parseLong(document.getString("CostGold"));
//                        //                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
//                    } else {
////                        Log.d(TAG, "No such document");
//                    }
//                } else {
////                    Log.d(TAG, "get failed with ", task.getException());
//                }
//
//
//            }
//
//        });

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

    public static String getUserNickNameOrDisplayNameByGoogleEmailWithSnapshot(DocumentSnapshot snapshot) {
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