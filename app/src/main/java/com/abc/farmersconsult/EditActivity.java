package com.abc.farmersconsult;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EditActivity extends AppCompatActivity {

    private Button EditPost,Delete;
    private TextView EditDescription;
    private ImageView mImage_View;
    private DatabaseReference EditPostRef,mUsersRef,postRef;
    private static final int REQUEST_CAMERA = 3;
    private static final int SELECT_FILE = 2;
    private String saveCurrentdate,saveCurrentTime, PostRandomName;
    private String downloadUrl;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    StorageReference PostImagesReference;

    String CurrentUserID;

    Uri imageHoldUri = null;

    private String postKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        postKey=getIntent().getExtras().get("PostKey").toString();
        mAuth= FirebaseAuth.getInstance();
        CurrentUserID=mAuth.getCurrentUser().getUid();
        progressDialog=new ProgressDialog(this);

        PostImagesReference= FirebaseStorage.getInstance().getReference();


        mUsersRef=FirebaseDatabase.getInstance().getReference().child("User");
        postRef=FirebaseDatabase.getInstance().getReference("Posts");

        EditPostRef= postRef.child(postKey);


        EditDescription=(TextView) findViewById(R.id.edit_description);
        EditPost=(Button) findViewById(R.id.edit_post);
        Delete=(Button) findViewById(R.id.delete_post);
        mImage_View=(ImageButton)findViewById(R.id.edit_Image);

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        mImage_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenTheIntent();
            }
        });




        EditPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String description=dataSnapshot.child("description").getValue().toString();
                String Post_Image=dataSnapshot.child("postimage").getValue().toString();
                EditDescription.setText(description);
                Picasso.with(EditActivity.this).load(Post_Image).into(mImage_View);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

















    }



    private void SendUserToMain() {
        Intent intent=new Intent(EditActivity.this,Dashboard.class);
        startActivity(intent);

    }


    private void OpenTheIntent() {

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
                mImage_View.setImageURI(imageHoldUri);






            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }









}
