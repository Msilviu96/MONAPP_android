package com.monapp_android.tasks;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;

public class ServiceManager extends AsyncTask<AbstractTask, Void, Void> {

    @Override
    protected Void doInBackground(AbstractTask... abstractTasks) {
        try {
            for(AbstractTask abstractTask : abstractTasks){
                abstractTask.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
