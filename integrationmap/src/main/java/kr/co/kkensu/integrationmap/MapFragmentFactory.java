package kr.co.kkensu.integrationmap;

import androidx.fragment.app.Fragment;

/**
 * 지도 종류에 따른 MapFragment 반환
 */
public class MapFragmentFactory {
    public static Fragment create(MapType mapType) {
        Fragment fragment = null;
        switch (mapType) {
            case KAKAO_MAP:
//                fragment = new com.buxikorea.buxi.map.googlemapweb.MapFragmentImpl();
                fragment = new kr.co.kkensu.integrationmap.googlemap.MapFragmentImpl();
                break;
            case NAVER_MAP:
                fragment = new kr.co.kkensu.integrationmap.navermap.MapFragmentImpl();
                break;
            default:
            case GOOGLE_MAP:
                fragment = new kr.co.kkensu.integrationmap.googlemap.MapFragmentImpl();
                break;
        }
        return fragment;
    }
}