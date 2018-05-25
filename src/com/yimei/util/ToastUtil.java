package com.yimei.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil{

	 /** 之前显示的内容 */  
    private static String oldMsg ;  
    /** Toast对象 */  
    private static Toast toast = null ;  
    /** 第一次时间 */  
    private static long oneTime = 0 ;  
    /** 第二次时间 */  
    private static long twoTime = 0 ;  
    
    /** 
     * 显示Toast 
     * @param context 
     * @param message 
     */  
    public static void showToast(Context context,String message,int time){  
        if(toast == null){  
            toast = Toast.makeText(context, message,time);  
            toast.show() ;  
            oneTime = System.currentTimeMillis() ;  
        }else{  
            twoTime = System.currentTimeMillis() ;  
            if(message.equals(oldMsg)){  
                if(twoTime - oneTime > Toast.LENGTH_SHORT){  
                    toast.show() ;  
                }  
            }else{  
                oldMsg = message ;  
                toast.setText(message) ;  
                toast.show() ;  
            }  
        }  
        oneTime = twoTime ;  
    }  
    
    public static Toast showToastLocation(Context context,String message,int time){  
        if(toast == null){  
            toast = Toast.makeText(context, message,time);  
            toast.show() ;  
            oneTime = System.currentTimeMillis() ;  
        }else{  
            twoTime = System.currentTimeMillis() ;  
            if(message.equals(oldMsg)){  
                if(twoTime - oneTime > Toast.LENGTH_SHORT){  
                    toast.show() ;  
                }  
            }else{  
                oldMsg = message ;  
                toast.setText(message) ;  
                toast.show() ;  
            }  
        }  
        oneTime = twoTime ;
        return toast;
    }
}
