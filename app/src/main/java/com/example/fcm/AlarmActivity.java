package com.example.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import com.example.fcm.helper.Helper;
import com.example.fcm.models.UserInfoToFirestore;
import com.example.fcm.models.MainWork;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.PowerManager.FULL_WAKE_LOCK;

public class AlarmActivity extends AppCompatActivity {
    private PowerManager.WakeLock wl;
    private MediaPlayer mMediaPlayer;
    private Button stop;
    private TextView description;
    private ConstraintLayout description_yeah, description_no;


    AnimationDrawable alertAnimation;
    Vibrator vibrator;
    private String id__;

    private PowerManager.WakeLock mWakeLock;



    private TextView tvAlarmJobName, tvAlarmJobNameNoDesc ;
    private TextView tvAlarmJobData, tvAlarmJobDataNoDesc;
    private TextView tvAvatarNameAlarm;
    private TextClock showClock;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private Uri alert_;
    private String melodyFtomFs;
    PowerManager pm;

    private static final String TAG = "MyApp";


    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();

    private CollectionReference noteRef_addWork = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");
    private DocumentReference noteRef_data = db_fstore.collection( user.getUid() ).document("Avatar");

    // Уведомления
    private String txt_desc;
    private String txt;
    private Timer mTimer;
    private MyTimerTask mMyTimerTask;
    private String txtToNotification;
    private String txtToNotificationDescript;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_alarm );
        tvAlarmJobName = findViewById( R.id.tvAlarmJobName );
        tvAlarmJobData = findViewById( R.id.tvAlarmJobData );
        description = findViewById(R.id.ed_alarm_description);
        tvAvatarNameAlarm = findViewById( R.id.tvAvatarNameAlarm );
        tvAlarmJobNameNoDesc = findViewById( R.id.tvAlarmJobNameNoDescription );
        tvAlarmJobDataNoDesc = findViewById( R.id.tvAlarmJobDataNoDiscritpion );

        description_yeah = findViewById( R.id.cnl_alarm_discription );
        description_no = findViewById( R.id.cnl_alarm_no_discription);

        showClock = findViewById( R.id.tv_clock );
        showClock.is24HourModeEnabled();
        description.setMovementMethod(new ScrollingMovementMethod());


        ImageView iv_alertAnim = findViewById(R.id.iv_alert);
        iv_alertAnim.setBackgroundResource( R.drawable.alertanimation );
        alertAnimation = (AnimationDrawable) iv_alertAnim.getBackground();

        vibrator =(Vibrator) getSystemService(VIBRATOR_SERVICE);

        alertAnimation.start();

        noteRef_data.get()
                .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                    @Override

                    public void onSuccess(DocumentSnapshot documentSnapshot) {


                        UserInfoToFirestore userInfoToFirestore = documentSnapshot.toObject( UserInfoToFirestore.class );

                        if(userInfoToFirestore.getNickname() == null) {
                            tvAvatarNameAlarm.setText(auth.getCurrentUser().getEmail());
                        } else {
                            tvAvatarNameAlarm.setText( userInfoToFirestore.getNickname());
                        }

//                        System.out.println("MELODY + " + userInfoToFirestore.getMelody() );
                        if(userInfoToFirestore.getMelody() != null){

                            melodyFtomFs= userInfoToFirestore.getMelody();
//                            System.out.println("MOT NULL"  + melodyFtomFs );
                            playSound(getApplicationContext(), getAlarmUri());

                        } else {
                            melodyFtomFs=null;
//                            System.out.println("NULL"  + melodyFtomFs );
                            playSound(getApplicationContext(), getAlarmUri());

                        }

                    }
                });


        Intent intent = getIntent();
        id__ = intent.getStringExtra( "jobId" );
//        System.out.println( intent.getStringExtra( "jobId" ) );
        noteRef_addWork.document( id__ ).get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                MainWork main_work = documentSnapshot.toObject( MainWork.class );

                if (main_work.getDiscription() == "") {
                    description_yeah.setVisibility( View.GONE );
                    description_no.setVisibility( View.VISIBLE );

                    tvAlarmJobNameNoDesc.setText(  main_work.getName() );
                    txtToNotification = main_work.getName();
                    txtToNotificationDescript = main_work.getDiscription();
                    tvAlarmJobDataNoDesc.setText( Helper.dataToString(main_work.getDate()) );


                } else {
                    description_yeah.setVisibility( View.VISIBLE );
                    description_no.setVisibility( View.GONE );

                    tvAlarmJobName.setText( main_work.getName() );
                    txtToNotification = main_work.getName();
                    txtToNotificationDescript = main_work.getDiscription();
                    tvAlarmJobData.setText( Helper.dataToString(main_work.getDate()));
                    description.setText( main_work.getDiscription() );
                    txt = main_work.getName();
                    txt_desc = main_work.getDiscription();

                }
//
            }
        } );

        if (mTimer != null) {
            mTimer.cancel();
        }
        //ТАЙМЕР НА № МИНУТЫ
        mTimer = new Timer();
        mMyTimerTask = new MyTimerTask();
//        mTimer.schedule(mMyTimerTask, 180000);
        mTimer.schedule(mMyTimerTask, 10000);


        stop = findViewById(R.id.btn_stop);
        stop.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                vibrator.cancel();
                mTimer.cancel();
                noteRef_addWork.document(intent.getStringExtra( "jobId" )).update( "alarm1", null );
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                finish();
                offScreen();



            }
        } );

        pm = (PowerManager) getSystemService( Context.POWER_SERVICE);
        if (pm != null) {
            wl = pm.newWakeLock( FULL_WAKE_LOCK, String.valueOf( 1 ) );
        }
        wl.acquire();

        this.getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        makeVibro();

    }

    private void makeVibro() {
        long[] pattern = {0, 100, 1000, 300, 200, 1000, 500, 2000, 100};
        vibrator.vibrate(pattern, 2);

    }


    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource( context, alert );
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService( Context.AUDIO_SERVICE );
            if (audioManager.getStreamVolume( AudioManager.STREAM_ALARM ) != 0) {
                mMediaPlayer.setAudioStreamType( AudioManager.STREAM_ALARM );
                mMediaPlayer.prepare();
                mMediaPlayer.setLooping( true );
                mMediaPlayer.start();

            }
        } catch (IOException e) {


            setStandartMelody();
            System.out.println( "OOPS" );
        }
    }

    private void setStandartMelody() {
        Uri alert = RingtoneManager
                .getDefaultUri( RingtoneManager.TYPE_ALARM );
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri( RingtoneManager.TYPE_RINGTONE );
            }
        }

        try {
            mMediaPlayer.setDataSource( getApplicationContext(), alert );
            final AudioManager audioManager = (AudioManager) getApplicationContext()
                    .getSystemService( Context.AUDIO_SERVICE );
            if (audioManager.getStreamVolume( AudioManager.STREAM_ALARM ) != 0) {
                mMediaPlayer.setAudioStreamType( AudioManager.STREAM_ALARM );
                mMediaPlayer.prepare();
                mMediaPlayer.setLooping( true );
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println( "ERROR @@@@ " +e.getMessage()  );

        }


    }

    private Uri getAlarmUri() {
        if (melodyFtomFs==null){
            Uri alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if (alert == null) {
                    alert = RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                }
            }return alert;
        }

        else {

            try{

                Uri alert = Uri.parse(melodyFtomFs);
                return alert;

            } catch (Exception e){

                Uri alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alert == null) {
                    alert = RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    if (alert == null) {
                        alert = RingtoneManager
                                .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    }
                }return alert;


            }

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_POWER ) {

            mMediaPlayer.stop();
            vibrator.cancel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class MyTimerTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
            Bitmap bitmap = BitmapFactory.decodeResource( getResources(),R.mipmap.icon_time_check__round );

                    //УВЕДОМЛЕНИЕ ЧЕРЕЗ 3 МИНУТЫ ЕСЛИ НЕ ОТКЛЮЧЕН БУДУЛЬНИК

            Intent mainIntent = new Intent(getApplicationContext(), CalendarMainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, mainIntent, 0);

                    NotificationManager myNotifManager = (NotificationManager) getApplicationContext().getSystemService( Context.NOTIFICATION_SERVICE );

                    NotificationCompat.Builder builder = new NotificationCompat.Builder( AlarmActivity.this);
                    builder.setSmallIcon( R.drawable.tc )
                            .setLargeIcon( bitmap )
                            .setContentTitle(txtToNotification)
//                            .setSubText("Привет Товарищь")
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText( txtToNotificationDescript))

                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel( true )
                            .setLights( Color.BLUE, 500, 500)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                    int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                    myNotifManager.notify( m, builder.build() );
//                    offScreen();
                    noteRef_addWork.document(id__).update( "alarm1", null );
                    vibrator.cancel();
                    mMediaPlayer.stop();

                    offScreen();


                }
            });
        }
    }

    private void offScreen() {



//        Intent mainIntent = new Intent(getApplicationContext(), CalendarMainActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, mainIntent, 0);


//        System.out.println( "OFFFF" );

//        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, String.valueOf( 0 ) );
//        wl.acquire();
        wl.release();
//        this.getWindow().clearFlags(
//                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
//                );

        finishAndRemoveTask();


    }
}
