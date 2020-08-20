package kr.co.kkensu.integrationmap;

import android.content.Context;

public class MapMarkerOptions {
    public static final String TAG = MapMarkerOptions.class.getSimpleName();

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static class Builder extends MapMarkerOptionsBuilder<Builder> {

        private Builder(Context context) {
            super(context);
        }

        public void start() {
            startImagePager();
        }

    }
}
