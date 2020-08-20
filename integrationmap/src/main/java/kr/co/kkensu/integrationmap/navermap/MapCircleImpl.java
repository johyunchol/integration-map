package kr.co.kkensu.integrationmap.navermap;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.CircleOverlay;

import kr.co.kkensu.integrationmap.MapCircle;
import kr.co.kkensu.integrationmap.MapPoint;

public class MapCircleImpl implements MapCircle {
    private CircleOverlay circle;

    public MapCircleImpl(CircleOverlay circle) {
        this.circle = circle;
    }

    @Override
    public void remove() {
        circle.setMap(null);
    }

    @Override
    public void setPoint(MapPoint point) {
        circle.setCenter(new LatLng(point.getLatitude(), point.getLongitude()));
    }

    @Override
    public MapPoint getCenter() {
        return new MapPoint(circle.getCenter().latitude, circle.getCenter().longitude);
    }
}