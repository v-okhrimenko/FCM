package com.example.fcm.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class DbConnection {

    public static CollectionReference DBJOBS = FirebaseFirestore.getInstance().collection( FirebaseAuth.getInstance().getCurrentUser().getUid() ).document("My DB").collection("MyWorksFull");
    public static CollectionReference DBTEMPLATES = FirebaseFirestore.getInstance().collection( FirebaseAuth.getInstance().getCurrentUser().getUid() ).document("My DB").collection("Jobs_full");



}
