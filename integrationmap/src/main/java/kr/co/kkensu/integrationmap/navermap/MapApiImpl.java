package kr.co.kkensu.integrationmap.navermap;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PolygonOverlay;
import com.naver.maps.map.overlay.PolylineOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import kr.co.kkensu.integrationmap.AnchorType;
import kr.co.kkensu.integrationmap.BaseMapApi;
import kr.co.kkensu.integrationmap.MapCircle;
import kr.co.kkensu.integrationmap.MapInfoWindow;
import kr.co.kkensu.integrationmap.MapMarker;
import kr.co.kkensu.integrationmap.MapMarkerIcon;
import kr.co.kkensu.integrationmap.MapMarkerOptions;
import kr.co.kkensu.integrationmap.MapPoint;
import kr.co.kkensu.integrationmap.MapPolyLine;
import kr.co.kkensu.integrationmap.MapPolygon;
import kr.co.kkensu.integrationmap.util.AsyncRun;
import kr.co.kkensu.integrationmap.util.BitmapUtil;
import kr.co.kkensu.integrationmap.util.ScreenUtil;
import kr.co.kkensu.maptest.R;

public class MapApiImpl extends BaseMapApi {
    public static final int ANIMATION_TIME = 300;
    private static final String TAG = "MapApiImpl";
    public float lastZoomLevel;
    private AsyncRun mapLoadedRun = new AsyncRun();
    private NaverMap map;
    private MapFragmentImpl mapFragment;
    private MapPoint lastMapPoint;
    private boolean isMoving;
    private OnCameraMoveListener cameraMoveListener;
    private View.OnTouchListener touchListener;
    private MapCallback<MapPoint> userRegisteredOnClickListener;
    private boolean isTouchMoveEnable = true;
    private Runnable zoomListener;
    private HashMap<Marker, MapInfoWindow> mapMarkerInfoWindow = new HashMap<>();

    private List<PolylineOverlay> polylineList = new ArrayList<>();
    private List<CircleOverlay> circleList = new ArrayList<>();
    private List<PolygonOverlay> polygonList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();

    private final NaverMap.OnMapClickListener mapOnClickListener = new NaverMap.OnMapClickListener() {
        @Override
        public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
            if (isTouchMoveEnable)
                setCenter(new MapPoint(latLng.latitude, latLng.longitude), true);
            if (userRegisteredOnClickListener != null)
                userRegisteredOnClickListener.handle(new MapPoint(latLng.latitude, latLng.longitude));
        }
    };

    public MapApiImpl(NaverMap naverMap, MapFragmentImpl mapFragment) {
        map = naverMap;
        map.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int i, boolean animated) {
                if (cameraMoveListener != null) {
                    if (!isMoving) {
                        lastMapPoint = null;
                    } else {
                        lastMapPoint = new MapPoint(map.getCameraPosition().target.latitude, map.getCameraPosition().target.longitude);
                    }
                    lastZoomLevel = (float) map.getCameraPosition().zoom;
                    cameraMoveListener.onMove(new MapPoint(map.getCameraPosition().target.latitude, map.getCameraPosition().target.longitude), lastZoomLevel, isMoving);
                }
            }
        });

        map.setOnMapClickListener(mapOnClickListener);
        this.mapFragment = mapFragment;

        mapFragment.setTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    isMoving = true;
                } else if (action == MotionEvent.ACTION_UP) {
                    isMoving = false;
                    if (lastMapPoint != null && cameraMoveListener != null) {
                        cameraMoveListener.onMove(lastMapPoint, lastZoomLevel, false);
                    }
                } else {
                    isMoving = true;
//                    if (cameraMoveListener != null)
//                        cameraMoveListener.onMove(lastMapPoint, lastZoomLevel, true);
                }
                return touchListener != null && touchListener.onTouch(v, event);
            }
        });
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setZoomControlEnabled(false);

        mapLoadedRun.fire();
        /*map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mapLoadedRun.fire();
            }
        });*/
    }

    public static List<MapPoint> convertToMapPoint(List<LatLng> list) {
        List<MapPoint> result = new ArrayList<>();
        for (LatLng latLng : list) {
            result.add(new MapPoint(latLng.latitude, latLng.longitude));
        }
        return result;
    }

    public static List<LatLng> convertToLatLng(List<MapPoint> list) {
        List<LatLng> result = new ArrayList<>();
        for (MapPoint latLng : list) {
            result.add(new LatLng(latLng.getLatitude(), latLng.getLongitude()));
        }
        return result;
    }

    public static List<List<LatLng>> convertToLatLng2(List<List<MapPoint>> list) {
        List<List<LatLng>> convertResult = new ArrayList<>();

        for (List<MapPoint> mapPointList : list) {
            List<LatLng> convertList = new ArrayList<>();
            for (MapPoint mapPoint : mapPointList) {
                convertList.add(new LatLng(mapPoint.getLatitude(), mapPoint.getLongitude()));
            }

            convertResult.add(convertList);
        }

        return convertResult;
    }

    @Override
    public void setOnCameraMoveListener(OnCameraMoveListener listener) {
        this.cameraMoveListener = listener;
    }

    @Override
    public MapPolygon addPolygon(List<MapPoint> list, int strokeColor, int fillColor, float zIndex) {
        PolygonOverlay polygon = new PolygonOverlay();

        polygon.setCoords(createOuterBounds());
        polygon.setOutlineWidth(10);
        polygon.setGlobalZIndex((int) zIndex);
        polygon.setOutlineColor(strokeColor);
        polygon.setHoles(convertToLatLng2(Collections.singletonList(list)));
        polygon.setColor(Color.parseColor("#44000000"));
        polygon.setMap(map);

        polygonList.add(polygon);

        return new MapPolygonImpl(polygon);
    }

    @Override
    public MapPolygon addMultiPolygon(List<List<MapPoint>> list, int strokeColor, int fillColor, float zIndex) {
        PolygonOverlay polygon = new PolygonOverlay();

        polygon.setCoords(createOuterBounds());
        polygon.setOutlineWidth(10);
        polygon.setGlobalZIndex((int) zIndex);
        polygon.setOutlineColor(strokeColor);
        polygon.setHoles(convertToLatLng2(list));
        polygon.setColor(Color.parseColor("#44000000"));
        polygon.setMap(map);

        polygonList.add(polygon);

        return new MapPolygonImpl(polygon);
    }


    private static List<LatLng> createOuterBounds() {
        float delta = 0.01f;

        return Arrays.asList(
                new LatLng(90 - delta, -180 + delta),
                new LatLng(0, -180 + delta),
                new LatLng(-90 + delta, -180 + delta),
                new LatLng(-90 + delta, 0),
                new LatLng(-90 + delta, 180 - delta),
                new LatLng(0, 180 - delta),
                new LatLng(90 - delta, 180 - delta),
                new LatLng(90 - delta, 0),
                new LatLng(90 - delta, -180 + delta)
        );
    }

    @Override
    public void setTouchListener(final View.OnTouchListener listener) {
        touchListener = listener;
    }


    @Override
    public void setCenter(final MapPoint center, final float zoomLevel, boolean animate) {
        if (center == null) return;

        isMoving = true;
        if (animate) {
            map.moveCamera(CameraUpdate.scrollAndZoomTo(new LatLng(center.getLatitude(), center.getLongitude()), zoomLevel).animate(CameraAnimation.Easing).finishCallback(finishCallback));
        } else {
            map.moveCamera(CameraUpdate.scrollAndZoomTo(new LatLng(center.getLatitude(), center.getLongitude()), zoomLevel).finishCallback(finishCallback));
        }
    }

    @Override
    public void scrollBy(int x, int y, boolean animate) {
        isMoving = true;
        if (animate) {
            map.moveCamera((CameraUpdate.scrollBy(new PointF(x, y)).animate(CameraAnimation.Easing)).finishCallback(finishCallback));
        } else {
            map.moveCamera((CameraUpdate.scrollBy(new PointF(x, y))).finishCallback(finishCallback));
        }
    }

    @Override
    public void zoom(Collection<MapPoint> includes, final int paddingInDp, final int viewSize, final boolean animate) {
        if (includes.size() == 0) {
            return;
        }

        final LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
        for (MapPoint point : includes) {
            latlngBuilder.include(new LatLng(point.getLatitude(), point.getLongitude()));
        }

        mapFragment.getView().post(new Runnable() {
            @Override
            public void run() {

                Runnable r = () -> {
                    isMoving = true;
                    if (animate) {
                        map.moveCamera(CameraUpdate.fitBounds(latlngBuilder.build(), paddingInDp).animate(CameraAnimation.Easing).finishCallback(finishCallback));
                    } else {
                        map.moveCamera(CameraUpdate.fitBounds(latlngBuilder.build(), paddingInDp).finishCallback(finishCallback));
                    }

                    if (zoomListener != null)
                        zoomListener.run();
                };

                if (!mapLoadedRun.isFired()) {
                    mapLoadedRun.post(new TimerTask() {
                        @Override
                        public void run() {
                            r.run();
                        }
                    });
                } else {
                    r.run();
                }

                /*final LatLngBounds bounds = latlngBuilder.build();

                int zoomLevel = getBoundsZoomLevel(bounds.getNorthEast(), bounds.getSouthWest(), mapFragment.getView().getWidth(), mapFragment.getView().getHeight());

                map.setContentPadding(0, paddingInDp, 0, viewSize);
                if (Math.abs(bounds.getNorthEast().latitude - bounds.getSouthWest().latitude) > Math.abs(bounds.getNorthEast().longitude - bounds.getSouthWest().longitude)) {
                    // 세로
                    zoomLevel -= (2 + (viewSize / 100));
                } else {
                    // 가로
                    zoomLevel -= (1 + (paddingInDp / 100));
                }

                int finalZoomLevel = zoomLevel;
                Runnable r = () -> {
                    isMoving = true;
                    if (animate) {
                        map.moveCamera(CameraUpdate.scrollAndZoomTo(bounds.getCenter(), finalZoomLevel).animate(CameraAnimation.Easing));
                    } else {
                        map.moveCamera(CameraUpdate.scrollAndZoomTo(bounds.getCenter(), finalZoomLevel));
                    }

                    if (zoomListener != null)
                        zoomListener.run();

                    map.setContentPadding(0, paddingInDp, 0, viewSize);
                };

                if (!mapLoadedRun.isFired()) {
                    mapLoadedRun.post(new TimerTask() {
                        @Override
                        public void run() {
                            r.run();
                        }
                    });
                } else {
                    r.run();
                }*/
            }
        });
    }

    private CameraUpdate.FinishCallback finishCallback = new CameraUpdate.FinishCallback() {
        @Override
        public void onCameraUpdateFinish() {
            isMoving = false;
            if (lastMapPoint != null && cameraMoveListener != null) {
                cameraMoveListener.onMove(lastMapPoint, lastZoomLevel, false);
            }
        }
    };

    public static int getBoundsZoomLevel(LatLng northeast, LatLng southwest, int width, int height) {
        final int GLOBE_WIDTH = 256; // a constant in Google's map projection
        final int ZOOM_MAX = 21;
        double latFraction = (latRad(northeast.latitude) - latRad(southwest.latitude)) / Math.PI;
        double lngDiff = northeast.longitude - southwest.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;
        double latZoom = zoom(height, GLOBE_WIDTH, latFraction);
        double lngZoom = zoom(width, GLOBE_WIDTH, lngFraction);
        double zoom = Math.min(Math.min(latZoom, lngZoom), ZOOM_MAX);
        return (int) (zoom);
    }

    private static double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }

    private static double zoom(double mapPx, double worldPx, double fraction) {
        final double LN2 = .693147180559945309417;
        return (Math.log(mapPx / worldPx / fraction) / LN2);
    }

    @Override
    public void setZoomListener(Runnable l) {
        zoomListener = l;
    }

    @Override
    public MapPolyLine addPolyline(List<MapPoint> points, int color, int width) {
        PolylineOverlay polyline = new PolylineOverlay();

        polyline.setColor(color);
        polyline.setWidth(width);
        polyline.setCapType(PolylineOverlay.LineCap.Round);
        polyline.setCoords(convertToLatLng(points));
        polyline.setGlobalZIndex(2);
        polyline.setMap(map);

        polylineList.add(polyline);

        return new MapPolyLineImpl(polyline);
    }

    @Override
    public void setOnClickListener(final MapCallback<MapPoint> listener) {
        userRegisteredOnClickListener = listener;
    }

    @Override
    public void clear() {
        for (PolylineOverlay polyline : polylineList) {
            polyline.setMap(null);
        }
        polylineList.clear();

        for (CircleOverlay circle : circleList) {
            circle.setMap(null);
        }
        circleList.clear();

        for (PolygonOverlay polygon : polygonList) {
            polygon.setMap(null);
        }
        polygonList.clear();


        for (Marker marker : markerList) {
            marker.setMap(null);
        }
        markerList.clear();
    }

    @Override
    public MapCircle addCircle(final MapPoint point, final float radiusInMeter, final int fillColor, final float strokeWidth, final int strokeColor) {
        CircleOverlay circle = new CircleOverlay();
        circle.setCenter(new LatLng(point.getLatitude(), point.getLongitude()));
        circle.setColor(fillColor);
        circle.setOutlineWidth((int) strokeWidth);
        circle.setRadius(radiusInMeter);
        circle.setGlobalZIndex(100);
        circle.setOutlineColor(strokeColor);
        circle.setMap(map);

        circleList.add(circle);

        return new MapCircleImpl(circle);
    }

    @Override
    public MapMarker addMarker(MapMarkerOptions options) {
        Marker marker = new Marker();

        marker.setPosition(new LatLng(options.getMapPoint().getLatitude(), options.getMapPoint().getLongitude()));

        if (options.getMapMarkerIcon() != null) {
            View markerView = options.getMapMarkerIcon().getMarkerView();
            marker.setIcon(OverlayImage.fromBitmap(BitmapUtil.createDrawableFromView(mapFragment.getContext(), markerView)));
        }

        marker.setAnchor(new PointF(options.getAnchorX(), options.getAnchorY()));

        if (options.getRotation() != 0)
            marker.setAngle(options.getRotation());

        if (options.getzIndex() > 0) {
            marker.setGlobalZIndex((int) options.getzIndex());
        }

        marker.setMap(map);

        if (options.getMapInfoWindow() != null) {
            if (!mapMarkerInfoWindow.containsKey(marker)) {
                mapMarkerInfoWindow.put(marker, options.getMapInfoWindow());
            }

//            setInfoWindow(mapInfoWindow, marker);

            marker.setOnClickListener(new Overlay.OnClickListener() {
                @Override
                public boolean onClick(@NonNull Overlay overlay) {
                    MapInfoWindow mapInfoWindow1 = mapMarkerInfoWindow.get(marker);

                    if (mapInfoWindow1 != null) {
                        if (mapInfoWindow1.getRunnable() != null)
                            mapInfoWindow1.getRunnable().run();
                    }

                    return false;
                }
            });
//            marker.showInfoWindow();
        }

        markerList.add(marker);

        return new MapMarkerImpl(marker);
    }

//    private void setInfoWindow(MapInfoWindow mapInfoWindow, Marker marker) {
//        InfoWindow infoWindow = new InfoWindow();
//        infoWindow.setAdapter(new InfoWindow.ViewAdapter() {
//            @NonNull
//            @Override
//            public View getView(@NonNull InfoWindow infoWindow) {
//                MapInfoWindow mapInfoWindow1 = mapMarkerInfoWindow.get(marker);
//
//                if (mapInfoWindow1 != null) {
//                    View view = mapInfoWindow1.getView();
//                    if (view == null) {
//                        view = LayoutInflater.from(mapFragment.getContext()).inflate(R.layout.view_map_info_window_go_to_navigation, null);
//
//                        //                    TextView txtName = Util.findView(view, R.id.txtName);
//                        TextView txtAddress = view.findViewById(R.id.txtAddress);
//
////                    txtName.setText(mapInfoWindow.getName());
//                        String address;
//                        if (mapInfoWindow.getAddress().length() > 20) {
//                            address = mapInfoWindow.getAddress().substring(0, 20);
//                            address += "...";
//                        } else {
//                            address = mapInfoWindow.getAddress();
//                        }
//
//                        txtAddress.setText(address);
//                    }
//                    return view;
//                }
//
//                return null;
//            }
//        });
//
//        infoWindow.setOnClickListener(new Overlay.OnClickListener() {
//            @Override
//            public boolean onClick(@NonNull Overlay overlay) {
//                if (mapMarkerInfoWindow.get(marker).getRunnable() != null) {
//                    mapMarkerInfoWindow.get(marker).getRunnable().run();
//                }
//
//                return false;
//            }
//        });
//
//        infoWindow.setMap(map);
//    }

    @Override
    public MapPoint getCenter() {
        CameraPosition positions = map.getCameraPosition();
        return new MapPoint(positions.target.latitude, positions.target.longitude);
    }


    @Override
    public void setCamera(MapPoint center, float bearing, float tilt, float zoomLevel, boolean animate) {
        CameraPosition cameraPosition = new CameraPosition(new LatLng(center.getLatitude(), center.getLongitude()), zoomLevel, tilt, bearing);

        isMoving = true;
        if (animate) {
            map.moveCamera(CameraUpdate.toCameraPosition(cameraPosition).animate(CameraAnimation.Easing).finishCallback(finishCallback));
        } else {
            map.moveCamera(CameraUpdate.toCameraPosition(cameraPosition).finishCallback(finishCallback));
        }
    }

    @Override
    public float getCameraRotation() {
        return (float) map.getCameraPosition().bearing;
    }

    @Override
    public float getZoomLevel() {
        CameraPosition positions = map.getCameraPosition();
        return (float) positions.zoom;
    }

    @Override
    public void setZoomLevel(final float level) {
        isMoving = true;
        map.moveCamera(CameraUpdate.scrollAndZoomTo(map.getCameraPosition().target, level).finishCallback(finishCallback));
    }

    @Override
    public void setMovable(boolean isMovable) {

        if (isMovable) {
            isTouchMoveEnable = true;
            setDefaultSetting();
        } else {
            isTouchMoveEnable = false;
            map.getUiSettings().setAllGesturesEnabled(false);
        }
    }

    private void setDefaultSetting() {
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setTiltGesturesEnabled(false);
    }
}
