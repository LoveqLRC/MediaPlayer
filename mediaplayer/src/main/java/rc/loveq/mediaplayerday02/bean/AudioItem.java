package rc.loveq.mediaplayerday02.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/29 09:51
 * Email:664215432@qq.com
 */

public class AudioItem implements Serializable {
    private String title;
    private String artist;
    private String data;

    public static AudioItem fromCursor(Cursor cursor){
        AudioItem audioItem=new AudioItem();
        audioItem.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        audioItem.setData(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
        audioItem.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        return audioItem;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
