package com.example.ghassan.miniprojetandroid;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class Home extends AppCompatActivity {

    private CardView add;
    private CardView list;
    private CardView map;
    private static final String TAG = "Home";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        add = (CardView)findViewById(R.id.btnAdd);
        list = (CardView)findViewById(R.id.btnList);
        map = (CardView)findViewById(R.id.btnMap);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, AjoutEndroit.class));
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, ListeEndroits.class));
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServiceOk()){
                    startActivity(new Intent(Home.this, Carte.class));
                }
            }
        });
    }

    public boolean isServiceOk(){
        Log.d(TAG, "isServiceOk: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Home.this);
        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServiceOk: google services marche bien");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServiceOk: erreur reparable");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Home.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "on peut pas faire des map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
