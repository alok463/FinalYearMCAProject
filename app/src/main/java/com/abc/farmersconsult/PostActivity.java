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

import  android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.FirebaseStorage;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {


    private ImageButton SelectPostImage;
    private EditText PostDescription, place_name;
    private Button UpdatePostBtn;
    private Toolbar mToolbar;
    public String description;
    private String saveCurrentdate,saveCurrentTime, PostRandomName;
    private String downloadUrl;
    ProgressDialog progressDialog;
    DatabaseReference mUsersRef,postRef;
    DatabaseReference NotificationRef;
    int PLACE_PICKER_REQUEST=1;
    String CurrentUserID;
    private static final int REQUEST_CAMERA = 3;
    private static final int SELECT_FILE = 2;
    Uri imageHoldUri = null;
    StorageReference PostImagesReference;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mAuth= FirebaseAuth.getInstance();
        CurrentUserID=mAuth.getCurrentUser().getUid();

       PostImagesReference=FirebaseStorage.getInstance().getReference();


       mUsersRef=FirebaseDatabase.getInstance().getReference().child("User");
       postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
       NotificationRef=FirebaseDatabase.getInstance().getReference().child("Notifications");
       NotificationRef.keepSynced(true);






        PostDescription=(EditText) findViewById(R.id.description);
        progressDialog=new ProgressDialog(this);
        UpdatePostBtn=(Button)findViewById(R.id.update_post_btn);
        SelectPostImage=(ImageButton) findViewById(R.id.imageButton);
        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenTheIntent();
            }
        });

        UpdatePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();
            }
        });





        mToolbar=(Toolbar) findViewById(R.id.update_post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");

    }

    private void ValidatePostInfo() {

         description=PostDescription.getText().toString();
        if(imageHoldUri==null){
            Toast.makeText(PostActivity.this, "Please Select Image", Toast.LENGTH_LONG).show();
        }

        else if(TextUtils.isEmpty(description)){
            Toast.makeText(PostActivity.this, "Please add a description about the Image", Toast.LENGTH_LONG).show();
        }

        else{
            progressDialog.setTitle("Uploading User Post");
            progressDialog.setMessage("Please Wait While The Image is Being Uploaded");

            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            StorageImageToFirebase();
        }

    }

    private void StorageImageToFirebase() {

        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentdate=currentDate.format(calForDate.getTime());

        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        saveCurrentTime=currentTime.format(calForTime.getTime());
        PostRandomName = saveCurrentdate + saveCurrentTime;

        StorageReference filepath=PostImagesReference.child("User_Post").child(imageHoldUri.getLastPathSegment() + PostRandomName + ".jpg");
      filepath.putFile(imageHoldUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
          @Override
          public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

              if(task.isSuccessful()){
                  
                  downloadUrl=task.getResult().getDownloadUrl().toString();

                  Toast.makeText(PostActivity.this, "Image Uploaded Successfully",Toast.LENGTH_LONG).show();
                  SavingPostInformation();
                  
              }
              else{
                  String message=task.getException().getMessage();
                  Toast.makeText(PostActivity.this,"Error occured"+ message,Toast.LENGTH_LONG).show();
              }

          }
      });

    }

    private void SavingPostInformation() {
    mUsersRef.child(CurrentUserID).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            String userFullName = dataSnapshot.child("Name").getValue().toString();
            String userProfileImage = dataSnapshot.child("Image").getValue().toString();
            String place_name=dataSnapshot.child("places").getValue().toString();
            String Occupations=dataSnapshot.child("Occupation").getValue().toString();

            HashMap postsMap = new HashMap();
           postsMap.put("userid", CurrentUserID);
        postsMap.put("date", saveCurrentdate);
            postsMap.put("time", saveCurrentTime);
          postsMap.put("description", description);
          postsMap.put("postimage", downloadUrl);
          postsMap.put("places",place_name);
          postsMap.put("Occupation",Occupations);
            postsMap.put("Image", userProfileImage);
            postsMap.put("Name", userFullName);
            postRef.child(CurrentUserID + PostRandomName).updateChildren(postsMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if(task.isSuccessful())
                            {

                                HashMap<String,String> notification_data=new HashMap<>();

                                SendUserToMain();
                                Toast.makeText(PostActivity.this, "New Post is updated successfully.", Toast.LENGTH_SHORT).show();

                                  progressDialog.dismiss();

                            }
                            else
                            {
                                Toast.makeText(PostActivity.this, "Error Occured while updating your post.", Toast.LENGTH_SHORT).show();
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        progressDialog.dismiss();
                                    }
                                });

                            }
                        }
                    });
            }


        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });





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
                    SelectPostImage.setImageURI(imageHoldUri);






                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }




        }









    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        int id=item.getItemId();
        if(id==android.R.id.home){
            SendUserToMain();

        }
        return  super.onOptionsItemSelected(item);
    }

    private void SendUserToMain() {
        Intent main=new Intent(PostActivity.this, Dashboard.class);
        startActivity(main);
    }


}
