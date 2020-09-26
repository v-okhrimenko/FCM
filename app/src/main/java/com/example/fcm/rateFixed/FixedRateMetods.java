package com.example.fcm.rateFixed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.fcm.ScreensActivity.CalendarMainActivity;
import com.example.fcm.R;
import com.example.fcm.helper.DbConnection;
import com.example.fcm.helper.Helper;
import com.example.fcm.interfsce.AddJobInterface;
import com.example.fcm.models.MainWork;
import com.example.fcm.models.TemplateJob;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FixedRateMetods implements AddJobInterface {

    @Override
    public String fieldNotEmpty(ArrayList field) {
        String returnResult = "all ok";
        int datesLength =  field.get( 2 ).toString().length();

       if(field.get( 0 ).equals( "" )){
            returnResult = "no name";
        }
        if(field.get( 1 ).equals( "" )){
            returnResult = "no price";
        }
        if(field.get( 1 ).equals( "" ) && field.get( 0 ).equals( "" )) {
            returnResult = "all empty";
        }
        if(!(field.get( 1 ).equals( "" )) && !(field.get( 0 ).equals( "" )) && datesLength==2) {
            returnResult = "no date";
        }
        return returnResult;
    }

    @Override
    public Boolean thisNameIsAlreadyUsedInTemplates(ArrayList presentTemplatesName, String currentTemplateName) {
        if(presentTemplatesName.contains( currentTemplateName )) return true;
        else return false;
    }

    @Override
    public void saveJobs(Context context, MainWork main_work, List<String> selectedDaysInCalendarArray) {

        Random randomUid = new Random();
        for (String dates: selectedDaysInCalendarArray) {
            int uniqId = randomUid.nextInt(100000000);
            main_work.setDate( Helper.stringToData( dates ) );
            main_work.setUniqId( uniqId );

            DbConnection.DBJOBS.document().set( main_work )
                    .addOnSuccessListener( aVoid -> {
                Helper.showCustomToast( context, context.getString( R.string.saved_successfully),
                        R.color.green_lite, R.drawable.check_ok_white, Toast.LENGTH_SHORT);
//

            })
                    .addOnFailureListener( e -> Helper.showCustomToast( context, context.getString( R.string.somsing_wrong ),  R.color.red,
                            R.drawable.alert, Toast.LENGTH_SHORT) );
        }
        context.startActivity(new Intent( context, CalendarMainActivity.class));
        ((Activity)context).overridePendingTransition(0, 0);
        ((Activity)context).finish();
    }

    @Override
    public void saveAsTemplate(Context context, TemplateJob templateJob) {
        DbConnection.DBTEMPLATES.document(templateJob.getTemplate_name()).set( templateJob );
    }

    @Override
    public void updatePresentTemplate(Context context, TemplateJob templateJob) {

        DbConnection.DBTEMPLATES.document(templateJob.getTemplate_name()).set(templateJob);
        }

}
