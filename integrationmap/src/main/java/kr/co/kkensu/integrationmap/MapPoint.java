package kr.co.kkensu.integrationmap;


import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;

/**
 * 지도상에 지점을 관리하기 위한 클래스 (위도, 경도)
 */
public class MapPoint implements Serializable {
    private double latitude;
    private double longitude;

    @JsonCreator
    MapPoint() {

    }

    public MapPoint(MapPoint mapPoint) {
        this(mapPoint.getLatitude(), mapPoint.getLongitude());
    }

    public MapPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapPoint mapPoint = (MapPoint) o;

        return Double.compare(mapPoint.latitude, latitude) == 0 && Double.compare(mapPoint.longitude, longitude) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
