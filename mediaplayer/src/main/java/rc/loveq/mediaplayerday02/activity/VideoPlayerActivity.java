package rc.loveq.mediaplayerday02.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rc.loveq.mediaplayerday02.R;
import rc.loveq.mediaplayerday02.bean.VideoItem;
import rc.loveq.mediaplayerday02.interfaces.Keys;
import rc.loveq.mediaplayerday02.utils.Utils;
import rc.loveq.mediaplayerday02.view.FullScreenVideoView;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/25 09:56
 * Email:664215432@qq.com
 */

public class VideoPlayerActivity extends BaseActivity {
    private static final int SHOW_SYSTEM_TIME = 1;//显示系统时间
    private static final String TAG = "VideoPlayerActivity";
    private static final int UPDATE_VIDEO_PROGRESS = 2;//更新video seek进度
    private static final int HIDE_CTRL_LAYOUT = 3;
    @BindView(R.id.video_view)
    FullScreenVideoView mVideoView;
    @BindView(R.id.btn_voice)
    Button mBtnVoice;
    @BindView(R.id.btn_exit)
    Button mBtnExit;
    @BindView(R.id.btn_pre)
    Button mBtnPre;
    @BindView(R.id.btn_play)
    Button mBtnPlay;
    @BindView(R.id.btn_next)
    Button mBtnNext;
    @BindView(R.id.btn_fullscreen)
    Button mBtnFullscreen;
    @BindView(R.id.tv_video_title)
    TextView mTvVideoTitle;
    @BindView(R.id.iv_system_battery)
    ImageView mIvSystemBattery;
    @BindView(R.id.tv_system_time)
    TextView mTvSystemTime;
    @BindView(R.id.ll_top_ctrl)
    LinearLayout mLlTopCtrl;
    @BindView(R.id.ll_bottom_ctrl)
    LinearLayout mLlBottomCtrl;
    @BindView(R.id.view_brightness)
    View mViewBrightness;
    @BindView(R.id.sb_voice)
    SeekBar mSbVoice;
    @BindView(R.id.sb_video)
    SeekBar mSbVideo;
    @BindView(R.id.tv_audio_current_position)
    TextView mTvAudioCurrentPosition;
    @BindView(R.id.tv_audio_duration)
    TextView mTvAudioDuration;

    private ArrayList<VideoItem> mVideoItemArrayList;
    private int mPosition;//当前播放的索引

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_SYSTEM_TIME:
                    initSystemTime();
                    break;
                case UPDATE_VIDEO_PROGRESS:
                    updateVideoProgress();
                    break;
                case HIDE_CTRL_LAYOUT:
                    showOrHideCtrlLayout();
                    break;
            }
        }
    };
    private BroadcastReceiver mBatteryChangeReceiver;
    private AudioManager mAudioManager;
    private int mCurrentVolume;
    private BroadcastReceiver mVoiceChangeReceiver;
    private GestureDetector mGestureDetector;
    private float mCurrentAlpha;
    private int mMaxVolume;
    private int mScreenHeight;
    private Uri mVideoUri;


    @Override
    public int getLayoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    public void initListener() {
        mVideoView.setOnPreparedListener(mOnPreparedListener);
        mSbVoice.setOnSeekBarChangeListener(mOnVoiceChangeListener);
        mSbVideo.setOnSeekBarChangeListener(mOnVideoChangeListener);
        mGestureDetector = new GestureDetector(this, mSimpleOnGestureListener);
    }

    @Override
    public void initData() {
        mVideoUri = getIntent().getData();
        if (mVideoUri != null) {
            mVideoView.setVideoURI(mVideoUri);
            mBtnPre.setEnabled(false);
            mBtnNext.setEnabled(false);
        }else{
        mVideoItemArrayList = (ArrayList<VideoItem>) getIntent().getSerializableExtra(Keys.VIDEO_ITEM);
        mPosition = getIntent().getIntExtra(Keys.VIDEO_CURRENT_POSITION, -1);
        mScreenHeight = Utils.getScreenHeight(VideoPlayerActivity.this);
        openVideo();
        }
        initSystemTime();
        initVoice();
        initBrightness();
        initCtrlLayout();
        registerBatteryChangeReceiver();
        registerVoiceChangeReceiver();
    }

    /**
     * 注册监听系统音量改变
     */
    private void registerVoiceChangeReceiver() {
        mVoiceChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                Log.d(TAG, "onReceive: 声音发生改变了");
                mSbVoice.setProgress(getCurrentVolume());
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(mVoiceChangeReceiver, intentFilter);
    }

    /**
     * 初始化音量
     */
    private void initVoice() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //抢占音乐播放权
        mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mSbVoice.setMax(mMaxVolume);

        mCurrentVolume = getCurrentVolume();
        mSbVoice.setProgress(mCurrentVolume);
    }

    /**
     * 获取系统当前的音量
     *
     * @return
     */
    private int getCurrentVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 初始化背景透明度
     */
    private void initBrightness() {
        mViewBrightness.setVisibility(View.VISIBLE);
        setBackgroundBrightness(0.0f);
    }

    /**
     * 改变背景的透明度
     *
     * @param alpha
     */
    private void setBackgroundBrightness(float alpha) {
        ViewHelper.setAlpha(mViewBrightness, alpha);
    }

    /**
     * 初始化上下面板，一开始进入页面是隐藏的
     */
    private void initCtrlLayout() {
        //这样是获取到的高度为零，因为get的时候还没测量完成,必须手动调用measures方法。
        //        Log.d(TAG, "initCtrlLayout:mLlBottomCtrl.getMeasuredHeight():  "+mLlBottomCtrl.getMeasuredHeight()+"" +
        //                "   mLlBottomCtrl.getHeight():"+mLlBottomCtrl.getHeight());

        //隐藏底部
        mLlBottomCtrl.measure(0, 0);
//        ViewHelper.setTranslationY(mLlBottomCtrl, mLlBottomCtrl.getMeasuredHeight());

        //隐藏顶部
        mLlTopCtrl.measure(0, 0);
//        ViewHelper.setTranslationY(mLlTopCtrl, -mLlTopCtrl.getMeasuredHeight());
        autoHideCtrlLayout();
    }

    /**
     * 初始化电池管理
     * 注册监听电池变化
     */
    private void registerBatteryChangeReceiver() {
        mBatteryChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive:  电量发生改变" + intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
                mIvSystemBattery.setImageLevel(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1));
            }
        };
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatteryChangeReceiver, intentFilter);
    }

    /**
     * 初始化系统时间
     * 每个一秒更新
     */
    private void initSystemTime() {
        mTvSystemTime.setText(DateFormat.format("kk:mm", System.currentTimeMillis()));
        mHandler.sendEmptyMessageDelayed(SHOW_SYSTEM_TIME, 1000);
    }

    /**
     * 获取avtivity传来的数据,设置VideoPath
     */
    private void openVideo() {
        if (mPosition == -1 || mVideoItemArrayList == null || mVideoItemArrayList.isEmpty()) {
            return;
        }
        mBtnNext.setEnabled(mPosition != mVideoItemArrayList.size() - 1);
        mBtnPre.setEnabled(mPosition != 0);
        VideoItem videoItem = mVideoItemArrayList.get(mPosition);
        String path = videoItem.getData();
        mTvVideoTitle.setText(videoItem.getTitle());
        mVideoView.setVideoPath(path);
    }

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mVideoView.start();
            if (mVideoUri != null) {
                mTvVideoTitle.setText(mVideoUri.getPath());
            }
            mTvAudioDuration.setText(DateFormat.format("kk:mm:ss", mVideoView.getDuration()));
            mSbVideo.setMax(mVideoView.getDuration());
            updateVideoProgress();
            updateBtnPlayBg();//更新播放按钮背景

        }
    };

    private void updateBtnPlayBg() {
        int resid;
        if (mVideoView.isPlaying()) {
            resid = R.drawable.selector_btn_pause;
        } else {
            resid = R.drawable.selector_btn_play;
        }
        mBtnPlay.setBackgroundResource(resid);
    }

    /**
     * 更新video seekbar进度
     */
    private void updateVideoProgress() {
        mSbVideo.setProgress(mVideoView.getCurrentPosition());
        mTvAudioCurrentPosition.setText(DateFormat.format("kk:mm:ss", mVideoView.getCurrentPosition()));
        mHandler.sendEmptyMessageDelayed(UPDATE_VIDEO_PROGRESS, 300);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBatteryChangeReceiver);
        unregisterReceiver(mVoiceChangeReceiver);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    @OnClick({R.id.btn_voice, R.id.btn_pre, R.id.btn_play, R.id.btn_next, R.id.btn_fullscreen, R.id.btn_exit})
    public void onClick(View view) {
        cancelHideCtrlMessage();//不允许隐藏控制面板
        switch (view.getId()) {
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_voice:
                mute();
                break;
            case R.id.btn_pre:
                pre();
                break;
            case R.id.btn_play:
                play();
                break;
            case R.id.btn_next:
                next();
                break;
            case R.id.btn_fullscreen:
                toggleFullScreen();
                break;
        }
        autoHideCtrlLayout();
    }

    /**
     * 下一个视频
     */
    private void next() {
        if (mVideoItemArrayList != null) {
            if (mPosition != mVideoItemArrayList.size() - 1) {
                mPosition++;
                openVideo();
            }
        }
    }

    /**
     * 上一个视频
     */
    private void pre() {
        if (mPosition != 0) {
            mPosition--;
            openVideo();
        }
    }

    /**
     * 如果正在播放则暂停
     * 如果正在暂停，则播放
     */
    private void play() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        } else {
            mVideoView.start();
        }
        updateBtnPlayBg();
    }

    /**
     * 如果是全屏的话则恢复默认大小
     * 如果是默认大小则全屏
     */
    private void toggleFullScreen() {
        mVideoView.toggleFullScreen();
        updateFullScreenBg();
    }

    /**
     * 更新全屏按钮背景
     */
    private void updateFullScreenBg() {
        int resid;
        if (mVideoView.isFullScreen()) {
            resid = R.drawable.selector_btn_defaultscreen;
        } else {
            resid = R.drawable.selector_btn_fullscreen;
        }
        mBtnFullscreen.setBackgroundResource(resid);
    }

    /**
     * 静音或者回复原来的音量
     */
    private void mute() {
        if (getCurrentVolume() > 0) {
            //如果音量大于0，那么静音。
            mCurrentVolume = getCurrentVolume();
            setVideoVolume(0);
            mSbVoice.setProgress(0);
        } else {
            //如果是静音，恢复以前的音量
            setVideoVolume(mCurrentVolume);
            mSbVoice.setProgress(mCurrentVolume);
        }
    }

    /**
     * 设置音量
     *
     * @param volume 音量值
     */
    private void setVideoVolume(int volume) {
        int flag = 0;//-1表示显示系统的音量控制面板，0表示不显示
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, flag);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cancelHideCtrlMessage();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                autoHideCtrlLayout();
                break;
        }
        return result;
    }

    private OnSeekBarChangeListener mOnVoiceChangeListener = new OnSeekBarChangeListener() {
        /**
         *
         * @param seekBar
         * @param progress
         * @param fromUser 是否用户触发
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                setVideoVolume(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
                cancelHideCtrlMessage();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
                autoHideCtrlLayout();
        }
    };

    private OnSeekBarChangeListener mOnVideoChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mVideoView.seekTo(progress);
                if (!mVideoView.isPlaying()) {
                    Log.d(TAG, "onProgressChanged:  mVideoView.start();");
                    mVideoView.start();
                }
                updateBtnPlayBg();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            cancelHideCtrlMessage();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            autoHideCtrlLayout();
        }
    };

    GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        private boolean mIsDownLeft;//手指是否安在屏幕的左边，true是

        @Override
        public void onLongPress(MotionEvent e) {
            play();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            toggleFullScreen();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float deltaY = e1.getY() - e2.getY();
            Log.d(TAG, "onScroll: mIsDownLeft" + mIsDownLeft);
            if (mIsDownLeft) {
                changeBrightness(deltaY);
            } else {
                changeSystemVoice(deltaY);
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            mCurrentVolume = getCurrentVolume();
            mIsDownLeft = e.getX() < Utils.getScreenWidth(VideoPlayerActivity.this) / 2;
            mCurrentAlpha = ViewHelper.getAlpha(mViewBrightness);
            Log.d(TAG, "onDown: mIsDownLeft" + mIsDownLeft);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            showOrHideCtrlLayout();
            return true;
        }
    };

    /**
     * 隐藏或显示控制面板
     */
    private void showOrHideCtrlLayout() {
        if (ViewHelper.getTranslationY(mLlTopCtrl) == 0) {//控制面板显示则隐藏它。
            ViewPropertyAnimator.animate(mLlTopCtrl).translationY(-mLlTopCtrl.getMeasuredHeight());
            ViewPropertyAnimator.animate(mLlBottomCtrl).translationY(mLlBottomCtrl.getMeasuredHeight());
        } else {//控制面板隐藏则显示它
            ViewPropertyAnimator.animate(mLlTopCtrl).translationY(0);
            ViewPropertyAnimator.animate(mLlBottomCtrl).translationY(0);
            autoHideCtrlLayout();
        }
    }

    /**
     * 每隔5秒隐藏控制面板
     */
    private void autoHideCtrlLayout() {
        cancelHideCtrlMessage();
        mHandler.sendEmptyMessageDelayed(HIDE_CTRL_LAYOUT, 5000);
    }

    /**
     * 取消发送隐藏控制面板消息
     */
    public void cancelHideCtrlMessage() {
        mHandler.removeMessages(HIDE_CTRL_LAYOUT);
    }

    /**
     * 改变系统的音量
     *
     * @param deltaY
     */
    private void changeSystemVoice(float deltaY) {
        float volume = mCurrentVolume + deltaY / mScreenHeight * mMaxVolume;
        if (volume < 0) {
            volume = 0;
        }
        if (volume > mMaxVolume) {
            volume = mMaxVolume;
        }
        setVideoVolume((int) volume);
    }

    /**
     * 改变屏幕的亮度
     *
     * @param deltaY
     */
    private void changeBrightness(float deltaY) {
        float alpha = mCurrentAlpha + deltaY / mScreenHeight * 1.0f;
        if (alpha < 0) {
            alpha = 0;
        }
        if (alpha > 0.8) {
            alpha = 0.8f;
        }
        setBackgroundBrightness(alpha);
    }

}
