package kr.co.kkensu.integrationmap.navermap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import kr.co.kkensu.integrationmap.MapApi;
import kr.co.kkensu.integrationmap.MapFragment;

//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;

public class MapFragmentImpl extends com.naver.maps.map.MapFragment implements MapFragment {

    private View.OnTouchListener listener;
    private TouchableWrapper touchableWrapper;
    private ScrollView scrollView;

    @Override
    public void getMapApi(final MapApi.MapCallback<MapApi> callback) {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull NaverMap naverMap) {
                callback.handle(new MapApiImpl(naverMap, MapFragmentImpl.this));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        touchableWrapper = new TouchableWrapper(getActivity());
        touchableWrapper.addView(view);
        return touchableWrapper;
    }

    public void enableDragInScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    public void setTouchListener(View.OnTouchListener listener) {
        this.listener = listener;
    }

    class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            if (listener != null)
                listener.onTouch(touchableWrapper, event);

            if (scrollView != null) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
            }
            return super.dispatchTouchEvent(event);
        }
    }
}