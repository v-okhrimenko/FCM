package com.example.fcm;

import com.example.fcm.helper.Helper;
import com.example.fcm.interf.Fdb_interface;
import com.example.fcm.models.Main_work_new;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;


public class FireBase_db implements Fdb_interface {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;


    {
        user = auth.getCurrentUser();
    }

    private FirebaseFirestore db_fstore = FirebaseFirestore.getInstance();
    private CollectionReference noteRef_addWork_Full = db_fstore.collection( user.getUid() ).document("My DB").collection("MyWorksFull");


    ArrayList<Float> allSumm = new ArrayList<>();
    float sum_noPay;



    @Override
    public float sumNoPay() {

        Date end_d = new Date( System.currentTimeMillis() );
        String d1 = Helper.dataToString( end_d );
        Date date_ok = Helper.stringToData( d1 );

        noteRef_addWork_Full.whereEqualTo( "status", false ).whereLessThan( "date", date_ok ).orderBy( "date", Query.Direction.DESCENDING ).get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Main_work_new main_work = documentSnapshot.toObject( Main_work_new.class );
                            allSumm.add( main_work.getZarabotanoFinal() );
                            sum_noPay = sum_noPay+main_work.getZarabotanoFinal();
                        }

                        System.out.println( sum_noPay );


                    }


                } );
        return sum_noPay;

    }


}
