package com.abc.farmersconsult;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Comments extends AppCompatActivity {
    private ImageButton postComment;
    private EditText commentInput;
    private RecyclerView comment_list_recycler_view;
    private DatabaseReference mUsersRef,postsRef,PostsReference,DBRef,RootRef;
    private String comment_string, current_UserID;
    private FirebaseAuth mAuth;
    StorageReference userProfileImageRef;
    Uri imageHoldUri = null;
    private  CommentsAdapter mCommentsAdapter;

    private List<UserComments> mCommentsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        mCommentsList=new ArrayList<>();
        userProfileImageRef= FirebaseStorage.getInstance().getReference();

        mAuth=FirebaseAuth.getInstance();
        current_UserID=mAuth.getCurrentUser().getUid();
        comment_string=getIntent().getExtras().get("CommentsPosition").toString();
        comment_list_recycler_view=(RecyclerView) findViewById(R.id.comments_list);
        comment_list_recycler_view.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        comment_list_recycler_view.setLayoutManager(linearLayoutManager);

        PostsReference=FirebaseDatabase.getInstance().getReference("Posts");
        mUsersRef= FirebaseDatabase.getInstance().getReference("User");
        commentInput=(EditText) findViewById(R.id.comment_input);
        postComment=(ImageButton) findViewById(R.id.post_comment_button);
        postsRef=FirebaseDatabase.getInstance().getReference("Posts").child(comment_string).child("comments");

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mUsersRef.child(current_UserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){
                            String username=dataSnapshot.child("Name").getValue().toString();
                            ValidateComment(username);
                            commentInput.setText("");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

     displayUserComments();

    }




    private void displayUserComments() {

        postsRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCommentsList.clear();
                for(DataSnapshot PostSnapShot: dataSnapshot.getChildren()){
                    UserComments posts=PostSnapShot.getValue(UserComments.class);

                    mCommentsList.add(posts);
                }
                mCommentsAdapter=new CommentsAdapter(mCommentsList,Comments.this);


                mCommentsAdapter.notifyDataSetChanged();
                comment_list_recycler_view.setAdapter(mCommentsAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void ValidateComment(String username) {

        String commentText=commentInput.getText().toString();
        if(TextUtils.isEmpty(commentText)){
            Toast.makeText(Comments.this,"Please Write a Comment",Toast.LENGTH_LONG).show();
            }
        else{


            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate=currentDate.format(calForDate.getTime());

            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
            final String saveCurrentTime=currentTime.format(calForTime.getTime());




            final String RandomKey= current_UserID + saveCurrentDate+ saveCurrentTime;
            HashMap commentsMap=new HashMap();
            commentsMap.put("userid", current_UserID);
            commentsMap.put("comment", commentText);
            commentsMap.put("date",saveCurrentDate);
            commentsMap.put("time", saveCurrentTime);
            commentsMap.put("Name", username);



         postsRef.child(RandomKey).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
             @Override
             public void onComplete(@NonNull Task task) {

                 if(task.isSuccessful()){
                     Toast.makeText(Comments.this, "You Have Commented Successfully",Toast.LENGTH_LONG).show();
                 }


             }
         });



        }


    }








}
