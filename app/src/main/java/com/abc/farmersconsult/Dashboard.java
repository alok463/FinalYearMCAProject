package com.abc.farmersconsult;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.FirebaseRecyclerOptions.Builder;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;



public class Dashboard extends AppCompatActivity implements PostAdapter.OnItemClickListener, PostAdapter.OnItemClicked {
   private DrawerLayout drawerLayout;
  //Navigation Elements
   private Toolbar mtoolbar;
   private NavigationView navigationView;
   private ActionBarDrawerToggle actionBarDrawerToggle;
   private RecyclerView recyclerView;

   private ImageView circleImageView;
   private ImageButton addnewpost;
   private List<Posts>PostList;
   private ValueEventListener mDBListener;


   private TextView UserText;
   private PostAdapter mPostAdapter;

  /* private FirebaseRecyclerOptions options;
   private RecyclerView.ViewHolder viewHolder;*/
   Context context;




   //Auth Fields of Firebase
   FirebaseAuth mAuth;
   FirebaseAuth.AuthStateListener mAuthStateListener;


   //Database References
   DatabaseReference  userRef,PostsReference,likesRef;

   String currentUserID;

   //Storage Reference

    FirebaseStorage mStorage;
    Boolean LikeCheck=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.Navigation_View);

      recyclerView=(RecyclerView) findViewById(R.id.all_users_post);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));


       PostList=new ArrayList<>();
       PostsReference=FirebaseDatabase.getInstance().getReference("Posts");


        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        //Navigation Header View
        View navView=navigationView.inflateHeaderView(R.layout.navigation_header);
        circleImageView=(ImageView) navView.findViewById(R.id.nav_profile);
        UserText=(TextView) navView.findViewById(R.id.User_Name);
        addnewpost=(ImageButton) findViewById(R.id.add_new_post_button);






        //Toolbar Settings
        mtoolbar = (Toolbar) findViewById(R.id.action_tool);
       setSupportActionBar(mtoolbar);
       getSupportActionBar().setTitle("Home");
       actionBarDrawerToggle=new ActionBarDrawerToggle(Dashboard.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
       drawerLayout.addDrawerListener(actionBarDrawerToggle);
       currentUserID=mAuth.getCurrentUser().getUid();
       actionBarDrawerToggle.syncState();
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);





       navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              OnMenuItemSelected(item);
              
               return false;
           }
       });

       addnewpost.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               SendUserToPostActivity();
           }
       });

        userRef= FirebaseDatabase.getInstance().getReference().child("User");
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                        if(dataSnapshot.hasChild("Name")) {
                            String fullname = dataSnapshot.child("Name").getValue().toString();
                            UserText.setText(fullname);
                        }
                        if(dataSnapshot.hasChild("Image")){
                            String image = dataSnapshot.child("Image").getValue().toString();
                            Picasso.with(Dashboard.this).load(image).into(circleImageView);
                        }



                   /* if(dataSnapshot.hasChild("Image"))
                    {

                    }

*/
                }
            }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });








    DisplayAllUsersPosts();







    }

    public void DisplayAllUsersPosts() {
     /*   PostsReference=FirebaseDatabase.getInstance().getReference();*/

     mStorage=FirebaseStorage.getInstance();
   PostsReference.addValueEventListener(new ValueEventListener() {
       @Override
       public void onDataChange(DataSnapshot dataSnapshot) {

           PostList.clear();
           for(DataSnapshot PostSnapShot: dataSnapshot.getChildren()){
               Posts posts=PostSnapShot.getValue(Posts.class);
               posts.setkey(PostSnapShot.getKey());
               PostList.add(posts);
           }
           mPostAdapter=new PostAdapter(PostList,Dashboard.this);

           mPostAdapter.setOnItemClickListener(Dashboard.this);
           mPostAdapter.setOnClick(Dashboard.this);
           mPostAdapter.notifyDataSetChanged();
           recyclerView.setAdapter(mPostAdapter);

       }

       @Override
       public void onCancelled(DatabaseError databaseError) {

       }
   });









          }













    private void SendUserToPostActivity() {

        Intent intent=new Intent(Dashboard.this, PostActivity.class);
        startActivity(intent);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    if(actionBarDrawerToggle.onOptionsItemSelected(item)){
        return  true;
    }
     return  super.onOptionsItemSelected(item);

    }








    private void OnMenuItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.add_new_post:
                SendUserToPostActivity();
                break;


            case R.id.nav_home:
               FirebaseMessaging.getInstance().subscribeToTopic("FARMER_CONSULTATION_FORUM");
                Toast.makeText(getApplicationContext(),"Subscribed to Push Notifications", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_profile:
                FirebaseMessaging.getInstance().unsubscribeFromTopic("FARMER_CONSULTATION_FORUM");
                Toast.makeText(Dashboard.this, "UnSubscribed to  Notifications", Toast.LENGTH_SHORT).show();

                break;

            case R.id.nav_log_out:
                Toast.makeText(getApplicationContext(),"Log out", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(Dashboard.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                break;
        }
    }


    @Override
    public void OnItemClick(int position) {
        Posts comments_key=PostList.get(position);
        final  String commented_key=comments_key.getKey();
        Intent intent=new Intent(Dashboard.this, Comments.class);
         intent.putExtra("CommentsPosition",commented_key);
         startActivity(intent);



    }

    @Override
    public void OnDeleteClick(int position) {


        Posts  select=PostList.get(position);
        final String selectedKey=select.getKey();


        StorageReference imageRef=mStorage.getReferenceFromUrl(select.getPostimage());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


                PostsReference.child(selectedKey).removeValue();
                Toast.makeText(Dashboard.this,"Post Deleted",Toast.LENGTH_LONG).show();
            }
        });



    }

    @Override
    public void OnEditClick(int position) {
        Posts get_key=PostList.get(position);
        final String edit_key=get_key.getKey();
        Intent intent=new Intent(Dashboard.this, EditActivity.class);
        intent.putExtra("PostKey", edit_key);
        startActivity(intent);



    }






}
