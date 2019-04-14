package com.torkuds.noticeinterceptor;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.Bundle;
import android.os.Environment;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("NewApi")
public class NotificationInterceptorService extends NotificationListenerService {

    // 在收到消息时触发
    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        long postTime = sbn.getPostTime();
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        String conversitionTitle = extras.getString(Notification.EXTRA_CONVERSATION_TITLE);
        String infoText = extras.getString(Notification.EXTRA_INFO_TEXT);
        String messages = extras.getString(Notification.EXTRA_MESSAGES);
        String people = extras.getString(Notification.EXTRA_PEOPLE);
        int progress = extras.getInt(Notification.EXTRA_PROGRESS);
        String self_display_name = extras.getString(Notification.EXTRA_SELF_DISPLAY_NAME);
        boolean show_when = extras.getBoolean(Notification.EXTRA_SHOW_WHEN);
        String sub_text = extras.getString(Notification.EXTRA_SUB_TEXT);
        Log.i("通知监听", "时间：" + postTime + "，包名：" + notificationPkg + "，标题：" + notificationTitle + "，内容：" + notificationText);
        Log.i("通知监听", "1：" + conversitionTitle + "，2：" + infoText + "，3：" + messages + "，4：" + people);
        Log.i("通知监听", "5：" + progress + "，6：" + self_display_name + "，7：" + show_when + "，8：" + sub_text);
        String content="\n"+"时间：" + longToString(postTime) + "，包名：" + notificationPkg + "，标题：" + notificationTitle + "，内容：" + notificationText;

        writeFile(content);

    }

    // 在删除消息时触发
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        Log.i("通知监听", "Notification removed " + notificationTitle + " & " + notificationText);

    }

    public static String longToString(long time)
    {
        Date date=new Date(time);
        SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String string=format1.format(date);
        return  string;
    }
    public static void writeFile(String str)
    {
        String filePath=null;
        boolean hasSDCard= Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(hasSDCard)
        {
            filePath=Environment.getExternalStorageDirectory().toString()+ File.separator+"tasks.txt";
        }else
        {
            filePath=Environment.getDownloadCacheDirectory().toString().toString()+File.separator+"tasks.txt";
        }
        try
        {
            File file=new File(filePath);
//            if(!file.exists())
//            {
//                File dir=new File(file.getParent());
//                dir.mkdir();
//                file.createNewFile();
//            }

//            File file=new File(filePath);
//            file.createNewFile();
            // FileOutputStream outputStream=new FileOutputStream(file);
            //outputStream.write(str.getBytes());
            //outputStream.close();
            RandomAccessFile raf=new RandomAccessFile(file,"rw");
            raf.seek(file.length());
            raf.write(str.getBytes());
            Log.i("MainActivityMTS"," write  succeeful");
//            if(file.exists())
//            {
//                Log.i("MainActivityMTS","create successful");
//            }
        }catch (Exception e)
        {
            Log.i("MainActivityMTS","can not write ");
        }
    }




}
