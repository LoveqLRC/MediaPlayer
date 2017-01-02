package rc.loveq.mediaplayerday02.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import butterknife.ButterKnife;
import rc.loveq.mediaplayerday02.interfaces.UiOperation;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/24 20:35
 * Email:664215432@qq.com
 */

public abstract class BaseActivity extends FragmentActivity implements UiOperation{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initData();
        initListener();
    }

}
