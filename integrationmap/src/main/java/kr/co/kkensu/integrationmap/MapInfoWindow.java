package kr.co.kkensu.integrationmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.co.kkensu.integrationmap.util.LocationUtil;
import kr.co.kkensu.integrationmap.util.TimeUtil;
import kr.co.kkensu.maptest.R;

/**
 * Created by johyunchol on 2017. 11. 6..
 */

public class MapInfoWindow {
    private Context context;
    private View view;
    private String name;
    private String address;
    private Runnable runnable;

    public MapInfoWindow(Context context, String name, String address, Runnable runnable) {
        this.context = context;
        this.name = name;
        this.address = address;
        this.runnable = runnable;
    }

    public MapInfoWindow(Context context, View view, String name, String address, Runnable runnable) {
        this.context = context;
        this.view = view;
        this.name = name;
        this.address = address;
        this.runnable = runnable;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public static View getInfoWindowGotoNavigation(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_map_info_window_go_to_navigation, null);
        return view;
    }

    public static View getInfowWindowDistanceTime(Context context, Integer distance, Integer time) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_map_info_window_distance_time, null);

        TextView distanceText = view.findViewById(R.id.distanceText);
        TextView timeText = view.findViewById(R.id.timeText);

        if (distance >= 1000) {
            distanceText.setText(String.format("약 %.1fkm", LocationUtil.distanceKM(distance)));
        } else {
            distanceText.setText(String.format("약 %dm", distance));
        }

        timeText.setText(String.format("%s", TimeUtil.formatTime(time)));

        return view;
    }

    public static View getInfowWindowTime(Context context, String time) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_map_info_window_time, null);

        TextView timeText = view.findViewById(R.id.timeText);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");

        Date date = null;
        try {
            date = sdf.parse(time);
            time = sdf2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        timeText.setText(String.format("%s", time));

        return view;
    }
}
