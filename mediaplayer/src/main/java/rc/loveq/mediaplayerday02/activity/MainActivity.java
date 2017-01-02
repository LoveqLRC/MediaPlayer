package rc.loveq.mediaplayerday02.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rc.loveq.mediaplayerday02.R;
import rc.loveq.mediaplayerday02.adapter.MainAdapter;
import rc.loveq.mediaplayerday02.fragment.AudioFragment;
import rc.loveq.mediaplayerday02.fragment.VideoFragment;
import rc.loveq.mediaplayerday02.utils.Utils;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    @BindView(R.id.tv_video)
    TextView mTvVideo;
    @BindView(R.id.tv_audio)
    TextView mTvAudio;
    @BindView(R.id.view_indicator)
    View mViewIndicator;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    private int mScreenWidth;


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initListener() {
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    public void initData() {
        mScreenWidth = Utils.getScreenWidth(this) / 2;
        initIndicator();
        initViewPager();
        changTitleState(true);
    }

    private void initViewPager() {
        ArrayList<Fragment> fragmentArrayList = new ArrayList<Fragment>();
        fragmentArrayList.add(new VideoFragment());
        fragmentArrayList.add(new AudioFragment());
        mViewPager.setAdapter(new MainAdapter(getSupportFragmentManager(), fragmentArrayList));

    }

    /**
     * 初始化Indicator
     */
    private void initIndicator() {
        mViewIndicator.getLayoutParams().width = mScreenWidth;
        mViewIndicator.requestLayout();
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            scrollIndicator(position, positionOffset);
        }

        @Override
        public void onPageSelected(int position) {
            changTitleState(position == 0);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 改变标题文本的颜色和大小
     *
     * @param isSelectVideo
     */
    private void changTitleState(boolean isSelectVideo) {
        mTvAudio.setSelected(!isSelectVideo);
        mTvVideo.setSelected(isSelectVideo);
        scaleTitle(isSelectVideo ? 1.2f : 1.0f, mTvVideo);
        scaleTitle(!isSelectVideo ? 1.2f : 1.0f, mTvAudio);
    }

    private void scaleTitle(float scale, TextView textView) {
        ViewPropertyAnimator.animate(textView).scaleX(scale).scaleY(scale);
    }

    private void scrollIndicator(int position, float positionOffset) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mViewIndicator.getLayoutParams();
        layoutParams.leftMargin = (int) (mScreenWidth * positionOffset + position * mScreenWidth);
        mViewIndicator.setLayoutParams(layoutParams);
    }



    @OnClick({R.id.tv_video, R.id.tv_audio})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_video:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_audio:
                mViewPager.setCurrentItem(1);
                break;
        }
    }
}
