package kr.co.kkensu.integrationmap.navermap;

import com.naver.maps.map.overlay.PolygonOverlay;

import java.util.List;

import kr.co.kkensu.integrationmap.MapPoint;
import kr.co.kkensu.integrationmap.MapPolygon;

public class MapPolygonImpl implements MapPolygon {
    private PolygonOverlay polygon;

    public MapPolygonImpl(PolygonOverlay polygon) {
        this.polygon = polygon;
    }


    @Override
    public List<MapPoint> getPoints() {
        return MapApiImpl.convertToMapPoint(polygon.getCoords());
    }

    @Override
    public void setPoints(List<MapPoint> points) {
        polygon.setCoords(MapApiImpl.convertToLatLng(points));
    }

    @Override
    public int getStrokeColor() {
        return polygon.getOutlineColor();
    }

    @Override
    public void setStrokeColor(int color) {
        polygon.setOutlineColor(color);
    }

    @Override
    public int getFillColor() {
        return polygon.getColor();
    }

    @Override
    public void setFillColor(int color) {
        polygon.setColor(color);
    }

    @Override
    public void remove() {
        polygon.setMap(null);
    }
}
