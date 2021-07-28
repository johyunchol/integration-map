package kr.co.kkensu.integrationmap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import kr.co.kkensu.integrationmap.R;

public class MapMarkerFactory {

    public static MapMarkerIcon createMarker(Context context, View view) {
        return createMarker(context, view, null);
    }

    public static MapMarkerIcon createMarker(Context context, View view, View.OnClickListener onClickListener) {
        if (onClickListener != null) view.setOnClickListener(onClickListener);
        return new MapMarkerIcon(view);
    }

    public static MapMarkerIcon createMarker(Context context, int drawableId) {
        return createMarker(context, drawableId, null);
    }

    public static MapMarkerIcon createMarker(Context context, int drawableId, View.OnClickListener onClickListener) {
        View markerView = LayoutInflater.from(context).inflate(R.layout.view_custom_marker, null);
        ImageView imgMarker = markerView.findViewById(R.id.imgMarker);
        imgMarker.setImageResource(drawableId);

        if (onClickListener != null) markerView.setOnClickListener(onClickListener);

        return new MapMarkerIcon(markerView);
    }
}
