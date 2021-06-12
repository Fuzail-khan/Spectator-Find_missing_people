package com.fuzailkhan.spectator_findmissingpeople;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {

    private ImageButton postCommentBtn;
    private EditText CommentInputText;
    private RecyclerView CommentLists;

    private DatabaseReference userRef,postRef;

    private String post_key,current_userId;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        post_key = getIntent().getExtras().get("PostKey").toString();

        mAuth = FirebaseAuth.getInstance();
        current_userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("All Users");
        postRef = FirebaseDatabase.getInstance().getReference("All posts").child(post_key).child("Comments");

        CommentLists = (RecyclerView) findViewById(R.id.rv_comments);
        CommentLists.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentLists.setLayoutManager(linearLayoutManager);

        CommentInputText = (EditText) findViewById(R.id.et_comment);
        postCommentBtn = (ImageButton) findViewById(R.id.ib_postComment);

        postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.child(current_userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String userName = snapshot.child("name").getValue().toString();

                            ValidateComment(userName);

                            CommentInputText.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Comments_ModelClass> options =
                new FirebaseRecyclerOptions.Builder<Comments_ModelClass>()
                        .setQuery(postRef,Comments_ModelClass.class)
                        .build();

        FirebaseRecyclerAdapter<Comments_ModelClass, CommentsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments_ModelClass, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull CommentsViewHolder holder, int position, @NonNull @NotNull Comments_ModelClass model) {
                holder.setUsername(model.getUsername());
                holder.setComment(model.getComment());
                holder.setDate(model.getDate());
                holder.setTime(model.getTime());
            }

            @NonNull
            @NotNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_comments_layout,parent,false);
                return new CommentsViewHolder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        CommentLists.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public CommentsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setUsername(String username){
            TextView myUserName = mView.findViewById(R.id.tv_commentUsername);
            myUserName.setText("" + username+"  ");
        }
        public void setComment(String comment){
            TextView myComment = mView.findViewById(R.id.tv_comment_text);
            myComment.setText(comment);
        }
        public void setDate(String date){
            TextView myDate = mView.findViewById(R.id.tv_comment_date);
            myDate.setText("  Date:" + date);
        }
        public void setTime(String time){
            TextView myTime = mView.findViewById(R.id.tv_comment_time);
            myTime.setText("  Time:" + time);
        }
    }

    private void ValidateComment(String userName) {
        String commentText = CommentInputText.getText().toString();
        if (TextUtils.isEmpty(commentText)){
            Toast.makeText(this,"Please write a comment",Toast.LENGTH_SHORT).show();
        }
        else {
            Calendar cdate  = Calendar.getInstance();
            SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String savedate = currentdate.format(cdate.getTime());

            Calendar ctime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String savetime = currentTime.format(ctime.getTime());

            final String RandomKey = current_userId + savedate + savetime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid",current_userId);
            commentsMap.put("comment",commentText);
            commentsMap.put("date",savedate);
            commentsMap.put("time",savetime);
            commentsMap.put("username",userName);

            postRef.child(RandomKey).updateChildren(commentsMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(CommentActivity.this,"Comment Added",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(CommentActivity.this,"Error Occured, Try again",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
