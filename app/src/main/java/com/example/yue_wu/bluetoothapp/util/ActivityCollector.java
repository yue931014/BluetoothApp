package com.example.yue_wu.bluetoothapp.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yue-wu on 2017/3/13.
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishAll(){
        for(Activity activity:activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
