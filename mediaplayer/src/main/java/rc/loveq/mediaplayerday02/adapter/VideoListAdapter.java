package rc.loveq.mediaplayerday02.adapter;


import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rc.loveq.mediaplayerday02.R;
import rc.loveq.mediaplayerday02.bean.VideoItem;
import rc.loveq.mediaplayerday02.utils.Utils;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/25 10:37
 * Email:664215432@qq.com
 */

public class VideoListAdapter extends CursorAdapter {

    public VideoListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    //创建一个View
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.adapter_video_list, null);
        ViewHolder viewHolder=new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    //绑定数据到View
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder= (ViewHolder) view.getTag();

        VideoItem videoItem=VideoItem.fromCursor(cursor);

        holder.mTvTitle.setText(videoItem.getTitle());
        holder.mTvSize.setText(Formatter.formatFileSize(context,videoItem.getSize()));
        holder.mTvDuration.setText(Utils.formatterMills(videoItem.getDuration()));
    }


    static class ViewHolder {
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.tv_size)
        TextView mTvSize;
        @BindView(R.id.tv_duration)
        TextView mTvDuration;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
