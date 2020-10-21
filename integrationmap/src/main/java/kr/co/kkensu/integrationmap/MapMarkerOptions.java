package kr.co.kkensu.integrationmap;

import android.content.Context;

public class MapMarkerOptions {
    private MapPoint mapPoint = null;
    private MapMarkerIcon mapMarkerIcon = null;
    private float anchorX = 0.5f;
    private float anchorY = 1f;
    private float rotation = 0f;
    private int zIndex = 0;
    private MapMarkerClickListener mapMarkerClickListener = null;
    private MapInfoWindow mapInfoWindow = null;
    private MapInfoWindowClickListener mapInfoWindowClickListener = null;

    public MapMarkerOptions() {

    }

    public MapPoint getMapPoint() {
        return mapPoint;
    }

    public void setMapPoint(MapPoint mapPoint) {
        this.mapPoint = mapPoint;
    }

    public MapMarkerIcon getMapMarkerIcon() {
        return mapMarkerIcon;
    }

    public void setMapMarkerIcon(MapMarkerIcon mapMarkerIcon) {
        this.mapMarkerIcon = mapMarkerIcon;
    }

    public float getAnchorX() {
        return anchorX;
    }

    public void setAnchorX(float anchorX) {
        this.anchorX = anchorX;
    }

    public float getAnchorY() {
        return anchorY;
    }

    public void setAnchorY(float anchorY) {
        this.anchorY = anchorY;
    }

    public void setMarkerAnchor(float anchorX, float anchorY) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public int getzIndex() {
        return zIndex;
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public MapMarkerClickListener getMapMarkerClickListener() {
        return mapMarkerClickListener;
    }

    public void setMapMarkerClickListener(MapMarkerClickListener mapMarkerClickListener) {
        this.mapMarkerClickListener = mapMarkerClickListener;
    }

    public MapInfoWindow getMapInfoWindow() {
        return mapInfoWindow;
    }

    public void setMapInfoWindow(MapInfoWindow mapInfoWindow) {
        this.mapInfoWindow = mapInfoWindow;
    }

    public MapInfoWindowClickListener getMapInfoWindowClickListener() {
        return mapInfoWindowClickListener;
    }

    public void setMapInfoWindowClickListener(MapInfoWindowClickListener mapInfoWindowClickListener) {
        this.mapInfoWindowClickListener = mapInfoWindowClickListener;
    }
}
