package com.yjm.applauncherlock;

import android.graphics.drawable.Drawable;

public class AppList {

    private String name;
    Drawable icon;
    private String packages;

    public AppList(String name, Drawable icon, String packages) {
        this.name = name;
        this.icon = icon;
        this.packages = packages;
    }
    public String getName() {
        return name;
    }
    public Drawable getIcon() {
        return icon;
    }
    public String getPackages() {
        return packages;
    }

}
