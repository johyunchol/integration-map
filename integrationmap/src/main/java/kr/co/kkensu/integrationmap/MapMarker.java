package kr.co.kkensu.integrationmap;

/**
 * 지도상에 마커을 관리하기 위한 클래스
 */
public interface MapMarker {
    void remove();

    void setPoint(MapPoint point);

    void setAngle(float rotation);
}
