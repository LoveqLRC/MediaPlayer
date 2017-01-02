package rc.loveq.mediaplayerday02.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import rc.loveq.mediaplayerday02.interfaces.UiOperation;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/24 20:35
 * Email:664215432@qq.com
 */

public abstract class BaseFragment extends Fragment implements UiOperation{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =View.inflate(getActivity(),getLayoutId(),null);
        ButterKnife.bind(this,view);
        initListener();
        initData();
        return view;
    }
}
