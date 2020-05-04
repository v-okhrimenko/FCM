package com.example.fcm.interfsce;

import android.content.Context;

import com.example.fcm.models.MainWork;
import com.example.fcm.models.TemplateJob;

import java.util.ArrayList;
import java.util.List;

public interface AddJobInterface {
    String fieldNotEmpty(ArrayList field);
    Boolean thisNameIsAlreadyUsedInTemplates (ArrayList presentTemplatesName, String currentTemplateName);
    void saveJobs(Context context, MainWork main_work, List<String> selectedDaysInCalendarArray);
    void saveAsTemplate(Context context, TemplateJob templateJob);
    void updatePresentTemplate(Context context, TemplateJob templateJob);
}
