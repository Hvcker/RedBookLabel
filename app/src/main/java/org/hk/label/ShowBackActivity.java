package org.hk.label;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import org.hk.label.view.ImageLabelView;
import org.hk.label.view.LabelView;

import java.util.ArrayList;
import java.util.List;

public class ShowBackActivity extends AppCompatActivity {

    public static final String EXTRA_LABLE_INFO = "com.hk.label." + "EXTRA_LABLE_INFO";

    private ImageLabelView mIlvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_back);
        mIlvShow = (ImageLabelView) findViewById(R.id.ilv_show);
        mIlvShow.setSourceBmp(BitmapFactory.decodeResource(getResources(), R.mipmap.test));
        mIlvShow.setCanAdd(false);
        mIlvShow.post(new Runnable() {
            @Override
            public void run() {
                showData();
                Snackbar.make(mIlvShow, "轻触非标签位置隐藏标签", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void showData() {
        String[] labelStr = getIntent().getStringExtra(EXTRA_LABLE_INFO).split(";");
        int length = labelStr.length;
        if (length > 0) {
            List<LabelView.LabelInfo> labelInfos = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                labelInfos.add(new LabelView.LabelInfo().fromString(labelStr[i]));
            }
            mIlvShow.addLabelViewsWithNoMoveAndChange(labelInfos);
        }
    }
}
