package rc.loveq.mediaplayerday02.fragment;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import rc.loveq.mediaplayerday02.R;
import rc.loveq.mediaplayerday02.activity.VitamioActivity;
import rc.loveq.mediaplayerday02.adapter.VideoListAdapter;
import rc.loveq.mediaplayerday02.bean.VideoItem;
import rc.loveq.mediaplayerday02.interfaces.Keys;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/24 21:03
 * Email:664215432@qq.com
 */

public class VideoFragment extends BaseFragment {
    public static final String TAG="VideoFragment";
    @BindView(R.id.media_lv)
    ListView mMediaLv;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_media_list;
    }

    @Override
    public void initListener() {
        mMediaLv.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    public void initData() {

        AsyncQueryHandler ash = new AsyncQueryHandler(getActivity().getContentResolver()) {
           //查询完成的回调
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                super.onQueryComplete(token, cookie, cursor);
//                Utils.printCursor(cursor);
                VideoListAdapter videoListAdapter = new VideoListAdapter(getActivity(), cursor);
                mMediaLv.setAdapter(videoListAdapter);
            }
        };
        String orderBy = MediaStore.Video.Media.TITLE + " ASC";
        String[] selectionArgs = null;
        Object cookie = null;
        int token = 0;
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE
                , MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION};
        String selection = null;
        ash.startQuery(token, cookie, uri, projection, selection, selectionArgs, orderBy);

    }

    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);//这里的cursor已经移动到position的位置了
            ArrayList<VideoItem> videoItemArrayList=getVideoItems(cursor);
            Log.d(TAG, "onItemClick:  "+videoItemArrayList.size());
            enterVideoPlayerActivity(videoItemArrayList,position);
        }
    };

    /**
     * 将cursor转换成ArrayList
     * @param cursor
     * @return
     */
    private ArrayList<VideoItem> getVideoItems(Cursor cursor) {
        ArrayList<VideoItem> videos = new ArrayList<VideoItem>();
        cursor.moveToFirst();
        do {
            videos.add(VideoItem.fromCursor(cursor));
        } while (cursor.moveToNext());

        return videos;
    }

    /**
     * 进入视频播放界面
     * @param videoItemArrayList
     * @param position
     */
    private void enterVideoPlayerActivity(ArrayList<VideoItem> videoItemArrayList, int position) {
//        Intent view = new Intent(getActivity(), VideoPlayerActivity.class);
        Intent view = new Intent(getActivity(), VitamioActivity.class);
        view.putExtra(Keys.VIDEO_ITEM,videoItemArrayList);
        view.putExtra(Keys.VIDEO_CURRENT_POSITION,position);
        startActivity(view);
    }
}
