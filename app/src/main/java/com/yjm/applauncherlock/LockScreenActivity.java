package com.yjm.applauncherlock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.yjm.applauncherlock.utilities.Constants;

import java.util.ArrayList;
import java.util.List;


public class LockScreenActivity extends AppCompatActivity  implements passwordDialog.EnterPasswordListener{

    List<AppList> list_app;
    ArrayList<String> applist;
    RecyclerView recyclerView;
    private LinearLayout main_layout;
    private TextView select_launcher, select_launcher_description;
//    private ImageView example;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_id);
        main_layout = findViewById(R.id.main_layout);
        select_launcher = findViewById(R.id.select_launcher);
        select_launcher_description = findViewById(R.id.select_launcher_description);
        Bitmap bitmap = BitmapFactory.decodeFile(Constants.Background_file_path);
        Drawable drawable = new BitmapDrawable(bitmap);
        main_layout.setBackground(drawable);
        select_launcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPreferredLauncherAndOpenChooser(LockScreenActivity.this);

            }
        });


        if(Constants.flag_setting == false) {
            Intent intent = new Intent(LockScreenActivity.this, MainActivity.class);
            finish();
            startActivity(intent);

        }
        else {

            initUI();
            if(!isMyAppLauncherDefault()){
                select_launcher_description.setVisibility(View.VISIBLE);
                select_launcher.setVisibility(View.VISIBLE);
            }
            else {

                select_launcher_description.setVisibility(View.GONE);
                select_launcher.setVisibility(View.GONE);
            }


        }
//        Toast.makeText(getApplicationContext(),"sdfsdf__________"+isMyAppLauncherDefault(), Toast.LENGTH_SHORT).show();





        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

//        ActionBar actionBar = getActionBar();
//        actionBar.hide();


    }
//

    private boolean isMyAppLauncherDefault() {
//        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
//        filter.addCategory(Intent.CATEGORY_HOME);
//
//        List<IntentFilter> filters = new ArrayList<IntentFilter>();
//        filters.add(filter);
//
//        final String myPackageName = getPackageName();
//        List<ComponentName> activities = new ArrayList<ComponentName>();
//        final PackageManager packageManager = (PackageManager) getPackageManager();
//
//        packageManager.getPreferredActivities(filters, activities, null);
//
//        for (ComponentName activity : activities) {
//            if (myPackageName.equals(activity.getPackageName())) {
//                return true;
//            }
//        }
//        return false;

        PackageManager localPackageManager = getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        String str = localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
        return str.equals(getPackageName());
    }

    public static void resetPreferredLauncherAndOpenChooser(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, Fake.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(selector);

        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
    }


    public void initUI(){


            applist = Constants.apps;
//            applist = (ArrayList<String>) getIntent().getSerializableExtra("apps");
            list_app = new ArrayList<>();
            for(int i = 0 ; i < applist.size() ; i ++){
                String package_name = applist.get(i);

                Drawable icon = null;
                try {
                    icon = getPackageManager().getApplicationIcon(package_name);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }


                final PackageManager pm = getApplicationContext().getPackageManager();
                ApplicationInfo ai;
                try {
                    ai = pm.getApplicationInfo( package_name, 0);
                } catch (final PackageManager.NameNotFoundException e) {
                    ai = null;
                }
                final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
                list_app.add(new AppList(applicationName, icon, package_name));


            }


            RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, list_app, new RecyclerViewAdapter.OnItemClickListener() {
                @Override public void onItemClick(AppList item) {



                    Intent intent = getPackageManager()
                            .getLaunchIntentForPackage(item.getPackages());

                    startActivity(intent);


                }
            });
            if (recyclerView != null){
                recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
                recyclerView.setAdapter(recyclerViewAdapter);

                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    Handler handler = new Handler();

                    int numberOfTaps = 0;
                    long lastTapTimeMs = 0;
                    long touchDownMs = 0;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                touchDownMs = System.currentTimeMillis();
                                break;
                            case MotionEvent.ACTION_UP:
                                handler.removeCallbacksAndMessages(null);

                                if ((System.currentTimeMillis() - touchDownMs) > ViewConfiguration.getTapTimeout()) {
                                    //it was not a tap

                                    numberOfTaps = 0;
                                    lastTapTimeMs = 0;
                                    break;
                                }

                                if (numberOfTaps > 0
                                        && (System.currentTimeMillis() - lastTapTimeMs) < ViewConfiguration.getDoubleTapTimeout()) {
                                    numberOfTaps += 1;
                                } else {

                                    numberOfTaps = 1;
                                }

                                lastTapTimeMs = System.currentTimeMillis();

                                if (numberOfTaps == 5) {

                                    openDialog();

                                } else if (numberOfTaps == 2) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            //handle double tap
                                            Toast.makeText(getApplicationContext(), "double", Toast.LENGTH_SHORT).show();
                                        }
                                    }, ViewConfiguration.getDoubleTapTimeout());
                                }
                        }

                        return true;
                    }
                });
            }



    }


    private void openDialog(){

        passwordDialog password_Dialog = new passwordDialog();
        password_Dialog.show(getSupportFragmentManager(),"Password");

    }

    @Override
    protected void onRestart() {
        super.onRestart();


        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Do nothing or catch the keys you want to block
        return false;
    }

    @Override
    public void applyTexts(String password) {
        String saved_password = "password";

        if (password.trim().equals(saved_password)){
            Intent intent = new Intent(LockScreenActivity.this, MainActivity.class);
            startActivity(intent);


        }
    }

}
