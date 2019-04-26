package com.echen.androidcommon.uicommon;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import com.echen.androidcommon.utility.LanguageUtility;
import com.echen.androidcommon.uicommon.customcontrol.PreferenceUtility;
import java.util.Locale;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by echen on 2015/11/16.
 */
public class BaseActivity extends AppCompatActivity {
//    protected String LANGUAGE_CODE_ENGLISH = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceUtility.init(this);
        switchLanguage(PreferenceUtility.getString(PreferenceUtility.KEY_LANGUAGE, LanguageUtility.LANGUAGE_CODE_CHINESE));
    }

    protected void switchLanguage(String languageCode) {
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        Locale locale = LanguageUtility.getMappedLanguageLocale(languageCode);
        if (locale != configuration.locale) {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, displayMetrics);
        }

        PreferenceUtility.commitString(PreferenceUtility.KEY_LANGUAGE, languageCode);
    }
}
