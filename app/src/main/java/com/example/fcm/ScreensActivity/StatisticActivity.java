package com.example.fcm.ScreensActivity;

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
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.fcm.R;
import com.example.fcm.helper.Helper;
import com.example.fcm.models.MainWork;
import com.example.fcm.models.UserInfoToFirestore;
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
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatisticActivity extends AppCompatActivity {

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
    private ImageButton showDrawer;


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
        //navView.setItemIconTintList(null);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_month, R.id.navigation_year, R.id.navigation_projects )
                .build();
        NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment );
//        NavigationUI.setupActionBarWithNavController( this, navController, appBarConfiguration );

        NavigationUI.setupWithNavController( navView, navController );

        showDrawer = findViewById( R.id.ib_showDrawer5 );
        drawerLayout = findViewById(R.id.Drawer_layo);
        showDrawer.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer( Gravity.LEFT );
            }
        } );

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
                        startActivity(new Intent( StatisticActivity.this, SettingsActivity.class));
                        //customType(StatisticActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case  R.id.need_finish:
                        item.setChecked( true );
                        startActivity(new Intent( StatisticActivity.this, OldNoFinishActivity.class));
                        //customType(CalendarMainActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case  R.id.not_pay:
                        item.setChecked( true );
                        startActivity(new Intent( StatisticActivity.this, NoPayActivity.class));
                        //customType(StatisticActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.calendar_work:
                        item.setChecked(true);
                        startActivity(new Intent( StatisticActivity.this, CalendarMainActivity.class));
                        //customType(StatisticActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);

                        finish();
//                        drawerLayout.closeDrawers();
                        return true;


                    case R.id.new_work:
                        item.setChecked( true );
                        startActivity(new Intent( StatisticActivity.this, AddJobActivity_new.class));
                        //customType(StatisticActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.add_job:
                        item.setChecked(true);
                        startActivity(new Intent( StatisticActivity.this, AddTemplateActivity.class));
                        //customType(StatisticActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.exit_to_login:
                        item.setChecked(true);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent( StatisticActivity.this, MainActivity.class));
                        overridePendingTransition(0, 0);
                        //customType(StatisticActivity.this,"fadein-to-fadeout");
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
                .whereEqualTo( "end",null ).orderBy( "date", Query.Direction.ASCENDING ).get( Source.CACHE)
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

        noteRef_data.get( Source.CACHE)
                .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                           @Override
                                           public void onSuccess(DocumentSnapshot documentSnapshot) {

                                               UserInfoToFirestore userInfoToFirestore = documentSnapshot.toObject( UserInfoToFirestore.class );
                                               try {
                                                   if(userInfoToFirestore.getNickname() == null || userInfoToFirestore.getNickname()=="") {
                                                       navUsername.setText(auth.getCurrentUser().getEmail());

                                                   } else {
                                                       navUsername.setText( userInfoToFirestore.getNickname());
                                                   }

                                                   if(userInfoToFirestore.getImg() != null){
                                                       final Uri myUri = Uri.parse( userInfoToFirestore.getImg() );
                                                       Picasso.with( StatisticActivity.this ).load( myUri ).into( avatar );

                                                   } else {
                                                       avatar.setImageResource( R.drawable.ic_account_circle_black_24dp );
                                                   }

                                               }catch (Exception e) {
                                                   UserInfoToFirestore userInfoToFirestore1 = new UserInfoToFirestore();
                                                   userInfoToFirestore1.setNickname( "" );
                                                   userInfoToFirestore1.setMelody( "" );

                                                   noteRef_data.set( userInfoToFirestore1 );
                                               }
                                           }
                                       }
                );

        noteRef_addWork_Full.whereEqualTo( "status", false ).whereLessThan("date", date_ok).orderBy( "date", Query.Direction.DESCENDING ).get( Source.CACHE)
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
                                MainWork main_work = documentSnapshot.toObject( MainWork.class );
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


            startActivity(new Intent( StatisticActivity.this, CalendarMainActivity.class));
            overridePendingTransition(0, 0);
            //customType(StatisticActivity.this,"fadein-to-fadeout");
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//



    }


