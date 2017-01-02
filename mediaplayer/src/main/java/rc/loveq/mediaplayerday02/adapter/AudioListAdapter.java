package rc.loveq.mediaplayerday02.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rc.loveq.mediaplayerday02.R;
import rc.loveq.mediaplayerday02.bean.AudioItem;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/29 10:21
 * Email:664215432@qq.com
 */

public class AudioListAdapter extends CursorAdapter {
    public AudioListAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = View.inflate(context, R.layout.adapter_audio_list, null);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        AudioItem audioItem=AudioItem.fromCursor(cursor);
        holder.mTvTitle.setText(audioItem.getTitle());
        holder.mTvArtist.setText(audioItem.getArtist());
    }

    static class ViewHolder {
        @BindView(R.id.tv_title)
        TextView mTvTitle;
        @BindView(R.id.tv_artist)
        TextView mTvArtist;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
