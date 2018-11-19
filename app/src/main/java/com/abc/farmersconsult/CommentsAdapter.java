package com.abc.farmersconsult;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentHolder> {
    private List<UserComments> mCommentList;
    private Context mContext;

    public CommentsAdapter(List<UserComments> CommentList,Context Context){
        mCommentList=CommentList;
        mContext=Context;
    }


    @NonNull
    @Override
    public CommentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.all_comments_layout,parent,false);
        return new CommentHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentHolder holder, int position) {
     UserComments currItem=mCommentList.get(position);
     holder.CommentUserName.setText(currItem.getName());
     holder.CommentTime.setText(currItem.getTime());
     holder.CommentsDate.setText(currItem.getDate());
     holder.TextComment.setText(currItem.getComment());


    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {

        private TextView TextComment;
        private TextView CommentsDate;
        private TextView CommentUserName;
        private  TextView CommentTime;

        public CommentHolder(View itemView) {
            super(itemView);
            TextComment=itemView.findViewById(R.id.comment_text);
            CommentsDate=itemView.findViewById(R.id.comment_date);
            CommentTime=itemView.findViewById(R.id.comment_time);
            CommentUserName=itemView.findViewById(R.id.comment_user_name);


        }
    }
}
