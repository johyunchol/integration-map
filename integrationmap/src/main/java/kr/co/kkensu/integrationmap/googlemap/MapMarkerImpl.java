package kr.co.kkensu.integrationmap.googlemap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import kr.co.kkensu.integrationmap.MapMarker;
import kr.co.kkensu.integrationmap.MapPoint;

public class MapMarkerImpl implements MapMarker {
    private Marker marker;

    public MapMarkerImpl(Marker marker) {
        this.marker = marker;
    }

    @Override
    public void remove() {
        marker.remove();
    }

    @Override
    public void setPoint(MapPoint point) {
        marker.setPosition(new LatLng(point.getLatitude(), point.getLongitude()));
    }

    @Override
    public void setAngle(float rotation) {
        marker.setRotation(rotation);
    }
}
