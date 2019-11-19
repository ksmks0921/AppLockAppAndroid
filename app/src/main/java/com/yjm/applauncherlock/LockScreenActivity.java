package com.yjm.applauncherlock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class LockScreenActivity extends AppCompatActivity {

    List<AppList> list_app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        ArrayList<String> applist = (ArrayList<String>) getIntent().getSerializableExtra("apps");
        list_app = new ArrayList<>();
        for(int i = 0 ; i < applist.size() ; i ++){
            String package_name = applist.get(i);
            Log.d("DD","length:" + package_name);
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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview_id);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, list_app, new RecyclerViewAdapter.OnItemClickListener() {
            @Override public void onItemClick(AppList item) {

                Intent intent = getPackageManager()
                        .getLaunchIntentForPackage(item.getPackages());
                startActivity(intent);

            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(recyclerViewAdapter);



    }
}
