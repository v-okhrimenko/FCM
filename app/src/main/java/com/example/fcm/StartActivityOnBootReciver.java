package com.example.fcm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.fcm.helper.Helper;
import com.example.fcm.models.MainWork;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;


public class StartActivityOnBootReciver extends BroadcastReceiver {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private AlarmManager alarmManager;
    private int request = 0;
    private PendingIntent pendingIntent;
    {
        user = auth.getCurrentUser();
    }
    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");


    @Override
    public void onReceive(Context context, Intent intent) {

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            ////// reset your alrarms here
            scheduleAlarms(context);
        }
    }

    private void scheduleAlarms(Context context) {

        Date date = new Date(System.currentTimeMillis());
        String d1 = Helper.dataToString( date );
        Date date_ok = Helper.stringToData( d1 );

        noteRef_addWork_Full.whereGreaterThanOrEqualTo("date", date_ok ).orderBy( "date", Query.Direction.ASCENDING ).get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot ds:queryDocumentSnapshots){
                    MainWork main_work = ds.toObject( MainWork.class );
                    request++;

                    if (main_work.getAlarm1()==null){
                    } else {
                        Date dt = main_work.getAlarm1();
                        Calendar c1 = Calendar.getInstance();
                        c1.setTime( dt );

                        System.out.println( main_work.getName() + " "+ main_work.getAlarm1()+ " "+main_work.getUniqId() );
                        String jobId = ds.getId().trim();

                        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
                        Intent intent = new Intent(context, AlarmResiver.class);
                        intent.putExtra( "jobId", jobId );
                        pendingIntent = PendingIntent.getBroadcast(context, request, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, c1.getTimeInMillis(), pendingIntent);
                    }
                }finitaLaComedia(context);
            }
        } );

    }
    private void finitaLaComedia(Context context) {
    }
}
