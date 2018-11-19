package com.abc.farmersconsult;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UsersSetup extends AppCompatActivity {
    String[] Occupation = {"Farmer", "PestExpert","Others"};
    String SpinnerValue;
    Spinner mSpinner;
    Button mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_setup);





        mSpinner=(Spinner)findViewById(R.id.Spinner);
        mNext=(Button) findViewById(R.id.Continue);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,Occupation);
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SpinnerValue=(String)mSpinner.getSelectedItem();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (SpinnerValue){
                    case "Farmer":
                        Intent farm=new Intent(UsersSetup.this, SetupProfile.class);
                        farm.putExtra("Farmers",SpinnerValue);
                        startActivity(farm);
                        finish();
                        break;


                    case "PestExpert":
                        Intent pest=new Intent(UsersSetup.this, PestExpert.class);
                        pest.putExtra("PestExpert",SpinnerValue);
                        startActivity(pest);
                        finish();
                        break;


                    case "Others":
                       deleteUsers();
                       break;

                }


            }
        });



    }

    private void deleteUsers() {

       final FirebaseAuth mAuth=FirebaseAuth.getInstance();
       final FirebaseUser mUser=mAuth.getCurrentUser();
       mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
             if(task.isSuccessful()){
                 Toast.makeText(UsersSetup.this,"You are not Authorized To Use this App",Toast.LENGTH_LONG).show();
                 startActivity(new Intent(UsersSetup.this,RegisterActivity.class));
                 finish();
             }

           }
       });


    }
}
