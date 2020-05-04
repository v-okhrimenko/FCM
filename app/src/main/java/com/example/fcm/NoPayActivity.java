package com.example.fcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fcm.recycleviewadapter.CalendarNoPayRv;
import com.example.fcm.helper.Helper;
import com.example.fcm.fixedrate.FixedJobReview;
import com.example.fcm.jobreview.ForHourJobReview;
import com.example.fcm.jobreview.ForSmenaJobReview;
import com.example.fcm.models.UserInfoToFirestore;
import com.example.fcm.models.MainWork;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class NoPayActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    //private FloatingActionButton newWork;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private int position;
    private String name_ud;
    private String desc_ud;
    private Float price_ud;
    private Date date_ud;
    private int uniq_ud;
    private Boolean chek_ud;

//    private ImageButton menu;
    TextView allNoPay;


    private float sum_noPay;
    private String id_ud;
    private int sum_PayAll;
    private String avatarname;
    private CircleImageView avatar;

    DocumentSnapshot ds;
    private String emailname;


    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    private CollectionReference noteRef_addWork = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorks");
    private DocumentReference noteRef_data = db_fstore.collection( user.getUid() ).document("Avatar");

    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");

    private RecyclerView recyclerView;
    public static CalendarNoPayRv adapter;
    ConstraintLayout constraintLayout;

    TextView incompleatEvents;
    TextView noPayEvents;
//    incompleatEvents = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
//    findItem(R.id.need_finish));
//
//    noPayEvents= (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
//    findItem(R.id.not_pay));
//    private String emailname;



    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration( config, getBaseContext().getResources().getDisplayMetrics() );

        SharedPreferences.Editor editor = getSharedPreferences( "Settings", MODE_PRIVATE ).edit();
        editor.putString( "My_Lang", lang );
        editor.apply();

    }


    public void loadLockale(){
        SharedPreferences prefs = getSharedPreferences( "Settings", Activity.MODE_PRIVATE );
        String language = prefs.getString( "My_Lang" , "");
        setLocale( language );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
//        loadLockale();
        setContentView( R.layout.activity_no_pay );


        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        avatar = (CircleImageView) headerView.findViewById(R.id.profile_image);
        incompleatEvents = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.need_finish));

        noPayEvents= (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.not_pay));


        chekNoInfo();
        dataToDrawer();
        addToNoPayRv();

        constraintLayout = findViewById( R.id.an_la );
        recyclerView = findViewById( R.id.main_work_rv_nopay );
//        startLayoutAnimation(constraintLayout,0);


//         ПОКАЗЫВАТЬ ВИДЕО
//
//        VideoView vv = (VideoView) findViewById(R.id.videoView);
//        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.underconstruction);
//        vv.setVideoURI(uri);
//
//        vv.start();
//
//        vv.setOnPreparedListener (new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.setLooping(true);
//            }
//        });

        drawerLayout = findViewById(R.id.Drawer_layo);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case  R.id.statistika:
                        item.setChecked( true );
                        startActivity(new Intent( NoPayActivity.this, StatisticActivity.class));
                        //customType(NoPayActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case  R.id.nastroiki:
                        item.setChecked( true );
                        startActivity(new Intent( NoPayActivity.this, SettingsActivity.class));
                        //customType(NoPayActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case  R.id.not_pay:
                        item.setChecked( true );
                        drawerLayout.closeDrawers();
                        return true;

                    case  R.id.need_finish:
                        item.setChecked( true );
                        startActivity(new Intent( NoPayActivity.this, OldNoFinishActivity.class));
                        //customType(CalendarMainActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.calendar_work:

                        item.setChecked(true);
                        startActivity(new Intent( NoPayActivity.this, CalendarMainActivity.class));
                        //customType(NoPayActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;


                    case R.id.new_work:
                        item.setChecked(true);
                        startActivity(new Intent( NoPayActivity.this, AddJobActivity_new.class));
                        //customType(NoPayActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.add_job:
                        item.setChecked(true);
                        startActivity(new Intent( NoPayActivity.this, AddTemplateActivity.class));
                        //customType(NoPayActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                        return true;

                    case R.id.exit_to_login:
                        item.setChecked(true);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent( NoPayActivity.this, MainActivity.class));
                        //customType(NoPayActivity.this,"fadein-to-fadeout");
                        overridePendingTransition(0, 0);
                        finish();
                }

                return false;
            }
        });

    }


    private void chekNoInfo() {
        Date date = new Date(System.currentTimeMillis());
        String d1 = Helper.dataToString( date );
        Date date_ok = Helper.stringToData( d1 );
        ArrayList<Float> chek_no_info = new ArrayList<>();

        noteRef_addWork_Full.whereEqualTo( "status", false ).whereLessThan("date", date_ok).orderBy( "date", Query.Direction.DESCENDING ).get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                            MainWork main_work = documentSnapshot.toObject( MainWork.class );
                            chek_no_info.add( main_work.getZarabotanoFinal() );

                        }
                        //System.out.println("ALL SUMMA" + chek_no_info );

                        if (chek_no_info.isEmpty()) {

                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( NoPayActivity.this );
                            LayoutInflater inflater = LayoutInflater.from( NoPayActivity.this );
                            final View regiserWindow = inflater.inflate( R.layout.show_alert_info_one_button, null );
                            builder.setView( regiserWindow );
                            final TextView shapka_txt = regiserWindow.findViewById( R.id.tv_info_shapka );
                            shapka_txt.setText( getResources().getString( R.string.Congratulations ) );
                            final TextView txt_txt = regiserWindow.findViewById( R.id.tv_info_txt );
                            txt_txt.setText(  getResources().getString( R.string.no_no_pay_info ) );
                            final Button ok = regiserWindow.findViewById( R.id.btn_info_ok );
                            final androidx.appcompat.app.AlertDialog dialog = builder.create();
                            dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                            dialog.setCancelable( false );
                            dialog.show();
                            ok.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    startActivity(new Intent( NoPayActivity.this, CalendarMainActivity.class));
                                    overridePendingTransition(0, 0);
                                    finish();
                                    dialog.dismiss();

                                } });

                        }
                    }
                } );
    }

    private void addToNoPayRv() {

//        scek_sum_no_pay();
        dataToDrawer();
        Date date = new Date(System.currentTimeMillis());
        String d1 = Helper.dataToString( date );
        Date date_ok = Helper.stringToData( d1 );

        Query query = noteRef_addWork_Full.whereEqualTo( "status", false ).whereLessThan("date", date_ok).orderBy( "date", Query.Direction.DESCENDING );


        FirestoreRecyclerOptions<MainWork> options = new FirestoreRecyclerOptions.Builder<MainWork>()
                .setQuery(query, MainWork.class)
                .build();
        adapter = new CalendarNoPayRv( options );
        recyclerView = findViewById( R.id.main_work_rv_nopay );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        runAnimation( recyclerView,0 );


        new ItemTouchHelper( new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {

            @Override
            public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
                return 0.6f;
            }


            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                position = viewHolder.getAdapterPosition();
                switch (direction) {

                    case ItemTouchHelper.LEFT:
                        add_to_undelete_data( position );
                        showUndoSnackbar_pay();

                        adapter.update( viewHolder.getAdapterPosition() );
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);
//                        scek_sum_no_pay();

                        dataToDrawer();
                        chekNoInfo();
                        break;
//
                    case ItemTouchHelper.RIGHT:
                        add_to_undelete_data(position);
                        delete_item(position);
                        showUndoSnackbar();
                        //chekNoInfo();
                        recyclerView.setAdapter(adapter);

                        break;

                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor( ContextCompat.getColor( NoPayActivity.this, R.color.white ) )
                        .addSwipeLeftActionIcon( R.drawable.ic_monetization )
                        .addSwipeRightActionIcon( R.drawable.del_icon_red )
                        .addSwipeRightBackgroundColor( ContextCompat.getColor( NoPayActivity.this, R.color.white ))
                        .create()
                        .decorate();
                super.onChildDraw( c, recyclerView, viewHolder, dX/5, dY, actionState, isCurrentlyActive );

            }
        } ).attachToRecyclerView( recyclerView );

        adapter.setOnItemClickListener( new CalendarNoPayRv.onItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                MainWork main_work = documentSnapshot.toObject( MainWork.class );
                System.out.println(main_work.getUniqId());
                String priceFix = String.valueOf(main_work.getPrice_fixed());
                String priceHour = String.valueOf(main_work.getPrice_hour());

                String priceSm = String.valueOf(main_work.getPrice_smena());
                String durationSm = String.valueOf(main_work.getSmena_duration());
                String overTimeProcent = String.valueOf(main_work.getOvertime_pocent());
                String startTime = String.valueOf(main_work.getStart());
                String endTime = String.valueOf(main_work.getEnd());
                String finalCost = String.valueOf(main_work.getZarabotanoFinal());

                String uid =String.valueOf( main_work.getUniqId());
                String name = main_work.getName();
                String date = String.valueOf(Helper.dataToString(main_work.getDate()));
                String description = main_work.getDiscription();
                String valuta = main_work.getValuta();
                String status = String.valueOf(main_work.getStatus());
                String alarm;
                String rounded_nimutes = String.valueOf( main_work.getRounded_minut() );
                String half_shiht = String.valueOf( main_work.getHalf_shift() );
                String half_shiht_hours = String.valueOf( main_work.getHalf_shift_hours() );


                String documentName = documentSnapshot.getId();

                if(main_work.getAlarm1()==null){
                    alarm = null;
                } else {
                    alarm = String.valueOf(main_work.getAlarm1());
                }



                String jobType = main_work.getTempalte_type();
                switch (jobType){
                    case "fixed":
                        Intent intent = new Intent( NoPayActivity.this, FixedJobReview.class);
                        intent.putExtra("uid", uid);
                        intent.putExtra("name", name);
                        intent.putExtra("price", priceFix );
                        intent.putExtra("date", date );
                        intent.putExtra("description", description );
                        intent.putExtra("valuta", valuta );
                        intent.putExtra("status", status );
                        intent.putExtra("alarm", alarm );
                        intent.putExtra("documentName", documentName );

                        startActivity(intent);
                        break;

                    case "for smena":

                        Intent intent_forSmena = new Intent( NoPayActivity.this, ForSmenaJobReview.class);

                        intent_forSmena.putExtra("uid", uid);
                        intent_forSmena.putExtra("name", name);
                        intent_forSmena.putExtra("priceShift", priceSm);
                        intent_forSmena.putExtra("durationSm", durationSm);
                        intent_forSmena.putExtra("overTimeProcent", overTimeProcent);
                        intent_forSmena.putExtra("startTime", startTime);
                        intent_forSmena.putExtra("endTime", endTime);
                        intent_forSmena.putExtra("finalCost", finalCost);
                        intent_forSmena.putExtra("date", date );
                        intent_forSmena.putExtra("description", description );
                        intent_forSmena.putExtra("valuta", valuta );
                        intent_forSmena.putExtra("status", status );
                        intent_forSmena.putExtra("alarm", alarm );
                        intent_forSmena.putExtra("documentName", documentName );
                        intent_forSmena.putExtra("rounded_nimutes", rounded_nimutes );
                        intent_forSmena.putExtra("half_shiht", half_shiht );
                        intent_forSmena.putExtra("half_shiht_hours", half_shiht_hours );

                        startActivity(intent_forSmena);
                        break;
                    case "for hour":

                        Intent intent_forHour = new Intent( NoPayActivity.this, ForHourJobReview.class);

                        intent_forHour.putExtra("uid", uid);
                        intent_forHour.putExtra("name", name);
                        intent_forHour.putExtra("priceHour", priceHour);
                        intent_forHour.putExtra("startTime", startTime);
                        intent_forHour.putExtra("endTime", endTime);
                        intent_forHour.putExtra("finalCost", finalCost);
                        intent_forHour.putExtra("date", date );
                        intent_forHour.putExtra("description", description );
                        intent_forHour.putExtra("valuta", valuta );
                        intent_forHour.putExtra("status", status );
                        intent_forHour.putExtra("alarm", alarm );
                        intent_forHour.putExtra("documentName", documentName );
                        intent_forHour.putExtra("rounded_nimutes", rounded_nimutes );

                        startActivity(intent_forHour);
                        break;
                }
            }

        } );


    }

    private void showUndoSnackbar_pay() {
        View view = findViewById(R.id.Drawer_layo);
        String undo = getString(R.string.UNDO_PAY);
        String itemdel = getString(R.string.Itempayd);
        Snackbar snackbar = Snackbar.make(view, itemdel, Snackbar.LENGTH_LONG);
        snackbar.setAction(undo, v ->undoPaid(position));
        snackbar.show();

    }

    private void undoPaid(int position) {

        id_ud = ds.getId();
        noteRef_addWork_Full.document( id_ud ).update( "status", false ) ;
        recyclerView.setAdapter(adapter);
        dataToDrawer();

    }

    private void startLayoutAnimation(ConstraintLayout constraintLayout, int type) {
        Context context = constraintLayout.getContext();
        LayoutAnimationController controller = null;

        if(type ==0)
            controller = AnimationUtils.loadLayoutAnimation( context, R.anim.layout_slide_from_rigth );

            constraintLayout.setLayoutAnimation( controller );

    }

    private void runAnimation(RecyclerView recyclerView, int type) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = null;


        if(type ==0)
            controller = AnimationUtils.loadLayoutAnimation( context, R.anim.layout_slide_from_rigth );
            recyclerView.setAdapter( adapter );
            recyclerView.setLayoutAnimation( controller );
            recyclerView.getAdapter().notifyDataSetChanged();
//            adapter.getTotalSum();

    }

    private void delete_item(int position) {
        adapter.delete( position );
        adapter.notifyItemRemoved( position );
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
//        scek_sum_no_pay();
        dataToDrawer();
    }

    private void add_to_undelete_data(Integer position) {
        ds = adapter.getSnapshots().getSnapshot( position );
    }

    private void showUndoSnackbar() {

        View view = findViewById(R.id.Drawer_layo);
        String undo = getString(R.string.UNDO);
        String itemdel = getString(R.string.Itemdeleted);
        Snackbar snackbar = Snackbar.make(view, itemdel, Snackbar.LENGTH_LONG);
        snackbar.setAction(undo, v ->undo(position));
        snackbar.show();
        chekNoInfo();

    }

    private void undo(int position) {

        MainWork main_work = ds.toObject( MainWork.class);
        noteRef_addWork_Full.document().set( main_work );
        recyclerView.setAdapter(adapter);

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

                                               UserInfoToFirestore userInfoToFirestore = documentSnapshot.toObject( UserInfoToFirestore.class );
                                               try {
                                                   if(userInfoToFirestore.getNickname() == null || userInfoToFirestore.getNickname()=="") {
                                                       navUsername.setText(auth.getCurrentUser().getEmail());

                                                   } else {
                                                       navUsername.setText( userInfoToFirestore.getNickname());
                                                   }

                                                   if(userInfoToFirestore.getImg() != null){
                                                       final Uri myUri = Uri.parse( userInfoToFirestore.getImg() );
                                                       Picasso.with( NoPayActivity.this ).load( myUri ).into( avatar );

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
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
//        scek_sum_no_pay();
        dataToDrawer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
//        scek_sum_no_pay();
        dataToDrawer();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            startActivity(new Intent( NoPayActivity.this, CalendarMainActivity.class));
            //customType(NoPayActivity.this,"fadein-to-fadeout");
            overridePendingTransition(0, 0);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}

