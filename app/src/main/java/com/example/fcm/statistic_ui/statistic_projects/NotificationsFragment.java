package com.example.fcm.statistic_ui.statistic_projects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fcm.ScreensActivity.JobStatisticReviewJob;
import com.example.fcm.R;
import com.example.fcm.helper.DbConnection;
import com.example.fcm.helper.Helper;
import com.example.fcm.models.MainWork;
import com.example.fcm.recycleviewadapter.JobsInStatisticJobsRv;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;


    private View root;
    private QuerySnapshot snap;
    ArrayList jobArrayFull = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mDayCount = new ArrayList<>();
    private ArrayList<String> mHourAtWork = new ArrayList<>();
    private ArrayList<String> mMinutesAtWork = new ArrayList<>();
    private ArrayList<String> mTotalEarned = new ArrayList<>();
    private ConstraintLayout clCliclLayout;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of( this ).get( NotificationsViewModel.class );
        root = inflater.inflate( R.layout.fragment_notifications, container, false );

        clCliclLayout = root.findViewById( R.id.cl_disableClick );
        clCliclLayout.setClickable( false );
        setJobsName();



        notificationsViewModel.getText().observe( this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        } );
        return root;
    }

    private void setJobsName() {
        ArrayList jobNameArray = new ArrayList<String>();
        Calendar today = Calendar.getInstance();
        today.set( Calendar.HOUR_OF_DAY,0 );
        today.set( Calendar.MINUTE,0 );
        today.set( Calendar.SECOND,0 );
        today.set( Calendar.MILLISECOND,0 );

        int month = today.get( Calendar.MONTH );
        String dateCheck = today.get( Calendar.DAY_OF_MONTH ) + "-"+(month+1)+"-"+today.get( Calendar.YEAR );
        Date todayDate = Helper.stringToData( dateCheck );

        System.out.println("Network "+Helper.isNetworkAvailable(getContext()));
        if(Helper.isNetworkAvailable(getContext())) {

            DbConnection.DBJOBS.whereLessThan( "date", todayDate ).get( Source.CACHE).addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                   @Override
                   public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                       snap = queryDocumentSnapshots;
                       for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {

                           MainWork mainWork = snapshots.toObject( MainWork.class );
                           if (jobNameArray.contains( mainWork.getName() )) {
                               continue;
                           } else
                               jobNameArray.add( mainWork.getName() );
                       }
                       // makeRV( jobNameArray );

                       getInfo(jobNameArray);
                       System.out.println("NETWORK IS OK");

                   }
               }
            );
        } else {

            DbConnection.DBJOBS.whereLessThan( "date", todayDate ).get( Source.CACHE).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    snap = queryDocumentSnapshots;
                    for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots) {

                        MainWork mainWork = snapshots.toObject( MainWork.class );
                        if (jobNameArray.contains( mainWork.getName() )) {
                            continue;
                        } else
                            jobNameArray.add( mainWork.getName() );
                    }
                    // makeRV( jobNameArray );

                    getInfo(jobNameArray);
                    System.out.println("NETWORK IS OK");

                }
            });
        }



    }

    private void getInfo(ArrayList jobNameArray) {
        for (Object jobArr : jobNameArray) {

            int count = 0;
            float totalSumma = 0;
            Integer[] totalHourMinute = {0,0};


            for(QueryDocumentSnapshot snapshot: snap) {
                MainWork mainWork = snapshot.toObject( MainWork.class );
                if (mainWork.getName().equals( jobArr.toString() )) {

                    count++;

                    totalSumma = totalSumma + mainWork.getZarabotanoFinal();
                    if (!mainWork.getTempalte_type().equals( "fixed" )) {
                        if (mainWork.getStart() != null && mainWork.getEnd() != null) {
                            ArrayList hourMinutesArray = Helper.getHoursMinutes( mainWork.getStart(), mainWork.getEnd() );
                            Integer h = new Integer( hourMinutesArray.get( 0 ).toString() );
                            Integer m = new Integer( hourMinutesArray.get( 1 ).toString() );
                            totalHourMinute[0] = totalHourMinute[0] + h;
                            totalHourMinute[1] = totalHourMinute[1] + m;
                        }
                    }

                    if (totalHourMinute[1] >= 60) {
                        ArrayList convertetMinutsResult = Helper.convertMinutestToHours( totalHourMinute[1] );
                        Integer h = new Integer( convertetMinutsResult.get( 0 ).toString() );
                        Integer m = new Integer( convertetMinutsResult.get( 1 ).toString() );
                        totalHourMinute[0] = totalHourMinute[0] + h;
                        totalHourMinute[1] = m;
                    }
                } else {
                    continue;
                }
                //System.out.println("2 "+ jobArr + " " + count + " " + totalSumma + " " + totalHourMinute[0] + " " + totalHourMinute[1] );
            }
            //System.out.println( jobArr + " " + count + " " + totalSumma + " " + totalHourMinute[0] + " " + totalHourMinute[1] );

            ArrayList currentJob = new ArrayList<String>();
            currentJob.add( jobArr);
            currentJob.add( count);
            currentJob.add( totalSumma);
            currentJob.add( totalHourMinute[0]);
            currentJob.add( totalHourMinute[1]);
            jobArrayFull.add(currentJob);
            System.out.println("!"+jobArrayFull);

            mNames.add( (String) jobArr );
            mDayCount.add( String.valueOf( count ) );
            mHourAtWork.add( String.valueOf( totalHourMinute[0] ) );
            mMinutesAtWork.add( String.valueOf( totalHourMinute[1] ) );
            mTotalEarned.add( String.valueOf( totalSumma ) );

            makeRV( mNames, mDayCount, mTotalEarned,  mHourAtWork, mMinutesAtWork );

        }
    }

    private void makeRV(ArrayList<String> mNames, ArrayList<String> mDayCount, ArrayList<String> mTotalEarned, ArrayList<String> mHourAtWork, ArrayList mMinutesAtWork) {
        JobsInStatisticJobsRv adapter = new JobsInStatisticJobsRv(getContext(), mNames,mDayCount,mTotalEarned,mHourAtWork, mMinutesAtWork );
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.rv_jobs_in_statistic_in_jobs);
        recyclerView.setHasFixedSize( true );
        //recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );
        recyclerView.setLayoutManager( new LinearLayoutManager( getContext() ) );
        recyclerView.setAdapter( adapter );
        adapter.setClickListener( new JobsInStatisticJobsRv.OnJobListener() {

            @Override
            public void onJobClick(View v, String name) {

            }

            @Override
            public void onDiagramClick(View v, String name) {
                System.out.println(name + " DIAGRAM");
                Bundle sendNameToOtherFragment = new Bundle();
                sendNameToOtherFragment.putString( "name", name );
                JobStatisticReviewJob jobStatisticReviewJob= new JobStatisticReviewJob();
                jobStatisticReviewJob.setArguments( sendNameToOtherFragment );
                clCliclLayout.setClickable( true );
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainJobFragment, jobStatisticReviewJob, "findThisFragment")
                        .addToBackStack(null)
                        .commit();


            }
        } );

    }
}