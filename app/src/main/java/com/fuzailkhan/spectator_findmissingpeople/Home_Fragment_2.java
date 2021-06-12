package com.fuzailkhan.spectator_findmissingpeople;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.logging.Handler;

public class Home_Fragment_2 extends Fragment implements View.OnClickListener{

    ImageView imageViewprofile;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout shimmerLayout;
    ImageButton button;
    FirebaseAuth auth;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference,viewref;
    Boolean viewchecker = false;
    DatabaseReference db1,db2;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment2,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageViewprofile = getActivity().findViewById(R.id.iv_profile_item);

        shimmerFrameLayout = getActivity().findViewById(R.id.shimmer_view_container);
        shimmerLayout = getActivity().findViewById(R.id.shimmerLayout);
        button = getActivity().findViewById(R.id.createpostBtn);
        reference = database.getReference("All posts");
        viewref = database.getReference("post views");
        recyclerView = getActivity().findViewById(R.id.rv_posts);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        auth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        db1 = database.getReference("All images").child(currentuid);
        db2 = database.getReference("All posts");



        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.createpostBtn:
                Intent intent = new Intent(getActivity(),PostActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Postmember> options =
                new FirebaseRecyclerOptions.Builder<Postmember>()
                .setQuery(reference,Postmember.class)
                .build();

        FirebaseRecyclerAdapter<Postmember,PostViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Postmember,PostViewHolder>(options){

                    @Override
                    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Postmember model) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String currentUserid = user.getUid();

                        final String postkey = getRef(position).getKey();
                        holder.SetPost(getActivity(),model.getName(),model.getUrl(),model.getPosturi(),model.getTime(),model.getUid(),
                                model.getType(),model.getPostname(),model.getAge(),model.getEyescolor(),model.getSkintone(),model.getHeight(),
                                model.getWeight(),model.getCcp(),model.getRbPostGender(),model.getMissingSince());

                        final  String name = getItem(position).getName();
                        final String posturl = getItem(position).getPosturi();
                        final String time = getItem(position).getTime();
                        final String userid = getItem(position).getUid();

                        holder.viewChecker(postkey);


                        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(),CommentActivity.class);
                                intent.putExtra("PostKey",postkey);
                                startActivity(intent);
                            }
                        });

                        holder.sharebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String shareText = name + "/n" + "/n" + posturl;
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra(Intent.EXTRA_TEXT,shareText);
                                intent.setType("text/plain");
                                startActivity(intent.createChooser(intent,"Share via"));
                            }
                        });

                        holder.menuoptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog(name,posturl,time,userid);
                            }
                        });

                        holder.viewBtn.setOnClickListener((view) ->{
                            viewchecker = true;

                            viewref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (viewchecker.equals(true)){
                                        viewref.child(postkey).child(currentUserid).setValue(true);
                                        viewchecker = false;

                                        AppCompatActivity activity = (AppCompatActivity)view.getContext();
                                        activity.getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.frame_fragment_container,new viewMissPersonDetail(model.getPosturi(),model.getPostname(),model.getAge(),
                                                        model.getEyescolor(),model.getSkintone(),model.getHeight(),model.getWeight(),model.getCcp(),
                                                        model.getRbPostGender(),model.getMissingSince()))
                                                .addToBackStack(null)
                                                .commit();

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        });
                    }
                    @NonNull
                    @Override
                    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_layout,parent,false);
                        shimmerFrameLayout.stopShimmer();
                        shimmerLayout.setVisibility(View.GONE);
                        return new PostViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.notifyDataSetChanged();


    }

    @Override
    public void onPause() {
        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }

    @Override
    public void onResume() {
        shimmerFrameLayout.startShimmer();
        super.onResume();

    }

    void showDialog(String name, String posturl, String time, String userid){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.post_options,null);
        TextView download = view.findViewById(R.id.tv_download_post);
        TextView delete = view.findViewById(R.id.tv_delete_post);
        TextView copyurl = view.findViewById(R.id.tv_copyUrl_post);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        alertDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserid = user.getUid();

        if (userid.equals(currentUserid)){
            delete.setVisibility(View.VISIBLE);
        }else {
            delete.setVisibility(View.INVISIBLE);
        }
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Query query = db1.orderByChild("time").equalTo(time);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            dataSnapshot.getRef().removeValue();

                            Toast.makeText(getActivity(),"Deleted",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                Query query2 = db2.orderByChild("time").equalTo(time);
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            dataSnapshot.getRef().removeValue();

                            Toast.makeText(getActivity(),"Deleted",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(posturl);
                reference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });
                alertDialog.dismiss();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(posturl));
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                DownloadManager.Request.NETWORK_MOBILE);
                        request.setTitle("Download");
                        request.setDescription("Downloading image...");
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,name+System.currentTimeMillis() + ".jpg");
                        DownloadManager manager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                        manager.enqueue(request);
                        Toast.makeText(getActivity(), "Downloading",Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        alertDialog.dismiss();
                        Toast.makeText(getActivity(),"No Permission",Toast.LENGTH_SHORT).show();
                    }
                };
                TedPermission.with(getActivity())
                        .setPermissionListener(permissionListener)
                        .setPermissions(Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
            }
        });

        copyurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cp = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("String",posturl);
                cp.setPrimaryClip(clip);
                clip.getDescription();
                Toast.makeText(getActivity(),"Copied to Clipboard",Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }
}
