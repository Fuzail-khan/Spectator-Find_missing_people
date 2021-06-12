package com.fuzailkhan.spectator_findmissingpeople;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class viewMissPersonDetail extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    TextView tvMissPersonName,tvMissPersonGender,tvMissPersonAge,tvMissPersonHeight,tvMissPersonWeight,tvMissPersonEyesColor,
            tvMissPersonSkinTone,tvMissPersonCountry,tvMissPersonMissingSince;
    ImageView imageView;

    String posturi, postname, age, eyescolor, skintone, height,  weight, ccp, postgender, missingsince;
    public viewMissPersonDetail() {
        // Required empty public constructor
    }

    public viewMissPersonDetail(String posturi,String postname,String age,String eyescolor,String skintone,String height, String weight,String ccp,
                                String postgender,String missingsince) {
        this.posturi = posturi;
        this.postname = postname;
        this.age = age;
        this.eyescolor = eyescolor;
        this.skintone = skintone;
        this.height = height;
        this.weight = weight;
        this.ccp = ccp;
        this.postgender = postgender;
        this.missingsince = missingsince;
    }

    public static viewMissPersonDetail newInstance(String param1, String param2) {
        viewMissPersonDetail fragment = new viewMissPersonDetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_miss_person_detail, container, false);

        tvMissPersonName = view.findViewById(R.id.tv_MissingPersonName);
        tvMissPersonGender = view.findViewById(R.id.tv_genderdata);
        tvMissPersonAge = view.findViewById(R.id.tv_agedata);
        tvMissPersonHeight = view.findViewById(R.id.tv_heightdata);
        tvMissPersonWeight = view.findViewById(R.id.tv_weightdata);
        tvMissPersonEyesColor = view.findViewById(R.id.tv_eyesColordata);
        tvMissPersonSkinTone = view.findViewById(R.id.tv_SkinTonedata);
        tvMissPersonCountry = view.findViewById(R.id.tv_nationalitydata);
        tvMissPersonMissingSince = view.findViewById(R.id.tv_missingSincedata);
        imageView = view.findViewById(R.id.imgViewMissPeopleProfilePic);


        tvMissPersonName.setText(postname);
        tvMissPersonGender.setText(postgender);
        tvMissPersonAge.setText(age);
        tvMissPersonHeight.setText(height);
        tvMissPersonWeight.setText(weight);
        tvMissPersonEyesColor.setText(eyescolor);
        tvMissPersonSkinTone.setText(skintone);
        tvMissPersonCountry.setText(ccp);
        tvMissPersonMissingSince.setText(missingsince);
        Picasso.get().load(posturi).into(imageView);


     //Animations for TextViews
        tvMissPersonName.setTranslationX(300);
        tvMissPersonName.animate().translationX(0).alpha(1).setDuration(600).setStartDelay(0).start();

        tvMissPersonGender.setTranslationX(300);
        tvMissPersonGender.animate().translationX(0).alpha(1).setDuration(650).setStartDelay(0).start();

        tvMissPersonAge.setTranslationX(300);
        tvMissPersonAge.animate().translationX(0).alpha(1).setDuration(700).setStartDelay(0).start();

        tvMissPersonHeight.setTranslationX(300);
        tvMissPersonHeight.animate().translationX(0).alpha(1).setDuration(750).setStartDelay(0).start();

        tvMissPersonWeight.setTranslationX(300);
        tvMissPersonWeight.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(0).start();

        tvMissPersonEyesColor.setTranslationX(300);
        tvMissPersonEyesColor.animate().translationX(0).alpha(1).setDuration(850).setStartDelay(0).start();

        tvMissPersonSkinTone.setTranslationX(300);
        tvMissPersonSkinTone.animate().translationX(0).alpha(1).setDuration(900).setStartDelay(0).start();

        tvMissPersonCountry.setTranslationX(300);
        tvMissPersonCountry.animate().translationX(0).alpha(1).setDuration(950).setStartDelay(0).start();

        tvMissPersonMissingSince.setTranslationX(300);
        tvMissPersonMissingSince.animate().translationX(0).alpha(1).setDuration(1000).setStartDelay(0).start();

        return view;
    }
    public void onBackPressed(){
        AppCompatActivity activity = (AppCompatActivity)getContext();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_fragment_container,new Home_Fragment_2())
                .addToBackStack(null)
                .commit();

    }
}