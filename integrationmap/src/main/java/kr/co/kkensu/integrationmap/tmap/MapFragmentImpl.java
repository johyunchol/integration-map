package kr.co.kkensu.integrationmap.tmap;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.skt.Tmap.TMapView;

import kr.co.kkensu.integrationmap.MapApi;
import kr.co.kkensu.integrationmap.MapFragment;
import kr.co.kkensu.integrationmap.R;

public class MapFragmentImpl extends Fragment implements MapFragment {

    private Context context;
    private String apiKey;
    private TMapView tMapView;
    private View.OnTouchListener listener;
    private TouchableWrapper touchableWrapper;
    private ScrollView scrollView;

    public MapFragmentImpl(Context context, String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
    }

    @Override
    public void getMapApi(final MapApi.MapCallback<MapApi> callback) {
        tMapView = new TMapView(context);
        callback.handle(new MapApiImpl(tMapView, MapFragmentImpl.this, apiKey));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tmap_view, container, false);
        ViewGroup viewGroup = view.findViewById(R.id.tmapView);
        viewGroup.addView(tMapView);

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