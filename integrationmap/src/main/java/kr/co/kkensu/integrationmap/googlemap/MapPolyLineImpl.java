package kr.co.kkensu.integrationmap.googlemap;


import com.google.android.gms.maps.model.Polyline;

import java.util.List;

import kr.co.kkensu.integrationmap.MapPoint;
import kr.co.kkensu.integrationmap.MapPolyLine;

public class MapPolyLineImpl implements MapPolyLine {
    private final Polyline polyLine;

    public MapPolyLineImpl(Polyline polyLine) {
        this.polyLine = polyLine;
    }

    @Override
    public List<MapPoint> getPoints() {
        return MapApiImpl.convertToMapPoint(polyLine.getPoints());
    }

    @Override
    public void setPoints(List<MapPoint> points) {
        polyLine.setPoints(MapApiImpl.convertToLatLng(points));
    }

    @Override
    public void remove() {
        polyLine.remove();
    }
}
