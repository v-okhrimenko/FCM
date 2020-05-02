package com.example.fcm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.fcm.helper.Helper;
import com.example.fcm.models.InfoToFs;
import com.example.fcm.models.Main_work_new;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;


public class Settings_activity extends AppCompatActivity {


    private ImageButton ukr, rus, eng;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Button avatar_select;
    private CircleImageView avatar_image;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_MELODY_REQUEST = 2;
    private Uri mImageUri;
    private Uri mMelodyUri;
    private String sMelodyUri;



    View view;
    TextView text;
    Toast toast;



    private StorageReference mStorageref;
//    private DatabaseReference mDataBaseRef;

    private Button melody;
    private Button selectName;
    private Button changePassword;
    private Button deleteAccount;
    private ImageButton btnPlay;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private String avatarname;
    private CircleImageView avatar;
    private TextView navUsername;
    private TextView alarmName;
    private TextView avatarName_in_app;
    private TextView customAvatarName;


    TextView pay;
    TextView nopay;
    private float sum_PayAll;
    private float sum_noPay;
    private int btnClickCheck = 0;
    private MediaPlayer mMediaPlayer;

    Uri myUri;

    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();

    private CollectionReference noteRef_addWork = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorks");
//    private DocumentReference noteRef_addAlarm = db_fstore.collection( user.getUid() ).document("My DB").collection("Settings").document("Melody");
    private DocumentReference noteRef_data = db_fstore.collection( user.getUid() ).document("Avatar");
    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");
    private CollectionReference noteRef_tempalte = db_fstore.collection( user.getUid() ).document( "My DB" ).collection( "Jobs_full" );


    TextView incompleatEvents;
    TextView noPayEvents;

    private String emailname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings );
        melody = (Button) findViewById( R.id.btn_melody ) ;
        alarmName = findViewById( R.id.tv_alarmName );
        btnPlay = findViewById( R.id.ib_play );
        btnPlay.setBackground( getDrawable( R.drawable.btn_play ) );
        avatarName_in_app = findViewById( R.id.tvAvatarName );
        customAvatarName = findViewById(R.id.customAvatarName);
        changePassword = findViewById(R.id.btn_change_pass);
        deleteAccount = findViewById(R.id.btn_clear_all_data_from_firestore);


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        avatar = (CircleImageView) headerView.findViewById(R.id.profile_image);
        incompleatEvents = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.need_finish));

        noPayEvents= (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.not_pay));
        navUsername = (TextView) headerView.findViewById(R.id.email_id);
        pay = (TextView) headerView.findViewById(R.id.header_PayAll_id_textView);
        nopay = (TextView) headerView.findViewById(R.id.header_noPay_id_textView);
        selectName = findViewById(R.id.btn_CustomName);


        //

        deleteAccount.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Settings_activity.this);
                LayoutInflater inflater = LayoutInflater.from(Settings_activity.this);
                final View regiserWindow = inflater.inflate(R.layout.delete_account, null);
                builder.setView(regiserWindow);

                final Button cancel = regiserWindow.findViewById( R.id.btn_cancel_delete_account );
                final Button delete = regiserWindow.findViewById( R.id.btn_delete_account );
                final ConstraintLayout constraintLayout = regiserWindow.findViewById( R.id.cl_error_chek_delete );
                final CheckBox chb_igree = regiserWindow.findViewById( R.id.chb_igree_delete_account );
                final TextView i_agree = regiserWindow.findViewById( R.id.tv_i_agree );



                final AlertDialog dialog = builder.create();
                dialog.setCancelable( false );
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.show();

                chb_igree.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //constraintLayout.setBackground( getResources().getDrawable( R.drawable.clear) );

                            i_agree.setTextColor( getColor( R.color.blue_lite ) );

                            chb_igree.setButtonTintList( ContextCompat.getColorStateList(Settings_activity.this, R.color.blue_lite) );

                        }
                    }
                } );


                delete.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(chb_igree.isChecked()){

                            noteRef_tempalte.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                        document.getReference().delete();

                                    }

                                    noteRef_addWork_Full.get().addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                                document.getReference().delete();

                                            }
                                            dialog.dismiss();
                                            byby();

                                            LayoutInflater inflater = getLayoutInflater();
                                            View layout = inflater.inflate(R.layout.custom_toast,
                                                    (ViewGroup) findViewById(R.id.custom_toast_container));

                                            TextView text = (TextView) layout.findViewById(R.id.text);
                                            text.setText(getString( R.string.database_cleared ));

                                            Toast toast = new Toast(getApplicationContext());
//                                    toast.setGravity( Gravity.CENTER_VERTICAL, 0, 0);
                                            toast.setDuration(Toast.LENGTH_LONG);
                                            toast.setView(layout);
                                            toast.show();



//                                    Toast.makeText( getBaseContext(), getString( R.string.database_cleared ) , Toast.LENGTH_SHORT).show();

                                        }
                                    } );

                                }
                            } );




                        } else {
//                            constraintLayout.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
//                            constraintLayout.setBackground( getResources().getDrawable( R.drawable.text_edit_error ) );
                            chb_igree.setButtonTintList( ContextCompat.getColorStateList(Settings_activity.this, R.color.red_lite) );
                            i_agree.setTextColor( getColor( R.color.red_lite ) );
                        }
                    }
                } );
                cancel.setOnClickListener( v1-> {dialog.dismiss();} );



            }
        } );

        changePassword.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings_activity.this);
                LayoutInflater inflater = LayoutInflater.from(Settings_activity.this);
                final View regiserWindow = inflater.inflate(R.layout.change_pass_inflater, null);
                builder.setView(regiserWindow);

                final Integer[] x = {0};
                final Integer[] y = {0};

//                final Editable[] ch_pas1 = new Editable[1];
                final CharSequence[] ch_pas1 = new CharSequence[1];

                final Boolean[] p1 = {false};
                final Boolean[] p2 = {false};

                final Button cancel = regiserWindow.findViewById( R.id.btn_cencel_change_pass );
                final Button change = regiserWindow.findViewById( R.id.btn_change_pass );
                final EditText pass1 = regiserWindow.findViewById( R.id.et_new_pass1 );
                final EditText pass_old = regiserWindow.findViewById( R.id.et_new_pass );
                final EditText pass2 = regiserWindow.findViewById( R.id.et_new_pass2 );
                final TextView pass2_label = regiserWindow.findViewById( R.id.tv_new_pass_label );

                final TextView sixChar = regiserWindow.findViewById( R.id.tv_no_short_as_6_char );
                final TextView error_current_password = regiserWindow.findViewById( R.id.no_current_pass );
                final TextView error_new_password = regiserWindow.findViewById( R.id.tv_error_new_password );

                final ImageView check_ok = regiserWindow.findViewById( R.id.iv_password_check_ok );
                final ImageView check_ok2 = regiserWindow.findViewById( R.id.iv_password2_check_ok );


                sixChar.setVisibility( View.INVISIBLE );
                check_ok.setVisibility( View.INVISIBLE );
                check_ok2.setVisibility( View.INVISIBLE );

                error_new_password.setVisibility( View.INVISIBLE );
                error_current_password.setVisibility( View.INVISIBLE );

                pass2.setEnabled( false );
                pass2.setBackground(getDrawable( R.drawable.text_edit_disabled ) );
                pass2_label.setTextColor( getResources().getColor( R.color.colorDivider ) );



                pass_old.addTextChangedListener( new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        pass_old.setBackground(getResources().getDrawable( R.drawable.text_edit_light_blue ));
                        error_current_password.setVisibility( View.INVISIBLE );
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length()<1){
                            pass_old.setBackground(getResources().getDrawable( R.drawable.text_edit_error ));
                            error_current_password.setVisibility( View.VISIBLE );
                        }

                    }
                } );

                pass1.addTextChangedListener( new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {


                        pass2.setText( null );
                        if (s.toString().contains( " " ) || s.toString().contains( ";" ) ||
                                s.toString().contains( ":" ) || s.toString().contains( "." ) ||
                                s.toString().contains( "," )){

                            pass1.setBackground(getDrawable( R.drawable.text_edit_error  ) );
                            check_ok.setVisibility( View.INVISIBLE );
                            pass2.setEnabled( false );
                            pass2.setBackground(getDrawable( R.drawable.text_edit_disabled ) );


                        } else {

                            pass1.setBackground(getDrawable( R.drawable.text_edit_light_blue ) );

                            x[0] = (pass1.getText().toString()).length();


                            if (x[0] >= 6) {
                                sixChar.setVisibility( View.INVISIBLE );
                                check_ok.setVisibility( View.VISIBLE );
                                p1[0] = true;
                                pass2.setEnabled( true );
                                pass2.setBackground(getDrawable( R.drawable.text_edit_light_blue ));
                                pass2_label.setTextColor( getResources().getColor( R.color.blue_lite ) );


                            }
                            else {

                                if (x[0] == 0) {
                                    sixChar.setVisibility( View.INVISIBLE );
                                    p1[0] = false;
                                    pass2.setEnabled( false );
                                    pass2.setBackground(getDrawable( R.drawable.text_edit_disabled ) );
                                    pass2_label.setTextColor( getResources().getColor( R.color.colorDivider ) );
                                } else {
                                    sixChar.setVisibility( View.VISIBLE );
                                    check_ok.setVisibility( View.INVISIBLE );
                                    p1[0] = false;
                                    pass2.setEnabled( false );
                                    pass2.setBackground(getDrawable( R.drawable.text_edit_disabled ) );
                                    pass2_label.setTextColor( getResources().getColor( R.color.colorDivider ) );
                                }

                            }
                        }

                        if(!pass1.getText().toString().equals( pass2.getText().toString() )){
                            check_ok2.setVisibility( View.INVISIBLE );
                        }
                            else {
                            if (!pass1.getText().toString().isEmpty() && !pass2.getText().toString().isEmpty()) {

                                check_ok2.setVisibility( View.VISIBLE );

                            }

                            else {
                                check_ok2.setVisibility( View.INVISIBLE );
                            }

                            }



                        }



                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                } );

                pass2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {



                        if ((pass1.getText().toString()).equals( pass2.getText().toString())) {
                            check_ok2.setVisibility( View.VISIBLE );
                            p2[0] = true;
                        } else {
                            check_ok2.setVisibility( View.INVISIBLE );
                            p2[0] = false;
                        }


                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        pass2.setBackground(getResources().getDrawable( R.drawable.text_edit_light_blue ));
                        error_new_password.setVisibility( View.INVISIBLE );


                        if ((pass1.getText().toString()).equals( pass2.getText().toString())) {
                            check_ok2.setVisibility( View.VISIBLE );
                            p2[0] = true;
                        } else {
                            check_ok2.setVisibility( View.INVISIBLE );
                            p2[0] = false;
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        if (s.length()<1){
                            pass2.setBackground(getResources().getDrawable( R.drawable.text_edit_error ));
                            error_new_password.setVisibility( View.VISIBLE );
                        }



                    }
                } );


                final AlertDialog dialog = builder.create();
                dialog.setCancelable( false );
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog.show();

                cancel.setOnClickListener( v1-> {dialog.dismiss();} );

                change.setOnClickListener( v2-> {
                    if (pass1.getText().toString().isEmpty()) {
                        pass1.setBackground(getResources().getDrawable( R.drawable.text_edit_error ));
                        sixChar.setVisibility( View.VISIBLE );

                    } else {
                        pass1.setBackground( getResources().getDrawable( R.drawable.text_edit_light_blue ) );
                        sixChar.setVisibility( View.INVISIBLE );
                    }


                    if (pass2.getText().toString().isEmpty()) {
                        pass2.setBackground(getResources().getDrawable( R.drawable.text_edit_error ));
                        error_new_password.setVisibility( View.VISIBLE );

                    } else {
                        pass2.setBackground(getResources().getDrawable( R.drawable.text_edit_light_blue ));
                        error_new_password.setVisibility( View.INVISIBLE );
                    }


                    if (pass_old.getText().toString().isEmpty()) {
                        pass_old.setBackground(getResources().getDrawable( R.drawable.text_edit_error ));
                        error_current_password.setVisibility( View.VISIBLE );

                    } else {
                        pass_old.setBackground(getResources().getDrawable( R.drawable.text_edit_light_blue ));
                        error_current_password.setVisibility( View.INVISIBLE );
                    }

                    if (pass1.getText().toString().equals( pass2.getText().toString() ) && (!pass1.getText().toString().isEmpty()) &&(!pass2.getText().toString().isEmpty()) &&(!pass_old.getText().toString().isEmpty()) ){

                        System.out.println( "OK" );



                        //СКрыть клаву в диалоговом окне
                        hideKeyboardFrom( getApplicationContext(), regiserWindow );

                        String newPassword  = pass1.getText().toString().trim();
                        String oldPass = pass_old.getText().toString().trim();
                        chagePasswordinFB(newPassword, oldPass);
                        dialog.dismiss();



                    }
                    else {
                        System.out.println( "ERROR" );
                    }
                } );
            }
        } );




        selectName.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings_activity.this);
                LayoutInflater inflater = LayoutInflater.from(Settings_activity.this);
                final View regiserWindow = inflater.inflate(R.layout.set_user_name_in_settings_inflater, null);
                builder.setView(regiserWindow);


                final Button ok = regiserWindow.findViewById( R.id.settings_btn_select );
                final Button cancel = regiserWindow.findViewById( R.id.settings_btn_cencel );
                final EditText name = regiserWindow.findViewById( R.id.settings_et_name);

                final AlertDialog dialog = builder.create();
                dialog.setCancelable( false );
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

                noteRef_data.get()
                        .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                            @Override

                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                InfoToFs infoToFs = documentSnapshot.toObject( InfoToFs.class );

                                if(infoToFs.getNickname() != ""){
                                    name.setText( infoToFs.getNickname() );
                                }
                            }
                        });


                dialog.show();

                ok.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((name.getText().toString().trim().isEmpty())){
                            name.setError( getString( R.string.no_avatar_name ) );
                        } else {
                            noteRef_data.update("nickname",name.getText().toString().trim());
                            customAvatarName.setText( name.getText().toString().trim() );
                            navUsername.setText( name.getText().toString().trim() );
                            dialog.dismiss();
                        }


                    }
                } );

                cancel.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                } );
            }
        } );


        btnPlay.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (btnClickCheck == 0){
                    playDemo( getApplicationContext(),sMelodyUri);
                    mMediaPlayer.start();
                    btnPlay.setBackground( getDrawable( R.drawable.stop ) );
                    btnClickCheck = 1;
                } else {

                    mMediaPlayer.stop();
                    btnPlay.setBackground( getDrawable( R.drawable.btn_play ) );
                    btnClickCheck = 0;

                }

            }
        } );

        melody.setOnClickListener( v-> {

            if(btnClickCheck == 1){
                mMediaPlayer.stop();
                btnPlay.setBackground( getDrawable( R.drawable.btn_play ) );
                btnClickCheck = 0;
            }




            Intent intent = new Intent( RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Ringtone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_RINGTONE);
            startActivityForResult( intent, PICK_MELODY_REQUEST);

        } );


        checkAvatar();




//        checkPerm();


        drawerLayout = findViewById(R.id.Drawer_layo);
        navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {


                    case  R.id.statistika:
                        item.setChecked( true );
                        startActivity(new Intent( Settings_activity.this, Statistic_activity.class));
                        //customType(Settings_activity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case  R.id.nastroiki:

                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;

                    case  R.id.not_pay:
                        item.setChecked( true );
                        startActivity(new Intent( Settings_activity.this, No_pay_activity.class));
                        //customType(Settings_activity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case  R.id.need_finish:
                        item.setChecked( true );
                        startActivity(new Intent( Settings_activity.this, OldNoFinishActivity.class));
                        //customType(Calendar_main_activity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.calendar_work:

                        item.setChecked( true );
                        startActivity(new Intent( Settings_activity.this, Calendar_main_activity.class));
                        //customType(Settings_activity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;


                    case R.id.new_work:
                        item.setChecked(true);
                        startActivity(new Intent( Settings_activity.this, AddJobActivity_new.class));
                        //customType(Settings_activity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.add_job:
                        item.setChecked(true);
                        startActivity(new Intent( Settings_activity.this, Add_work_template_activity.class));
                        //customType(Settings_activity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.exit_to_login:
                        item.setChecked(true);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent( Settings_activity.this, MainActivity.class));
                        //customType(Settings_activity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                }

                return false;
            }
        });

        mStorageref = FirebaseStorage.getInstance().getReference("Avatar");
//        mDataBaseRef = FirebaseDatabase.getInstance().getReference("Avatar");

        avatar_image = findViewById( R.id.profile_image_nastroiki );

        avatar_select = findViewById( R.id.textView_change_avatar );
        avatar_select.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnClickCheck == 1){
                    mMediaPlayer.stop();
                    btnPlay.setBackground( getDrawable( R.drawable.btn_play ) );
                    btnClickCheck = 0;
                }
                openFileChooser();
            }
        } );
    }

    private void byby() {

        startActivity(new Intent( Settings_activity.this, MainActivity.class));
        //customType(Settings_activity.this,"fadein-to-fadeout");
        overridePendingTransition(0, 0);
        finish();
    }

    private void chagePasswordinFB(String newPassword, String oldPass) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email,oldPass);

        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){


                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast,
                                        (ViewGroup) findViewById(R.id.custom_toast_container));

//                                LinearLayout ln = layout.findViewById( R.id.custom_toast_container );
                                ImageView iv = layout.findViewById( R.id.iv );

                                iv.setVisibility( View.INVISIBLE );



                                TextView text = (TextView) layout.findViewById(R.id.text);
                                text.setText(getString( R.string.somsing_wrong ));

                                Toast toast = new Toast(getApplicationContext());
//                                    toast.setGravity( Gravity.CENTER_VERTICAL, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();

//                                Toast.makeText( Settings_activity.this, getString( R.string.somsing_wrong ), Toast.LENGTH_LONG).show();



                            }else {


                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.custom_toast,
                                        (ViewGroup) findViewById(R.id.custom_toast_container));

                                LinearLayout ln = layout.findViewById( R.id.custom_toast_container );
                                ln.setBackground( getResources().getDrawable( R.drawable.event_bg ) );
                                ln.setBackgroundTintList( ContextCompat.getColorStateList(Settings_activity.this, R.color.green_lite) );


                                ImageView iv = layout.findViewById( R.id.iv );
                                iv.setBackground( getResources().getDrawable( R.drawable.check_ok_white ) );

                                TextView text = (TextView) layout.findViewById(R.id.text);
                                text.setText(getString( R.string.password_successfully_modified));

                                Toast toast = new Toast(getApplicationContext());
//                                    toast.setGravity( Gravity.CENTER_VERTICAL, 0, 0);
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setView(layout);
                                toast.show();
                                //Toast.makeText( Settings_activity.this, getString( R.string.password_successfully_modified ), Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }else {

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast,
                            (ViewGroup) findViewById(R.id.custom_toast_container));

                    //LinearLayout ln = layout.findViewById( R.id.custom_toast_container );
                    ImageView iv = layout.findViewById( R.id.iv );
                    iv.setBackground( getResources().getDrawable( R.drawable.alert ) );
                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText(getString( R.string.authentication_failed ));

                    Toast toast = new Toast(getApplicationContext());
//                                    toast.setGravity( Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();


//                    Toast.makeText( Settings_activity.this, getString( R.string.authentication_failed ), Toast.LENGTH_LONG).show();

                }
            }
        });
    }


    private void playDemo(Context context, String melody) {
        Uri alert = Uri.parse( String.valueOf( melody ) );
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource( context, alert );
            mMediaPlayer.setAudioStreamType( AudioManager.STREAM_ALARM );
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    private void setLocale(String lang) {
//        Locale locale = new Locale(lang);
//        Locale.setDefault(locale);
//        Configuration config = new Configuration();
//        config.locale = locale;
//        getBaseContext().getResources().updateConfiguration( config, getBaseContext().getResources().getDisplayMetrics() );
//
//        SharedPreferences.Editor editor = getSharedPreferences( "Settings", MODE_PRIVATE ).edit();
//        editor.putString( "My_Lang", lang );
//        editor.apply();
//
//    }
//
//    public void loadLockale(){
//        SharedPreferences prefs = getSharedPreferences( "Settings", Activity.MODE_PRIVATE );
//        String language = prefs.getString( "My_Lang" , "");
//        setLocale( language );
//    }


    private void checkAvatar() {



        Date date = new Date(System.currentTimeMillis());
        String d1 = Helper.dataToString( date );
        Date date_ok = Helper.stringToData( d1 );
        ArrayList<Float> allSumm = new ArrayList<>();
        ArrayList<Float> allSummP = new ArrayList<>();
        allSumm.clear();
        allSummP.clear();

        sum_PayAll = 0;
        sum_noPay = 0;

        Date date_ = new Date(System.currentTimeMillis());
        String d1_ = Helper.dataToString( date_ );
        Date date_ok_ = Helper.stringToData( d1_ );

        noteRef_addWork_Full.whereLessThan("date", date_ok_ ).whereEqualTo( "needFinish", true )
                .whereEqualTo( "end",null ).orderBy( "date", Query.Direction.ASCENDING ).get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots.size() != 0) {
                            incompleatEvents.setGravity( Gravity.CENTER_VERTICAL);
                            //incompleatEvents.
                            incompleatEvents.setTextAlignment( View.TEXT_ALIGNMENT_CENTER);
                            incompleatEvents.setTypeface(null, Typeface.BOLD);
                            incompleatEvents.setTextColor(getResources().getColor(R.color.white));
                            incompleatEvents.setBackground( getResources().getDrawable( R.drawable.red_dot , null) );

                            incompleatEvents.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY));
                            incompleatEvents.setText( String.valueOf( queryDocumentSnapshots.size() ) );

                        } else {
                            try {
                                incompleatEvents.setGravity( Gravity.CENTER_VERTICAL);
                                //incompleatEvents.
                                incompleatEvents.setTextAlignment( View.TEXT_ALIGNMENT_CENTER);
                                incompleatEvents.setTypeface(null, Typeface.BOLD);
                                incompleatEvents.setTextColor(getResources().getColor(R.color.white));
                                incompleatEvents.setBackground( getResources().getDrawable( R.drawable.red_dot , null) );

                                //incompleatEvents.setLayoutParams(new DrawerLayout.LayoutParams( DrawerLayout.LayoutParams.WRAP_CONTENT, DrawerLayout.LayoutParams.WRAP_CONTENT));
                                incompleatEvents.setBackground( getResources().getDrawable( R.drawable.clear , null) );
                                incompleatEvents.setText( "" );
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });


        noteRef_addWork_Full.whereEqualTo( "status", false ).whereLessThan("date", date_ok).orderBy( "date", Query.Direction.DESCENDING ).get()
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getMessage());
                    }
                } )
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() != 0) {
                            noPayEvents.setGravity( Gravity.CENTER_VERTICAL);
                            //incompleatEvents.
                            noPayEvents.setTextAlignment( View.TEXT_ALIGNMENT_CENTER);
                            noPayEvents.setTypeface(null, Typeface.BOLD);
                            noPayEvents.setTextColor(getResources().getColor(R.color.white));
                            noPayEvents.setBackground( getResources().getDrawable( R.drawable.red_dot , null) );
                            noPayEvents.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY));
                            noPayEvents.setText( String.valueOf( queryDocumentSnapshots.size() ) );

                            for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                                Main_work_new main_work = documentSnapshot.toObject( Main_work_new.class );
                                allSumm.add( main_work.getZarabotanoFinal() );
                            }
                            sum_noPay=0;
                            if(allSumm.size()<1){
                                nopay.setText( "0");

                            } else {
                                for(int i=0; i<allSumm.size(); i++) {
                                    sum_noPay=sum_noPay+ allSumm.get( i );
                                }
                                nopay.setText( String.valueOf(sum_noPay ));

                            }

                        } else {

                            nopay.setText( "0" );

                            try {
                                noPayEvents.setGravity( Gravity.CENTER_VERTICAL);
                                noPayEvents.setTextAlignment( View.TEXT_ALIGNMENT_CENTER);
                                noPayEvents.setTypeface(null, Typeface.BOLD);
                                noPayEvents.setTextColor(getResources().getColor(R.color.white));
                                noPayEvents.setBackground( getResources().getDrawable( R.drawable.red_dot , null) );

                                noPayEvents.setBackground( getResources().getDrawable( R.drawable.clear , null) );
                                noPayEvents.setText( "" );
                            } catch (Resources.NotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } );


        avatarname = auth.getCurrentUser().getUid();
        avatarName_in_app.setText( auth.getCurrentUser().getEmail());
        noteRef_data.get()
                .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                    @Override

                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        InfoToFs infoToFs = documentSnapshot.toObject( InfoToFs.class );

//                        if(infoToFs.getNickname() != ""){
//                            customAvatarName.setText( infoToFs.getNickname() );
//
//                            navUsername.setText(infoToFs.getNickname());

                        //}
                        if(infoToFs.getNickname() == null || infoToFs.getNickname()=="" ) {
                            navUsername.setText(auth.getCurrentUser().getEmail());
                        } else {
                            navUsername.setText( infoToFs.getNickname());
                            customAvatarName.setText( infoToFs.getNickname() );
                        }
                        try {
                            if(infoToFs.getMelody() !=null ) {

                                Uri uri = Uri.parse( infoToFs.getMelody() );
                                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
                                String title = ringtone.getTitle(getApplicationContext());
                                sMelodyUri = infoToFs.getMelody();
                                alarmName.setText( title );

                                if (title=="") {
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

                                    Ringtone ringtone_new = RingtoneManager.getRingtone( getApplicationContext(), alert );
                                    String title_new = ringtone_new.getTitle( getApplicationContext() );
                                    sMelodyUri = String.valueOf( alert );
                                    alarmName.setText( title_new );

                                }
//
                            } else {
                                Uri alert = RingtoneManager
                                        .getDefaultUri(RingtoneManager.TYPE_ALARM);
                                if (alert == null) {
                                    alert = RingtoneManager
                                            .getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
                                    if (alert == null) {
                                        alert = RingtoneManager
                                                .getDefaultUri( RingtoneManager.TYPE_RINGTONE );
                                    }
                                }
                                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), alert);
                                String title = ringtone.getTitle(getApplicationContext());
                                sMelodyUri = String.valueOf( alert );
                                alarmName.setText( title );}
                        }
                        catch (Exception e){
                            System.out.println( e.getMessage() );


                        }

                        if(infoToFs.getImg() != null){
                            myUri = Uri.parse( infoToFs.getImg() );
                            Picasso.with( Settings_activity.this ).load( myUri ).into( avatar_image );
                            Picasso.with( Settings_activity.this ).load( myUri ).into( avatar );


                        } else {
                            avatar_image.setImageResource( R.drawable.ic_account_circle_black_200dp );
                            avatar.setImageResource( R.drawable.ic_account_circle_black_24dp );

                        }

                    }
                });

    }

    private void checkPerm() {
        String [] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


        if (EasyPermissions.hasPermissions( this,perms )){
//
        } else {

            String info = getString(R.string.needPermission);
            EasyPermissions.requestPermissions( this,info,123,perms );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        EasyPermissions.onRequestPermissionsResult( requestCode, permissions, grantResults, this );
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType( "image/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( intent, PICK_IMAGE_REQUEST );

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

    if (requestCode == PICK_MELODY_REQUEST && resultCode == RESULT_OK){


            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                sMelodyUri = uri.toString();

                // ИМЯ РИНГТОНА




                Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
                String title = ringtone.getTitle(this);
                alarmName.setText( title );

                if (title==null){
                }

                uploadAlarm();
            }

            else {
                sMelodyUri = null;
                uploadAlarm();

            }



        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with( this ).load( mImageUri ).into(avatar_image);
            Picasso.with( Settings_activity.this ).load( mImageUri ).into( avatar );

            NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
            View headerView = navigationView.getHeaderView(0);

            uploadFile();

        }
    }

    private void uploadAlarm() {

        InfoToFs uploadMelody = new InfoToFs();
        uploadMelody.setMelody( sMelodyUri );

        noteRef_data.update( "melody", sMelodyUri);
    }

    private String getFileExtention (Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType( cR.getType( uri ) );
    }

    private void uploadFile() {


        Runnable upd = new Runnable() {
            public void run() {
        if (mImageUri != null) {
            String avatarname;
            avatarname = auth.getCurrentUser().getUid();


            StorageReference fileReference = mStorageref.child( avatarname+"."+getFileExtention( mImageUri ));
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 15, baos);
                byte[] data__ = baos.toByteArray();
                fileReference.putBytes( data__ )
                        .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String av_save = getString( R.string.avatarAddd );

                                Toast.makeText( Settings_activity.this, av_save , Toast.LENGTH_SHORT).show();

                                mStorageref.child(avatarname+"."+getFileExtention( mImageUri )).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
//                                        InfoToFs infoToFs = new InfoToFs();
//                                        infoToFs.setImg( uri.toString() );
//                                        infoToFs.setMelody( null );

                                        noteRef_data.update("img",uri.toString())
                                                .addOnSuccessListener( new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {


                                                    }
                                                } )
                                                .addOnFailureListener( new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText( Settings_activity.this, e.getMessage().toString(), Toast.LENGTH_SHORT ).show();
                                                    }
                                                } );

                                    }
                                });


                            }
                        } )
                        .addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String av_no_save = getString( R.string.avatarNoAdd );
                                Toast.makeText( Settings_activity.this, av_no_save+e.getMessage() , Toast.LENGTH_SHORT).show();

                            }
                        } );

            } catch (IOException e) {
                e.printStackTrace();
            }



        } else {


        }

    }
        };
        new Thread(upd).start();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            startActivity(new Intent( Settings_activity.this, Calendar_main_activity.class));
            //customType(Settings_activity.this,"fadein-to-fadeout");
            overridePendingTransition(0, 0);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if(btnClickCheck==1){
                mMediaPlayer.stop();
                btnPlay.setBackground( getDrawable( R.drawable.btn_play ) );
                btnClickCheck = 0;
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(btnClickCheck==1){
                mMediaPlayer.stop();
                btnPlay.setBackground( getDrawable( R.drawable.btn_play ) );
                btnClickCheck = 0;
            }

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
