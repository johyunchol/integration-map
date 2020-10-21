package kr.co.kkensu.integrationmap.tmap;

import android.graphics.Color;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapPolygon;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import kr.co.kkensu.integrationmap.BaseMapApi;
import kr.co.kkensu.integrationmap.MapCircle;
import kr.co.kkensu.integrationmap.MapInfoWindow;
import kr.co.kkensu.integrationmap.MapMarker;
import kr.co.kkensu.integrationmap.MapMarkerOptions;
import kr.co.kkensu.integrationmap.MapPoint;
import kr.co.kkensu.integrationmap.MapPolyLine;
import kr.co.kkensu.integrationmap.MapPolygon;
import kr.co.kkensu.integrationmap.util.AsyncRun;
import kr.co.kkensu.integrationmap.util.BitmapUtil;

public class MapApiImpl extends BaseMapApi {
    public static final int ANIMATION_TIME = 300;
    private static final String TAG = "MapApiImpl";
    public float lastZoomLevel;
    AsyncRun mapLoadedRun = new AsyncRun();
    private TMapView map;
    private MapFragmentImpl mapFragment;
    private MapPoint lastMapPoint;
    private boolean isMoving;
    private OnCameraMoveListener cameraMoveListener;
    private View.OnTouchListener touchListener;
    private MapCallback<MapPoint> userRegisteredOnClickListener;
    private boolean isTouchMoveEnable = true;
    private Runnable zoomListener;
    private HashMap<TMapMarkerItem2, MapInfoWindow> mapMarkerInfoWindow = new HashMap<>();

    private List<TMapPolyLine> polylineList = new ArrayList<>();
    private List<TMapCircle> circleList = new ArrayList<>();
    private List<TMapPolygon> polygonList = new ArrayList<>();
    private List<TMapMarkerItem2> markerList = new ArrayList<>();

    private final TMapView.OnClickListenerCallback mapOnClickListener = new TMapView.OnClickListenerCallback() {
        @Override
        public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
            return false;
        }

        @Override
        public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
            if (isTouchMoveEnable)
                setCenter(new MapPoint(tMapPoint.getLatitude(), tMapPoint.getLongitude()), true);
            if (userRegisteredOnClickListener != null)
                userRegisteredOnClickListener.handle(new MapPoint(tMapPoint.getLatitude(), tMapPoint.getLongitude()));

            return false;
        }
    };

    public MapApiImpl(TMapView tMapView, MapFragmentImpl mapFragment, String apiKey) {
        this.mapFragment = mapFragment;

        map = tMapView;
        map.setSKTMapApiKey(apiKey);

        map.setOnEnableScrollWithZoomLevelListener(new TMapView.OnEnableScrollWithZoomLevelCallback() {
            @Override
            public void onEnableScrollWithZoomLevelEvent(float v, TMapPoint tMapPoint) {

            }
        });

        map.setOnDisableScrollWithZoomLevelListener(new TMapView.OnDisableScrollWithZoomLevelCallback() {
            @Override
            public void onDisableScrollWithZoomLevelEvent(float v, TMapPoint tMapPoint) {
                if (cameraMoveListener != null) {
                    if (!isMoving) {
                        lastMapPoint = null;
                    } else {
                        lastMapPoint = new MapPoint(tMapPoint.getLatitude(), tMapPoint.getLongitude());
                    }
                    lastZoomLevel = map.getZoomLevel();
                    cameraMoveListener.onMove(new MapPoint(tMapPoint.getLatitude(), tMapPoint.getLongitude()), lastZoomLevel, isMoving);
                }
            }
        });


        map.setOnClickListenerCallBack(mapOnClickListener);

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
                    if (cameraMoveListener != null)
                        cameraMoveListener.onMove(lastMapPoint, lastZoomLevel, true);
                }
                return touchListener != null && touchListener.onTouch(v, event);
            }
        });


        map.setCompassMode(false);

        mapLoadedRun.fire();
    }

    public static List<MapPoint> convertToMapPoint(List<TMapPoint> list) {
        List<MapPoint> result = new ArrayList<>();
        for (TMapPoint tMapPoint : list) {
            result.add(new MapPoint(tMapPoint.getLatitude(), tMapPoint.getLongitude()));
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

    @Override
    public void setOnCameraMoveListener(OnCameraMoveListener listener) {
        this.cameraMoveListener = listener;
    }

    @Override
    public MapPolygon addPolygon(List<MapPoint> list, int strokeColor, int fillColor, float zIndex) {
        TMapPolygon polygon = new TMapPolygon();
        polygon.setPolygonWidth(0);
        polygon.setLineColor(strokeColor);
        polygon.setLineAlpha(0);
        polygon.setAreaColor(Color.parseColor("#000000"));
        polygon.setAreaAlpha(50);

        for (LatLng outerBound : createOuterBounds()) {
            polygon.addPolygonPoint(new TMapPoint(outerBound.latitude, outerBound.longitude));
        }

        for (MapPoint mapPoint : list) {
            polygon.addPolygonPoint(new TMapPoint(mapPoint.getLatitude(), mapPoint.getLongitude()));
        }

        int id = polygonList.size() + 1;
        map.addTMapPolygon(String.valueOf(id), polygon);
        polygonList.add(polygon);

        return new MapPolygonImpl(map, polygon);
    }

    @Override
    public MapPolygon addMultiPolygon(List<List<MapPoint>> list, int strokeColor, int fillColor, float zIndex) {
        TMapPolygon polygon = new TMapPolygon();
        polygon.setPolygonWidth(0);
        polygon.setLineColor(strokeColor);
        polygon.setLineAlpha(0);
        polygon.setAreaColor(Color.parseColor("#000000"));
        polygon.setAreaAlpha(50);

        for (LatLng outerBound : createOuterBounds()) {
            polygon.addPolygonPoint(new TMapPoint(outerBound.latitude, outerBound.longitude));
        }

        for (List<MapPoint> mapPoints : list) {
            for (MapPoint mapPoint : mapPoints) {
                polygon.addPolygonPoint(new TMapPoint(mapPoint.getLatitude(), mapPoint.getLongitude()));
            }
        }

        int id = polygonList.size() + 1;
        map.addTMapPolygon(String.valueOf(id), polygon);
        polygonList.add(polygon);

        return new MapPolygonImpl(map, polygon);
    }


    private static List<LatLng> createOuterBounds() {
        float delta = 0.01f;

        return new ArrayList<LatLng>() {{
            add(new LatLng(90 - delta, -180 + delta));
            add(new LatLng(0, -180 + delta));
            add(new LatLng(-90 + delta, -180 + delta));
            add(new LatLng(-90 + delta, 0));
            add(new LatLng(-90 + delta, 180 - delta));
            add(new LatLng(0, 180 - delta));
            add(new LatLng(90 - delta, 180 - delta));
            add(new LatLng(90 - delta, 0));
            add(new LatLng(90 - delta, -180 + delta));
        }};
    }

    @Override
    public void setTouchListener(final View.OnTouchListener listener) {
        touchListener = listener;
    }


    @Override
    public void setCenter(final MapPoint center, final float zoomLevel, boolean animate) {
        if (center == null) return;

        map.setCenterPoint(center.getLongitude(), center.getLatitude(), animate);
    }

    @Override
    public void scrollBy(int x, int y, boolean animate) {
        /*if (animate) {
            map.animateCamera(CameraUpdateFactory.scrollBy(x, y));
        } else {
            map.moveCamera(CameraUpdateFactory.scrollBy(x, y));
        }*/

        map.scrollBy(x, y);
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
                final LatLngBounds bounds = latlngBuilder.build();

                int zoomLevel = getBoundsZoomLevel(bounds.northeast, bounds.southwest, mapFragment.getView().getWidth(), mapFragment.getView().getHeight());

                map.setPadding(0, paddingInDp, 0, viewSize);
                if (Math.abs(bounds.northeast.latitude - bounds.southwest.latitude) > Math.abs(bounds.northeast.longitude - bounds.southwest.longitude)) {
                    // 세로
                    zoomLevel -= (3 + (viewSize / 100));
                } else {
                    // 가로
                    zoomLevel -= (2 + (paddingInDp / 100));
                }

                int finalZoomLevel = zoomLevel;
                Runnable r = () -> {
                    setCenter(new MapPoint(bounds.getCenter().latitude, bounds.getCenter().longitude), animate);
                    map.setZoomLevel(finalZoomLevel);
//                    if (map.getZoomLevel() < finalZoomLevel) {
//                        for (int i = map.getZoomLevel(); i < finalZoomLevel; i++) {
//                            map.MapZoomIn();
//                        }
//                    }
//
//                    if (zoomListener != null)
//                        zoomListener.run();

                    map.setPadding(0, 0, 0, 0);
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
            }
        });
    }

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
        TMapPolyLine polyline = new TMapPolyLine();
        polyline.setLineColor(Color.BLUE);
        polyline.setLineWidth(20);

        for (MapPoint point : points) {
            polyline.addLinePoint(new TMapPoint(point.getLatitude(), point.getLongitude()));
        }

        int id = polylineList.size() + 1;
        map.addTMapPolyLine(String.valueOf(id), polyline);
        polylineList.add(polyline);

        return new MapPolyLineImpl(map, polyline);
    }

    @Override
    public void setOnClickListener(final MapCallback<MapPoint> listener) {
        userRegisteredOnClickListener = listener;
    }

    @Override
    public void clear() {
        map.removeAllMarkerItem();
        map.removeAllTMapCircle();
        map.removeAllTMapPOIItem();
        map.removeAllTMapPolygon();
        map.removeAllTMapPolyLine();
    }

    @Override
    public MapCircle addCircle(final MapPoint point, final float radiusInMeter, final int fillColor, final float strokeWidth, final int strokeColor) {
        TMapCircle circle = new TMapCircle();
        circle.setCenterPoint(new TMapPoint(point.getLatitude(), point.getLongitude()));
        circle.setRadius(radiusInMeter);
        circle.setLineColor(strokeColor);
        circle.setAreaColor(fillColor);
        circle.setCircleWidth(strokeWidth);
        circle.setAreaAlpha(50);

        int id = circleList.size() + 1;
        map.addTMapCircle(String.valueOf(id), circle);

        return new MapCircleImpl(map, circle);
    }

    @Override
    public MapMarker addMarker(MapMarkerOptions options) {
//    public MapMarker addMarker(final MapPoint result, final MapMarkerIcon mapMarkerIcon, float rotation, float zIndex, AnchorType anchorType, MapInfoWindow mapInfoWindow) {
        TMapMarkerItem2 marker = new TMapMarkerItem2();
        marker.setTMapPoint(new TMapPoint(options.getMapPoint().getLatitude(), options.getMapPoint().getLongitude()));

        mapFragment.getView().post(new Runnable() {
            @Override
            public void run() {
                if (options.getMapMarkerIcon() != null) {
                    View markerView = options.getMapMarkerIcon().getMarkerView();
                    marker.setIcon(BitmapUtil.createDrawableFromView(mapFragment.getContext(), markerView));
                }

                marker.setPosition(options.getAnchorX(), options.getAnchorY());

//        if (rotation != 0)
//            markerOptions.rotation(rotation);

//        if (zIndex > 0) {
//            markerOptions.zIndex(zIndex);
//        }

//        Marker marker = map.addMarker(markerOptions);
                int id = markerList.size() + 1;
                map.addMarkerItem2(String.valueOf(id), marker);

                if (options.getMapInfoWindow() != null) {
                    if (!mapMarkerInfoWindow.containsKey(marker)) {
                        mapMarkerInfoWindow.put(marker, options.getMapInfoWindow());
                    }

//            setInfoWindow(mapInfoWindow);

                    map.setOnMarkerClickEvent(new TMapView.OnCalloutMarker2ClickCallback() {
                        @Override
                        public void onCalloutMarker2ClickEvent(String s, TMapMarkerItem2 tMapMarkerItem2) {
                            MapInfoWindow mapInfoWindow1 = mapMarkerInfoWindow.get(marker);

                            if (mapInfoWindow1 != null) {
                                if (mapInfoWindow1.getRunnable() != null)
                                    mapInfoWindow1.getRunnable().run();
                            }
                        }
                    });
                }
            }
        });


        return new MapMarkerImpl(map, marker);
    }

    private void setInfoWindow(MapInfoWindow mapInfoWindow) {
//        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//            @Override
//            public View getInfoWindow(Marker marker) {
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
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                return null;
//            }
//        });
//
//        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                if (mapMarkerInfoWindow.get(marker).getRunnable() != null) {
//                    mapMarkerInfoWindow.get(marker).getRunnable().run();
//                }
//            }
//        });
    }

    @Override
    public MapPoint getCenter() {
        return new MapPoint(map.getCenterPoint().getLatitude(), map.getCenterPoint().getLongitude());
    }


    @Override
    public void setCamera(MapPoint center, float bearing, float tilt, float zoomLevel, boolean animate) {
        /*if (animate)
            map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(center.getLatitude(), center.getLongitude()))
                    .bearing(bearing)
                    .tilt(tilt)
                    .zoom(zoomLevel)
                    .build()), 100, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {

                }

                @Override
                public void onCancel() {

                }
            });
        else
            map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                    .target(new LatLng(center.getLatitude(), center.getLongitude()))
                    .bearing(bearing)
                    .tilt(tilt)
                    .zoom(zoomLevel)
                    .build()));*/
    }

    @Override
    public float getCameraRotation() {
        return map.getRotation();
    }

    @Override
    public float getZoomLevel() {
        return map.getZoomLevel();
    }

    @Override
    public void setZoomLevel(final float level) {
        map.setZoomLevel((int) level);
    }

    @Override
    public void setMovable(boolean isMovable) {

        if (isMovable) {
            isTouchMoveEnable = true;
            setDefaultSetting();
        } else {
            isTouchMoveEnable = false;
//            map.getUiSettings().setAllGesturesEnabled(false);
        }
    }

    private void setDefaultSetting() {
        map.setLanguage(TMapView.LANGUAGE_KOREAN);
        map.setIconVisibility(true);
        map.setZoomLevel(10);
        map.setMapType(TMapView.MAPTYPE_STANDARD);
        map.setCompassMode(true);
        map.setTrackingMode(true);
    }

}
