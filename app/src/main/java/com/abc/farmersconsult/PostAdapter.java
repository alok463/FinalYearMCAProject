package com.abc.farmersconsult;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private List<Posts> mPostList;
    private Context mContext;
    private OnItemClickListener mListener;
    private OnItemClicked onClick;
    private String current_user;
    private FirebaseAuth mAuth;
    DatabaseReference mUserRef;


    public PostAdapter(List<Posts> mPost_List, Context context) {
        mPostList = mPost_List;
        mContext = context;
    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.post_all_layout, parent, false);


        return new PostHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser().getUid();
        Posts currItem = mPostList.get(position);
        holder.Date.setText(currItem.getDate());
        holder.Has_Username.setText(currItem.getName());
        holder.Description.setText(currItem.getDescription());
        holder.Places_User.setText(currItem.getPlaces());
        holder.Post_Occupation.setText(currItem.getOccupation());
        holder.Time.setText(currItem.getTime());
        Picasso.with(mContext).load(currItem.getImage()).into(holder.profile_image);
        Picasso.with(mContext).load(currItem.getPostimage()).into(holder.post_image);
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        private CircleImageView profile_image;
        private ImageView post_image;
        private TextView Description, Places_User, Post_Occupation;
        private TextView Date, Time, Has_Updated_Post, Has_Username, Like_Count;
        private ImageButton Likes_Button, Comment_Button;


        public PostHolder(View itemView) {
            super(itemView);

            Date = itemView.findViewById(R.id.post_date);
            Time = itemView.findViewById(R.id.post_time);
            /*Has_Updated_Post=itemView.findViewById(R.id.text);*/
            Has_Username = itemView.findViewById(R.id.post_user_name);
            Places_User = itemView.findViewById(R.id.post_place);
            Description = itemView.findViewById(R.id.post_description);
            profile_image = itemView.findViewById(R.id.post_profile_image);
            post_image = itemView.findViewById(R.id.post_image);
            Post_Occupation = itemView.findViewById(R.id.post_occupation);
            Comment_Button = itemView.findViewById(R.id.comments);


            Comment_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onClick.OnItemClick(getAdapterPosition());

                }
            });


            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }


        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.OnItemClick(position);


                }
            }
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {



            //Firebase Database


            menu.setHeaderTitle("Select Action");
            MenuItem Edit = menu.add(Menu.NONE, 1, 1, "Edit Posts");
            MenuItem Delete = menu.add(Menu.NONE, 2, 2, "Delete Posts");
            Edit.setOnMenuItemClickListener(this);
            Delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
           /* mAuth = FirebaseAuth.getInstance();
            current_user = mAuth.getCurrentUser().getUid();*/

            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            mListener.OnEditClick(position);
                            return true;

                        case 2:
                            mListener.OnDeleteClick(position);
                            return true;


                    }


                }
            }
            return false;



    }
}



   public interface OnItemClickListener{
        public void OnItemClick(int position);
        public void OnDeleteClick(int position);
        public void OnEditClick(int position);



    }

   public void setOnItemClickListener(OnItemClickListener listener){

        mListener=listener;
   }




  public interface OnItemClicked{
        void OnItemClick(int position);
    }



    public void setOnClick(OnItemClicked onClick)
    {
        this.onClick=onClick;
    }

}
