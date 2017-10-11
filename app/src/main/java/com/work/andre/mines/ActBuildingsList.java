package com.work.andre.mines;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.work.andre.mines.database.DBase;

import java.util.ArrayList;
import java.util.List;

import static com.work.andre.mines.ActMap.USERGOOGLEEMAIL;
import static com.work.andre.mines.ActMap.mvMap;
import static com.work.andre.mines.database.DBase.buildingCategoryOffice;
import static com.work.andre.mines.database.DBase.buildingTypeClay;
import static com.work.andre.mines.database.DBase.buildingTypeHQ;
import static com.work.andre.mines.database.DBase.buildingTypeStone;
import static com.work.andre.mines.database.DBase.buildingTypeWood;
import static com.work.andre.mines.database.DBase.getUserNickNameOrDisplayNameByGoogleEmail;

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
    ListView lvBuildingList;

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


    private static List<Buildings> buildings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings_list);

        bCategory = getIntent().getStringExtra(BUILDINGCATEGORY);
        currentUserGoogleEmail = getIntent().getStringExtra(USERGOOGLEEMAIL);
        currentUserNickName = getUserNickNameOrDisplayNameByGoogleEmail(currentUserGoogleEmail);

        buildings = new ArrayList<Buildings>();


        buildingWoodCount = 0;
        buildingStoneCount = 0;
        buildingClayCount = 0;
        buildingHQCount = 0;
        //.....
        buildingAllCount = 0;

        fillBuildings();

        this.initUI();

        tvOwnerName.setText(currentUserNickName);


        tvBuildingAllText.setVisibility(View.VISIBLE);
        tvBuildingInAll.setVisibility(View.VISIBLE);

        if (bCategory.equals(buildingCategoryOffice)) {         //Офисы
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


        } else {  //Шахты
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

        ArrayAdapter<Buildings> adapter = new BuildingsAdapter(this);
        lvBuildingList.setAdapter(adapter);

        lvBuildingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {

                //Достаём объект на текущей позиции
                Object myBuildingObject = lvBuildingList.getItemAtPosition(position);
                //Кастим этот объект в объект класса Buildings
                Buildings myBuilding = (Buildings) myBuildingObject;
                //Получаем id этого объекта
                int myBuildingID = myBuilding.buildingID;
                goToCurrentBuildingLocationByBuildingID(myBuildingID);
            }
        });
    }

    public void initUI() {
        ivMainIcon = (ImageView) findViewById(R.id.ivMainIcon);
        tvBuildingText = (TextView) findViewById(R.id.tvBuildingText);
        tvOwnerName = (TextView) findViewById(R.id.tvOwnerName);
        lvBuildingList = (ListView) findViewById(R.id.lvBuildingList);

        tvBuildingWoodText = (TextView) findViewById(R.id.tvBuildingWoodText);
        tvBuildingWoodInfo = (TextView) findViewById(R.id.tvBuildingWoodInfo);

        tvBuildingStoneText = (TextView) findViewById(R.id.tvBuildingStoneText);
        tvBuildingStoneInfo = (TextView) findViewById(R.id.tvBuildingStoneInfo);

        tvBuildingClayText = (TextView) findViewById(R.id.tvBuildingClayText);
        tvBuildingClayInfo = (TextView) findViewById(R.id.tvBuildingClayInfo);

        tvBuildingHQText = (TextView) findViewById(R.id.tvBuildingHQText);
        tvBuildingHQInfo = (TextView) findViewById(R.id.tvBuildingHQInfo);
        tvBuildingInAll = (TextView) findViewById(R.id.tvBuildingInAll);

        tvBuildingWoodInfo.setText(String.valueOf(buildingWoodCount));
        tvBuildingStoneInfo.setText(String.valueOf(buildingStoneCount));
        tvBuildingClayInfo.setText(String.valueOf(buildingClayCount));
        tvBuildingHQInfo.setText(String.valueOf(buildingHQCount));

        tvBuildingAllText = (TextView) findViewById(R.id.tvBuildingAllText);
    }

    public void goToCurrentBuildingLocationByBuildingID(int myBuildingID) {
        Intent intentActMap = new Intent(this, ActMap.class);
        intentActMap.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
        intentActMap.putExtra(ActMap.GOTOSELECTEDMINE, true);

        LatLng target = MyApp.getMyDBase().getBuildingLatLngByBuildingID(myBuildingID);
        double BuildingLat = target.latitude;
        double BuildingLng = target.longitude;

        intentActMap.putExtra(ActMap.SELECTEDMINELAT, BuildingLat);
        intentActMap.putExtra(ActMap.SELECTEDMINELNG, BuildingLng);
        startActivity(intentActMap);

        LatLng myBuildingMineLocation = MyApp.getMyDBase().getBuildingLatLngByBuildingID(myBuildingID);

        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(myBuildingMineLocation, 15F);
        mvMap.moveCamera(camUpdate);
    }

    private static class Buildings {
        public final String buildingName;
        public final String buildingType;
        public final int buildingLVL;
        public final int buildingID;

        public Buildings(String buildingName, String buildingType, int buildingLVL, int buildingID) {
            this.buildingName = buildingName;
            this.buildingType = buildingType;
            this.buildingLVL = buildingLVL;
            this.buildingID = buildingID;
        }
    }

    private class BuildingsAdapter extends ArrayAdapter<Buildings> {

        public BuildingsAdapter(Context context) {
            super(context, R.layout.list_my_buildings, buildings);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Buildings buildings = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_my_buildings, null);
            }
            ((TextView) convertView.findViewById(R.id.tvBuildingNameFromList))
                    .setText(buildings.buildingName);
            ((TextView) convertView.findViewById(R.id.tvBuildingTypeFromList))
                    .setText(buildings.buildingType);
            ((TextView) convertView.findViewById(R.id.tvBuildingLVLFromList))
                    .setText(buildings.buildingLVL + " ур.");
            ((TextView) convertView.findViewById(R.id.tvBuildingIDFromList))
                    .setText(String.valueOf(buildings.buildingID));
            return convertView;
        }
    }

    private void fillBuildings() {

        Cursor cr = MyApp.getMyDBase().getReadableCursor(DBase.BuildingsListTable.TABLE_BUILDINGS_LIST);
        if (cr.moveToFirst()) {
            do {
//              Индексы колонок
//                int col_buildingOwnerID = cr.getColumnIndex(DBase.BuildingsListTable.COLUMN_BUILDING_OWNER_ID);
                int col_buildingOwnerGoogleEmail = cr.getColumnIndex(DBase.BuildingsListTable.COLUMN_BUILDING_OWNER_GOOGLE_EMAIL);
                int col_buildingName = cr.getColumnIndex(DBase.BuildingsListTable.COLUMN_BUILDING_NAME);
                int col_buildingCategory = cr.getColumnIndex(DBase.BuildingsListTable.COLUMN_BUILDING_CATEGORY);
                int col_buildingType = cr.getColumnIndex(DBase.BuildingsListTable.COLUMN_BUILDING_TYPE);
                int col_buildingLVL = cr.getColumnIndex(DBase.BuildingsListTable.COLUMN_BUILDING_LVL);
                int col_ID = cr.getColumnIndex(DBase.BuildingsListTable._ID);

//              Значения колонок
//                int ownerID = cr.getInt(col_buildingOwnerID);
                String ownerName = getUserNickNameOrDisplayNameByGoogleEmail(cr.getString(col_buildingOwnerGoogleEmail));

                String buildingName = cr.getString(col_buildingName);
                String buildingCategory = cr.getString(col_buildingCategory);
                String buildingType = cr.getString(col_buildingType);
                int buildingLVL = cr.getInt(col_buildingLVL);
                int buildingID = cr.getInt(col_ID);

                if ((ownerName.equals(currentUserNickName)) && (bCategory.equals(buildingCategory))) {

                    buildings.add(new Buildings(buildingName, buildingType, buildingLVL, buildingID));

                    if (buildingType.equals(buildingTypeHQ)) {
                        buildingHQCount++;
                    }
                    if (buildingType.equals(buildingTypeWood)) {
                        buildingWoodCount++;
                    }
                    if (buildingType.equals(buildingTypeStone)) {
                        buildingStoneCount++;
                    }
                    if (buildingType.equals(buildingTypeClay)) {
                        buildingClayCount++;
                    }

                    //.....
                    buildingAllCount++;

                }
            } while (cr.moveToNext());
        }
        cr.close();
    }
}
