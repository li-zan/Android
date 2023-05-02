package com.example.projecttemplate;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;

public class SmsService extends Service {
    public SmsService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        String phone = bundle.getString("phone");
        String msg = bundle.getString("msg");
        SmsManager smsManager = getSystemService(SmsManager.class);
        List<String> list = smsManager.divideMessage(msg);
        for (String m : list) {
            smsManager.sendTextMessage(phone, null, m, null, null);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}