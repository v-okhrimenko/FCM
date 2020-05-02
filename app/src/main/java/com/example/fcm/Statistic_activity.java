package com.example.fcm;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fcm.helper.Helper;
import com.example.fcm.models.InfoToFs;
import com.example.fcm.models.Main_work_new;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Statistic_activity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private CircleImageView avatar;
    private int sum_PayAll;
    private float sum_noPay;
    private String avatarname;
    TextView incompleatEvents;
    private String emailname;
    TextView noPayEvents;


    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = db_fstore.collection( user.getUid() ).document("My DB").collection("Jobs");
    public CollectionReference noteRef_addWork = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorks");
    private DocumentReference noteRef_data = db_fstore.collection( user.getUid() ).document("Avatar");
    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_statistic );
        BottomNavigationView navView = findViewById( R.id.nav_view );
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_month, R.id.navigation_year, R.id.navigation_projects )
                .build();
        NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment );
//        NavigationUI.setupActionBarWithNavController( this, navController, appBarConfiguration );

        NavigationUI.setupWithNavController( navView, navController );


        drawerLayout = findViewById(R.id.Drawer_layo);
        navigationView = findViewById(R.id.navigationView);


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        avatar = (CircleImageView) headerView.findViewById(R.id.profile_image);
        incompleatEvents = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.need_finish));

        noPayEvents= (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.not_pay));

        dataToDrawer();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case  R.id.statistika:
                        item.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;


                    case  R.id.nastroiki:
                        item.setChecked( true );
                        startActivity(new Intent( Statistic_activity.this, Settings_activity.class));
                        //customType(Statistic_activity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case  R.id.need_finish:
                        item.setChecked( true );
                        startActivity(new Intent( Statistic_activity.this, OldNoFinishActivity.class));
                        //customType(Calendar_main_activity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case  R.id.not_pay:
                        item.setChecked( true );
                        startActivity(new Intent( Statistic_activity.this, No_pay_activity.class));
                        //customType(Statistic_activity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.calendar_work:

                        item.setChecked(true);
                        startActivity(new Intent( Statistic_activity.this, Calendar_main_activity.class));
                        //customType(Statistic_activity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
//                        drawerLayout.closeDrawers();
                        return true;


                    case R.id.new_work:
                        item.setChecked( true );
                        startActivity(new Intent( Statistic_activity.this, AddJobActivity_new.class));
                        //customType(Statistic_activity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.add_job:
                        item.setChecked(true);
                        startActivity(new Intent( Statistic_activity.this, Add_work_template_activity.class));
                        //customType(Statistic_activity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.exit_to_login:
                        item.setChecked(true);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent( Statistic_activity.this, MainActivity.class));
                        overridePendingTransition(0, 0);
                        //customType(Statistic_activity.this,"fadein-to-fadeout");
                        finish();
                }

                return false;
            }
        });
    }
    private void dataToDrawer() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.email_id);
        navUsername.setText(auth.getCurrentUser().getEmail());
        emailname = auth.getCurrentUser().getEmail();

        Date date = new Date(System.currentTimeMillis());
        String d1 = Helper.dataToString( date );
        Date date_ok = Helper.stringToData( d1 );
        ArrayList<Float> allSumm = new ArrayList<>();
        ArrayList<Float> allSummP = new ArrayList<>();
        allSumm.clear();
        allSummP.clear();

        sum_noPay = 0f;

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

        noteRef_data.get()
                .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                           @Override
                                           public void onSuccess(DocumentSnapshot documentSnapshot) {

                                               InfoToFs infoToFs = documentSnapshot.toObject( InfoToFs.class );
                                               try {
                                                   if(infoToFs.getNickname() == null || infoToFs.getNickname()=="") {
                                                       navUsername.setText(auth.getCurrentUser().getEmail());

                                                   } else {
                                                       navUsername.setText( infoToFs.getNickname());
                                                   }

                                                   if(infoToFs.getImg() != null){
                                                       final Uri myUri = Uri.parse( infoToFs.getImg() );
                                                       Picasso.with( Statistic_activity.this ).load( myUri ).into( avatar );

                                                   } else {
                                                       avatar.setImageResource( R.drawable.ic_account_circle_black_24dp );
                                                   }

                                               }catch (Exception e) {
                                                   InfoToFs infoToFs1 = new InfoToFs();
                                                   infoToFs1.setNickname( "" );
                                                   infoToFs1.setMelody( "" );

                                                   noteRef_data.set( infoToFs1 );
                                               }
                                           }
                                       }
                );

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
                                TextView nopay = (TextView) headerView.findViewById(R.id.header_noPay_id_textView);

                                nopay.setText( "0");

                            } else {
                                for(int i=0; i<allSumm.size(); i++) {
                                    sum_noPay=sum_noPay+ allSumm.get( i );
                                }
                                TextView nopay = (TextView) headerView.findViewById(R.id.header_noPay_id_textView);
                                nopay.setText( String.valueOf(sum_noPay ));

                            }

                        } else {

                            TextView nopay = (TextView) headerView.findViewById(R.id.header_noPay_id_textView);
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
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            startActivity(new Intent( Statistic_activity.this, Calendar_main_activity.class));
            overridePendingTransition(0, 0);
            //customType(Statistic_activity.this,"fadein-to-fadeout");
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    }


