package kr.co.kkensu.integrationmap.tmap;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import kr.co.kkensu.integrationmap.MapCircle;
import kr.co.kkensu.integrationmap.MapPoint;


public class MapCircleImpl implements MapCircle {
    private TMapView tMapView;
    private TMapCircle circle;

    public MapCircleImpl(TMapView tMapView, TMapCircle circle) {
        this.tMapView = tMapView;
        this.circle = circle;
    }

    @Override
    public void setPoint(MapPoint point) {
        circle.setCenterPoint(new TMapPoint(point.getLatitude(), point.getLongitude()));
    }

    @Override
    public MapPoint getCenter() {
        return new MapPoint(circle.getCenterPoint().getLatitude(), circle.getCenterPoint().getLongitude());
    }

    @Override
    public void remove() {
        tMapView.removeTMapCircle(circle.getID());
    }
}