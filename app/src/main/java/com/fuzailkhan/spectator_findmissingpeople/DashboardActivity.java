package com.fuzailkhan.spectator_findmissingpeople;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = DashboardActivity.class.getSimpleName();
    ChipNavigationBar chipNavigationBar;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        chipNavigationBar = findViewById(R.id.bottom_nav);
        bottomMenu();

        if (savedInstanceState==null){
            chipNavigationBar.setItemSelected(R.id.home_bottom,true);
            fragmentManager = getSupportFragmentManager();
            Home_Fragment_2 homeFragment = new Home_Fragment_2();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_fragment_container,homeFragment)
                    .commit();
        }

    }

    private void bottomMenu() {

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.profile_bottom:
                        fragment = new Profile_Fragment_1();
                        break;
                    case R.id.home_bottom:
                        fragment = new Home_Fragment_2();
                        break;
                    case R.id.about_bottom:
                        fragment = new AboutUs_Fragment_3();
                        break;
            }
            if (fragment!=null) {
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_fragment_container,fragment)
                        .commit();
             }else {
                Log.e(TAG,"Error in creating fragment");
             }
            }
        });

    }
}