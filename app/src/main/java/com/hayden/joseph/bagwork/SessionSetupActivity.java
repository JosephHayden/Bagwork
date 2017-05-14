package com.hayden.joseph.bagwork;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;

/**
 * Created by Joseph on 6/5/2016.
 */
public class SessionSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_setup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void startSession(View view){
        LinearLayout punches = (LinearLayout) findViewById(R.id.sideIndependentShots);
        ArrayList<Integer> punchOptions = new ArrayList<>();
        for(int i = 0; i < punches.getChildCount(); i++){
            CheckBox p = (CheckBox) punches.getChildAt(i);
            if(p.isChecked()){
                punchOptions.add(p.getId());
            }
        }
        TableLayout sdPunches = (TableLayout) findViewById(R.id.sideDependentShots);
        for(int i = 1; i < sdPunches.getChildCount(); i++){
            TableRow row = (TableRow) sdPunches.getChildAt(i);
            for(int j=0; j < row.getChildCount(); j++){
                CheckBox p = (CheckBox) row.getChildAt(j);
                if(p.isChecked()) {
                    punchOptions.add(p.getId());
                }
            }
        }
        LinearLayout moveBox = (LinearLayout) findViewById(R.id.moveSelection);
        for(int i = 0; i < moveBox.getChildCount(); i++){
            CheckBox p = (CheckBox) moveBox.getChildAt(i);
            if(p.isChecked()){
                punchOptions.add(p.getId());
            }
        }
        if(punchOptions.isEmpty()){
            DialogFragment fr = SelectionErrorDialogFragment.newInstance(R.string.alert_setup_dialog_title);
            fr.show(getFragmentManager(), "dialog");
        } else {
            Bundle extraData = new Bundle();
            String str = ArrayListHelper.ArrayListToString(punchOptions);
            extraData.putString("punches", str);
            extraData.putInt("maxChainLength", Integer.parseInt(((Spinner) findViewById(R.id.roundLengthSpinner)).getSelectedItem().toString()));
            extraData.putInt("maxRounds", Integer.parseInt(((Spinner)findViewById(R.id.numRoundsSpinner)).getSelectedItem().toString()));
            extraData.putBoolean("compounding", ((CheckBox)findViewById(R.id.compoundingPunchesCheckBox)).isChecked());
            Intent intent = new Intent(this, SessionActivity.class);
            intent.putExtras(extraData);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toggleAllMoves(View v){
        CheckBox movesCheck = (CheckBox)v;
        boolean checked = movesCheck.isChecked();
        LinearLayout moveSelection = (LinearLayout)findViewById(R.id.moveSelection);
        for(int i = 0; i < moveSelection.getChildCount(); i++){
            View child = moveSelection.getChildAt(i);
            if(child instanceof CheckBox){
                ((CheckBox) child).setChecked(checked);
            }
        }
    }

    public void toggleAllPunches(View v){
        CheckBox punchesCheck = (CheckBox)v;
        boolean checked = punchesCheck.isChecked();
        LinearLayout base = (LinearLayout)findViewById(R.id.punchSelection);
        setCheckOnChildren(base, checked);
    }

    private void setCheckOnChildren(ViewGroup v, boolean checked){
        for(int i = 0; i < v.getChildCount(); i++){
            View next = v.getChildAt(i);
            if(next instanceof ViewGroup){
                setCheckOnChildren((ViewGroup)next, checked);
            } else if (next instanceof CheckBox) {
                ((CheckBox) next).setChecked(checked);
            }
        }
    }
}
