package kr.co.kkensu.integrationmap.googlemap;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;

import kr.co.kkensu.integrationmap.MapCircle;
import kr.co.kkensu.integrationmap.MapPoint;


public class MapCircleImpl implements MapCircle {
    private Circle circle;

    public MapCircleImpl(Circle circle) {
        this.circle = circle;
    }

    @Override
    public void remove() {
        circle.remove();
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