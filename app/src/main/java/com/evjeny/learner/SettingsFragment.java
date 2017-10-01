package com.evjeny.learner;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;


/**
 * Created by Evjeny on 07.07.2017 21:21.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
