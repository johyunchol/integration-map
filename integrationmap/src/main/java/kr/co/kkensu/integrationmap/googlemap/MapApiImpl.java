package kr.co.kkensu.integrationmap.googlemap;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.Collection;
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
    AsyncRun mapLoadedRun = new AsyncRun();
    private GoogleMap map;
    private MapFragmentImpl mapFragment;
    private MapPoint lastMapPoint;
    private boolean isMoving;
    private OnCameraMoveListener cameraMoveListener;
    private View.OnTouchListener touchListener;
    private MapCallback<MapPoint> userRegisteredOnClickListener;
    private boolean isTouchMoveEnable = true;
    private Runnable zoomListener;
    private HashMap<Marker, MapInfoWindow> mapMarkerInfoWindow = new HashMap<>();

    private final GoogleMap.OnMapClickListener mapOnClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if (isTouchMoveEnable)
                setCenter(new MapPoint(latLng.latitude, latLng.longitude), true);
            if (userRegisteredOnClickListener != null)
                userRegisteredOnClickListener.handle(new MapPoint(latLng.latitude, latLng.longitude));
        }
    };

    public MapApiImpl(GoogleMap googleMap, MapFragmentImpl mapFragment) {
        map = googleMap;
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (cameraMoveListener != null) {
                    if (!isMoving) {
                        lastMapPoint = null;
                    } else {
                        lastMapPoint = new MapPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);
                    }
                    lastZoomLevel = cameraPosition.zoom;
                    cameraMoveListener.onMove(new MapPoint(cameraPosition.target.latitude, cameraPosition.target.longitude), lastZoomLevel, isMoving);
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
                    if (cameraMoveListener != null)
                        cameraMoveListener.onMove(lastMapPoint, lastZoomLevel, true);
                }
                return touchListener != null && touchListener.onTouch(v, event);
            }
        });
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setTiltGesturesEnabled(false);

        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mapLoadedRun.fire();
            }
        });
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

    @Override
    public void setOnCameraMoveListener(OnCameraMoveListener listener) {
        this.cameraMoveListener = listener;
    }

    @Override
    public MapPolygon addPolygon(List<MapPoint> list, int strokeColor, int fillColor, float zIndex) {
        PolygonOptions rectOptions = new PolygonOptions();
        rectOptions
//                .addAll(convertToLatLng2(list))
                .addAll(createOuterBounds())
                .strokeWidth(10)
                .zIndex(zIndex)
                .strokeColor(strokeColor)
                .addHole(convertToLatLng(list))
//                .addHole(createHole(new LatLng(37.683758, 127.308075), 1000))
//                .fillColor(fillColor);
                .fillColor(Color.parseColor("#44000000"));
        return new MapPolygonImpl(map.addPolygon(rectOptions));
    }

    @Override
    public MapPolygon addMultiPolygon(List<List<MapPoint>> list, int strokeColor, int fillColor, float zIndex) {
        PolygonOptions rectOptions = new PolygonOptions();
        rectOptions
//                .addAll(convertToLatLng2(list))
                .addAll(createOuterBounds())
                .strokeWidth(10)
                .zIndex(1)
                .strokeColor(strokeColor)
                .fillColor(Color.parseColor("#44000000"));

        for (List<MapPoint> mapPointList : list) {
            rectOptions.addHole(convertToLatLng(mapPointList));
        }

        return new MapPolygonImpl(map.addPolygon(rectOptions));
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
        if (animate) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(center.getLatitude(), center.getLongitude()), zoomLevel), ANIMATION_TIME, null);
        } else
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(center.getLatitude(), center.getLongitude()), zoomLevel));
    }

    @Override
    public void scrollBy(int x, int y, boolean animate) {
        if (animate) {
            map.animateCamera(CameraUpdateFactory.scrollBy(x, y));
        } else {
            map.moveCamera(CameraUpdateFactory.scrollBy(x, y));
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
                final LatLngBounds bounds = latlngBuilder.build();

                int zoomLevel = getBoundsZoomLevel(bounds.northeast, bounds.southwest, mapFragment.getView().getWidth(), mapFragment.getView().getHeight());

                map.setPadding(0, paddingInDp, 0, viewSize);
                if (Math.abs(bounds.northeast.latitude - bounds.southwest.latitude) > Math.abs(bounds.northeast.longitude - bounds.southwest.longitude)) {
                    // 세로
                    zoomLevel -= (2 + (viewSize / 100));
                } else {
                    // 가로
                    zoomLevel -= (1 + (paddingInDp / 100));
                }

                int finalZoomLevel = zoomLevel;
                Runnable r = () -> {
                    if (animate) {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), finalZoomLevel));
                    } else {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), finalZoomLevel));
                    }

                    if (zoomListener != null)
                        zoomListener.run();

                    map.setPadding(0, paddingInDp, 0, viewSize);
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
        PolylineOptions options = new PolylineOptions();
        options.color(color);
        options.width(width);
        options.startCap(new RoundCap());
        options.endCap(new RoundCap());

        options.addAll(convertToLatLng(points));
        options.zIndex(2);
        Polyline polyLine = map.addPolyline(options);
        return new MapPolyLineImpl(polyLine);
    }

    @Override
    public void setOnClickListener(final MapCallback<MapPoint> listener) {
        userRegisteredOnClickListener = listener;
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public MapCircle addCircle(final MapPoint point, final float radiusInMeter, final int fillColor, final float strokeWidth, final int strokeColor) {
        CircleOptions options = new CircleOptions()
                .center(new LatLng(point.getLatitude(), point.getLongitude()))
                .fillColor(fillColor)
                .radius(radiusInMeter)
                .strokeWidth(strokeWidth)
                .zIndex(100)
                .strokeColor(strokeColor);

        Circle circle = map.addCircle(options);
        return new MapCircleImpl(circle);
    }


    @Override
//    public MapMarker addMarker(final MapPoint result, final MapMarkerIcon icon, float rotation, float zIndex, AnchorType anchorType, MapInfoWindow mapInfoWindow) {
    public MapMarker addMarker(MapMarkerOptions options) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(options.getMapPoint().getLatitude(), options.getMapPoint().getLongitude()));

        if (options.getMapMarkerIcon() != null) {
            View markerView = options.getMapMarkerIcon().getMarkerView();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapUtil.createDrawableFromView(mapFragment.getContext(), markerView)));
        }

        markerOptions.anchor(options.getAnchorX(), options.getAnchorY());

        if (options.getRotation() != 0) {
            markerOptions.rotation(options.getRotation());
        }

        markerOptions.zIndex(options.getzIndex());

        Marker marker = map.addMarker(markerOptions);

        if (options.getMapInfoWindow() != null) {
            if (!mapMarkerInfoWindow.containsKey(marker)) {
                mapMarkerInfoWindow.put(marker, options.getMapInfoWindow());
            }

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    MapInfoWindow mapInfoWindow1 = mapMarkerInfoWindow.get(marker);

                    if (mapInfoWindow1 != null) {
                        if (mapInfoWindow1.getRunnable() != null)
                            mapInfoWindow1.getRunnable().run();
                    }

                    return false;
                }
            });
        }

        return new MapMarkerImpl(marker);
    }

/*    private void setInfoWindow(MapInfoWindow mapInfoWindow) {
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                MapInfoWindow mapInfoWindow1 = mapMarkerInfoWindow.get(marker);

                if (mapInfoWindow1 != null) {
                    View view = mapInfoWindow1.getView();
                    if (view == null) {
                        view = LayoutInflater.from(mapFragment.getContext()).inflate(R.layout.view_map_info_window_go_to_navigation, null);

                        //                    TextView txtName = Util.findView(view, R.id.txtName);
                        TextView txtAddress = view.findViewById(R.id.txtAddress);

//                    txtName.setText(mapInfoWindow.getName());
                        String address;
                        if (mapInfoWindow.getAddress().length() > 20) {
                            address = mapInfoWindow.getAddress().substring(0, 20);
                            address += "...";
                        } else {
                            address = mapInfoWindow.getAddress();
                        }

                        txtAddress.setText(address);
                    }
                    return view;
                }

                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (mapMarkerInfoWindow.get(marker).getRunnable() != null) {
                    mapMarkerInfoWindow.get(marker).getRunnable().run();
                }
            }
        });
    }*/

    @Override
    public MapPoint getCenter() {
        CameraPosition positions = map.getCameraPosition();
        return new MapPoint(positions.target.latitude, positions.target.longitude);
    }


    @Override
    public void setCamera(MapPoint center, float bearing, float tilt, float zoomLevel, boolean animate) {
        if (animate)
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
                    .build()));
    }

    @Override
    public float getCameraRotation() {
        return map.getCameraPosition().bearing;
    }

    @Override
    public float getZoomLevel() {
        CameraPosition positions = map.getCameraPosition();
        return positions.zoom;
    }

    @Override
    public void setZoomLevel(final float level) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(map.getCameraPosition().target, level));
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
