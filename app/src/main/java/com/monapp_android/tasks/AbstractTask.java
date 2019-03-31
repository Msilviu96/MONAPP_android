package com.monapp_android.tasks;

import android.os.AsyncTask;

import java.net.URL;

public abstract class AbstractTask{

    public abstract void beforeRun();
    public abstract void run();

}
