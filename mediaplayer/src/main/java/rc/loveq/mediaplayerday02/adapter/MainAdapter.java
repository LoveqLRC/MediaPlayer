package rc.loveq.mediaplayerday02.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/24 22:08
 * Email:664215432@qq.com
 */

public class MainAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mList;
    public MainAdapter(FragmentManager fm,ArrayList<Fragment> mList) {
        super(fm);
        this.mList=mList;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
