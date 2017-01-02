package rc.loveq.mediaplayerday02.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import rc.loveq.mediaplayerday02.R;
import rc.loveq.mediaplayerday02.activity.AudioPlayerActivity;
import rc.loveq.mediaplayerday02.bean.AudioItem;
import rc.loveq.mediaplayerday02.interfaces.IAudioPlayerService;
import rc.loveq.mediaplayerday02.interfaces.Keys;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/29 14:03
 * Email:664215432@qq.com
 */

public class AudioPlayerService extends Service implements IAudioPlayerService {
    public static final String TAG = "AudioPlayerService";
    public static final String STOP_UPDATE_UI = "stop_update_ui";//发送广播停止更新Ui
    public static final String UPDATE_UI = "update_ui";//发送广播更新UI
    public static final int PLAY_MODE_ORDER = 1;//顺序播放
    public static final int PLAY_MODE_RANDOM = 2;//随机播放
    public static final int PLAY_MODE_SINGLE = 3;//单曲循环
    public static final int WHAT_ROOT = 4;
    private static final int WHAT_PRE = 5;
    private static final int WHAT_NEXT = 6;
    private static final int WHAT_PLAY = 7;
    private SharedPreferences mSp;
    private NotificationManager mNotificationManager;
    private int mCurrentPlayMode;
    private int mCurrentPosition;
    private ArrayList<AudioItem> mAudioItems;
    private MediaPlayer mMediaPlayer;
    private AudioItem mAudioItem;
    private int notifyId = 1;
    private Random mRandom;


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        mSp = PreferenceManager.getDefaultSharedPreferences(this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mRandom = new Random();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        mCurrentPlayMode = mSp.getInt(Keys.AUDIO_PLAY_MODE, PLAY_MODE_ORDER);
        int what = intent.getIntExtra(Keys.AUDIO_WHAT, -1);
        switch (what) {
            case WHAT_NEXT:
                next();
                break;
            case WHAT_PRE:
                pre();
                break;
            case WHAT_PLAY:
                start();
                break;
            default:
                mAudioItems = (ArrayList<AudioItem>) intent.getSerializableExtra(Keys.AUDIO_ITEM);
                mCurrentPosition = intent.getIntExtra(Keys.AUDIO_CURRENT_POSITION, -1);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        AudioPlayerBinder binder = new AudioPlayerBinder();
        binder.audioPlayerService = this;
        return binder;
    }

    @Override
    public void openAudio() {
        Log.d(TAG, "openAudio: ");
        if (mAudioItems == null || mAudioItems.isEmpty() || mCurrentPosition == -1) {
            return;
        }
        mAudioItem = mAudioItems.get(mCurrentPosition);
        String audioPath = mAudioItem.getData();

        //抢占音乐播放权
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        //释放资源
        release();
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    next();
                }
            });

            mMediaPlayer.setDataSource(audioPath);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start() {
        if (mMediaPlayer != null) {
            sendNotification();
            mMediaPlayer.start();
        }
    }


    /**
     * 发送通知
     */
    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.icon_notification)
                .setTicker("当前正在播放" + mAudioItem.getTitle())
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setContentText(mAudioItem.getArtist())
                .setContentTitle(mAudioItem.getTitle())
                .setContentIntent(getActivityPendingIntent(WHAT_ROOT))
                .setContent(getRemoteView());

        mNotificationManager.notify(notifyId, builder.build());
    }

    private RemoteViews getRemoteView() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.tv_title, mAudioItem.getTitle());
        remoteViews.setTextViewText(R.id.tv_artist, mAudioItem.getArtist());
        remoteViews.setOnClickPendingIntent(R.id.ll_root, getActivityPendingIntent(WHAT_ROOT));
        remoteViews.setOnClickPendingIntent(R.id.btn_pre, getServicePendingIntent(WHAT_PRE));
        remoteViews.setOnClickPendingIntent(R.id.btn_next, getServicePendingIntent(WHAT_NEXT));
        return remoteViews;
    }

    private PendingIntent getServicePendingIntent(int what) {
        int requestCode = what;
        Intent intent = new Intent(this, AudioPlayerService.class);
        intent.putExtra(Keys.AUDIO_WHAT, what);
        PendingIntent pendingIntent = PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private PendingIntent getActivityPendingIntent(int what) {
        int requestCode = what;//用于判断PendingIntent是否同一个
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(Keys.AUDIO_WHAT, what);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null) {
            mNotificationManager.cancel(notifyId);
            mMediaPlayer.pause();
        }
    }

    @Override
    public void pre() {
        Log.d(TAG, "pre: ");
        if (mMediaPlayer == null) {
            return;
        }
        switch (mCurrentPlayMode) {
            case PLAY_MODE_ORDER:
                if (mCurrentPosition != 0) {
                    mCurrentPosition--;
                } else {
                    mCurrentPosition = mAudioItems.size() - 1;
                }
                break;
            case PLAY_MODE_RANDOM:
                mCurrentPosition = mRandom.nextInt(mAudioItems.size());
                break;
            case PLAY_MODE_SINGLE:
                break;
        }
        openAudio();
    }

    @Override
    public void next() {
        if (mMediaPlayer == null) {
            return;
        }
        switch (mCurrentPlayMode) {
            case PLAY_MODE_ORDER:
                if (mCurrentPosition != mAudioItems.size() - 1) {
                    mCurrentPosition++;
                    Log.d(TAG, "next: PLAY_MODE_ORDER mCurrentPosition" + mCurrentPosition);
                } else {
                    mCurrentPosition = 0;
                }
                break;
            case PLAY_MODE_RANDOM:
                mCurrentPosition = mRandom.nextInt(mAudioItems.size());
                break;
            case PLAY_MODE_SINGLE:
                break;
        }
        openAudio();
    }

    @Override
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * Mediaplayer释放资源前,必须停止AudioPlayerActivity获取当前进度的mhanlder
     * 不然会触发OnCompletionListener的回调。
     *
     * @return
     */
    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }


    @Override
    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public void seekTo(int msec) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(msec);
        }
    }

    @Override
    public int switchPlayMode() {
        switch (mCurrentPlayMode) {
            case PLAY_MODE_ORDER:
                mCurrentPlayMode = PLAY_MODE_RANDOM;
                break;
            case PLAY_MODE_RANDOM:
                mCurrentPlayMode = PLAY_MODE_SINGLE;
                break;
            case PLAY_MODE_SINGLE:
                mCurrentPlayMode = PLAY_MODE_ORDER;
                break;
        }
        mSp.edit().putInt(Keys.AUDIO_PLAY_MODE, mCurrentPlayMode).apply();
        return mCurrentPlayMode;
    }

    @Override
    public int getCurrentPlayMode() {
        return mCurrentPlayMode;
    }

    @Override
    public AudioItem getCurrentAudioItem() {
        return mAudioItem;
    }

    public class AudioPlayerBinder extends Binder {
        public IAudioPlayerService audioPlayerService;
    }


    /*
     * release the media player in any state
     *  Mediaplayer释放资源前,必须停止AudioPlayerActivity获取当前进度的mhanlder
     * 不然会触发OnCompletionListener的回调。
     */
    private void release() {
        if (mMediaPlayer != null) {
            stopUpdateUI();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 发送广播通知AudioPlayerActivity停止更新UI界面
     */
    private void stopUpdateUI() {
        Intent intent = new Intent(STOP_UPDATE_UI);
        Log.d(TAG, "stopUpdateUI: ");
        sendBroadcast(intent);

    }

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.d(TAG, "onPrepared: ");
            start();
            updateAudioUI();
        }
    };

    /**
     * 发送广播通知AudioPlayerActivity更新UI界面
     */
    private void updateAudioUI() {
        Log.d(TAG, "updateAudioUI: ");
        Intent intent = new Intent(UPDATE_UI);
        intent.putExtra(Keys.AUDIO_BEAN, mAudioItem);
        sendBroadcast(intent);
    }


}
