package kr.co.kkensu.integrationmap;

import java.util.List;

/**
 * 지도상에 폴리라인을 관리하기 위한 클래스
 */
public interface MapPolyLine {
    List<MapPoint> getPoints();

    void setPoints(List<MapPoint> points);

    void remove();
}
