package kr.co.kkensu.integrationmap;

import java.util.List;

/**
 * 지도상에 폴리곤을 관리하기 위한 클래스
 */
public interface MapPolygon {
    List<MapPoint> getPoints();

    void setPoints(List<MapPoint> points);

    int getStrokeColor();

    void setStrokeColor(int color);

    int getFillColor();

    void setFillColor(int color);

    void remove();
}
