package kr.co.kkensu.integrationmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.kkensu.maptest.R;

import static kr.co.kkensu.integrationmap.MapInfoWindow.getInfoWindowGotoNavigation;

public class MarkerIconName {
    public static String createFinish() {
        return "pin_finish";
    }

    public static MapMarkerIcon createMe() {
        return new MapMarkerIcon(MapMarkerIcon.MapType.TYPE_RESOURCE, "my_position", new MapMarkerIcon.MarkerSize(42, 42));
    }

    public static MapMarkerIcon createDeparture(Context context) {
        View markerView = LayoutInflater.from(context).inflate(R.layout.layout_location_marker_departure, null);
        return new MapMarkerIcon(MapMarkerIcon.MapType.TYPE_VIEW, markerView);
    }

    public static MapMarkerIcon createDestination(Context context) {
        return createDestination(context, null);
    }

    public static MapMarkerIcon createDestination(Context context, Runnable runnable) {
        View markerView = LayoutInflater.from(context).inflate(R.layout.layout_location_marker, null);
        TextView txtName = markerView.findViewById(R.id.txtName);
        txtName.setText("목적지");

        LinearLayout imgMarker = markerView.findViewById(R.id.imgMarker);
//        imgMarker.setBackgroundResource(R.drawable.ic_pin_center);
//        imgMarker.setBackgroundTintList(context.getResources().getColorStateList(R.color.appColorMain));

        if (runnable != null) {
            ViewGroup containerInfoWindow = markerView.findViewById(R.id.containerInfoWindow);
            containerInfoWindow.addView(getInfoWindowGotoNavigation(context));
        }

        return new MapMarkerIcon(MapMarkerIcon.MapType.TYPE_VIEW, markerView);
    }
}
