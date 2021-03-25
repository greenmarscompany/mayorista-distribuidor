package com.greenmars.distribuidor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.greenmars.distribuidor.util.UserPagerAdapter;

public class CreateUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        UserPagerAdapter userPagerAdapter = new UserPagerAdapter(getSupportFragmentManager());
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager_user);
        viewPager.setAdapter(userPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayour_user);
        tabLayout.setupWithViewPager(viewPager);
    }
}
