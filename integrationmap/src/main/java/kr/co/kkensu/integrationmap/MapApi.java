package kr.co.kkensu.integrationmap;

import android.view.View;

import java.util.Collection;
import java.util.List;

/**
 * 지도와 관련된 모든 상호작용을 위한 클래스
 */
public interface MapApi {
    MapPolygon addPolygon(List<MapPoint> list);

    MapPolygon addPolygon(List<MapPoint> list, int strokeColor, int fillColor);

    MapPolygon addPolygon(List<MapPoint> list, int strokeColor, int fillColor, float zIndex);

    MapPolygon addMultiPolygon(List<List<MapPoint>> list);

    MapPolygon addMultiPolygon(List<List<MapPoint>> list, int strokeColor, int fillColor);

    MapPolygon addMultiPolygon(List<List<MapPoint>> list, int strokeColor, int fillColor, float zIndex);

    void setTouchListener(View.OnTouchListener listener);

    void setCenter(MapPoint center, boolean animate);

    void setCenter(MapPoint center, float zoomLevel, boolean animate);

    void scrollBy(int x, int y);

    void scrollBy(int x, int y, boolean animate);

    void zoom(MapPoint... includes);

    /**
     * 점들을 모두 포함하게 줌인/아웃
     *
     * @param includes
     */
    void zoom(final Collection<MapPoint> includes);

    void zoom(final Collection<MapPoint> includes, int paddingInDp);

    void zoom(Collection<MapPoint> includes, int paddingInDp, boolean animate);

    void zoom(Collection<MapPoint> includes, int paddingInDp, int viewSize, boolean animate);

    MapPolyLine addPolyline(List<MapPoint> points);

    MapPolyLine addPolyline(List<MapPoint> points, int color, int width);

    void setOnClickListener(MapCallback<MapPoint> point);

    MapCircle addCircle(MapPoint point, float radiusInMeter);

    void clear();

    MapCircle addCircle(MapPoint point, float radiusInMeter, int fillColor, float strokeWidth, int strokeColor);

    MapMarker addMarker(MapPoint result);

    MapMarker addMarker(MapPoint myPoint, MapMarkerIcon mapMarkerIcon);

    MapMarker addMarker(MapPoint myPoint, MapMarkerIcon mapMarkerIcon, MapInfoWindow mapInfoWindow);

    MapMarker addMarker(final MapPoint result, final MapMarkerIcon mapMarkerIcon, float rotation, float zIndex, AnchorType center, MapInfoWindow mapInfoWindow);

    MapPoint getCenter();

    void setCenter(MapPoint center);

    void setCamera(MapPoint point, float bearing, float tilt, float zoomLevel);

    float getCameraRotation();

    void setCamera(MapPoint center, float bearing, float tilt, float zoomLevel, boolean animate);

    void setOnCameraMoveListener(OnCameraMoveListener listener);

    float getZoomLevel();

    void setZoomLevel(float level);

    /**
     * 지도를 움직일 수 있게 / 없게 하는 설정
     *
     * @param isMovable true면 이동가능
     */
    void setMovable(boolean isMovable);

    interface OnCameraMoveListener {
        void onMove(MapPoint center, float zoomLevel, boolean isMoving);
    }

    interface MapCallback<T> {
        void handle(T result);
    }

    public void setZoomListener(Runnable l);

}
