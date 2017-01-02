package rc.loveq.mediaplayerday02.interfaces;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/25 11:52
 * Email:664215432@qq.com
 */

public interface Keys {
    /*传递到视频页的ArrayList*/
    String VIDEO_ITEM = "video_item";

    /*传递到视频页当前播放的位置*/
    String VIDEO_CURRENT_POSITION = "video_current_position";

    /*传递到音乐页的ArrayList*/
    String AUDIO_ITEM = "audio_item";

    /*传递到音乐页当前播放的位置*/
    String AUDIO_CURRENT_POSITION = "audio_current_position";

    /*当前音乐的播放模式*/
    String AUDIO_PLAY_MODE="audio_play_mode";

    /*当前播放音乐的java bean*/
    String AUDIO_BEAN="audio_bean";

    /*通过Notification点击的what*/
    String AUDIO_WHAT="audio_what";
}
