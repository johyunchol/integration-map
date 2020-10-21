package kr.co.kkensu.integrationmap;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * MapApi에 공통적인 부분을 구현한 클래스
 */
public abstract class BaseMapApi implements MapApi {
    public static final int VIEW_SIZE_FREE = -1;
    public static final int VIEW_SIZE_WIDTH = -2;
    public static final int DEFAULT_POLY_LINE_COLOR = Color.RED;

    public static final int DEFAULT_CIRCLE_FILL_COLOR = Color.argb(100, 100, 100, 100);
    public static final int DEFAULT_CIRCLE_STROKE_WIDTH = 1;
    public static final int DEFAULT_CIRCLE_STROKE_COLOR = Color.GRAY;
    public static final int DEFAULT_POLY_LINE_WIDTH = 10;
    public static final int DEFAULT_ZOOM_PADDING = 60;
    int DEFAULT_POLYGON_STROKE_COLOR = Color.parseColor("#42a5f5");
    int DEFAULT_POLYGON_FILL_COLOR = Color.parseColor("#4c1976d2");
    int DEFAULT_MULTI_POLYGON_STROKE_COLOR = Color.parseColor("#42a5f5");
    int DEFAULT_MULTI_POLYGON_OUTSIDE_COLOR = Color.parseColor("#4c1976d2");

    @Override
    final public MapPolygon addPolygon(List<MapPoint> list) {
        return addPolygon(list, DEFAULT_POLYGON_STROKE_COLOR, DEFAULT_POLYGON_FILL_COLOR, 0);
    }

    @Override
    final public MapPolygon addPolygon(List<MapPoint> list, int strokeColor, int fillColor) {
        return addPolygon(list, strokeColor, fillColor, 0);
    }

    @Override
    public MapPolygon addMultiPolygon(List<List<MapPoint>> list) {
        return addMultiPolygon(list, DEFAULT_MULTI_POLYGON_STROKE_COLOR, DEFAULT_MULTI_POLYGON_OUTSIDE_COLOR, 0);
    }

    @Override
    public MapPolygon addMultiPolygon(List<List<MapPoint>> list, int strokeColor, int fillColor) {
        return addMultiPolygon(list, strokeColor, fillColor, 0);
    }

    @Override
    public MapPolygon addMultiPolygon(List<List<MapPoint>> list, int strokeColor, int fillColor, float zIndex) {
        return addMultiPolygon(list, strokeColor, fillColor, zIndex);
    }

    @Override
    final public void setCenter(MapPoint center, boolean animate) {
        setCenter(center, getZoomLevel(), animate);
    }

    @Override
    final public void setCenter(MapPoint center) {
        setCenter(center, false);
    }

    @Override
    public void scrollBy(int x, int y) {
        scrollBy(x, y, false);
    }

    @Override
    final public void zoom(MapPoint... includes) {
        if (includes.length == 0) {
            return;
        }
        ArrayList<MapPoint> list = new ArrayList<>();
        Collections.addAll(list, includes);
        zoom(list);
    }

    @Override
    final public void zoom(Collection<MapPoint> includes) {
        zoom(includes, DEFAULT_ZOOM_PADDING);
    }

    @Override
    final public void zoom(Collection<MapPoint> includes, int paddingInDp) {
        zoom(includes, paddingInDp, VIEW_SIZE_FREE, true);
    }

    @Override
    public void zoom(Collection<MapPoint> includes, int paddingInDp, boolean animate) {
        zoom(includes, paddingInDp, VIEW_SIZE_FREE, animate);
    }


    @Override
    final public MapPolyLine addPolyline(List<MapPoint> points) {
        return addPolyline(points, DEFAULT_POLY_LINE_COLOR, DEFAULT_POLY_LINE_WIDTH);
    }


    @Override
    final public MapCircle addCircle(MapPoint point, float radiusInMeter) {
        return addCircle(point, radiusInMeter, DEFAULT_CIRCLE_FILL_COLOR, DEFAULT_CIRCLE_STROKE_WIDTH, DEFAULT_CIRCLE_STROKE_COLOR);
    }


//    @Override
//    final public MapMarker addMarker(MapPoint point) {
//        return addMarker(point, null, 0, 0, AnchorType.TYPE_DEFAULT, null);
//    }
//
//    @Override
//    final public MapMarker addMarker(MapPoint point, MapMarkerIcon mapMarkerIcon) {
//        return addMarker(point, mapMarkerIcon, 0, 0, AnchorType.TYPE_DEFAULT, null);
//    }
//
//    @Override
//    public MapMarker addMarker(MapPoint point, MapMarkerIcon mapMarkerIcon, MapInfoWindow mapInfoWindow) {
//        return addMarker(point, mapMarkerIcon, 0, 0, AnchorType.TYPE_DEFAULT, mapInfoWindow);
//    }

    @Override
    final public void setCamera(MapPoint point, float bearing, float tilt, float zoomLevel) {
        setCamera(point, bearing, tilt, zoomLevel, false);
    }
}
