package com.fuzailkhan.spectator_findmissingpeople;

import android.content.Intent;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.logging.Handler;


public class PostViewHolder extends RecyclerView.ViewHolder {


    ImageView imageViewprofile,iv_post;
    TextView tv_name,tv_time,tv_nameprofile,tv_viewpost;
    Button sharebtn,viewBtn;
    ImageButton menuoptions,commentBtn;
    DatabaseReference viewref;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    int viewscount;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public void SetPost(FragmentActivity activity,String name, String url,String posturi,String time,String uid,String type,
                        String postname,String age,String eyescolor,String skintone,String height,String weight,String ccp,
                        String rbPostGender,String missingSince){

        imageViewprofile = itemView.findViewById(R.id.iv_profile_item);
        iv_post = itemView.findViewById(R.id.iv_post_item);
        tv_nameprofile = itemView.findViewById(R.id.tv_name_post);
        tv_viewpost = itemView.findViewById(R.id.tv_views_post);
        tv_time = itemView.findViewById(R.id.tv_time_post);
        sharebtn = itemView.findViewById(R.id.shareButton_post);
        menuoptions = itemView.findViewById(R.id.moreOptionButton_post);
        viewBtn = itemView.findViewById(R.id.viewButton_post);
        commentBtn = itemView.findViewById(R.id.commentButton_post);





        if (type.equals("iv")){
            Picasso.get().load(url).into(imageViewprofile);
            Picasso.get().load(posturi).into(iv_post);
            tv_time.setText(time);
            tv_nameprofile.setText(name);
        }

    }
    public void viewChecker(final String postkey){
        viewBtn = itemView.findViewById(R.id.viewButton_post);

        viewref = database.getReference("post views");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        viewref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                viewscount = (int)snapshot.child(postkey).getChildrenCount();
                tv_viewpost.setText(Integer.toString(viewscount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

