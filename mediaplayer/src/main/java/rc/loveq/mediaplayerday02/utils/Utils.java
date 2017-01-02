package rc.loveq.mediaplayerday02.utils;

import android.content.Context;
import android.database.Cursor;
import android.view.WindowManager;

import java.text.SimpleDateFormat;

import rc.loveq.mediaplayerday02.interfaces.Constans;

/**
 * Author：Rc
 * Csdn：http://blog.csdn.net/loveqrc
 * 0n 2016/12/24 20:54
 * Email:664215432@qq.com
 */

public  class Utils {
    /**
     * 获取屏幕的宽
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return  wm.getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕的高
     * @param context
     * @return
     */
    public  static  int getScreenHeight(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    public static void printCursor(Cursor cursor){
        if (cursor == null) {
            System.out.println("cursor == null");
            return;
        }
        while (cursor.moveToNext()){
            System.out.println("-----------------");
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String columnName = cursor.getColumnName(i);
                String value = cursor.getString(i);
                System.out.println("columnName : "+columnName+"    columnValue : "+value);
            }
        }
    }

    /**
     * 格式化一个毫秒值，如果有小时，则格式化为时分秒，如：02:30:59，如果没有小时则格式化为分秒，如：30:59
     * @param duration
     * @return
     */
    public static CharSequence formatterMills(long duration){
        SimpleDateFormat simpleDateFormat;
        if (duration/Constans.HOUR_MILLS>0){
            simpleDateFormat=new SimpleDateFormat("kk:mm:ss");
        }else{
            simpleDateFormat=new SimpleDateFormat("mm:ss");
        }

        return  simpleDateFormat.format(duration);
    }

}
