package rc.loveq.mediaplayerday02.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rc.loveq.mediaplayerday02.R;
import rc.loveq.mediaplayerday02.bean.AudioItem;
import rc.loveq.mediaplayerday02.interfaces.IAudioPlayerService;
import rc.loveq.mediaplayerday02.interfaces.Keys;
import rc.loveq.mediaplayerday02.service.AudioPlayerService;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/29 10:41
 * Email:664215432@qq.com
 */

public class AudioPlayerActivity extends BaseActivity {
    @BindView(R.id.iv_vision)
    ImageView mIvVision;
    @BindView(R.id.tv_artist)
    TextView mTvArtist;
    @BindView(R.id.sb_audio)
    SeekBar mSbAudio;
    @BindView(R.id.btn_play_mode)
    Button mBtnPlayMode;
    @BindView(R.id.btn_pre)
    Button mBtnPre;
    @BindView(R.id.btn_play)
    Button mBtnPlay;
    @BindView(R.id.btn_next)
    Button mBtnNext;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_play_time)
    TextView mTvPlayTime;
    public static final String TAG = "AudioPlayerActivity";
    private ServiceConnection mConnection;
    private BroadcastReceiver mUpdateUi;
    public static final int UPDATE_PLAY_TIME = 0;//更新播放时间
    private IAudioPlayerService mAudioPlayerService;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PLAY_TIME:
                    updatePlayTime();
                    break;
            }
        }
    };
    private BroadcastReceiver mStopUpdateUiReceiver;

    @Override
    public int getLayoutId() {
        return R.layout.activity_audio_player;
    }


    @Override
    public void initData() {
        animateBg();
        registerUpdateUiReceiver();
        connectToService();
        registerStopUpdateUiReceiver();
    }

    /**
     * 注册停止更新Ui
     */
    private void registerStopUpdateUiReceiver() {
        mStopUpdateUiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "停止更新---------: ");
                mHandler.removeCallbacksAndMessages(null);
            }
        };
        IntentFilter intentFilter=new IntentFilter(AudioPlayerService.STOP_UPDATE_UI);
        registerReceiver(mStopUpdateUiReceiver,intentFilter);
    }

    /**
     * 初始化动画背景
     */
    private void animateBg() {
        AnimationDrawable animationDrawable = (AnimationDrawable) mIvVision.getBackground();
        animationDrawable.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        unregisterReceiver(mUpdateUi);
        unregisterReceiver(mStopUpdateUiReceiver);
        mHandler.removeCallbacksAndMessages(null);

    }

    private void registerUpdateUiReceiver() {
        mUpdateUi = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "registerUpdateUiReceiver: ");
                AudioItem audioItem = (AudioItem) intent.getSerializableExtra(Keys.AUDIO_BEAN);
                updateUi(audioItem);
            }
        };
        registerReceiver(mUpdateUi, new IntentFilter(AudioPlayerService.UPDATE_UI));
    }

    private void updateUi(AudioItem audioItem) {
        if (audioItem == null || mAudioPlayerService == null) {
            return;
        }
        mTvArtist.setText(audioItem.getArtist());
        mTvTitle.setText(audioItem.getTitle());
        mSbAudio.setMax(mAudioPlayerService.getDuration());
        updateBtnPlayBg();
        updatePlayTime();
        updatePlayModeBg();
    }

    /**
     * 更新播放进度时间
     */
    private void updatePlayTime() {
        Log.d(TAG, "updatePlayTime: ");
        mTvPlayTime.setText(DateFormat.format("mm:ss", mAudioPlayerService.getCurrentPosition()) + "/" +
                DateFormat.format("mm:ss", mAudioPlayerService.getDuration()));
        mSbAudio.setProgress(mAudioPlayerService.getCurrentPosition());
        mHandler.sendEmptyMessageDelayed(UPDATE_PLAY_TIME, 300);
    }

    /**
     * 更新播放按钮的背景
     */
    private void updateBtnPlayBg() {
        int resid = mAudioPlayerService.isPlaying() ? R.drawable.selector_btn_audio_pause : R.drawable.selector_btn_audio_play;
        mBtnPlay.setBackgroundResource(resid);
    }

    /**
     * 创建链接到Service
     */
    private void connectToService() {
        final ArrayList<AudioItem> audioItems = (ArrayList<AudioItem>) getIntent().getSerializableExtra(Keys.AUDIO_ITEM);
        int currentPosition = getIntent().getIntExtra(Keys.AUDIO_CURRENT_POSITION, -1);
        Intent intentService = new Intent(this, AudioPlayerService.class);
        Intent intent = getIntent();
        final int what = intent.getIntExtra(Keys.AUDIO_WHAT, -1);
        intentService.putExtra(Keys.AUDIO_WHAT,-1);
        intentService.putExtra(Keys.AUDIO_ITEM, audioItems);
        intentService.putExtra(Keys.AUDIO_CURRENT_POSITION, currentPosition);
        startService(intentService);
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                Log.d(TAG, "onServiceConnected: ");
                mAudioPlayerService = ((AudioPlayerService.AudioPlayerBinder) binder).audioPlayerService;
                if (what==AudioPlayerService.WHAT_ROOT){
                    AudioItem audioItem = mAudioPlayerService.getCurrentAudioItem();
                    Log.d(TAG, "onServiceConnected:                audioItem"+audioItem);
                    updateUi(mAudioPlayerService.getCurrentAudioItem());
                }else{
                mAudioPlayerService.openAudio();
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected: ");
            }
        };
        bindService(intentService, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void initListener() {
        mSbAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mAudioPlayerService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @OnClick({R.id.btn_play_mode, R.id.btn_pre, R.id.btn_play, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play_mode:
                mAudioPlayerService.switchPlayMode();
                updatePlayModeBg();
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
        }
    }

    /**
     * 切换播放模式
     */
    private void updatePlayModeBg() {
        int resid=R.drawable.selector_btn_playmode_order;
        switch (mAudioPlayerService.getCurrentPlayMode()){
            case AudioPlayerService.PLAY_MODE_ORDER:
                resid=R.drawable.selector_btn_playmode_order;
                break;
            case AudioPlayerService.PLAY_MODE_RANDOM:
                resid=R.drawable.selector_btn_playmode_random;
                break;
            case AudioPlayerService.PLAY_MODE_SINGLE:
                resid=R.drawable.selector_btn_playmode_single;
                break;
        }
        mBtnPlayMode.setBackgroundResource(resid);
    }

    /**
     * 下一曲
     */
    private void next() {
        mAudioPlayerService.next();
    }

    /**
     * 上一曲
     */
    private void pre() {
        mAudioPlayerService.pre();
    }

    private void play() {

        if (mAudioPlayerService.isPlaying()) {
            mAudioPlayerService.pause();
        } else {
            mAudioPlayerService.start();
        }
        updateBtnPlayBg();
    }
}
