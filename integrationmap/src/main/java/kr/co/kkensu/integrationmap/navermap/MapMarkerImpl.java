package kr.co.kkensu.integrationmap.navermap;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.Marker;

import kr.co.kkensu.integrationmap.MapMarker;
import kr.co.kkensu.integrationmap.MapPoint;

public class MapMarkerImpl implements MapMarker {
    private Marker marker;

    public MapMarkerImpl(Marker marker) {
        this.marker = marker;
    }

    @Override
    public void remove() {
        marker.setMap(null);
    }

    @Override
    public void setPoint(MapPoint point) {
        marker.setPosition(new LatLng(point.getLatitude(), point.getLongitude()));
    }

    @Override
    public void setAngle(float rotation) {
        marker.setAngle(rotation);
    }
}
