package kr.co.kkensu.integrationmap;

/**
 * Created by johyunchol on 2017. 11. 6..
 */

public class MapMarkerIcon {
    MapType mapType;
    Object markerName;
    MarkerSize markerSize;

    public MapMarkerIcon(MapType mapType, Object markerName, MarkerSize markerSize) {
        this.mapType = mapType;
        this.markerName = markerName;
        this.markerSize = markerSize;
    }

    public MapMarkerIcon(MapType mapType, Object markerName) {
        this.mapType = mapType;
        this.markerName = markerName;
    }

    public MapType getMapType() {
        return mapType;
    }

    public void setMapType(MapType mapType) {
        this.mapType = mapType;
    }

    public void setMarkerName(Object markerName) {
        this.markerName = markerName;
    }

    public Object getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public MarkerSize getMarkerSize() {
        return markerSize;
    }

    public void setMarkerSize(MarkerSize markerSize) {
        this.markerSize = markerSize;
    }

    public static class MarkerSize {
        private int width;
        private int height;

        public MarkerSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public enum MapType {
        TYPE_VIEW(1),
        TYPE_RESOURCE(2);

        public int value;

        MapType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static MapType fromValue(int value) {
            for (MapType state : MapType.values()) {
                if (state.value == value) {
                    return state;
                }
            }
            return TYPE_RESOURCE;
        }
    }
}
