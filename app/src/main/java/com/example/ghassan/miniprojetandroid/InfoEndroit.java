package com.example.ghassan.miniprojetandroid;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class InfoEndroit extends AppCompatActivity {

    private TextView nom;
    private TextView adr;
    private TextView tag;
    private TextView ajoutePar;
    private ImageView image_endroit;
    private Button geolocaliser;

    private ListView commentsListView;

    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference myRef;
    private DatabaseReference cmpRefComment;

    private double lat;
    private double lng;

    private FirebaseListAdapter<Commentaire> firebaseListAdapter;

    private FirebaseDatabase mFireBase;

    private EditText mComment;
    private int cmpComment;
    private String idEndroit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_endroit);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        initComponents();

        Picasso.get().load(getIntent().getExtras().getString("filePath")).fit().centerCrop().into(image_endroit);

        nom.setText(getIntent().getExtras().getString("nom"));
        adr.setText(getIntent().getExtras().getString("adr"));
        tag.setText(getIntent().getExtras().getString("tag"));
        ajoutePar.setText(getIntent().getExtras().getString("userId"));

        idEndroit = (String) getIntent().getExtras().get("idEndroit");

        lat = getIntent().getExtras().getDouble("lat");
        lng = getIntent().getExtras().getDouble("lng");

        mFireBase = FirebaseDatabase.getInstance();
        myRef = mFireBase.getReferenceFromUrl("https://miniprojetandroid-3f944.firebaseio.com/Comments/" + idEndroit + "/");

        setListAdapter(myRef);

        geolocaliser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent versCarte = new Intent(InfoEndroit.this, Carte.class);
                versCarte.putExtra("nomPlace", nom.getText().toString());
                versCarte.putExtra("lat", lat);
                versCarte.putExtra("lng", lng);
                startActivity(versCarte);
            }
        });

        cmpRefComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //on recupere le nombre de commentaires stockés
                cmpComment = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setListAdapter(DatabaseReference ref){
        firebaseListAdapter = new FirebaseListAdapter<Commentaire>(
                InfoEndroit.this,
                Commentaire.class,
                android.R.layout.two_line_list_item,
                ref
        ) {
            @Override
            protected void populateView(View v, Commentaire model, int position) {
                TextView t1 = (TextView) v.findViewById(android.R.id.text1);
                t1.setText(model.getUser() + " : ");

                TextView t2 = (TextView) v.findViewById(android.R.id.text2);
                t2.setText(model.getComment());
            }
        };

        commentsListView.setAdapter(firebaseListAdapter);

        mComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //comment
                    Commentaire nvCommentaire = new Commentaire(
                            FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                            mComment.getText().toString()
                    );

                    myRef.child("" + (++cmpComment)).setValue(nvCommentaire);
                    mComment.setText("");
                    cmpRefComment.setValue(cmpComment);
                }

                return false;
            }
        });
    }

    private void initComponents() {
        nom = (TextView)findViewById(R.id.nomEndroit);
        adr = (TextView)findViewById(R.id.adrEndroit);
        tag = (TextView)findViewById(R.id.tagEndroit);
        ajoutePar = (TextView)findViewById(R.id.ajoute_par);
        image_endroit = (ImageView) findViewById(R.id.image);
        geolocaliser = (Button)findViewById(R.id.geolocaliserBtn);
        commentsListView = (ListView)findViewById(R.id.commentListView);
        mComment = (EditText)findViewById(R.id.input_comment);

        cmpRefComment = database.getReference("cmpComment");
    }
}
