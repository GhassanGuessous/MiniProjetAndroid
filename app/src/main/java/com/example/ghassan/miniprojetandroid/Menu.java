package com.example.ghassan.miniprojetandroid;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class Menu extends AppCompatActivity {

    private Button addPlace;
    private Button map;
    private Button list;

    private static final String TAG = "Menu Activity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        addPlace = (Button)findViewById(R.id.addPlaceBtn);
        map = (Button)findViewById(R.id.mapBtn);
        list = (Button)findViewById(R.id.listBtn);

        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Menu.this, AjoutEndroit.class));
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServiceOk()){
                    startActivity(new Intent(Menu.this, Carte.class));
                }
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Menu.this, ListeEndroits.class));
            }
        });
    }

    public boolean isServiceOk(){
        Log.d(TAG, "isServiceOk: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Menu.this);
        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServiceOk: google services hua hadak");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServiceOk: kain erreur w n9edrou nsel7ouh");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Menu.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "mat9derch dir map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
