package org.hk.label.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hvcker on 2016/6/20 0020.
 * Good good study,day day up!
 */
public class LabelView extends View {


    public static final int LEFT_LINE = 1;
    /**
     * 线在点的右边
     */
    public static final int RIGHT_LINE = 2;
    /**
     * 折线在点的左边
     */
    public static final int LEFT_BROKEN_LINE = 3;
    /**
     * 折线在点的左右
     */
    public static final int RIGHT_BROKEN_LINE = 4;

    /**
     * 字体大小，单位：sp
     */
    private static final int TEXT_SIZE_SP = 12;

    /**
     * 线与文字之间的间距，单位：dp
     */
    private static final int TEXT_BOTTOM_LINE_PADDING_DP = 6;

    /**
     * 线与文字之间的间距，单位：dp
     */
    private static final int TEXT_TOP_LINE_PADDING_DP = 8;

    /**
     * 线的粗细，单位：px
     */
    private static final int LINE_SIZE = 4;

    /**
     * 点的半径
     */
    private static final int DOT_RADIUS = 16;

    /**
     * 点外面那个透明的圆环的半径
     */
    private static final int DOT_STROKE = 12;

    /**
     * 文字的大小，px
     */
    private int mTextSize;

    /**
     * 线与文字之间的间距，px
     */
    private int mTextPaddingBottom;

    /**
     * 线与文字之间的间距，px
     */
    private int mTextPaddingTop;
    /**
     * 文字两边的间距，px
     */
    private int mTextSidePadding;

    /**
     * 标签的信息
     */
    private LabelInfo mLabelInfo;

    /**
     * 父容器点击的点
     */
    private Point mTouchPoint;

    /**
     * 点击的点在控件本身的哪个位置
     */
    private Point mPointInView = new Point();

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 点击变换样式在View中的区域
     */
    private RectF mChageStyleRect = new RectF();

    /**
     * 文字区域
     */
    private Rect[] mInfoBounds;


    private ImageLabelView mParent;


    private boolean mCanMove = true;

    private boolean mCanChangeStyle = true;

    /**
     * 不支持在xml文件中声明，只能手动添加到父控件
     *
     * @param context
     * @param labelInfo
     */
    public LabelView(Context context, LabelInfo labelInfo) {
        super(context);
        this.mLabelInfo = labelInfo;

        setWillNotDraw(true);
        setClickable(true);
        setVisibility(INVISIBLE);

        mTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP,
                getResources().getDisplayMetrics());
        mTextPaddingBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_BOTTOM_LINE_PADDING_DP,
                getResources().getDisplayMetrics());
        mTextPaddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_TOP_LINE_PADDING_DP,
                getResources().getDisplayMetrics());
        mTextSidePadding = mTextPaddingBottom * 2;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(mTextSize);

        mInfoBounds = this.mLabelInfo.getInfoBounds(mPaint);
    }

    public void setLableInfo(LabelInfo lableInfo) {
        mLabelInfo = lableInfo;

    }

    public LabelInfo getLableInfo() {
        RectF imageRectf = mParent.getImageEdge();
        mLabelInfo.cX = (mTouchPoint.x - imageRectf.left) / imageRectf.width();
        mLabelInfo.cY = (mTouchPoint.y - imageRectf.top) / imageRectf.height();
        return mLabelInfo;
    }

    public void setTouchPoint(Point point) {
        this.mTouchPoint = point;
        Log.d("viewload", "init mTouchPoint:" + mTouchPoint);
        setWillNotDraw(false);
        initLocation();
    }

    public void setMove(boolean move){
        this.mCanMove = move;
    }

    public void setChange(boolean change){
        this.mCanChangeStyle = change;
    }

    public Point getTouchPoint() {
        return this.mTouchPoint;
    }


    private void initLocation() {
        post(new Runnable() {
            @Override
            public void run() {
                final int marginX = mTouchPoint.x - mPointInView.x;
                final int marginY = mTouchPoint.y - mPointInView.y;
                moveLocation(marginX, marginY);
                setVisibility(VISIBLE);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("viewload", "onMeasure");
        /**
         *计算测量宽度
         */
        final int strNum = mInfoBounds.length;

        //圆形宽度
        final int dotSize = DOT_RADIUS + DOT_STROKE;
        //padding
        final int paddingX = getPaddingLeft() + getPaddingRight();
        //文字两边padding
        final int textBouthSidePadding = mTextSidePadding * 2;

        int[] intArr = new int[strNum];
        for (int i = 0; i < strNum; i++) {
            intArr[i] = mInfoBounds[i].width();
        }
        Arrays.sort(intArr);
        final int maxTextWidth = intArr[strNum - 1];

        //宽度结果
        final int width = dotSize + paddingX + textBouthSidePadding + maxTextWidth;

        /**
         * 计算测量高度
         */
        //padding
        final int paddingY = getPaddingTop() + getPaddingBottom();
        //文字区域的高度
        int textTotalHeight = 0;

        int textEachHeight = mInfoBounds[0].height() +
                //线的宽度
                LINE_SIZE +
                //线与文字之间的间距
                mTextPaddingBottom;

        int height = paddingY;
        if (strNum > 1) {
            textTotalHeight = textEachHeight * 3;
            textTotalHeight += mTextPaddingTop * 2;
            height += textTotalHeight;
        } else {
            height += textEachHeight +
                    //点的半径
                    DOT_RADIUS +
                    //圆环的半径
                    DOT_STROKE;
        }

        switch (mLabelInfo.style) {
            case LEFT_LINE:
                mPointInView.x = width - DOT_STROKE - DOT_RADIUS - getPaddingRight();
                if (strNum > 1) {
                    mPointInView.y = height - getPaddingBottom() - textEachHeight - mTextPaddingTop;
                } else {
                    mPointInView.y = height - getPaddingBottom() - DOT_STROKE - DOT_RADIUS;
                }
                break;

            case RIGHT_LINE:
                mPointInView.x = DOT_STROKE + DOT_RADIUS + getPaddingRight();
                if (strNum > 1) {
                    mPointInView.y = height - getPaddingBottom() - textEachHeight - mTextPaddingTop;
                } else {
                    mPointInView.y = height - getPaddingBottom() - DOT_STROKE - DOT_RADIUS;
                }
                break;
        }

        //设置测量结果
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("viewload", "onDraw");
        int strNum = mInfoBounds.length;
        switch (mLabelInfo.style) {
            case LEFT_LINE: {
                //画圆圈
                final float cx = mPointInView.x;
                final float cy = mPointInView.y;

                mPaint.setColor(Color.parseColor("#3C000000"));
                canvas.drawCircle(cx, cy, DOT_RADIUS + DOT_STROKE, mPaint);
                //画圆圈
                mPaint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawCircle(cx, cy, DOT_RADIUS, mPaint);

                final float radius = DOT_RADIUS + DOT_STROKE;
                mChageStyleRect.left = cx - radius;
                mChageStyleRect.right = cx + radius;
                mChageStyleRect.top = cy - radius;
                mChageStyleRect.bottom = cy + radius;

                //画头部的横线写头部的字
                final float lineTopEndX = mPointInView.x;
                final float lineTopStartX = lineTopEndX - 2 * mTextSidePadding - mInfoBounds[0].width() - getPaddingLeft();
                final float lineTopStartY = mInfoBounds[0].height() + getPaddingTop() + mTextPaddingBottom + LINE_SIZE;
                final float lineTopEndY = lineTopStartY;
                mPaint.setStrokeWidth(LINE_SIZE);
                canvas.drawLine(lineTopStartX, lineTopStartY, lineTopEndX, lineTopEndY, mPaint);

                final float textTopStartX = lineTopStartX + mTextSidePadding;
                final float textTopEndY = lineTopStartY - mTextPaddingBottom;
                canvas.drawText(mLabelInfo.getInfos().get(0), textTopStartX, textTopEndY, mPaint);

                if (strNum > 1) {
                    //画竖线
                    final float lineRightStartX = lineTopEndX;
                    final float lineRightStartY = lineTopEndY;
                    final float lineRightEndX = lineRightStartX;
                    final float lineRightEndY = getHeight() - getPaddingBottom();
                    canvas.drawLine(lineRightStartX, lineRightStartY, lineRightEndX, lineRightEndY, mPaint);

                    String bottomText;
                    Rect bottomRec;
                    if (strNum > 2) {//画中间的线写中间的字
                        final String midText = mLabelInfo.getInfos().get(1);

                        final float lineMidEndX = lineTopEndX;
                        final float lineMidStartX = lineMidEndX - 2 * mTextSidePadding - mInfoBounds[1].width()
                                - getPaddingLeft();
                        final float lineMidStartY = mPointInView.y;
                        final float lineMidEndY = lineMidStartY;
                        canvas.drawLine(lineMidStartX, lineMidStartY, lineMidEndX, lineMidEndY, mPaint);

                        final float textMidStartX = lineMidStartX + mTextSidePadding;
                        final float textMidEndY = lineMidStartY - mTextPaddingBottom;
                        canvas.drawText(midText, textMidStartX, textMidEndY, mPaint);

                        bottomText = mLabelInfo.getInfos().get(2);
                        bottomRec = mInfoBounds[2];
                    } else {
                        bottomRec = mInfoBounds[1];
                        bottomText = mLabelInfo.getInfos().get(1);
                    }
                    //画底部的线写底部的字
                    final float lineBottomEndX = lineTopEndX;
                    final float lineBottomStartX = lineBottomEndX - 2 * mTextSidePadding - bottomRec.width()
                            - getPaddingLeft();
                    final float lineBottomStartY = lineRightEndY;
                    final float lineBottomEndY = lineBottomStartY;
                    canvas.drawLine(lineBottomStartX, lineBottomStartY, lineBottomEndX, lineBottomEndY, mPaint);

                    final float textBottomStartX = lineBottomStartX + mTextSidePadding;
                    final float textBottomEndY = lineBottomStartY - mTextPaddingBottom;
                    canvas.drawText(bottomText, textBottomStartX, textBottomEndY, mPaint);
                }

                break;
            }
            case RIGHT_LINE: {
                //画圆圈
                final float cx = mPointInView.x;
                final float cy = mPointInView.y;
                mPaint.setColor(Color.parseColor("#3C000000"));
                canvas.drawCircle(cx, cy, DOT_RADIUS + DOT_STROKE, mPaint);
                //画圆圈
                mPaint.setColor(Color.parseColor("#FFFFFF"));
                canvas.drawCircle(cx, cy, DOT_RADIUS, mPaint);

                final float radius = DOT_RADIUS + DOT_STROKE;
                mChageStyleRect.left = cx - radius;
                mChageStyleRect.right = cx + radius;
                mChageStyleRect.top = cy - radius;
                mChageStyleRect.bottom = cy + radius;

                //画头部的线写头部的字
                final float lineStartX = mPointInView.x;
                final float lineStartY = mInfoBounds[0].height() + getPaddingTop() + mTextPaddingBottom + LINE_SIZE;
                final float lineEndX = getWidth();
                final float lineEndY = lineStartY;
                mPaint.setStrokeWidth(LINE_SIZE);
                canvas.drawLine(lineStartX, lineStartY, lineEndX, lineEndY, mPaint);

                //写文字
                final float textStartX = lineStartX + mTextSidePadding;
                final float textEndY = lineStartY - mTextPaddingBottom;
                canvas.drawText(mLabelInfo.getInfos().get(0), textStartX, textEndY, mPaint);

                if (strNum > 1) {
                    //画竖线
                    final float lineLeftStartX = lineStartX;
                    final float lineLeftStartY = lineStartY;
                    final float lineLeftEndX = lineLeftStartX;
                    final float lineLeftEndY = getHeight() - getPaddingBottom();
                    canvas.drawLine(lineLeftStartX, lineLeftStartY, lineLeftEndX, lineLeftEndY, mPaint);

                    String bottomText;
                    Rect bottomRec;
                    if (strNum > 2) {//画中间的线写中间的字
                        final String midText = mLabelInfo.getInfos().get(1);

                        final float lineMidStartX = lineLeftStartX;
                        final float lineMidStartY = mPointInView.y;
                        final float lineMidEndX = lineMidStartX + 2 * mTextSidePadding + mInfoBounds[1].width()
                                + getPaddingLeft();
                        final float lineMidEndY = lineMidStartY;
                        canvas.drawLine(lineMidStartX, lineMidStartY, lineMidEndX, lineMidEndY, mPaint);

                        final float textMidStartX = lineMidStartX + mTextSidePadding;
                        final float textMidEndY = lineMidStartY - mTextPaddingBottom;
                        canvas.drawText(midText, textMidStartX, textMidEndY, mPaint);

                        bottomText = mLabelInfo.getInfos().get(2);
                        bottomRec = mInfoBounds[2];
                    } else {
                        bottomRec = mInfoBounds[1];
                        bottomText = mLabelInfo.getInfos().get(1);
                    }
                    //画底部的线写底部的字
                    final float lineBottomStartX = lineStartX;
                    final float lineBottomStartY = lineLeftEndY;
                    final float lineBottomEndX = lineBottomStartX + 2 * mTextSidePadding + bottomRec.width()
                            + getPaddingLeft();
                    final float lineBottomEndY = lineBottomStartY;
                    canvas.drawLine(lineBottomStartX, lineBottomStartY, lineBottomEndX, lineBottomEndY, mPaint);

                    final float textBottomStartX = lineBottomStartX + mTextSidePadding;
                    final float textBottomEndY = lineBottomStartY - mTextPaddingBottom;
                    canvas.drawText(bottomText, textBottomStartX, textBottomEndY, mPaint);
                }
                break;
            }
            case LEFT_BROKEN_LINE:

                break;

            case RIGHT_BROKEN_LINE:

                break;
        }
    }

    private float downX;

    private float downY;

    private float mRawDownX;

    private float mRawDownY;

    /**
     * margin值的范围
     */
    private RectF mEdge;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float rawX = event.getRawX();
        final float rawY = event.getRawY();

        final float x = event.getX();
        final float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = x;
                downY = y;

                mRawDownX = rawX;
                mRawDownY = rawY;

                if (mCanMove) {
                    if (mFatherHeight == 0) {
                        int[] locations = new int[2];
                        ((View) getParent()).getLocationOnScreen(locations);
                        mFatherHeight = locations[1];
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mCanMove) {
                    moveViewWithFinger(rawX, rawY);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (rawX == mRawDownX && rawY == mRawDownY) {//表示单击
                    if (x > mChageStyleRect.left && x < mChageStyleRect.right
                            && y > mChageStyleRect.top && y < mChageStyleRect.bottom) {
                        if (mCanChangeStyle) {
                            chageStyle();

                        }
                        return true;
                    } else {
                        if (mListener != null) {
                            mListener.clickText(this);
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void chageStyle() {
        if (mLabelInfo.style == LEFT_LINE) {
            mLabelInfo.style = RIGHT_LINE;
            mPointInView.x = getWidth() - mPointInView.x;
        } else {
            mLabelInfo.style = LEFT_LINE;
            mPointInView.x = getWidth() - mPointInView.x;
        }
        final float marginX = mTouchPoint.x - mPointInView.x;
        moveLocation(marginX, mCurrentMarginY);
        invalidate();
    }

    private int mFatherHeight = 0;

    private float mCurrentMarginX;

    private float mCurrentMarginY;

    private void moveViewWithFinger(float rawX, float rawY) {
        float marginLeft = rawX - downX;
        float marginTop = rawY - mFatherHeight - downY;
        moveLocation(marginLeft, marginTop);
    }

    private void moveLocation(float x, float y) {
        if (mEdge == null) {
            if (mParent == null) {
                mParent = (ImageLabelView) getParent();
            }
            mEdge = new RectF(mParent.getImageEdge());
            mEdge.right = mEdge.right - getWidth();
            mEdge.bottom = mEdge.bottom - getHeight();
        }

        if (x < mEdge.left) x = mEdge.left;
        if (x > mEdge.right) x = mEdge.right;

        if (y < mEdge.top) y = mEdge.top;
        if (y > mEdge.bottom) y = mEdge.bottom;

        mTouchPoint.x = (int) (x + mPointInView.x);
        //写一篇文章记住这个错误，妈的
        mTouchPoint.y = (int) (y + mPointInView.y);

        mCurrentMarginX = x;
        mCurrentMarginY = y;

        layout((int) x, (int) y, (int) x + getWidth(), (int) y + getHeight());

        //一定要设置margin，不然从父容器中再添加一个子控件的时候，他会重新layout，这时候是layout(0,0,getWidth(),getHeight())
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.leftMargin = (int) x;
        params.topMargin = (int) y;
    }

    private OnClickTextListener mListener;

    public void setOnClickTextListener(OnClickTextListener listener) {
        mListener = listener;
    }

    public interface OnClickTextListener {
        void clickText(LabelView lableView);
    }

    public static class LabelInfo {

        public float cX;

        public float cY;

        public String brand = "";

        public String name = "";

        public String bizhong = "";

        public String price = "";

        public String country = "";

        public String place = "";

        public String url = " ";

        public int style = LEFT_LINE;

        private List<String> infos;

        private Rect[] infoBounds;

        public List<String> getInfos() {
            if (infos != null) {
                return infos;
            }
            infos = new ArrayList();

            String first = getStr(brand, name);
            if (!TextUtils.isEmpty(first))
                infos.add(first);
            String second = getStr(price, bizhong);
            if (!TextUtils.isEmpty(second))
                infos.add(second);
            String third = getStr(country, place);
            if (!TextUtils.isEmpty(third))
                infos.add(third);
            return infos;
        }

        public Rect[] getInfoBounds(Paint paint) {
            if (infoBounds == null) {
                List<String> infos = getInfos();
                int size = infos.size();
                infoBounds = new Rect[size];
                Rect rect;
                String info;
                for (int i = 0; i < size; i++) {
                    rect = new Rect();
                    info = infos.get(i);
                    paint.getTextBounds(infos.get(i), 0, info.length(), rect);
                    infoBounds[i] = rect;
                }
            }

            return infoBounds;
        }

        private String getStr(String str1, String str2) {
            StringBuffer sb = new StringBuffer();
            sb.append(TextUtils.isEmpty(str1) ? "" : str1);
            sb.append(TextUtils.isEmpty(str2) ? "" : str2);
            return sb.toString();
        }


        /**
         * lable:"{brad:"普拉达"，手包，人民币，1，深圳，万象城，0.5，0.5，1;}"
         *
         * @return
         */
        @Override
        public String toString() {
            return brand + "," + name + "," + bizhong + "," + price + "," + country + "," + place + "," +
                    +cX + "," + cY + "," + style + "," + url;
        }

        public LabelInfo fromString(String labal) {
            String[] labelStr = labal.split(",");
            brand = labelStr[0];
            name = labelStr[1];
            bizhong = labelStr[2];
            price = labelStr[3];
            country = labelStr[4];
            place = labelStr[5];
            cX = Float.parseFloat(labelStr[6]);
            cY = Float.parseFloat(labelStr[7]);
            style = Integer.parseInt(labelStr[8]);
            url = labelStr[9];
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LabelInfo labelInfo = (LabelInfo) o;

            if (!brand.equals(labelInfo.brand)) return false;
            if (!name.equals(labelInfo.name)) return false;
            if (!bizhong.equals(labelInfo.bizhong)) return false;
            if (!price.equals(labelInfo.price)) return false;
            if (!country.equals(labelInfo.country)) return false;
            return place.equals(labelInfo.place);

        }

        @Override
        public int hashCode() {
            int result = brand.hashCode();
            result = 31 * result + name.hashCode();
            result = 31 * result + bizhong.hashCode();
            result = 31 * result + price.hashCode();
            result = 31 * result + country.hashCode();
            result = 31 * result + place.hashCode();
            return result;
        }

        @Override
        public Object clone(){
            LabelInfo labelInfo = new LabelInfo();
            labelInfo.name = this.name;
            labelInfo.brand = this.brand;
            labelInfo.bizhong = this.bizhong;
            labelInfo.price = this.price;
            labelInfo.country = this.country;
            labelInfo.place = this.place;
            return labelInfo;
        }
    }
}
