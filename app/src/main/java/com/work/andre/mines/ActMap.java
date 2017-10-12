package com.work.andre.mines;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.location.LocationListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.work.andre.mines.ActBuildingDetails.buildingID;
import static com.work.andre.mines.ActBuildingsList.BUILDINGCATEGORY;
import static com.work.andre.mines.database.DBase.addNewBuilding;
import static com.work.andre.mines.database.DBase.buildingCategoryMining;
import static com.work.andre.mines.database.DBase.buildingCategoryOffice;
import static com.work.andre.mines.database.DBase.buildingTypeClay;
import static com.work.andre.mines.database.DBase.buildingTypeHQ;
import static com.work.andre.mines.database.DBase.buildingTypeStone;
import static com.work.andre.mines.database.DBase.buildingTypeWood;

import static com.work.andre.mines.database.DBase.buildingTypeClayEn;
import static com.work.andre.mines.database.DBase.buildingTypeHQEn;
import static com.work.andre.mines.database.DBase.buildingTypeStoneEn;
import static com.work.andre.mines.database.DBase.buildingTypeWoodEn;


import static com.work.andre.mines.database.DBase.fbBuildings;
import static com.work.andre.mines.database.DBase.fbUsers;
import static com.work.andre.mines.database.DBase.getBuildingCategoryByBuildingType;
import static com.work.andre.mines.database.DBase.getBuildingID;
import static com.work.andre.mines.database.DBase.getUserNickNameOrDisplayNameByGoogleEmailWithSnapshot;
import static com.work.andre.mines.database.DBase.getUserResourcesWithSnapshot;
import static com.work.andre.mines.database.DBase.resClay;
import static com.work.andre.mines.database.DBase.resGold;
import static com.work.andre.mines.database.DBase.resStone;
import static com.work.andre.mines.database.DBase.resWood;

public class ActMap extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener {

    public static final String USERGOOGLEEMAIL = "userGoogleEmail";
    public static final String GOOGLEAPI = "GoogleAPI";

    public static final String GOTOSELECTEDMINE = "goToSelectedMine";
    public static final String SELECTEDMINELAT = "selectedMineLat";
    public static final String SELECTEDMINELNG = "selectedMineLng";

    public static String Structure = "Постройка";

    static long userGold;
    static long userWood;
    static long userStone;
    static long userClay;

    static long costGold;
    static long costWood;
    static long costStone;
    static long costClay;

    static boolean getCost;
    static boolean getUserMoney;

    static boolean more;

    static boolean payIsOk;


    static String buildingType;
    static String buildingCategory;
    static String buildingName;
    static int structureLVL;
    static double buildingLat;
    static double buildingLng;
    static String buildingBuildDate;

    Context context;
    AlertDialog.Builder ad;

    static String userGoogleEmail;
    static String buildingOwnerNickName;

    //Объекты для карты
    public static GoogleMap mvMap = null;
    SupportMapFragment mapFragment;
    private LocationManager locManager = null;
    private LocListener locListener = null;
    public static Location myLocation = null;

    //Пользовательский интерфейс
    TextView tvCurrentUserNickName;
    TextView tvUserGold;
    TextView tvUserWood;
    TextView tvUserStone;
    TextView tvUserClay;
    Button btnHQ;
    Button btnBuildings;
    Button btnMines;
    Button btnSettings;

    //Алерт диалог
    static Spinner spnrBuildingTypesList;
    static TextView tvBuildingName;
    static EditText etBuildingName;

    static boolean isGoToSelectedMine;
    static double selectedMineLat;
    static double selectedMineLng;

    String currentUserNickName;
    String currentUserGoogleEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        this.initUI();

        //Храним здесь email текущего пользователя
        currentUserGoogleEmail = getIntent().getStringExtra(USERGOOGLEEMAIL);

        //Устанавливаем доступное количество ресурсов у пользователя
//        setResourcesCount();

        //Получаем ссылку на Location Manager
        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        myLocation = getMyGPS(locManager);

        isGoToSelectedMine = getIntent().getBooleanExtra(GOTOSELECTEDMINE, false);
        selectedMineLat = getIntent().getDoubleExtra(SELECTEDMINELAT, 0.0);
        selectedMineLng = getIntent().getDoubleExtra(SELECTEDMINELNG, 0.0);

        //......................................FIRESTORE......................................
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(fbUsers).document(currentUserGoogleEmail);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (snapshot != null && snapshot.exists()) {
                    currentUserNickName = getUserNickNameOrDisplayNameByGoogleEmailWithSnapshot(snapshot);
                    updateUI();
                    setResourcesCount(snapshot);
                }
            }
        });
    }

    private void updateUI() {
        tvCurrentUserNickName.setText(currentUserNickName);
    }

    public void setResourcesCount(DocumentSnapshot snapshot) {
        tvUserGold.setText(String.valueOf(getUserResourcesWithSnapshot(snapshot, resGold)));
        tvUserWood.setText(String.valueOf(getUserResourcesWithSnapshot(snapshot, resWood)));
        tvUserStone.setText(String.valueOf(getUserResourcesWithSnapshot(snapshot, resStone)));
        tvUserClay.setText(String.valueOf(getUserResourcesWithSnapshot(snapshot, resClay)));
    }

    public Location getMyGPS(LocationManager locationManager) {

        /* Define Location variable */
        Location loc = null;

        try {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
//                return TODO;
            }
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                return loc;
            }

		/* Get information from Network location provider */
            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) {
                return loc;
            }

		/* Get information from Passive location provider */
            loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (loc != null) {
                return loc;
            }
        } catch (Exception e) {
        }

        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        String buildingID = (String) marker.getTag();


        Intent intentBuildingDetails = new Intent(this, ActBuildingDetails.class);
        intentBuildingDetails.putExtra(ActBuildingDetails.BUILDINGID, buildingID);
        intentBuildingDetails.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
        startActivity(intentBuildingDetails);
    }

    private class LocListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            myLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

    }

    public void initUI() {
        tvCurrentUserNickName = (TextView) findViewById(R.id.tvCurrentUserNickName);
        tvUserGold = (TextView) findViewById(R.id.tvUserGoldInfo);
        tvUserWood = (TextView) findViewById(R.id.tvUserWoodInfo);
        tvUserStone = (TextView) findViewById(R.id.tvUserStoneInfo);
        tvUserClay = (TextView) findViewById(R.id.tvUserClayInfo);

        btnHQ = (Button) findViewById(R.id.btnHQ);
        btnHQ.setOnClickListener(this);

        btnBuildings = (Button) findViewById(R.id.btnBuildings);
        btnBuildings.setOnClickListener(this);

        btnMines = (Button) findViewById(R.id.btnMines);
        btnMines.setOnClickListener(this);

        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frMap);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnHQ) {
            Intent intentHQ = new Intent(this, ActHQ.class);
            intentHQ.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
            startActivity(intentHQ);
        }

        if (v.getId() == R.id.btnBuildings) {
            Intent intentBuildings = new Intent(this, ActBuildingsList.class);
            intentBuildings.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
            intentBuildings.putExtra(BUILDINGCATEGORY, buildingCategoryOffice);
            startActivity(intentBuildings);
        }

        if (v.getId() == R.id.btnMines) {
            Intent intentBuildings = new Intent(this, ActBuildingsList.class);
            intentBuildings.putExtra(BUILDINGCATEGORY, buildingCategoryMining);
            intentBuildings.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
            startActivity(intentBuildings);
        }

        if (v.getId() == R.id.btnSettings) {
            Intent intentSettings = new Intent(this, ActSettings.class);
            intentSettings.putExtra(USERGOOGLEEMAIL, currentUserGoogleEmail);
            startActivity(intentSettings);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

//        if (MyApp.getMyDBase().canIPutMyNewBuildingHere(latLng)) {
//            addNewBuildingDialog(latLng);
//        } else {
//            Toast toast = Toast.makeText(getApplicationContext(), "Слишком близко к соседней постройке!", Toast.LENGTH_SHORT);
//            toast.show();
//        }

        addNewBuildingDialog(latLng);

    }

    public void addNewBuildingDialog(final LatLng latLng) {

        //Создание диалогового окна
        final LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.add_new_building_dialog, null);

        //Cоздаем AlertDialog
        AlertDialog.Builder mDialogNBuilder = new AlertDialog.Builder(this);

        //Прикручиваем лейаут к алерту
        mDialogNBuilder.setView(dialogView);

        //Инициализация компонентов
        spnrBuildingTypesList = (Spinner) dialogView.findViewById(R.id.spnrBuildingTypesList);
        tvBuildingName = (TextView) dialogView.findViewById(R.id.tvBuildingName);
        etBuildingName = (EditText) dialogView.findViewById(R.id.etBuildingName);

        List<String> structureList = new ArrayList<String>();
        structureList.add(buildingTypeWood);
        structureList.add(buildingTypeStone);
        structureList.add(buildingTypeClay);

        if (MyApp.getMyDBase().getHQAviable(currentUserGoogleEmail) == 0) {
            structureList.add(buildingTypeHQ);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, structureList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnrBuildingTypesList.setAdapter(adapter);
        etBuildingName.setText(Structure);

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
                        LatLng target = new LatLng(latLng.latitude, latLng.longitude);

                        //Заполняем поля для записи постройки
                        buildingType = spnrBuildingTypesList.getSelectedItem().toString();                  //Тип постройки
                        buildingName = etBuildingName.getText().toString();                                  //Название постройки
                        structureLVL = 1;                                                                    //Уровень постройки
                        buildingLat = target.latitude;                                                   //Координата Lat
                        buildingLng = target.longitude;                                                  //Координата Lng

                        buildingCategory = getBuildingCategoryByBuildingType(buildingType);

                        Locale local = new Locale("ru", "RU");
                        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, local);
                        Date currentDate = new Date();
                        buildingBuildDate = df.format(currentDate);                                      //Дата постройки (текущая)

                        getCost = false;
                        getUserMoney = false;
                        more = true;
                        payIsOk = true;

                        //Оплачиваем постройку
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
                        String doc = bType + structureLVL;

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

                                        getCost = true;
                                    }
                                }
                            }
                        });

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

                                        if (((costGold > 0) && (userGold >= costGold)) ||
                                                ((costWood > 0) && (userWood >= costWood)) ||
                                                ((costStone > 0) && (userStone > costStone)) ||
                                                ((costClay > 0) && (userClay > costClay))) {

                                            HashMap<String, Object> updatedData = new HashMap<>();
                                            updatedData.put("userGold", userGold - costGold);
                                            updatedData.put("userWood", userWood - costWood);
                                            updatedData.put("userStone", userStone - costStone);
                                            updatedData.put("userClay", userClay - costClay);

                                            FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                                            DocumentReference documentReference = db2.collection(fbUsers).document(currentUserGoogleEmail);

                                            more = false;
                                            //Добавляем новую постройку
                                            payIsOk = true;
                                            addNewBuilding(currentUserNickName, currentUserGoogleEmail, buildingType, buildingCategory, buildingName, structureLVL, buildingLat, buildingLng, buildingBuildDate);

                                            for (Map.Entry entry : updatedData.entrySet()) {
                                                documentReference.update(entry.getKey().toString(), entry.getValue());
                                            }
                                        }
                                    }
                                    if (!more) {
                                        break;
                                    }
                                }
                            }
                        }).start();
                    }
                });

        AlertDialog alertDialog = mDialogNBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onMapReady(GoogleMap map) {

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
//                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
//            Log.e(TAG, "Can't find style. Error: ", e);
        }

        /* Enables the my-location layer in the map */
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        mvMap = map;
        mvMap.setOnMapLongClickListener(this);
        mvMap.setOnInfoWindowClickListener(this);

        LatLng target;
        CameraUpdate camUpdate;

        if (myLocation == null) {
            target = new LatLng(0.0, 0.0);
        } else {
            target = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        }

        if (isGoToSelectedMine) {
            target = new LatLng(selectedMineLat, selectedMineLng);
            camUpdate = CameraUpdateFactory.newLatLngZoom(target, 16F);
        } else {
            camUpdate = CameraUpdateFactory.newLatLngZoom(target, 15F);
        }

        mvMap.moveCamera(camUpdate);
        loadBuildings();
    }

    private void loadBuildings() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(fbBuildings)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
//                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        mvMap.clear();
                        for (final DocumentSnapshot doc : value) {

                            userGoogleEmail = (String) doc.get("userGoogleEmail");
                            String buildingLat;
                            String buildingLng;

                            buildingLat = (String) doc.get("buildingLat");
                            buildingLng = (String) doc.get("buildingLng");
                            buildingID = getBuildingID(buildingLat, buildingLng);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection(fbUsers)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {

                                                    if (doc.get("userGoogleEmail").equals(document.get("UserGoogleEmail"))) {
                                                        if (String.valueOf(document.get("NickName")).length() > 0) {
                                                            setupBuilding(String.valueOf(document.get("UserGoogleEmail")), String.valueOf(document.get("NickName")), (long) doc.get("buildingLVL"), (String) doc.get("buildingType"), (String) doc.get("buildingName"), buildingID, (String) doc.get("buildingLat"), (String) doc.get("buildingLng"));
                                                        } else {
                                                            setupBuilding(String.valueOf(document.get("UserGoogleEmail")), String.valueOf(document.get("DisplayName")), (long) doc.get("buildingLVL"), (String) doc.get("buildingType"), (String) doc.get("buildingName"), buildingID, (String) doc.get("buildingLat"), (String) doc.get("buildingLng"));
                                                        }
                                                    }

                                                }
                                            } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
                        }
//                        Log.d(TAG, "Current cites in CA: " + cities);
                    }
                });
    }

    private void setupBuilding(String userGoogleEmail, String userName, long buildingLVL, String buildingType, String buildingName, String buildingID, String strbuildingLat, String strbuildingLng) {

        double buildingLat = Double.parseDouble(strbuildingLat);
        double buildingLng = Double.parseDouble(strbuildingLng);

        String myBuildingID = getBuildingID(buildingLat, buildingLng);
        LatLng target = new LatLng(buildingLat, buildingLng);

        float markSize = 100f;

        //Создаём наложение на карту
        GroundOverlayOptions newarkMap = new GroundOverlayOptions();
        try {
            if (buildingType.equals(buildingTypeHQ)) {
                markSize = 150f;
                newarkMap.image(BitmapDescriptorFactory.fromResource(R.drawable.headquoter));

                GroundOverlayOptions newarkMapZone = new GroundOverlayOptions();
                if (currentUserGoogleEmail.equals(userGoogleEmail)) {
                    newarkMapZone.image(BitmapDescriptorFactory.fromResource(R.drawable.hq_zone_green));
                } else {
                    newarkMapZone.image(BitmapDescriptorFactory.fromResource(R.drawable.hq_zone_red));
                }
                newarkMapZone.zIndex(-1000);
                newarkMapZone.position(target, 1000f, 1000f);
                mvMap.addGroundOverlay(newarkMapZone);
            } else if (buildingType.equals(buildingTypeWood)) {
                newarkMap.image(BitmapDescriptorFactory.fromResource(R.drawable.mine_forest));
            } else if (buildingType.equals(buildingTypeStone)) {
                newarkMap.image(BitmapDescriptorFactory.fromResource(R.drawable.mine_stone));
            } else if (buildingType.equals(buildingTypeClay)) {
                newarkMap.image(BitmapDescriptorFactory.fromResource(R.drawable.mine_sand));
            } else {
                newarkMap.image(BitmapDescriptorFactory.fromResource(R.drawable.mineicon));
            }

            newarkMap.position(target, markSize, markSize);
            mvMap.addGroundOverlay(newarkMap);

            buildingOwnerNickName = userName;

            //Создаём маркер на карте
            Marker marker = mvMap.addMarker(new MarkerOptions()
                    .position(target)
                    .title(buildingLVL + "lvl: " + buildingName)
                    .alpha(0)
                    .snippet("Владелец: " + userName)
                    .flat(true)
                    .draggable(false));

            marker.setTag(myBuildingID);

            if (buildingType.equals(buildingTypeHQ)) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.building_hq_mini_marker));
            } else if (buildingType.equals(buildingTypeWood)) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mine_forest_mini_marker));
            } else if (buildingType.equals(buildingTypeStone)) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mine_stone_mini_marker));
            } else if (buildingType.equals(buildingTypeClay)) {
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.mine_sand_mini_marker));
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {

		/* Invoke a parent method */
        super.onResume();

		/* Create Location Listener object (if needed) */
        if (locListener == null)
            locListener = new LocListener();

		/* Setting up Location Listener */
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000L, 1.0F, locListener);
    }

    @Override
    protected void onPause() {

		/* Remove Location Listener */
        locManager.removeUpdates(locListener);

		/* Invoke a parent method */
        super.onPause();

    }

    public void onBackPressed() {
        // super.onBackPressed();
        openQuitDialog();
    }

    private void openQuitDialog() {
        context = ActMap.this;
        ad = new AlertDialog.Builder(context);

        String title = "Внимание!";
        String message = "Вы действительно хотите выйти?";
        String btnYes = "Извольте. Мне пора!";
        String btnNo = "Пожалуй, ещё немного задержусь";

        ad.setTitle(title);  // заголовок
        ad.setMessage(message); // сообщение
        ad.setPositiveButton(btnYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent intentActMap = new Intent(ActMap.this, ActMain.class);
                startActivity(intentActMap);
            }
        });
        ad.setNegativeButton(btnNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                //передумали удялть шахту
            }
        });
        ad.show();
    }
}