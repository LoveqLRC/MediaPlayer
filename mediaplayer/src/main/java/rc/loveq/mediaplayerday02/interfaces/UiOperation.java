package rc.loveq.mediaplayerday02.interfaces;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/24 20:37
 * Email:664215432@qq.com
 */

/**
 * Ui操作封装类
 */
public interface UiOperation {
    /**绑定布局Id**/
    int getLayoutId();
    /**绑定接口*/
    void initListener();

    /**绑定数据**/
    void initData();
}
