package rc.loveq.mediaplayerday02.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.MotionEvent;

import rc.loveq.mediaplayerday02.R;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/24 20:41
 * Email:664215432@qq.com
 */

public class SplashActivity extends BaseActivity {

    private Handler mHandler;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                enterHome();

            }
        }, 3000);
    }

    /**
     * 进入主页
     */
    private void enterHome() {
        Intent view = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(view);
        finish();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeCallbacksAndMessages(null);
                enterHome();
                break;
        }
        return super.onTouchEvent(event);
    }
}
