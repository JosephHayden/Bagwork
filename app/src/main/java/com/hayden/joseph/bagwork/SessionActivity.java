package com.hayden.joseph.bagwork;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Activity in charge of generating and displaying a chain of shots for a given set of parameters.
 */
public class SessionActivity extends Activity {

    private ShotMachine machine;
    private long startTime = System.currentTimeMillis();
    private static Handler timerHandler;
    private Runnable timerRunnable;
    private static CustomAnimator.AnimationHandler animHandler;
    private TextToSpeech TTS;
    private ChainManager manager;
    private Profile profile;
    private int divisionWidth;
    private boolean compoundingShots;
    private int maxChainLength;
    private int maxRounds;
    private int currentRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Shot> restoredShotList = null;
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("currentShotList")){
                restoredShotList = savedInstanceState.getParcelableArrayList("currentShotList");
            }
        }
        setContentView(R.layout.activity_session);

        Point size = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(size);
        int WIDTH = size.x;
        int NUMDIV = 3;
        this.divisionWidth = WIDTH/NUMDIV;

        Bundle extras = this.getIntent().getExtras();
        String punches = extras.getString("punches");
        this.compoundingShots = extras.getBoolean("compounding");
        this.maxChainLength = extras.getInt("maxChainLength");
        this.maxRounds = extras.getInt("maxRounds");
        this.profile = Profile.getSingletonInstance(this);
        // This is crappy. Required atm because of when rounds are updated. Should fix later.
        if(compoundingShots) {
            this.currentRound = 1;
        } else {
            this.currentRound = 0;
        }
        ArrayList<Integer> allowedPunches = ArrayListHelper.StringToArrayList(punches);

        manager = new ChainManager(this, profile, new Ruleset(allowedPunches.toArray(new Integer[allowedPunches.size()]), Ruleset.Mode.RANDOM_WITHOUT_MOVES));
        if(restoredShotList != null){
            manager.setChain(restoredShotList);
        }
        machine = new ShotMachine(manager.getChain(), this);
        setSoundButton(); // Initialize sound toggle to correct value;

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis();
                if (millis - startTime > 1000) {
                    Bundle bundle = new Bundle();
                    bundle.putString("step", "true");
                    Message msg = new Message();
                    msg.setData(bundle);
                    timerHandler.handleMessage(msg);
                    startTime = System.currentTimeMillis();
                }
                if(currentRound <= maxRounds)
                    timerHandler.postDelayed(this, 50);
            }
        };

        // Create animation handler.
        animHandler = new CustomAnimator.AnimationHandler(this.machine);

        timerHandler = new Handler() {

            @Override
            public void handleMessage(Message msg){
                Bundle bundle = msg.getData();
                if(bundle.containsKey("step") && bundle.getString("step").equals("true")){
                    machine.Enqueue(divisionWidth/2);
                    machine.Step();
                    updateUI();
                }
                if(machine.isEmpty()){
                    if(currentRound < maxRounds) {
                        if (compoundingShots && machine.shotListSize() < maxChainLength) {
                            // If compounding and we haven't reached max length, just add to the current chain.
                            manager.addElement();
                            machine.reInitialize(manager.getChain());
                        } else if (compoundingShots) {
                            // If compounding but max length, create a new shot list of length one.
                            currentRound += 1;
                            manager.setChain(new ArrayList<Shot>());
                            manager.addElement();
                            machine.reInitialize(manager.getChain());
                        } else {
                            // If not compounding, just create new list of desired length.
                            currentRound += 1;
                            manager.setChain(manager.getChain(maxChainLength));
                            machine.reInitialize(manager.getChain());
                        }
                        updateRoundCounter(currentRound);
                    } else {
                        currentRound += 1;
                        TextView roundCounter = (TextView)findViewById(R.id.roundCounter);
                        if(!roundCounter.getText().equals("Finished")){
                            roundCounter.setText("Finished");
                        }
                        timerHandler.removeCallbacks(timerRunnable);
                    }
                }
            }
        };

        // Starts timer.
        timerHandler.postDelayed(timerRunnable, 0);

        // Setup pause/start button.
        final Button pauseButton = (Button) findViewById(R.id.pausebutton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pauseButton.getText().equals("Stop")) {
                    timerHandler.removeCallbacks(timerRunnable);
                    pauseButton.setText("Start");
                } else {
                    timerHandler.postDelayed(timerRunnable, 0);
                    pauseButton.setText("Stop");
                }
            }
        });

        // Text to Speech
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    TTS.setLanguage(Locale.US);
                }
            }
        });
    }

    private void updateRoundCounter(int round){
        ((TextView)findViewById(R.id.roundCounter)).setText(Integer.toString(round));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void updateUI(){
        RelativeLayout callout_bar = (RelativeLayout) this.findViewById(R.id.callout_bar);
        ShotView firstShotView;
        ShotView midShotView;
        ShotView lastShotView;
        ShotView output;
        if((firstShotView = machine.getElement(0)) != null) {
            View firstView = firstShotView.getView();
            callout_bar.addView(firstView);
            CustomAnimator.animateScaleElement(firstView, 0, 1, 0, 1);
        }
        if((midShotView = machine.getElement(1)) != null) {
            View e1 = midShotView.getView();
            CustomAnimator.animateTranslateElement(e1, (int)e1.getTranslationX(), divisionWidth + divisionWidth/2);
            if(profile.getSound()) {
                Speak(midShotView.getShot().getName(profile.getVerbose()));
            }
            TextView fullName = (TextView)findViewById(R.id.fullShotName);
            fullName.setText(midShotView.getShot().getFullName());
        } else {
            // If there is no mid-shot, clear full text.
            TextView fullName = (TextView)findViewById(R.id.fullShotName);
            fullName.setText("");
        }
        if((lastShotView = machine.getElement(2)) != null) {
            View e2 = lastShotView.getView();
            CustomAnimator.animateTranslateElement(e2, (int) e2.getTranslationX(), 2 * divisionWidth + divisionWidth / 2);
        }
        if((output = machine.getOutput()) != null) {
            View outputShot = output.getView();
            CustomAnimator.animateRemoveElement(outputShot, animHandler);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void Speak(String text){
        if (Build.VERSION.RELEASE.startsWith("5")) {
            TTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else {
            TTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
        TTS.shutdown();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        ArrayList<Shot> chain = manager.getChain();
        outState.putParcelableArrayList("currentShotList", chain);
    }

    private void setSoundButton(){
        ToggleButton button = (ToggleButton)findViewById(R.id.toggleButton);
        button.setChecked(profile.getSound());
    }

    public void toggleSound(View view){
        if(((ToggleButton)view).isChecked()){
            profile.setSound(true);
        } else {
            profile.setSound(false);
        }
    }
}
