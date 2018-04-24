package com.example.ghassan.miniprojetandroid;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.tech.TagTechnology;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AjoutEndroit extends AppCompatActivity {

    private static final String TAG = "AjoutEndroit";

    private TextView nom;
    private TextView adr;
    private Spinner tag;
    private String tagSelected = "";

    private ImageView capturer;

    private ProgressDialog mProgressDialog;

    private StorageReference mStorage;

    private static final int CAMERA_REQUEST_CODE = 1;

    private String mCurrentPhotoPath;

    private Intent takePictureIntent;

    private FirebaseDatabase database;
    private String userId;
    private DatabaseReference myRef;
    private DatabaseReference cmpRef;
    private Uri imageFilePath;

    private int cmpValue;

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private boolean mLocationPermissionsGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_endroit);

        nom = (TextView)findViewById(R.id.nomTxt);
        adr = (TextView)findViewById(R.id.adrTxt);
        tag = (Spinner)findViewById(R.id.tagTxt);

        userId = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReferenceFromUrl("https://miniprojetandroid-3f944.firebaseio.com/");
        cmpRef = database.getReference("cmp");

        capturer = (ImageView) findViewById(R.id.captureIcon);

        mProgressDialog = new ProgressDialog(this);

        mStorage = FirebaseStorage.getInstance().getReference();

        initSpinners();

        capturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!testerChamps()){
                    Toast.makeText(AjoutEndroit.this, "Veuillez remplir les champs !", Toast.LENGTH_SHORT).show();
                }else{
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                }
            }
        });

        cmpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //on recupere le nombre d'endroits stockés
                cmpValue = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK ){

            mProgressDialog.setMessage("Envoie en cours...");
            mProgressDialog.show();

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // RECUPERE L'URI A PARTIR DU BITMAP
            Uri uri = getImageUri(getApplicationContext(), imageBitmap);

            if(uri != null) {
                cmpValue++;
                StorageReference filePath = mStorage.child("Endroits").child("" + cmpValue).child(uri.getLastPathSegment());

                filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //ajout de l'endroit apres avoir ajouter sa photo
                        imageFilePath = taskSnapshot.getDownloadUrl();
                        ajout();

                        mProgressDialog.dismiss();
                        viderChamps();
                        Toast.makeText(AjoutEndroit.this, "Ajout effectué avec succes", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(AjoutEndroit.this, "Ajout échoué", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void viderChamps() {
        nom.setText("");
        adr.setText("");
        initSpinners();
    }

    public void ajout(){

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LatLng latLng = getLocation();
        if(latLng != null) {
            Endroit endroit = new Endroit(
                    "e" + cmpValue,
                    nom.getText().toString(),
                    adr.getText().toString(),
                    tagSelected,
                    userId,
                    imageFilePath + "",
                    latLng.latitude,
                    latLng.longitude
            );

            myRef.child("Endroits").child("" + cmpValue).setValue(endroit);
            cmpRef.setValue(cmpValue);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public boolean testerChamps(){
        if(nom.getText().equals(""))
            return false;
        else if(adr.getText().equals(""))
            return false;
        else if(tagSelected.equals(""))
            return false;
        else
            return true;
    }

    public void initSpinners(){
        tag = (Spinner)findViewById(R.id.tagTxt);
        tag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tagSelected = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<CharSequence> tagsAdapter = ArrayAdapter.createFromResource(this,
                R.array.tags, android.R.layout.simple_spinner_item);

        tagsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tag.setAdapter(tagsAdapter);
    }

    public LatLng getLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        }else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null){
                Log.d(TAG, "getLocation: ============> lat = " + location.getLatitude() + ", lng = " + location.getLongitude());
                return new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                Log.d(TAG, "getLocation: Unable to find correct location.");
            }
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }
}
