package kr.co.kkensu.integrationmap.tmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import kr.co.kkensu.integrationmap.MapMarker;
import kr.co.kkensu.integrationmap.MapPoint;

public class MapMarkerImpl implements MapMarker {
    private TMapView tMapView;
    private TMapMarkerItem2 marker;

    public MapMarkerImpl(TMapView tMapView, TMapMarkerItem2 marker) {
        this.tMapView = tMapView;
        this.marker = marker;
    }

    @Override
    public void setPoint(MapPoint point) {
        marker.setTMapPoint(new TMapPoint(point.getLatitude(), point.getLongitude()));
    }

    @Override
    public void setAngle(float rotation) {
//        marker.setRotation(rotation);
    }

    @Override
    public void remove() {
        tMapView.removeMarkerItem2(marker.getID());
    }
}
