package kr.co.kkensu.integrationmap;


import android.widget.ScrollView;

/**
 * 지도 Fragment (View로 생각하면 됨)의 기본 구조로, 지도를 다루기 위한 MapApi를 얻을 수 있다.
 */
public interface MapFragment {
    void getMapApi(MapApi.MapCallback<MapApi> callback);

    void enableDragInScrollView(ScrollView scrollView);
}
