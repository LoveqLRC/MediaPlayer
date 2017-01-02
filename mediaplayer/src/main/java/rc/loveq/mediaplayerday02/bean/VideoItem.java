package rc.loveq.mediaplayerday02.bean;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/25 10:44
 * Email:664215432@qq.com
 */

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Video bean
 */
public class VideoItem implements Serializable{
    //Video名字
    String title;

    //Video时长
    Long duration;

    //Video大小
    Long size;

    //Video路径
    String data;

    public  static  VideoItem fromCursor(Cursor cursor){
        VideoItem videoItem=new VideoItem();
        videoItem.setData(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
        videoItem.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
        videoItem.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
        videoItem.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
        return  videoItem;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
