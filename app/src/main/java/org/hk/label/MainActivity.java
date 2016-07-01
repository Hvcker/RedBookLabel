package org.hk.label;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.hk.label.view.ImageLabelView;
import org.hk.label.view.LabelView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LabelView.OnClickTextListener {

    private ImageLabelView mIlvPic;

    private TextView mTvTip;

    private TextView mTvTip2;

    private Point mCurrentPoint;

    private LabelView mEditLableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIlvPic = (ImageLabelView) findViewById(R.id.ilv_pic);
        mIlvPic.setSourceBmp(BitmapFactory.decodeResource(getResources(), R.mipmap.test));
        mTvTip = (TextView) findViewById(R.id.tv_tip);
        mTvTip2 = (TextView) findViewById(R.id.tv_tip2);
        mIlvPic.setOnTouchPicListener(new ImageLabelView.onTouchPicListener() {
            @Override
            public void onTouchPic(int touchX, int touchY) {
                mCurrentPoint = new Point(touchX, touchY);
                mTvTip.setText("添加:" + mCurrentPoint.toString());
            }
        });
    }

    public void addLabelOfOne(View view) {
        LabelView.LabelInfo info = new LabelView.LabelInfo();
        if (mEditLableView != null) {
            mIlvPic.removeLableView(mEditLableView);
            info.style = mEditLableView.getLableInfo().style;
            mEditLableView = null;
        }
        info.bizhong = "利文斯顿";
        info.price = "23";
        mIlvPic.addLabelView(mCurrentPoint, info, MainActivity.this);
    }

    public void addLabelOfTwo(View view) {
        LabelView.LabelInfo info = new LabelView.LabelInfo();
        if (mEditLableView != null) {
            mIlvPic.removeLableView(mEditLableView);
            info.style = mEditLableView.getLableInfo().style;
            mEditLableView = null;
        }
        info.bizhong = "利文斯顿";
        info.price = "23";
        info.brand = "哈根达斯";
        info.name = "提拉米苏";
        mIlvPic.addLabelView(mCurrentPoint, info, MainActivity.this);
    }

    public void addLabelOfThree(View view) {

        LabelView.LabelInfo info = new LabelView.LabelInfo();
        if (mEditLableView != null) {
            mIlvPic.removeLableView(mEditLableView);
            info.style = mEditLableView.getLableInfo().style;
            mEditLableView = null;
        }
        info.bizhong = "利文斯顿";
        info.price = "23";
        info.country = "中国";
        info.place = "哈哈哈";
        info.brand = "哈根达斯";
        info.name = "提拉米苏";
        mIlvPic.addLabelView(mCurrentPoint, info, MainActivity.this);
    }

    public void submit(View view) {
        List<LabelView> labelViews = mIlvPic.getLabelViews();
        int size = labelViews.size();
        StringBuilder lableSb = null;
        if (size > 0) {
            lableSb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                String str = labelViews.get(i).getLableInfo().toString();
                lableSb.append(str);
                if (i != size - 1) {
                    lableSb.append(";");
                }
            }
            Intent intent = new Intent(MainActivity.this, ShowBackActivity.class);
            intent.putExtra(ShowBackActivity.EXTRA_LABLE_INFO, lableSb.toString());
            startActivity(intent);
        }
    }

    @Override
    public void clickText(LabelView lableView) {
        this.mEditLableView = lableView;
        mCurrentPoint = lableView.getTouchPoint();
        mTvTip2.setText("修改:" + mCurrentPoint.toString());
    }
}
