package com.hayden.joseph.bagwork;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

/**
 * Created by Joseph on 8/26/2016.
 */
public class SettingsActivity extends Activity {

    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        profile = Profile.getSingletonInstance(this);
        profile.loadFromFile(this);
        RadioButton handButton = profile.getHandedness() == Handedness.Side.LEFT ? (RadioButton) findViewById(R.id.radioButtonLeft) : (RadioButton) findViewById(R.id.radioButtonRight);
        handButton.setChecked(true);
        CheckBox verboseCheck = (CheckBox) findViewById(R.id.verboseCheckbox);
        verboseCheck.setChecked(profile.getVerbose());
    }

    public void saveSettings(View v){
        Handedness.Side side = ((RadioButton) findViewById(R.id.radioButtonLeft)).isChecked() ? Handedness.Side.LEFT : Handedness.Side.RIGHT;
        profile.setHandedness(side);
        Boolean verbose = ((CheckBox) findViewById(R.id.verboseCheckbox)).isChecked();
        profile.setVerbose(verbose);
        profile.saveToFile(this);
        (this).finish();
    }
}
