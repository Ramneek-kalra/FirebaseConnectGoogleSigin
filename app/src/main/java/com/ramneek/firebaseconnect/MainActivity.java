package com.ramneek.firebaseconnect;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String TAG="LOG MESS:", degree;
    EditText name,className,address;
    Button submit,search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.uName);
        className = findViewById(R.id.uClass);
        address = findViewById(R.id.uAddress);
        submit = findViewById(R.id.buttonCloud);
        search = findViewById(R.id.buttonSearch);

        final Spinner spinner = findViewById(R.id.uDegree);
        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList <String>();
        categories.add("Select Degree");
        categories.add("BE/B.Tech");
        categories.add("BBA");
        categories.add("MBA");
        categories.add("B.Sc");
        categories.add("BA");
        categories.add("B.Pharmacy");
        ArrayAdapter <String> dataAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Create a new user with a first and last name
                final Map<String, Object> user = new HashMap<>();
                user.put("Name", name.getText().toString());
                user.put("Degree",degree);
                user.put("Class", className.getText().toString());
                user.put("Address", address.getText().toString());

                DocumentReference reference = db.collection("users").document(name.getText().toString()+"");
                reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            if(snapshot.exists()){
                                Toast.makeText(MainActivity.this, "Data Already Exists!", Toast.LENGTH_SHORT).show();
                            }else{
                                db.collection("users").document(name.getText().toString()+"").set(user);
                                Toast.makeText(MainActivity.this, "Data Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                                name.setText("");
                                className.setText("");
                                spinner.setId(0);
                                address.setText("");
                            }
                        }
                    }
                });
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SearchActivity.class));
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        degree = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Please do Select Valid Degree!", Toast.LENGTH_SHORT).show();
    }
}
