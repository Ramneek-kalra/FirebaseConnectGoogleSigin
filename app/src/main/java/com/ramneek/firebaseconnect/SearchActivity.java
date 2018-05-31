package com.ramneek.firebaseconnect;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity{
    String TAG="LOG MESS:", degree;
    EditText name,className,address,degreeName,searchName;
    Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        name = findViewById(R.id.uName_search);
        className = findViewById(R.id.uClass_search);
        address = findViewById(R.id.uAddress_search);
        degreeName = findViewById(R.id.uDegree_search);
        searchName = findViewById(R.id.SearchText);

        search = findViewById(R.id.buttonCloud_search);


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();

                DocumentReference reference = db.collection("users").document(searchName.getText().toString()+"");
                reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            if(snapshot.exists()){
                                name.setText(snapshot.getString("Name"));
                                className.setText(snapshot.getString("Class"));
                                address.setText(snapshot.getString("Address"));
                                degreeName.setText(snapshot.getString("Degree"));
                                Toast.makeText(SearchActivity.this, "Data Exists!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(SearchActivity.this, "Sorry! Please Register yourself by Register Activity!", Toast.LENGTH_SHORT).show();
                                name.setText("");
                                searchName.setText("");
                                className.setText("");
                                degreeName.setText("");
                                address.setText("");
                            }
                        }
                    }
                });
            }
        });
    }
}
