package com.work.andre.mines;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import static com.work.andre.mines.ActMap.USERGOOGLEEMAIL;
import static com.work.andre.mines.ActMap.mvMap;
import static com.work.andre.mines.database.DBase.buildingCategoryOffice;
import static com.work.andre.mines.database.DBase.buildingTypeClay;
import static com.work.andre.mines.database.DBase.buildingTypeHQ;
import static com.work.andre.mines.database.DBase.buildingTypeStone;
import static com.work.andre.mines.database.DBase.buildingTypeWood;
import static com.work.andre.mines.database.DBase.fbBuildings;
import static com.work.andre.mines.database.DBase.fbUsers;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class ActBuildingsList extends AppCompatActivity {

    public static final String BUILDINGCATEGORY = "buildingCategory"; //отображаем список шахт или зданий
    String bCategory;

    static String currentUserNickName;
    static String currentUserGoogleEmail;

    public static int buildingWoodCount;
    public static int buildingStoneCount;
    public static int buildingClayCount;
    public static int buildingHQCount;
    //.....
    public static int buildingAllCount;

    ImageView ivMainIcon;

    //..........................
    TextView tvBuildingText;
    TextView tvOwnerName;

    TextView tvBuildingAllText;
    TextView tvBuildingInAll;

    TextView tvBuildingHQText;
    TextView tvBuildingHQInfo;

    TextView tvBuildingWoodText;
    TextView tvBuildingWoodInfo;

    TextView tvBuildingStoneText;
    TextView tvBuildingStoneInfo;

    TextView tvBuildingClayText;
    TextView tvBuildingClayInfo;

    //.....

    private List<Buildings> buildingsList;
    private RecyclerView rv;

    View ChildView;
    int RecyclerViewItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings_list);

        bCategory = getIntent().getStringExtra(BUILDINGCATEGORY);
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
                    String MyUserNickName = (String) snapshot.get("NickName");
                    if (MyUserNickName.length() > 0) {
                        currentUserNickName = MyUserNickName;
                    } else {
                        currentUserNickName = (String) snapshot.get("DisplayName");
                    }

                    tvOwnerName.setText(currentUserNickName);
                    //Log.d(TAG, "Current data: " + snapshot.getData());
                }
            }
        });
        //.....................................................................................

        buildingWoodCount = 0;
        buildingStoneCount = 0;
        buildingClayCount = 0;
        buildingHQCount = 0;
        //.....
        buildingAllCount = 0;

        this.initUI();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        initializeData();
        initializeAdapter();

        tvBuildingAllText.setVisibility(View.VISIBLE);
        tvBuildingInAll.setVisibility(View.VISIBLE);

        //Офисы
        if (bCategory.equals(buildingCategoryOffice)) {
            ivMainIcon.setImageResource(R.drawable.builnings_list_picture);

            //Показываем офисы
            tvBuildingHQText.setVisibility(View.VISIBLE);
            tvBuildingHQInfo.setVisibility(View.VISIBLE);

            //Скрываем шахты
            tvBuildingWoodText.setVisibility(View.GONE);
            tvBuildingWoodInfo.setVisibility(View.GONE);
            tvBuildingStoneText.setVisibility(View.GONE);
            tvBuildingStoneInfo.setVisibility(View.GONE);
            tvBuildingClayText.setVisibility(View.GONE);
            tvBuildingClayInfo.setVisibility(View.GONE);


            tvBuildingHQInfo.setText(String.valueOf(buildingHQCount));


        } else {

            //Шахты
            ivMainIcon.setImageResource(R.drawable.mine_list_picture);

            //Скрываем офисы
            tvBuildingHQText.setVisibility(View.GONE);
            tvBuildingHQInfo.setVisibility(View.GONE);

            //Показываем шахты
            tvBuildingWoodText.setVisibility(View.VISIBLE);
            tvBuildingWoodInfo.setVisibility(View.VISIBLE);
            tvBuildingStoneText.setVisibility(View.VISIBLE);
            tvBuildingStoneInfo.setVisibility(View.VISIBLE);
            tvBuildingClayText.setVisibility(View.VISIBLE);
            tvBuildingClayInfo.setVisibility(View.VISIBLE);

            tvBuildingWoodInfo.setText(String.valueOf(buildingWoodCount));
            tvBuildingStoneInfo.setText(String.valueOf(buildingStoneCount));
            tvBuildingClayInfo.setText(String.valueOf(buildingClayCount));
        }

        //.....
        tvBuildingInAll.setText(String.valueOf(buildingAllCount));
    }

    private void initializeData() {
        buildingsList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(fbBuildings)
                .whereEqualTo("userGoogleEmail", currentUserGoogleEmail)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            //Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (value != null) {
                            for (DocumentSnapshot doc : value) {
                                if (bCategory.equals(doc.getString("buildingCategory"))) {
                                    int pic = 0;
                                    if (doc.getString("buildingType").equals(buildingTypeWood)) {
                                        pic = R.drawable.mine_forest;
                                        buildingWoodCount++;
                                    } else if (doc.getString("buildingType").equals(buildingTypeStone)) {
                                        pic = R.drawable.mine_stone;
                                        buildingStoneCount++;
                                    } else if (doc.getString("buildingType").equals(buildingTypeClay)) {
                                        pic = R.drawable.mine_sand;
                                        buildingClayCount++;
                                    } else if (doc.getString("buildingType").equals(buildingTypeHQ)) {
                                        pic = R.drawable.headquoter;
                                        buildingHQCount++;
                                    }
                                    buildingAllCount++;
                                    updateData();

                                    buildingsList.add(new Buildings(doc.getString("buildingID"), doc.getString("buildingName"), doc.getLong("buildingLVL"), pic));
                                }
                            }
                        }
                        //Log.d(TAG, "Current cites in CA: " + cities);
                    }
                });
    }

    private void initializeAdapter() {

        RVAdapter adapter = new RVAdapter(buildingsList);
        rv.setAdapter(adapter);

        rv.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(ActBuildingsList.this, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {

                    RecyclerViewItemPosition = Recyclerview.getChildPosition(ChildView);

                    //Достаём объект на текущей позиции
                    Object myBuildingObject = buildingsList.get(RecyclerViewItemPosition);
                    //Кастим этот объект в объект класса Buildings
                    Buildings myBuilding = (Buildings) myBuildingObject;
                    //Получаем id этого объекта
                    String myBuildingID = myBuilding.buildingID;
                    goToCurrentBuildingLocationByBuildingID(myBuildingID);
                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }

//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
        });

    }

    public void initUI() {
        rv = (RecyclerView) findViewById(R.id.rv);

        ivMainIcon = (ImageView) findViewById(R.id.ivMainIcon);
        tvBuildingText = (TextView) findViewById(R.id.tvBuildingText);
        tvOwnerName = (TextView) findViewById(R.id.tvOwnerName);

        tvBuildingWoodText = (TextView) findViewById(R.id.tvBuildingWoodText);
        tvBuildingWoodInfo = (TextView) findViewById(R.id.tvBuildingWoodInfo);

        tvBuildingStoneText = (TextView) findViewById(R.id.tvBuildingStoneText);
        tvBuildingStoneInfo = (TextView) findViewById(R.id.tvBuildingStoneInfo);

        tvBuildingClayText = (TextView) findViewById(R.id.tvBuildingClayText);
        tvBuildingClayInfo = (TextView) findViewById(R.id.tvBuildingClayInfo);

        tvBuildingHQText = (TextView) findViewById(R.id.tvBuildingHQText);
        tvBuildingHQInfo = (TextView) findViewById(R.id.tvBuildingHQInfo);
        tvBuildingInAll = (TextView) findViewById(R.id.tvBuildingInAll);

        tvBuildingAllText = (TextView) findViewById(R.id.tvBuildingAllText);
    }

    private void updateData() {
        tvBuildingWoodInfo.setText(String.valueOf(buildingWoodCount));
        tvBuildingStoneInfo.setText(String.valueOf(buildingStoneCount));
        tvBuildingClayInfo.setText(String.valueOf(buildingClayCount));
        tvBuildingHQInfo.setText(String.valueOf(buildingHQCount));
        tvBuildingInAll.setText(String.valueOf(buildingAllCount));
    }

    public void goToCurrentBuildingLocationByBuildingID(String myBuildingID) {
        Intent intentActMap = new Intent(this, ActMap.class);
        intentActMap.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
        intentActMap.putExtra(ActMap.GOTOSELECTEDMINE, true);

        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, myBuildingID.split(" "));

        double BuildingLat = Double.valueOf(arrayList.get(0));
        double BuildingLng = Double.valueOf(arrayList.get(1));

        intentActMap.putExtra(ActMap.SELECTEDMINELAT, BuildingLat);
        intentActMap.putExtra(ActMap.SELECTEDMINELNG, BuildingLng);
        startActivity(intentActMap);

        LatLng myBuildingMineLocation = new LatLng(BuildingLat, BuildingLng);

        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(myBuildingMineLocation, 15F);
        mvMap.moveCamera(camUpdate);
    }

}
