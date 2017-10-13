package com.work.andre.mines.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.SphericalUtil;
import com.work.andre.mines.MyApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.work.andre.mines.ActBuildingDetails.buildingID;


public class DBase extends DBSQLite {

    public static long myPrice;
    public static LatLng myTarget;

    public static boolean canIputMyBuildHere;

    //FIREBASE
    public static DatabaseReference myRef;
    public static String myCurrentUserGoogleEmail;
    public static String myUEmail;
    static String userNickName;
    //..............................

    public static int resValue;
    public static String myResType;

    public static String myNick;
    public static String fbUsers = "users";
    public static String fbBuildings = "buildings";

    private static final String SQL_WHERE_BY_ID = BaseColumns._ID + "=?"; //НазваниеКолонки_id=?

    public static double minDistanceBetweenTwoBuildings = 100.0; //Минимальное расстояние между шахтами

    int userStartGold = 1000;
    int userStartWood = 100;
    int userStartStone = 100;
    int userStartClay = 100;

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

    public static final String DB_NAME = "DBE10000130.db";
    public static final int DB_VERSION = 2;

    public static String resGold = "Gold";
    public static String resWood = "Wood";
    public static String resStone = "Stone";
    public static String resClay = "Clay";


    public static String userGold = "userGold";
    public static String userWood = "userWood";
    public static String userStone = "userStone";
    public static String userClay = "userClay";


    public DBase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Создание базы данных с таблицами
    @Override
    public void onCreate(SQLiteDatabase db) {
        DBSQLite.execSQL(db, UsersTable.SQL_CREATE);
        DBSQLite.execSQL(db, BuildingsListTable.SQL_CREATE);
        DBSQLite.execSQL(db, BuildingsInfo.SQL_CREATE);
    }

    //Таблицы базы данных
    //Таблица с пользователями
    public static class UsersTable implements BaseColumns {

        public static final String TABLE_USERS = "Users";
        public static final String COLUMN_USERGOOGLEEMAIL = "UserGoogleEmail";
        public static final String COLUMN_DISPLAYNAME = "DisplayName";
        public static final String COLUMN_NICKNAME = "NickName";
        public static final String COLUMN_GIVENNAME = "GivenName";
        public static final String COLUMN_FAMILYNAME = "FamilyName";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_SEX = "sex";
        public static final String COLUMN_ISHQAVIABLE = "isHQAviable";
        public static final String COLUMN_USER_GOLD = "userGold";
        public static final String COLUMN_USER_WOOD = "userWood";
        public static final String COLUMN_USER_STONE = "userStone";
        public static final String COLUMN_USER_CLAY = "userClay";
        public static final String COLUMN_REGISTERDATE = "registerdate";

        public static final String SQL_CREATE = "CREATE TABLE " + TABLE_USERS +
                " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERGOOGLEEMAIL + " TEXT, " +
                COLUMN_DISPLAYNAME + " TEXT, " +
                COLUMN_NICKNAME + " TEXT, " +
                COLUMN_GIVENNAME + " TEXT, " +
                COLUMN_FAMILYNAME + " TEXT, " +
                COLUMN_AGE + " INTEGER, " +
                COLUMN_SEX + " TEXT, " +
                COLUMN_ISHQAVIABLE + " INTEGER, " +
                COLUMN_USER_GOLD + " LONG, " +
                COLUMN_USER_WOOD + " LONG, " +
                COLUMN_USER_STONE + " LONG, " +
                COLUMN_USER_CLAY + " LONG, " +
                COLUMN_REGISTERDATE + " TEXT)";
    }

    //Таблица с перечнем всех построек
    public static class BuildingsListTable implements BaseColumns {

        public static final String TABLE_BUILDINGS_LIST = "BuildingsList";
        public static final String COLUMN_BUILDING_OWNER_GOOGLE_EMAIL = "BuildingOwnerGoogleEmail";
        public static final String COLUMN_BUILDING_CATEGORY = "BuildingCategory";
        public static final String COLUMN_BUILDING_TYPE = "BuildingType";
        public static final String COLUMN_BUILDING_NAME = "BuildingName";
        public static final String COLUMN_BUILDING_LVL = "BuildingLVL";
        public static final String COLUMN_BUILDING_LNG = "BuildingLong";
        public static final String COLUMN_BUILDING_LAT = "BuildingLat";
        public static final String COLUMN_BUILDING_BUILD_DATE = "BuildingBuildDate";

        public static final String SQL_CREATE = "CREATE TABLE " + TABLE_BUILDINGS_LIST +
                " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BUILDING_OWNER_GOOGLE_EMAIL + " TEXT, " +
                COLUMN_BUILDING_CATEGORY + " TEXT, " +
                COLUMN_BUILDING_TYPE + " TEXT, " +
                COLUMN_BUILDING_NAME + " TEXT, " +
                COLUMN_BUILDING_LVL + " INTEGER, " +
                COLUMN_BUILDING_LNG + " DOUBLE, " +
                COLUMN_BUILDING_LAT + " DOUBLE, " +
                COLUMN_BUILDING_BUILD_DATE + " TEXT)";
    }

    //Таблица с информацией о постройках
    public static class BuildingsInfo implements BaseColumns {

        public static final String TABLE_BUILDINGS_INFO = "BuildingsInfo";
        public static final String COLUMN_BUILDING_TYPE = "BuildingType";
        public static final String COLUMN_BUILDING_LVL = "BuildingLVL";
        public static final String COLUMN_BUILDING_GOLD_COST = "BuildingGoldCost";
        public static final String COLUMN_BUILDING_WOOD_COST = "BuildingWoodCost";
        public static final String COLUMN_BUILDING_STONE_COST = "BuildingStoneCost";
        public static final String COLUMN_BUILDING_CLAY_COST = "BuildingClayCost";
        public static final String COLUMN_BUILDING_GOLD_INCOME = "BuildingGoldIncome";
        public static final String COLUMN_BUILDING_WOOD_INCOME = "BuildingWoodIncome";
        public static final String COLUMN_BUILDING_STONE_INCOME = "BuildingStoneIncome";
        public static final String COLUMN_BUILDING_CLAY_INCOME = "BuildingClayIncome";

        public static final String SQL_CREATE = "CREATE TABLE " + TABLE_BUILDINGS_INFO +
                " (" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BUILDING_TYPE + " TEXT, " +
                COLUMN_BUILDING_LVL + " INTEGER, " +
                COLUMN_BUILDING_GOLD_COST + " INTEGER, " +
                COLUMN_BUILDING_WOOD_COST + " INTEGER, " +
                COLUMN_BUILDING_STONE_COST + " INTEGER, " +
                COLUMN_BUILDING_CLAY_COST + " INTEGER, " +
                COLUMN_BUILDING_GOLD_INCOME + " INTEGER, " +
                COLUMN_BUILDING_WOOD_INCOME + " INTEGER, " +
                COLUMN_BUILDING_STONE_INCOME + " INTEGER, " +
                COLUMN_BUILDING_CLAY_INCOME + " INTEGER)";
    }


    //Первоначальное заполнение таблицы со стоимостью шахт
    public static void fillTheBuildingsInfo() {
        //Заполнение строк таблицы с информацией о постройках
        //ШТАБ
        MyApp.getMyDBase().addBuildingInfo(buildingTypeHQ, 1, 0, 10, 10, 10, 10, 0, 0, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeHQ, 2, 0, 100, 100, 100, 20, 0, 0, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeHQ, 3, 0, 1000, 1000, 1000, 30, 0, 0, 0);

        //ЛЕСОПИЛКА
        MyApp.getMyDBase().addBuildingInfo(buildingTypeWood, 1, 10, 0, 0, 0, 0, 10, 0, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeWood, 2, 20, 0, 0, 0, 0, 20, 0, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeWood, 3, 30, 0, 0, 0, 0, 30, 0, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeWood, 4, 40, 0, 0, 0, 0, 40, 0, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeWood, 5, 50, 0, 0, 0, 0, 50, 0, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeWood, 6, 60, 0, 0, 0, 0, 60, 0, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeWood, 7, 70, 0, 0, 0, 0, 70, 0, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeWood, 8, 80, 0, 0, 0, 0, 80, 0, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeWood, 9, 90, 0, 0, 0, 0, 90, 0, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeWood, 10, 100, 0, 0, 0, 0, 100, 0, 0);

        //РУДНИК
        MyApp.getMyDBase().addBuildingInfo(buildingTypeStone, 1, 10, 0, 0, 0, 0, 0, 10, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeStone, 2, 20, 0, 0, 0, 0, 0, 20, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeStone, 3, 30, 0, 0, 0, 0, 0, 30, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeStone, 4, 40, 0, 0, 0, 0, 0, 40, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeStone, 5, 50, 0, 0, 0, 0, 0, 50, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeStone, 6, 60, 0, 0, 0, 0, 0, 60, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeStone, 7, 70, 0, 0, 0, 0, 0, 70, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeStone, 8, 80, 0, 0, 0, 0, 0, 80, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeStone, 9, 90, 0, 0, 0, 0, 0, 90, 0);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeStone, 10, 100, 0, 0, 0, 0, 0, 100, 0);

        //КАРЬЕР
        MyApp.getMyDBase().addBuildingInfo(buildingTypeClay, 1, 10, 0, 0, 0, 0, 0, 0, 10);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeClay, 2, 20, 0, 0, 0, 0, 0, 0, 20);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeClay, 3, 30, 0, 0, 0, 0, 0, 0, 30);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeClay, 4, 40, 0, 0, 0, 0, 0, 0, 40);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeClay, 5, 50, 0, 0, 0, 0, 0, 0, 50);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeClay, 6, 60, 0, 0, 0, 0, 0, 0, 60);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeClay, 7, 70, 0, 0, 0, 0, 0, 0, 70);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeClay, 8, 80, 0, 0, 0, 0, 0, 0, 80);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeClay, 9, 90, 0, 0, 0, 0, 0, 0, 90);
        MyApp.getMyDBase().addBuildingInfo(buildingTypeClay, 10, 100, 0, 0, 0, 0, 0, 0, 100);
    }

    public long addBuildingInfo(String buildingType, int buildingLVL, int costGold, int costWood, int costStone, int costClay, int incomeGold, int incomeWood, int incomeStone, int incomeClay) {
        ContentValues cvBuildingsInfo = new ContentValues();
        cvBuildingsInfo.put(BuildingsInfo.COLUMN_BUILDING_TYPE, buildingType);
        cvBuildingsInfo.put(BuildingsInfo.COLUMN_BUILDING_LVL, buildingLVL);
        cvBuildingsInfo.put(BuildingsInfo.COLUMN_BUILDING_GOLD_COST, costGold);
        cvBuildingsInfo.put(BuildingsInfo.COLUMN_BUILDING_WOOD_COST, costWood);
        cvBuildingsInfo.put(BuildingsInfo.COLUMN_BUILDING_STONE_COST, costStone);
        cvBuildingsInfo.put(BuildingsInfo.COLUMN_BUILDING_CLAY_COST, costClay);
        cvBuildingsInfo.put(BuildingsInfo.COLUMN_BUILDING_GOLD_INCOME, incomeGold);
        cvBuildingsInfo.put(BuildingsInfo.COLUMN_BUILDING_WOOD_INCOME, incomeWood);
        cvBuildingsInfo.put(BuildingsInfo.COLUMN_BUILDING_STONE_INCOME, incomeStone);
        cvBuildingsInfo.put(BuildingsInfo.COLUMN_BUILDING_CLAY_INCOME, incomeClay);
        return this.getWritableDatabase().insert(BuildingsInfo.TABLE_BUILDINGS_INFO, null, cvBuildingsInfo);
    }

    public static boolean isTableBuildingsInfo() {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(BuildingsInfo.TABLE_BUILDINGS_INFO);
        if (cr.moveToFirst()) {
            return true;
        }
        cr.close();
        return false;
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

    public static void addNewBuilding(String userNickName, String userGoogleEmail, String buildingType, String buildingCategory, String buildingName, long buildingLVL, double buildingLat, double buildingLng, String buildingBuildDate) {

        String strLat = String.valueOf(buildingLat);
        String strLng = String.valueOf(buildingLng);
        String buildingID = getBuildingID(buildingLat, buildingLng);

        //......................................FIRESTORE......................................
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> newBuilding = new HashMap<>();
        newBuilding.put("buildingID", buildingID);
        newBuilding.put("userGoogleEmail", userGoogleEmail);
        newBuilding.put("userNickName", userNickName);
        newBuilding.put("buildingType", buildingType);
        newBuilding.put("buildingCategory", buildingCategory);
        newBuilding.put("buildingName", buildingName);
        newBuilding.put("buildingLVL", buildingLVL);
        newBuilding.put("buildingLat", strLat);
        newBuilding.put("buildingLng", strLng);
        newBuilding.put("buildingBuildDate", buildingBuildDate);


        db.collection(fbBuildings).document(buildingID).set(newBuilding);

        //.....................................................................................
    }

    public List<Integer> getBuildingCostByLVLandType(String buildingType, int buildingLVL) {

        List<Integer> buildingCost = new ArrayList<>();
        int col_buildingLVL;
        int col_buildingType;
        int col_buildingCostGold = 0;
        int col_buildingCostWood = 0;
        int col_buildingCostStone = 0;
        int col_buildingCostClay = 0;

        Cursor crBuildings = MyApp.getMyDBase().getReadableCursor(BuildingsInfo.TABLE_BUILDINGS_INFO);
        if (crBuildings.moveToFirst()) {
            do {
                col_buildingLVL = crBuildings.getColumnIndex(BuildingsInfo.COLUMN_BUILDING_LVL);
                col_buildingType = crBuildings.getColumnIndex(BuildingsInfo.COLUMN_BUILDING_TYPE);
                col_buildingCostGold = crBuildings.getColumnIndex(BuildingsInfo.COLUMN_BUILDING_GOLD_COST);
                col_buildingCostWood = crBuildings.getColumnIndex(BuildingsInfo.COLUMN_BUILDING_WOOD_COST);
                col_buildingCostStone = crBuildings.getColumnIndex(BuildingsInfo.COLUMN_BUILDING_STONE_COST);
                col_buildingCostClay = crBuildings.getColumnIndex(BuildingsInfo.COLUMN_BUILDING_CLAY_COST);

                if ((crBuildings.getInt(col_buildingLVL) == buildingLVL) && (crBuildings.getString(col_buildingType).equals(buildingType))) {
                    buildingCost.add(crBuildings.getInt(col_buildingCostGold));    //Золото
                    buildingCost.add(crBuildings.getInt(col_buildingCostWood));    //Дерево
                    buildingCost.add(crBuildings.getInt(col_buildingCostStone));   //Камень
                    buildingCost.add(crBuildings.getInt(col_buildingCostClay));    //Глина
                    return buildingCost;
                }
            } while (crBuildings.moveToNext());
        }
        crBuildings.close();
        return null;
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


    // Create a query against the collection.
//        Query query = bInfoRef.whereEqualTo("state", "CA");

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

    //Получить тип здания по ID здания
    public String getBuildingTypeByBuildingID(int buildingID) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(BuildingsListTable.TABLE_BUILDINGS_LIST);
        if (cr.moveToFirst()) {
            do {
                int col_ID = cr.getColumnIndex(BuildingsListTable._ID);
                int col_buildingType = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_TYPE);
                if (cr.getInt(col_ID) == buildingID) {
                    return cr.getString(col_buildingType);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return "Не найдено";
    }

    //Получить уровень шахты по ID шахты
    public int getBuildingLVLByBuildingID(int buildingID) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(BuildingsListTable.TABLE_BUILDINGS_LIST);
        if (cr.moveToFirst()) {
            do {
                int col_ID = cr.getColumnIndex(BuildingsListTable._ID);
                int col_buildingLVL = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_LVL);
                if (cr.getInt(col_ID) == buildingID) {
                    return cr.getInt(col_buildingLVL);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return 0;
    }

//    //Получить название здания по ID здания
//    public String getBuildingNameByBuildingID(int buildingID) {
//        Cursor cr = MyApp.getMyDBase().getReadableCursor(BuildingsListTable.TABLE_BUILDINGS_LIST);
//        if (cr.moveToFirst()) {
//            do {
//                int col_ID = cr.getColumnIndex(BuildingsListTable._ID);
//                int col_buildingName = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_NAME);
//                if (cr.getInt(col_ID) == buildingID) {
//                    return cr.getString(col_buildingName);
//                }
//            } while (cr.moveToNext());
//        }
//        cr.close();
//        return "Не найдено";
//    }

    //Получить название здания по ID здания
    public static String getBuildingOwnerNameByBuildingIDWithSnapshot(DataSnapshot dataSnapshot, String buildingID) {
        return dataSnapshot.child(buildingID).child("userNickName").getValue(String.class);
    }

    //Получить название здания по ID здания
    public static String getBuildingNameByBuildingIDWithSnapshot(DataSnapshot dataSnapshot, String buildingID) {
        return dataSnapshot.child(buildingID).child("buildingName").getValue(String.class);
    }

    //Получить название здания по ID здания
    public static String getBuildingTypeByBuildingIDWithSnapshot(DataSnapshot dataSnapshot, String buildingID) {
        return dataSnapshot.child(buildingID).child("buildingType").getValue(String.class);
    }

    public static String getBuildingGoogleEmailByBuildingIDWithSnapshot(DataSnapshot dataSnapshot, String buildingID) {
        return dataSnapshot.child(buildingID).child("userGoogleEmail").getValue(String.class);
    }

    //Получить название здания по ID здания
    public static int getBuildingLVLByBuildingIDWithSnapshot(DataSnapshot dataSnapshot, String buildingID) {
        try {
            return dataSnapshot.child(buildingID).child("buildingLVL").getValue(Integer.class);
        } catch (Exception e) {
        }
        return 0;
    }


    //Получить координаты здания по ID здания
    public LatLng getBuildingLatLngByBuildingID(int buildingID) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(BuildingsListTable.TABLE_BUILDINGS_LIST);
        if (cr.moveToFirst()) {
            do {
                int col_ID = cr.getColumnIndex(BuildingsListTable._ID);
                int col_buildingLat = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_LAT);
                int col_buildingLng = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_LNG);

                LatLng myBuildingLocation = new LatLng(cr.getDouble(col_buildingLat), cr.getDouble(col_buildingLng));

                if (cr.getInt(col_ID) == buildingID) {
                    return myBuildingLocation;
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return null;
    }

    //Удалить здание по ID
    public boolean deleteBuildingByBuildingID(int id) {
        return 1 == this.getWritableDatabase().delete(
                BuildingsListTable.TABLE_BUILDINGS_LIST, SQL_WHERE_BY_ID,
                new String[]{String.valueOf(id)});
    }

    //    //Проверка можно ли поставить здесь свою постройку, допустимо ли расстояние от соседних построек
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

//
//        dbLoc.collection(fbBuildings)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value,
//                                        @Nullable FirebaseFirestoreException e) {
//                        if (e != null) {
////                            Log.w(TAG, "Listen failed.", e);
//                            return;
//                        }
//
//                        for (final DocumentSnapshot doc : value) {
//
//                            String buildingLatStr;
//                            String buildingLngStr;
//
//                            buildingLatStr = (String) doc.get("buildingLat");
//                            buildingLngStr = (String) doc.get("buildingLng");
//
//                            double buildingLat;
//                            double buildingLng;
//
//                            buildingLat = Double.parseDouble(buildingLatStr);
//                            buildingLng = Double.parseDouble(buildingLngStr);
//
//                            LatLng myBuilding = new LatLng(buildingLat, buildingLng);
//
//                            if (SphericalUtil.computeDistanceBetween(myTarget, myBuilding) > minDistanceBetweenTwoBuildings) {
//                                canIputMyBuildHere = true;
//                            }
//
//                        }
////                        Log.d(TAG, "Current cites in CA: " + cities);
//                    }
//                });

        return canIputMyBuildHere;

//        Cursor cr = MyApp.getMyDBase().getReadableCursor(BuildingsListTable.TABLE_BUILDINGS_LIST);
//        if (cr.moveToFirst()) {
//            do {
//                int col_buildingLat = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_LAT);
//                int col_buildingLng = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_LNG);
//
//                LatLng myBuilding = new LatLng(cr.getDouble(col_buildingLat), cr.getDouble(col_buildingLng));
//
//                if (SphericalUtil.computeDistanceBetween(target, myBuilding) < minDistanceBetweenTwoBuildings) {
//                    return false;
//                }
//            } while (cr.moveToNext());
//        }
//        cr.close();
//        return true;
    }

//**************************************************************************************
//Работа с таблицей Пользователей
//*******************************

    //Добавить нового пользователя
    public long addUser(String email, String givenName, String familyName, String personName, String currentDate) {
        ContentValues cvUsers = new ContentValues();
        cvUsers.put(UsersTable.COLUMN_USERGOOGLEEMAIL, email);
        cvUsers.put(UsersTable.COLUMN_GIVENNAME, givenName);
        cvUsers.put(UsersTable.COLUMN_FAMILYNAME, familyName);
        cvUsers.put(UsersTable.COLUMN_DISPLAYNAME, personName);
        cvUsers.put(UsersTable.COLUMN_REGISTERDATE, currentDate);
        cvUsers.put(UsersTable.COLUMN_USER_GOLD, userStartGold);
        cvUsers.put(UsersTable.COLUMN_USER_WOOD, userStartWood);
        cvUsers.put(UsersTable.COLUMN_USER_STONE, userStartStone);
        cvUsers.put(UsersTable.COLUMN_USER_CLAY, userStartClay);
        cvUsers.put(UsersTable.COLUMN_ISHQAVIABLE, 0);
        return this.getWritableDatabase().insert(UsersTable.TABLE_USERS, null, cvUsers);
    }

    //Получить дату регистрации пользователя
    public String getRegistrationDate(String userGoogleEmail) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(UsersTable.TABLE_USERS);
        if (cr.moveToFirst()) {
            do {
                int col_userGoogleEmail = cr.getColumnIndex(UsersTable.COLUMN_USERGOOGLEEMAIL);
                int col_Date = cr.getColumnIndex(UsersTable.COLUMN_REGISTERDATE);
                if (cr.getString(col_userGoogleEmail).equals(userGoogleEmail)) {
                    return cr.getString(col_Date);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return "Не найдено";
    }

    //Получить имя пользователя
    public String getUserNickNameByGoogleEmail(String userGoogleEmail) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(UsersTable.TABLE_USERS);
        if (cr.moveToFirst()) {
            do {
                int col_userNickName = cr.getColumnIndex(UsersTable.COLUMN_NICKNAME);
                int col_userGoogleEmail = cr.getColumnIndex(UsersTable.COLUMN_USERGOOGLEEMAIL);
                if (cr.getString(col_userGoogleEmail).equals(userGoogleEmail)) {
                    return cr.getString(col_userNickName);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return "Не найдено";
    }

    //Получить имя пользователя
    public String getFirstName(String userGoogleEmail) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(UsersTable.TABLE_USERS);
        if (cr.moveToFirst()) {
            do {
                int col_userGoogleEmail = cr.getColumnIndex(UsersTable.COLUMN_USERGOOGLEEMAIL);
                int col_firstName = cr.getColumnIndex(UsersTable.COLUMN_GIVENNAME);
                if (cr.getString(col_userGoogleEmail).equals(userGoogleEmail)) {
                    return cr.getString(col_firstName);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return "Не найдено";
    }

    //Получить фамилию пользователя
    public String getSecondName(String userGoogleEmail) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(UsersTable.TABLE_USERS);
        if (cr.moveToFirst()) {
            do {
                int col_userGoogleEmail = cr.getColumnIndex(UsersTable.COLUMN_USERGOOGLEEMAIL);
                int col_secondName = cr.getColumnIndex(UsersTable.COLUMN_FAMILYNAME);
                if (cr.getString(col_userGoogleEmail).equals(userGoogleEmail)) {
                    return cr.getString(col_secondName);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return "Не найдено";
    }

    //Получить DisplayName по GoogleEmail
    public String getUserDisplayNameByGoogleEmail(String googleEmail) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(UsersTable.TABLE_USERS);
        if (cr.moveToFirst()) {
            do {
                int col_displayName = cr.getColumnIndex(UsersTable.COLUMN_DISPLAYNAME);
                int col_userGoogleEmail = cr.getColumnIndex(UsersTable.COLUMN_USERGOOGLEEMAIL);
                if (cr.getString(col_userGoogleEmail).equals(googleEmail)) {
                    return cr.getString(col_displayName);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return "не найдено";
    }

    //Получить Google Email пользоватея по ID постройки
    public String getUserGoogleEmailByBuildingID(int buildingID) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(BuildingsListTable.TABLE_BUILDINGS_LIST);
        if (cr.moveToFirst()) {
            do {
                int col_ID = cr.getColumnIndex(BuildingsListTable._ID);
                int col_ownerGoogleEmail = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_OWNER_GOOGLE_EMAIL);
//                int col_ownerID = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_OWNER_ID);
                if (cr.getInt(col_ID) == buildingID) {
//                    int ownerId = cr.getInt(col_ownerID);
//                    return getUserGoogleEmailByUserID(ownerId);
                    return cr.getString(col_ownerGoogleEmail);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return "Не найдено";
    }

    //Получить возраст пользователя
    public int getUserAge(String userGoogleEmail) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(UsersTable.TABLE_USERS);
        if (cr.moveToFirst()) {
            do {
                int col_userGoogleEmail = cr.getColumnIndex(UsersTable.COLUMN_USERGOOGLEEMAIL);
                int col_userAge = cr.getColumnIndex(UsersTable.COLUMN_AGE);
                if (cr.getString(col_userGoogleEmail).equals(userGoogleEmail)) {
                    return cr.getInt(col_userAge);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return 0;
    }

    //Получить пол пользователя
    public String getUserSex(String userGoogleEmail) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(UsersTable.TABLE_USERS);
        if (cr.moveToFirst()) {
            do {
                int col_userGoogleEmail = cr.getColumnIndex(UsersTable.COLUMN_USERGOOGLEEMAIL);
                int col_userSex = cr.getColumnIndex(UsersTable.COLUMN_SEX);
                if (cr.getString(col_userGoogleEmail).equals(userGoogleEmail)) {
                    return cr.getString(col_userSex);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return "Не найдено";
    }

    //Получить имя пользоватея по ID пользователя
    public String getUserNickNameByUserID(int userID) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(UsersTable.TABLE_USERS);
        if (cr.moveToFirst()) {
            do {
                int col_ID = cr.getColumnIndex(UsersTable._ID);
                int col_nickname = cr.getColumnIndex(UsersTable.COLUMN_NICKNAME);
                if (cr.getInt(col_ID) == userID) {
                    return cr.getString(col_nickname);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return "Не найдено";
    }

    //Получить имя пользоватея по ID пользователя
    public String getUserDisplayNameByUserID(int userID) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(UsersTable.TABLE_USERS);
        if (cr.moveToFirst()) {
            do {
                int col_ID = cr.getColumnIndex(UsersTable._ID);
                int col_displayname = cr.getColumnIndex(UsersTable.COLUMN_DISPLAYNAME);
                if (cr.getInt(col_ID) == userID) {
                    return cr.getString(col_displayname);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return "Не найдено";
    }

    //Получить имя пользоватея по ID пользователя
    public String getUserGoogleEmailByUserID(int userID) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(UsersTable.TABLE_USERS);
        if (cr.moveToFirst()) {
            do {
                int col_ID = cr.getColumnIndex(UsersTable._ID);
                int col_googleEmail = cr.getColumnIndex(UsersTable.COLUMN_USERGOOGLEEMAIL);
                if (cr.getInt(col_ID) == userID) {
                    return cr.getString(col_googleEmail);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return "Не найдено";
    }


//    //Получить имя пользоватея по ID здания
//    public String getUserNameByBuildingID(int buildingID) {
//        Cursor cr = MyApp.getMyDBase().getReadableCursor(BuildingsListTable.TABLE_BUILDINGS);
//        if (cr.moveToFirst()) {
//            do {
//                int col_ID = cr.getColumnIndex(BuildingsListTable._ID);
//                int col_ownerID = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_OWNER_ID);
//                if (cr.getInt(col_ID) == buildingID) {
//                    int ownerId = cr.getInt(col_ownerID);
//                    return getUserNameByUserID(ownerId);
//                }
//            } while (cr.moveToNext());
//        }
//        cr.close();
//        return "Не найдено";
//    }
//
//    //Получить имя пользоватея по ID здания
//    public String getUserDisplayNameByBuildingID(int buildingID) {
//        Cursor cr = MyApp.getMyDBase().getReadableCursor(BuildingsListTable.TABLE_BUILDINGS);
//        if (cr.moveToFirst()) {
//            do {
//                int col_ID = cr.getColumnIndex(BuildingsListTable._ID);
//                int col_ownerID = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_OWNER_ID);
//                if (cr.getInt(col_ID) == buildingID) {
//                    int ownerId = cr.getInt(col_ownerID);
//                    return getUserDisplayNameByUserID(ownerId);
//                }
//            } while (cr.moveToNext());
//        }
//        cr.close();
//        return "Не найдено";
//    }

    //Получить ID пользователя
    public int getUserIDbyUserGoogleEmail(String userGoogleEmail) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(UsersTable.TABLE_USERS);
        if (cr.moveToFirst()) {
            do {
                int col_userGoogleEmail = cr.getColumnIndex(UsersTable.COLUMN_USERGOOGLEEMAIL);
                int col_ID = cr.getColumnIndex(UsersTable._ID);
                if (cr.getString(col_userGoogleEmail).equals(userGoogleEmail)) {
                    return cr.getInt(col_ID);
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return 0;
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

    //Получить ресурсы пользователя списком
    public List<Integer> getUserResources(int userID) {
        List<Integer> userResources = new ArrayList<>();
        Cursor cr = MyApp.getMyDBase().getReadableCursor(UsersTable.TABLE_USERS);
        if (cr.moveToFirst()) {
            do {
                int col_userID = cr.getColumnIndex(UsersTable._ID);
                int col_gold = cr.getColumnIndex(UsersTable.COLUMN_USER_GOLD);
                int col_wood = cr.getColumnIndex(UsersTable.COLUMN_USER_WOOD);
                int col_stone = cr.getColumnIndex(UsersTable.COLUMN_USER_STONE);
                int col_clay = cr.getColumnIndex(UsersTable.COLUMN_USER_CLAY);
                if (cr.getInt(col_userID) == userID) {
                    userResources.add(cr.getInt(col_gold));
                    userResources.add(cr.getInt(col_wood));
                    userResources.add(cr.getInt(col_stone));
                    userResources.add(cr.getInt(col_clay));
                    return userResources;
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return null;
    }

    //Получить имя пользоватея по ID здания
    public String getUserNameByBuildingID(int buildingID) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(BuildingsListTable.TABLE_BUILDINGS_LIST);
        if (cr.moveToFirst()) {
            do {
                int col_ID = cr.getColumnIndex(BuildingsListTable._ID);
                int col_ownerGoogleEmail = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_OWNER_GOOGLE_EMAIL);
//                int col_ownerID = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_OWNER_ID);
                if (cr.getInt(col_ID) == buildingID) {
//                    int ownerId = cr.getInt(col_ownerID);
//                    return getUserNickNameByUserID(ownerId);
                    return getUserNickNameByGoogleEmail(cr.getString(col_ownerGoogleEmail));
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return "Не найдено";
    }

    //Получить имя пользоватея по ID здания
    public String getUserDisplayNameByBuildingID(int buildingID) {
        Cursor cr = MyApp.getMyDBase().getReadableCursor(BuildingsListTable.TABLE_BUILDINGS_LIST);
        if (cr.moveToFirst()) {
            do {
                int col_ID = cr.getColumnIndex(BuildingsListTable._ID);
                int col_ownerGoogleEmail = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_OWNER_GOOGLE_EMAIL);
//                int col_ownerID = cr.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_OWNER_ID);
                if (cr.getInt(col_ID) == buildingID) {
//                    int ownerId = cr.getInt(col_ownerID);
//                    return getUserDisplayNameByUserID(ownerId);
                    return getUserDisplayNameByGoogleEmail(cr.getString(col_ownerGoogleEmail));
                }
            } while (cr.moveToNext());
        }
        cr.close();
        return "Не найдено";
    }

    //Проверить открыт ли штаб
    public int getHQAviable(String userGoogleEmail) {
        Cursor crBuildings = MyApp.getMyDBase().getReadableCursor(BuildingsListTable.TABLE_BUILDINGS_LIST);
        if (crBuildings.moveToFirst()) {
            do {
//                int col_ownerID = crBuildings.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_OWNER_ID);
                int col_ownerGoogleEmail = crBuildings.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_OWNER_GOOGLE_EMAIL);
                int col_buildingType = crBuildings.getColumnIndex(BuildingsListTable.COLUMN_BUILDING_TYPE);

                int userID = MyApp.getMyDBase().getUserIDbyUserGoogleEmail(userGoogleEmail);

                if (crBuildings.getString(col_ownerGoogleEmail).equals(userGoogleEmail) && (crBuildings.getString(col_buildingType).equals(buildingTypeHQ))) {
                    return 1;
                }
            } while (crBuildings.moveToNext());
        }
        crBuildings.close();
        return 0;
    }

    public boolean setHQAviable(int aviable, long id) {
        ContentValues v = new ContentValues();

        v.put(UsersTable.COLUMN_ISHQAVIABLE, aviable);

        return 1 == this.getWritableDatabase().update(UsersTable.TABLE_USERS, v,
                SQL_WHERE_BY_ID, new String[]{String.valueOf(id)});
    }

    //Обновить настройки пользователя
    public boolean updateUserSettings(long id, String userNickName, String userFirstName, String userSecondName, int userAge, String userSex) {
        ContentValues v = new ContentValues();

        v.put(UsersTable.COLUMN_NICKNAME, userNickName);
        v.put(UsersTable.COLUMN_GIVENNAME, userFirstName);
        v.put(UsersTable.COLUMN_FAMILYNAME, userSecondName);
        v.put(UsersTable.COLUMN_AGE, userAge);
        v.put(UsersTable.COLUMN_SEX, userSex);

        return 1 == this.getWritableDatabase().update(UsersTable.TABLE_USERS, v,
                SQL_WHERE_BY_ID, new String[]{String.valueOf(id)});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//		/* Drop tables */
//        DBSQLite.dropTable(db, TableEmpl.T_NAME);
//        DBSQLite.dropTable(db, TableDep.T_NAME);
//
//		/* Invoke onCreate method */
//        this.onCreate(db);

    }

//    public static String showUserNickNameOrDisplayNameByGoogleEmail(String userGoogleEmail) {

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
//        .....................................................................................


//        myCurrentUserGoogleEmail = userGoogleEmail;
//
//        //FIREBASE
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference(fbUsers);
//
//
//        // Read from the database
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    String allowedEmail = allowedEmail(myCurrentUserGoogleEmail);
//                    String userNickName = dataSnapshot.child(allowedEmail).child("NickName").getValue(String.class);
//                    String userDisplayName = dataSnapshot.child(allowedEmail).child("DisplayName").getValue(String.class);
//
//                    if (userNickName.length() > 0) {
//                        myNick = userNickName;
//                    } else {
//                        myNick = userDisplayName;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                // Failed to read value
//            }
//        });
//        return myNick;
//    }

    public static String showUserNickNameOrDisplayNameByBuildingIDWithSnapshot(DataSnapshot dataSnapshot, String buildingID) {

        //FIREBASE
        String bID = dataSnapshot.child(buildingID).getKey();
        String uEmail = dataSnapshot.child(buildingID).child("userGoogleEmail").getValue(String.class);
        myUEmail = uEmail;


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(fbUsers);

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String allowedEmail = allowedEmail(myUEmail);
                String userNickName = dataSnapshot.child(allowedEmail).child("NickName").getValue(String.class);
                String userDisplayName = dataSnapshot.child(allowedEmail).child("DisplayName").getValue(String.class);

                if (userNickName.length() > 0) {
                    myNick = userNickName;
                } else {
                    myNick = userDisplayName;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
        return myNick;
//
//        //FIREBASE
//        String userGoogleEmail = dataSnapshot.child(buildingID).child("userGoogleEmail").getValue(String.class);
//
//        String allowedEmail = null;
//        if (userGoogleEmail != null) {
//            allowedEmail = allowedEmail(userGoogleEmail);
//        }
//
//        String userNickName = null;
//        String userDisplayName = null;
//        if (allowedEmail != null) {
//            userNickName = dataSnapshot.child(allowedEmail).child("NickName").getValue(String.class);
//            userDisplayName = dataSnapshot.child(allowedEmail).child("DisplayName").getValue(String.class);
//        }
//
//        if (userNickName != null) {
//            if (userNickName.length() > 0) {
//                myNick = userNickName;
//            } else {
//                myNick = userDisplayName;
//            }
//        } else {
//            myNick = userDisplayName;
//        }
//        return myNick;
    }


    public static String getUserNickNameOrDisplayNameByGoogleEmail(String userGoogleEmail) {

        //......................................FIRESTORE......................................
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(fbUsers).document(userGoogleEmail);
//        myNick = String.valueOf(docRef.get().getResult().get("NickName"));
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

//                        userDisplayName = (String) document.get("DisplayName");
//                        userNickName = (String) document.get("NickName");
//                        userFirstName = (String) document.get("GivenName");
//                        userSecondName = (String) document.get("FamilyName");
//                        userAge = (int) document.get("Age");
//                        userSex = (String) document.get("Sex");
//                        registrationDate = (String) document.get("registrationDate");
//                        updateData();
                    }

                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        //.....................................................................................


//        String MyUserNickName = (String) snapshot.get("NickName");
//        if (MyUserNickName.length() > 0) {
//            return MyUserNickName;
//        } else {
//            return (String) snapshot.get("DisplayName");
//        }
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

    public static String showUserNickNameOrDisplayNameByUserID(int userID) {
        //Показываем имя текущего пользователя
        if (MyApp.getMyDBase().getUserNickNameByUserID(userID) != null) {
            return MyApp.getMyDBase().getUserNickNameByUserID(userID);
        } else {
            return MyApp.getMyDBase().getUserDisplayNameByUserID(userID);
        }
    }

//    public static String showUserNickNameOrDisplayNameByBuildingID(int buildingID) {
//        //Показываем имя текущего пользователя
//        if (MyApp.getMyDBase().getUserNameByBuildingID(buildingID) != null) {
//            return MyApp.getMyDBase().getUserNameByBuildingID(buildingID);
//        } else {
//            return MyApp.getMyDBase().getUserDisplayNameByBuildingID(buildingID);
//        }
//    }

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
