package rc.loveq.mediaplayerday02.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import io.vov.vitamio.widget.VideoView;
import rc.loveq.mediaplayerday02.utils.Utils;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2017/1/1 22:08
 * Email:664215432@qq.com
 */

public class VitamioVideoView extends VideoView {
    private boolean isFullScreen=false;//是否全屏，默认不是全屏
    public VitamioVideoView(Context context) {
        super(context);
    }

    public VitamioVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VitamioVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    /**
     * 如果是全屏的话则恢复默认大小
     * 如果是默认大小则全屏
     */
    public void toggleFullScreen(){
        ViewGroup.LayoutParams params = getLayoutParams();
        if (isFullScreen) {
            params.width=getMeasuredWidth();
            params.height=getMeasuredHeight();
        }else{
            params.width= Utils.getScreenWidth(getContext());
            params.height= Utils.getScreenHeight(getContext());
        }
        isFullScreen=!isFullScreen;
        requestLayout();
    }

    /**
     * 返回是否全屏
     * @return
     */
    public boolean isFullScreen(){
        return isFullScreen;
    }
}
