package com.example.fcm.helper;

import com.example.fcm.models.Main_work_new;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class DataToDrawer {

    private static String[] r;
    public ArrayList<String> drawerData = new ArrayList<>();


    private CircleImageView avatar;
    private String avatarname;




    String result;



    public static String init(){
        r = new String[1];

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser[] user = new FirebaseUser[1];

        {
            user[0] = auth.getCurrentUser();
        }

        final FirebaseFirestore[] db_fstore = {FirebaseFirestore.getInstance()};

//    private static CollectionReference noteRef_addWork = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorks");
        final DocumentReference[] noteRef_data = {db_fstore[0].collection( user[0].getUid() ).document( "Avatar" )};
        CollectionReference noteRef_addWork_Full = db_fstore[0].collection( user[0].getUid() ).document("My DB").collection("MyWorksFull");


        float sum_PayAll;
        final float[] sum_noPay = new float[1];


        Date date1 = new Date(System.currentTimeMillis());
        String d1 = Helper.dataToString( date1 );
        Date date_ok = Helper.stringToData( d1 );
        ArrayList<Float> allSumm = new ArrayList<>();
        ArrayList<Float> allSummP = new ArrayList<>();
        allSumm.clear();
        allSummP.clear();

        String x = "1";

        sum_PayAll = 0;
        sum_noPay[0] = 0;

        noteRef_addWork_Full.whereEqualTo( "status", false ).whereLessThan("date", date_ok).orderBy( "date", Query.Direction.DESCENDING ).get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                            Main_work_new main_work = documentSnapshot.toObject( Main_work_new.class );
                            allSumm.add( main_work.getZarabotanoFinal() );
                            System.out.println( allSumm );

                        }

                        sum_noPay[0] =0;
                        for(int i=0; i<allSumm.size(); i++) {
                            sum_noPay[0] = sum_noPay[0] + allSumm.get( i );
                            //drawerData.add( Integer.toString(sum_noPay) );
                            System.out.println( "++++++++++++"+ sum_noPay[0] );

                        }
                        System.out.println( sum_noPay[0] +")_)_)_)_)__)_");
                        r[0] =String.valueOf( sum_noPay[0] );

                    }



                } );
        return r[0];


    }


}
