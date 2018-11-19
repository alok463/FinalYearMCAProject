package com.abc.farmersconsult;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.widget.Button;

import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupProfile extends AppCompatActivity {
    EditText editText,place_name,email;
    private static final int REQUEST_CAMERA = 3;
    int PLACE_PICKER_REQUEST=1;

    private static final int SELECT_FILE = 2;
    Button button;
    Spinner spinner;
   ImageView circleImageView;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    DatabaseReference mUserRef;
    StorageReference userProfileImageRef;
    String CurrentUserID;
    private int contact;
    Uri imageHoldUri = null;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        //ProgressDialog
        progressDialog=new ProgressDialog(this);



        //Firebase Auth
        mAuth=FirebaseAuth.getInstance();



        CurrentUserID=mAuth.getCurrentUser().getUid();

        //Firebase Database
        mUserRef= FirebaseDatabase.getInstance().getReference().child("User").child(mAuth.getCurrentUser().getUid());
        userProfileImageRef=FirebaseStorage.getInstance().getReference();

       button=(Button)findViewById(R.id.Save);
        editText=(EditText) findViewById(R.id.Name);
        place_name=(EditText)findViewById(R.id.Place);
        spinner=(Spinner) findViewById(R.id.spinner_occupation);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,CountryData.options));

        circleImageView=(ImageView) findViewById(R.id.Image);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccount();
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 ProfilePickSelection();
            }
        });



    }

    private void saveAccount() {

        final String username = editText.getText().toString().trim();
        final String places = place_name.getText().toString().trim();
       final String code = getIntent().getStringExtra("Farmers");
        final String Land_Type=CountryData.options[spinner.getSelectedItemPosition()];
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(places)) {
            if (imageHoldUri != null) {

                progressDialog.setTitle("Saving Profile Image");
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);
                StorageReference ImageStore = userProfileImageRef.child("User_Profile").child(imageHoldUri.getLastPathSegment());
                String ProfileImage = imageHoldUri.getLastPathSegment();












                ImageStore.putFile(imageHoldUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Uri imageUrl = taskSnapshot.getDownloadUrl();
                        mUserRef.child("Name").setValue(username);
                        mUserRef.child("places").setValue(places);
                        mUserRef.child("Occupation").setValue(code);
                        mUserRef.child("Land_Type").setValue(Land_Type);
                        mUserRef.child("userid").setValue(mAuth.getCurrentUser().getUid());
                        mUserRef.child("Image").setValue(imageUrl.toString());
                        progressDialog.dismiss();
                        Intent moveToDash=new Intent(SetupProfile.this, Dashboard.class);
                        moveToDash.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(moveToDash);


                    }
                });


            } else {
                Toast.makeText(SetupProfile.this, "Please select the profile pic", Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(SetupProfile.this, "Please enter Status and Username", Toast.LENGTH_LONG).show();

        }


    }











    private void ProfilePickSelection() {
        //DISPLAY DIALOG TO CHOOSE CAMERA OR GALLERY

        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");

        //SET ITEMS AND THERE LISTENERS
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    private void cameraIntent() {

        //CHOOSE CAMERA
        Log.d("ALOK", "entered here");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {

        //CHOOSE IMAGE FROM GALLERY
        Log.d("ALOK", "entered here");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //SAVE URI FROM GALLERY
        if(requestCode == SELECT_FILE && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();



            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }else if ( requestCode == REQUEST_CAMERA && resultCode == RESULT_OK ){
            //SAVE URI FROM CAMERA



            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }


        //image crop library code
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageHoldUri = result.getUri();
                circleImageView.setImageURI(imageHoldUri);






           } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


        if(requestCode==PLACE_PICKER_REQUEST){
            if( resultCode==RESULT_OK) {

                Place place=PlacePicker.getPlace(SetupProfile.this,data);
                place_name.setText(place.getAddress());
            }
        }




    }

    public void goPlacePicker(View view){
        PlacePicker.IntentBuilder builder=new PlacePicker.IntentBuilder();
        try {

            startActivityForResult(builder.build(SetupProfile.this),PLACE_PICKER_REQUEST);

        }catch (GooglePlayServicesRepairableException e){
            e.printStackTrace();
        }
        catch (GooglePlayServicesNotAvailableException e){
            e.printStackTrace();
        }

    }






}




