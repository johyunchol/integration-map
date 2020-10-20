package kr.co.kkensu.integrationmap.tmap;


import com.google.android.gms.maps.model.Polyline;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.util.List;

import kr.co.kkensu.integrationmap.MapPoint;
import kr.co.kkensu.integrationmap.MapPolyLine;

public class MapPolyLineImpl implements MapPolyLine {
    private TMapView tMapView;
    private TMapPolyLine polyLine;

    public MapPolyLineImpl(TMapView tMapView, TMapPolyLine polyLine) {
        this.tMapView = tMapView;
        this.polyLine = polyLine;
    }

    @Override
    public List<MapPoint> getPoints() {
        return MapApiImpl.convertToMapPoint(polyLine.getPassPoint());
    }

    @Override
    public void setPoints(List<MapPoint> points) {
        for (MapPoint point : points) {
            polyLine.addLinePoint(new TMapPoint(point.getLatitude(), point.getLongitude()));
        }
    }

    @Override
    public void remove() {
        tMapView.removeTMapPolyLine(polyLine.getID());
    }
}
