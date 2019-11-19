package com.yjm.applauncherlock;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListAppActivity extends AppCompatActivity {

    private ListView userInstalledApps;
    private List<AppList> installedApps;
    private AppAdapter installedAppAdapter;
    private LinearLayout add_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_app);

        userInstalledApps = (ListView) findViewById(R.id.app_list);
        installedApps = getInstalledApps();
        installedAppAdapter = new AppAdapter(this, installedApps);
        userInstalledApps.setAdapter(installedAppAdapter);


        add_btn = (LinearLayout) findViewById(R.id.add_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringBuilder result = new StringBuilder();



                 ArrayList<String> mylist = new ArrayList<>();
//                ArrayList<AppList_for_send> send_data = new ArrayList<>();


                for(int i=0 ; i < installedApps.size(); i++)

                {


                    if(installedAppAdapter.mCheckStates.get(i) == true)
                    {



                        mylist.add(installedApps.get(i).getPackages());

                    }

                }
//                Log.d("DD","length:" + installedApps.size());
                Intent intent = new Intent(ListAppActivity.this, LockScreenActivity.class);
                intent.putExtra("apps", mylist);

                startActivity(intent);


            }
        });


    }


    private Object[] appendValue(Object[] obj, Object newObj) {

        ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(obj));
        temp.add(newObj);
        return temp.toArray();

    }


    public class AppAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener{

        public LayoutInflater layoutInflater;
        public List<AppList> listStorage;
        SparseBooleanArray mCheckStates;

        public AppAdapter(Context context, List<AppList> customizedListView) {
            layoutInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listStorage = customizedListView;
            mCheckStates = new SparseBooleanArray(listStorage.size());
        }

        @Override
        public int getCount() {
            return listStorage.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder listViewHolder;
            if(convertView == null){
                listViewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.list_cell, parent, false);

                listViewHolder.textInListView = (TextView)convertView.findViewById(R.id.app_name);
                listViewHolder.imageInListView = (ImageView)convertView.findViewById(R.id.app_icon);
                listViewHolder.chkSelect = (CheckBox) convertView.findViewById(R.id.checkbox);

                convertView.setTag(listViewHolder);

            }else{
                listViewHolder = (ViewHolder)convertView.getTag();
            }
            listViewHolder.textInListView.setText(listStorage.get(position).getName());
            listViewHolder.imageInListView.setImageDrawable(listStorage.get(position).getIcon());

            listViewHolder.chkSelect.setTag(position);
            listViewHolder.chkSelect.setChecked(mCheckStates.get(position, false));
            listViewHolder.chkSelect.setOnCheckedChangeListener(this);

            return convertView;
        }

        class ViewHolder{
            TextView textInListView;
            ImageView imageInListView;
            CheckBox chkSelect;

        }
        public boolean isChecked(int position) {
            return mCheckStates.get(position, false);
        }

        public void setChecked(int position, boolean isChecked) {
            mCheckStates.put(position, isChecked);

        }

        public void toggle(int position) {
            setChecked(position, !isChecked(position));

        }


        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

            mCheckStates.put((Integer) compoundButton.getTag(), isChecked);

        }

    }







    private List<AppList> getInstalledApps() {

        PackageManager pm = getPackageManager();
        List<AppList> apps = new ArrayList<AppList>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        //List<PackageInfo> packs = getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!isSystemPackage(p))) {
                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                String packages = p.applicationInfo.packageName;
                apps.add(new AppList(appName, icon, packages));
            }
        }

        return apps;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }


}
