package kr.co.kkensu.integrationmap.googlemap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import kr.co.kkensu.integrationmap.MapApi;
import kr.co.kkensu.integrationmap.MapFragment;

public class MapFragmentImpl extends SupportMapFragment implements MapFragment {

    private View.OnTouchListener listener;
    private TouchableWrapper touchableWrapper;
    private ScrollView scrollView;

    @Override
    public void getMapApi(final MapApi.MapCallback<MapApi> callback) {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                callback.handle(new MapApiImpl(googleMap, MapFragmentImpl.this));
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