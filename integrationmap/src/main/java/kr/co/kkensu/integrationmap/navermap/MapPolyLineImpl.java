package kr.co.kkensu.integrationmap.navermap;


import com.naver.maps.map.overlay.PolylineOverlay;

import java.util.List;

import kr.co.kkensu.integrationmap.MapPoint;
import kr.co.kkensu.integrationmap.MapPolyLine;

public class MapPolyLineImpl implements MapPolyLine {
    private final PolylineOverlay polyLine;

    public MapPolyLineImpl(PolylineOverlay polyLine) {
        this.polyLine = polyLine;
    }

    @Override
    public List<MapPoint> getPoints() {
        return MapApiImpl.convertToMapPoint(polyLine.getCoords());
    }

    @Override
    public void setPoints(List<MapPoint> points) {
        polyLine.setCoords(MapApiImpl.convertToLatLng(points));
    }

    @Override
    public void remove() {
        polyLine.setMap(null);
    }
}
