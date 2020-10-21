package kr.co.kkensu.integrationmap;

import android.content.Context;

import androidx.fragment.app.Fragment;

import kr.co.kkensu.maptest.R;

/**
 * 지도 종류에 따른 MapFragment 반환
 */
public class MapFragmentFactory {
    public static Fragment create(Context context, MapType mapType) {
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

            case TMAP:
                fragment = new kr.co.kkensu.integrationmap.tmap.MapFragmentImpl(context, context.getString(R.string.tmap_api_key));
                break;
        }
        return fragment;
    }
}