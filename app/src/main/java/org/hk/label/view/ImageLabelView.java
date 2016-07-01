package org.hk.label.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hvcker on 2016/6/20 0020.
 * Good good study,day day up!
 */
public class ImageLabelView extends FrameLayout {

    private Paint mPaint;

    private Context mContext;

    private Bitmap mSourceBmp;

    private boolean mCanAdd = true;


    private boolean mShowLabel = true;

    public List<LabelView> getLabelViews() {
        return mLabelViews;
    }

    public boolean isShowLabel() {
        return mShowLabel;
    }

    //所有的标签控件
    private List<LabelView> mLabelViews;

    //图片的边缘
    private RectF mImageEdge;

    public ImageLabelView(Context context) {
        this(context, null);
    }

    public ImageLabelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setWillNotDraw(false);
        setClickable(true);
        mContext = context;
        mPaint = new Paint();
        mLabelViews = new ArrayList<>();
        Log.d("viewload", "ImageLabelView2");
    }

    public void setCanAdd(boolean add) {
        this.mCanAdd = add;
        if (!add) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mShowLabel = !mShowLabel;
                    int visible = mShowLabel ? VISIBLE : GONE;
                    for (LabelView labelView : mLabelViews) {
                        labelView.setVisibility(visible);
                    }
                }
            });
        }
    }

    public RectF getImageEdge() {
        return mImageEdge;
    }

    public Bitmap getSourceBmp() {
        return mSourceBmp;
    }

    public void setSourceBmp(Bitmap Bmp) {
        this.mSourceBmp = Bmp;
        post(new Runnable() {
            @Override
            public void run() {
                fitImageView();
                initEdge();
                postInvalidate();
            }
        });
    }

    private void initEdge() {
        if (mImageEdge == null) {
            mImageEdge = new RectF(0, 0, getWidth(), getHeight());
        }
        Log.d("hvcker", "View:" + getWidth() + "," + getHeight());
        Log.d("hvcker", "btimap:" + mSourceBmp.getWidth() + "," + mSourceBmp.getHeight());
        if (mSourceBmp.getWidth() / getWidth() > mSourceBmp.getHeight() / getHeight()) {
            int offsetY = (getHeight() - mSourceBmp.getHeight()) / 2;
            mImageEdge.left = 0;
            mImageEdge.top = offsetY;
            mImageEdge.right = getWidth();
            mImageEdge.bottom = getHeight() - offsetY;
        } else {
            int offsetX = (getWidth() - mSourceBmp.getWidth()) / 2;
            mImageEdge.left = offsetX;
            mImageEdge.top = 0;
            mImageEdge.right = getWidth() - offsetX;
            mImageEdge.bottom = getHeight();
        }
    }

    private void fitImageView() {
        if (mSourceBmp != null) {
            float scale = Math.min((float) getHeight() / (float) mSourceBmp.getHeight(),
                    (float) getWidth() / (float) mSourceBmp.getWidth());
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap newBm = Bitmap.createBitmap(mSourceBmp, 0, 0, mSourceBmp.getWidth(),
                    mSourceBmp.getHeight(), matrix, true);
            //mSourceBmp.recycle();
            mSourceBmp = newBm;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mSourceBmp != null && mImageEdge != null) {
            canvas.drawColor(Color.parseColor("#000000"));
            canvas.drawBitmap(mSourceBmp, mImageEdge.left, mImageEdge.top, mPaint);
        }
        Log.d("viewload", "onDraw");
    }

    private float mDownX;

    private float mDownY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCanAdd) {
            final float x = event.getX();
            final float y = event.getY();
            Log.d("hvcker", "=====parent========" + event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = x;
                    mDownY = y;
                    break;

                case MotionEvent.ACTION_MOVE:

                    break;

                case MotionEvent.ACTION_UP:
                    if (mImageEdge != null) {
                        if (x > mImageEdge.left && x < mImageEdge.right &&
                                y < mImageEdge.bottom && y > mImageEdge.top)
                            if (Math.abs(x - mDownX) < 5 && Math.abs(y - mDownY) < 5) {
                                if (mTouchPicListener != null) {
                                    mTouchPicListener.onTouchPic((int) x, (int) y);
                                }
                            }
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    public void addLabelView(Point p, LabelView.LabelInfo info, LabelView.OnClickTextListener listener) {
        addLableViewByMoveAndChange(p, info, true, true, listener);
    }

    private void addLableViewByMoveAndChange(Point p, LabelView.LabelInfo info, boolean move,
                                             boolean change, LabelView.OnClickTextListener listener) {
        int size = info.getInfos().size();
        if (size > 0) {
            LabelView labelView = new LabelView(mContext, info);
            labelView.setOnClickTextListener(listener);
            labelView.setPadding(6, 6, 6, 6);
            addView(labelView);
            labelView.setTouchPoint(p);
            labelView.setMove(move);
            labelView.setChange(change);
            mLabelViews.add(labelView);
        }
    }

    public void addLabelViewsWithNoMoveAndChange(List<LabelView.LabelInfo> labelInfos) {
        Point p;
        for (LabelView.LabelInfo info : labelInfos) {
            int x = (int) (mImageEdge.width() * info.cX + mImageEdge.left);
            int y = (int) (mImageEdge.height() * info.cY + mImageEdge.top);
            p = new Point(x, y);
            addLableViewByMoveAndChange(p, info, false, false, null);
        }
    }

    public void removeLableView(LabelView labelView) {
        removeView(labelView);
        mLabelViews.remove(labelView);
    }

    private onTouchPicListener mTouchPicListener;

    public void setOnTouchPicListener(onTouchPicListener listener) {
        mTouchPicListener = listener;
    }

    public interface onTouchPicListener {
        void onTouchPic(int touchX, int touchY);
    }
}
