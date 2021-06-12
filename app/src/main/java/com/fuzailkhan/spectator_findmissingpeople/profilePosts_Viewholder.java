package com.fuzailkhan.spectator_findmissingpeople;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class profilePosts_Viewholder extends RecyclerView.ViewHolder {

    ImageView imageView;

    public profilePosts_Viewholder(@NonNull View itemView) {
        super(itemView);
    }
    public void SetImage(FragmentActivity activity,String name, String url, String postUri, String time,
                         String uid, String type){

        imageView = itemView.findViewById(R.id.iv_myPosts_single);
        Picasso.get().load(postUri).into(imageView);
    }
}
