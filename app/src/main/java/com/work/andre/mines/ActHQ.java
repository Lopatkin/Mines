package com.work.andre.mines;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import static com.work.andre.mines.ActMap.USERGOOGLEEMAIL;
import static com.work.andre.mines.R.drawable.hq2d;
import static com.work.andre.mines.R.drawable.hq2d_reconstruction;

public class ActHQ extends AppCompatActivity implements View.OnClickListener {

    Button btnOpenHQ;
    ImageView imgHQ;

    public static String currentUserNickName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hq);
        initUI();

        currentUserNickName = getIntent().getStringExtra(USERGOOGLEEMAIL);

        if (MyApp.getMyDBase().getHQAviable(currentUserNickName) == 0) {
            imgHQ.setImageResource(hq2d_reconstruction);
        } else {
            imgHQ.setImageResource(hq2d);
        }
    }

    public void initUI() {
//        btnOpenHQ = (Button) findViewById(R.id.btnOpenHQ);
//        btnOpenHQ.setOnClickListener(this);
        imgHQ = (ImageView) findViewById(R.id.imgHQ);
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.btnOpenHQ) {
//
//            int userID = MyApp.getMyDBase().getUserID(currentUserNickName);
//            MyApp.getMyDBase().setHQAviable(1, userID);
//
//            Intent intentActMap = new Intent(this, ActMap.class);
//            intentActMap.putExtra(USERNICKNAME, currentUserNickName);
//            startActivity(intentActMap);
//        }

    }
}