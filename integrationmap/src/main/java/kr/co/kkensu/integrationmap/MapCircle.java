package kr.co.kkensu.integrationmap;

/**
 * 지도상에 원을 관리하기 위한 클래스
 */
public interface MapCircle {
    void remove();

    void setPoint(MapPoint point);

    MapPoint getCenter();
}
