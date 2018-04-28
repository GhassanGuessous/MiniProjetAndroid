package com.example.ghassan.miniprojetandroid;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.Serializable;

public class ListeEndroits extends AppCompatActivity{

    private Spinner tags;
    private ListView liste_des_endroits;
    private Button searchBtn;
    private String tag = "";

    private FirebaseDatabase mFireBase;
    private DatabaseReference myRef;

    private FirebaseListAdapter<Endroit> firebaseListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_endroits);

        initSpinners();

        liste_des_endroits = (ListView) findViewById(R.id.listView);
        searchBtn = (Button)findViewById(R.id.search);

        mFireBase = FirebaseDatabase.getInstance();
        myRef = mFireBase.getReferenceFromUrl("https://miniprojetandroid-3f944.firebaseio.com/Endroits/");

        setListAdapter1(myRef);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tag.equals("Tout")) {
                    Query query = myRef.orderByChild("tag").equalTo(tag);
                    setListAdapter2(query);
                }else{
                    setListAdapter1(myRef);
                }
            }
        });

        liste_des_endroits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Endroit selected = (Endroit) parent.getItemAtPosition(position);

                Intent versInfos = new Intent(ListeEndroits.this, InfoEndroit.class);

                versInfos.putExtra("idEndroit", selected.getId());
                versInfos.putExtra("nom", selected.getNom());
                versInfos.putExtra("adr", selected.getAdresse());
                versInfos.putExtra("tag", selected.getTag());
                versInfos.putExtra("userId", selected.getUserId());
                versInfos.putExtra("filePath", selected.getImageFilePath());
                versInfos.putExtra("lat", selected.getLat());
                versInfos.putExtra("lng", selected.getLng());

                startActivity(versInfos);
            }
        });
    }

    public void setListAdapter1(DatabaseReference ref){
        firebaseListAdapter = new FirebaseListAdapter<Endroit>(
                ListeEndroits.this,
                Endroit.class,
                android.R.layout.two_line_list_item,
                ref
        ) {
            @Override
            protected void populateView(View v, Endroit model, int position) {
                TextView t1 = (TextView) v.findViewById(android.R.id.text1);
                t1.setText(model.getNom());

                TextView t2 = (TextView) v.findViewById(android.R.id.text2);
                t2.setText(model.getAdresse());
            }
        };

        liste_des_endroits.setAdapter(firebaseListAdapter);
    }

    public void setListAdapter2(Query ref){
        firebaseListAdapter = new FirebaseListAdapter<Endroit>(
                ListeEndroits.this,
                Endroit.class,
                android.R.layout.two_line_list_item,
                ref
        ) {
            @Override
            protected void populateView(View v, Endroit model, int position) {
                TextView t1 = (TextView) v.findViewById(android.R.id.text1);
                t1.setText(model.getNom());

                TextView t2 = (TextView) v.findViewById(android.R.id.text2);
                t2.setText(model.getAdresse());
            }
        };

        liste_des_endroits.setAdapter(firebaseListAdapter);
    }

    public void initSpinners(){
        tags = (Spinner)findViewById(R.id.tags_spinner);
        tags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tag = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<CharSequence> tagsAdapter = ArrayAdapter.createFromResource(this,
                R.array.tags, android.R.layout.simple_spinner_item);

        tagsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tags.setAdapter(tagsAdapter);
    }
}
