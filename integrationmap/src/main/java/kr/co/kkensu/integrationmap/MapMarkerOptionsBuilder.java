package kr.co.kkensu.integrationmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.StringRes;

import java.io.Serializable;
import java.util.List;

public abstract class MapMarkerOptionsBuilder<T extends MapMarkerOptionsBuilder> {

    private Context context;

    private CharSequence title;

    /**
     * 마커의 좌표
     **/
    private MapPoint mapPoint = null;

    /**
     * 마커의 어느 위치가 지도의 좌표를 표시할지
     **/
    private float anchorX = 0.5f;
    private float anchorY = 0.5f;


    public MapMarkerOptionsBuilder(Context context) {
        this.context = context;
    }

    protected void startImagePager() {
//        if (imageList == null) {
//            throw new IllegalArgumentException("You must setImageList() on ImagePager");
//        } else if (ObjectUtils.isEmpty(imageList)) {
//            throw new IllegalArgumentException("You must setPermissions() on ImagePager");
//        }

//        Intent intent = new Intent(context, ImagePagerActivity.class);
//        intent.putExtra(ImagePagerActivity.ARG_IMAGE_LIST, (Serializable) imageList);
//        intent.putExtra(ImagePagerActivity.ARG_THUMBNAIL_LIST, (Serializable) thumbnailList);
//
//        intent.putExtra(ImagePagerActivity.ARG_TITLE, title);
//        intent.putExtra(ImagePagerActivity.ARG_POSITION, position);
//        intent.putExtra(ImagePagerActivity.ARG_IS_SHOW_POSITION, isShowPosition);
//        intent.putExtra(ImagePagerActivity.ARG_IS_SHOW_BOTTOM_VIEW, isShowBottomView);
//        intent.putExtra(ImagePagerActivity.ARG_CLOSE_TYPE, closeType.getValue());
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
//
//        context.startActivity(intent);
    }

    @SuppressLint("ResourceType")
    private CharSequence getText(@StringRes int stringRes) {
        if (stringRes <= 0) {
            throw new IllegalArgumentException("Invalid String resource id");
        }
        return context.getText(stringRes);
    }

    public T setTitle(@StringRes int stringRes) {
        return setTitle(getText(stringRes));
    }

    public T setTitle(CharSequence title) {
        this.title = title;
        return (T) this;
    }

    public T setMapPoint(MapPoint mapPoint) {
        this.mapPoint = mapPoint;
        return (T) this;
    }

    public T setAnchor(float anchorX, float anchorY) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        return (T) this;
    }

    public MapPoint getMapPoint() {
        return this.mapPoint;
    }

    public float getAnchorX() {
        return anchorX;
    }

    public float getAnchorY() {
        return anchorY;
    }
}
