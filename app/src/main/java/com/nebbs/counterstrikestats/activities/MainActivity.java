package com.nebbs.counterstrikestats.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.nebbs.counterstrikestats.R;
import com.nebbs.counterstrikestats.fragments.SectionsPageAdapter;
import com.nebbs.counterstrikestats.fragments.BasicStatsFragment;
import com.nebbs.counterstrikestats.fragments.Tab2Fragment;
import com.nebbs.counterstrikestats.fragments.Tab3Fragment;
import com.nebbs.counterstrikestats.objects.User;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private User user;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        user = (User)i.getSerializableExtra("user");


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Counter Strike Stats");
        setSupportActionBar(toolbar);
        Log.d(TAG, "onCreate: Starting.");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //Initialise authentication
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Context context = getApplicationContext();
        Intent i = new Intent();
        switch (item.getItemId()) {

            case R.id.action_profile:
                System.out.println("ProfileActivity");
                i.setClass(context, ProfileActivity.class);
                i.putExtra("user", user);
                startActivity(i);
                return true;
            case R.id.action_settings:
                System.out.println("Settings");
                return true;
            case R.id.action_logout:
                System.out.println("Logout");
                auth.signOut();
                i.setClass(context, LoginActivity.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new BasicStatsFragment(), "Basic Stats");
        adapter.addFragment(new Tab2Fragment(), "TAB2");
        adapter.addFragment(new Tab3Fragment(), "TAB3");
        viewPager.setAdapter(adapter);
    }


}
