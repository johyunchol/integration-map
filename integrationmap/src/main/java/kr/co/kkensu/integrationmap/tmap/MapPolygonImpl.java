package kr.co.kkensu.integrationmap.tmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolygon;
import com.skt.Tmap.TMapView;

import java.util.List;

import kr.co.kkensu.integrationmap.MapPoint;
import kr.co.kkensu.integrationmap.MapPolygon;

public class MapPolygonImpl implements MapPolygon {
    private TMapView tMapView;
    private TMapPolygon polygon;

    public MapPolygonImpl(TMapView tMapView, TMapPolygon polygon) {
        this.tMapView = tMapView;
        this.polygon = polygon;
    }


    @Override
    public List<MapPoint> getPoints() {
        return MapApiImpl.convertToMapPoint(polygon.getPolygonPoint());
    }

    @Override
    public void setPoints(List<MapPoint> points) {
        for (LatLng latLng : MapApiImpl.convertToLatLng(points)) {
            polygon.addPolygonPoint(new TMapPoint(latLng.latitude, latLng.longitude));
        }
    }

    @Override
    public int getStrokeColor() {
        return polygon.getLineColor();
    }

    @Override
    public void setStrokeColor(int color) {
        polygon.setLineColor(color);
    }

    @Override
    public int getFillColor() {
        return polygon.getAreaColor();
    }

    @Override
    public void setFillColor(int color) {
        polygon.setAreaColor(color);
    }

    @Override
    public void remove() {
        tMapView.removeTMapPolygon(polygon.getID());
    }
}
