package com.echen.androidcommon.uicommon;

import android.app.Application;
import android.content.Context;

/**
 * Created by echen on 3/28/2019
 */
public class BaseApplication extends Application {

    //region public properties

    //endregion

    protected BaseApplication(){}

    public Context getAppContext() {
        return getApplicationContext();
    }


}
