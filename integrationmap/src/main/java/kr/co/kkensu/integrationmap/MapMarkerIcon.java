package kr.co.kkensu.integrationmap;

import android.view.View;

/**
 * Created by johyunchol on 2017. 11. 6..
 */

public class MapMarkerIcon {
    View markerView;

    public MapMarkerIcon(View markerView) {
        this.markerView = markerView;
    }

    public View getMarkerView() {
        return markerView;
    }

    public void setMarkerView(View markerView) {
        this.markerView = markerView;
    }
}
