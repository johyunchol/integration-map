package kr.co.kkensu.integrationmap.googlemap;

import com.google.android.gms.maps.model.Polygon;

import java.util.List;

import kr.co.kkensu.integrationmap.MapPoint;
import kr.co.kkensu.integrationmap.MapPolygon;

public class MapPolygonImpl implements MapPolygon {
    private Polygon polygon;

    public MapPolygonImpl(Polygon polygon) {
        this.polygon = polygon;
    }


    @Override
    public List<MapPoint> getPoints() {
        return MapApiImpl.convertToMapPoint(polygon.getPoints());
    }

    @Override
    public void setPoints(List<MapPoint> points) {
        polygon.setPoints(MapApiImpl.convertToLatLng(points));
    }

    @Override
    public int getStrokeColor() {
        return polygon.getStrokeColor();
    }

    @Override
    public void setStrokeColor(int color) {
        polygon.setStrokeColor(color);
    }

    @Override
    public int getFillColor() {
        return polygon.getFillColor();
    }

    @Override
    public void setFillColor(int color) {
        polygon.setFillColor(color);
    }

    @Override
    public void remove() {
        polygon.remove();
    }
}
