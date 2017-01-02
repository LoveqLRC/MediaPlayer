package rc.loveq.mediaplayerday02.fragment;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import rc.loveq.mediaplayerday02.R;
import rc.loveq.mediaplayerday02.activity.AudioPlayerActivity;
import rc.loveq.mediaplayerday02.adapter.AudioListAdapter;
import rc.loveq.mediaplayerday02.bean.AudioItem;
import rc.loveq.mediaplayerday02.interfaces.Keys;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/24 21:03
 * Email:664215432@qq.com
 */

public class AudioFragment extends BaseFragment {
    @BindView(R.id.media_lv)
    ListView mMediaLv;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_media_list;
    }

    @Override
    public void initData() {
        AsyncQueryHandler asyncQueryHandler=new AsyncQueryHandler(getActivity().getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                AudioListAdapter audioListAdapter=new AudioListAdapter(getActivity(),cursor);
                mMediaLv.setAdapter(audioListAdapter);
//                Utils.printCursor(cursor);
            }
        };
        int token=0;
        Object cookie=null;
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection={MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA};
        String selection=null;
        String[] selectionArgs=null;
        String orderby= MediaStore.Audio.Media.TITLE+" ASC";
        asyncQueryHandler.startQuery(token,cookie,uri,projection,selection,selectionArgs,orderby);
    }
    @Override
    public void initListener() {
        mMediaLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);//这里获取到cursor已经移动到对应的位置了
                ArrayList<AudioItem> audioItems=getAudioItems(cursor);
                enterAudioPlayerActivity(audioItems,position);
            }
        });
    }

    /**
     * 进入音乐播放界面
     * @param audioItems
     * @param position
     */
    private void enterAudioPlayerActivity(ArrayList<AudioItem> audioItems, int position) {
        Intent intent=new Intent(getActivity(), AudioPlayerActivity.class);
        intent.putExtra(Keys.AUDIO_ITEM,audioItems);
        intent.putExtra(Keys.AUDIO_CURRENT_POSITION,position);
        startActivity(intent);
    }

    /**
     * 根据传入的cursor对象获取audio集合
     * @param cursor
     * @return
     */
    private ArrayList<AudioItem> getAudioItems(Cursor cursor) {
        ArrayList<AudioItem> audioItems=new ArrayList<AudioItem>();
        cursor.moveToFirst();//保证传入的cursor在第一行
        do{
            audioItems.add(AudioItem.fromCursor(cursor));
        }while (cursor.moveToNext());
        return audioItems;
    }
}
