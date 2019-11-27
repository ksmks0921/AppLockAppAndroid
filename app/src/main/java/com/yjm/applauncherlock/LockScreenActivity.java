package com.yjm.applauncherlock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
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

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


import com.yjm.applauncherlock.utilities.Constants;

import java.util.ArrayList;
import java.util.List;


public class LockScreenActivity extends AppCompatActivity  implements passwordDialog.EnterPasswordListener{

    List<AppList> list_app;
    ArrayList<String> applist;
    RecyclerView recyclerView;
//    private ImageView example;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_id);
        Bitmap bitmap = BitmapFactory.decodeFile(Constants.Background_file_path);
        Drawable drawable = new BitmapDrawable(bitmap);
        recyclerView.setBackground(drawable);

        if(Constants.flag_setting == false) {
            Intent intent = new Intent(LockScreenActivity.this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);

        }
        else {

             initUI();
        }





        View decorView = getWindow().getDecorView();

        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

//        ActionBar actionBar = getActionBar();
//        actionBar.hide();


    }
//






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
//            imageView.setImageDrawable(icon);

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

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        ActivityManager activityManager = (ActivityManager) getApplicationContext()
//                .getSystemService(Context.ACTIVITY_SERVICE);
//
//        activityManager.moveTaskToFront(getTaskId(), 0);
//    }

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
