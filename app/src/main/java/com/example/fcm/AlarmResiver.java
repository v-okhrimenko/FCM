package com.example.fcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmResiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        String message = intent.getStringExtra( "jobId" );
//        System.out.println( "JobId   "+message );

        Intent scheduledIntent = new Intent(context, AlarmActivity.class);

        scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        scheduledIntent.putExtra( "jobId", message );
        context.startActivity(scheduledIntent);



    }
}
