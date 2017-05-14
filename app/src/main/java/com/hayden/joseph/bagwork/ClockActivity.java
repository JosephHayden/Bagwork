package com.hayden.joseph.bagwork;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by Joseph on 7/22/2016.
 * Handles displaying round clock.
 */
public class ClockActivity extends Activity {
    private Runnable timerRunnable;
    private static Handler timerHandler;
    private final ClockMachine clockMachine = new ClockMachine();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);
        final long roundLength = this.getIntent().getExtras().getLong("roundLength");
        final long restLength = this.getIntent().getExtras().getLong("restLength");
        int maxRounds = this.getIntent().getExtras().getInt("maxRounds");
        // Initialize clockMachine
        this.clockMachine.setMaxRounds(maxRounds);
        this.clockMachine.setRoundLength(roundLength);
        this.clockMachine.setRestLength(restLength);
        this.clockMachine.Start();

        ((TextView) findViewById(R.id.clockRound)).setText("Round 1");

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                clockMachine.updateRoundClock();
                String formattedTime = String.format("%02d:%02d", (int)((clockMachine.getCurrentTime()/(1000*60)))%60,
                        (int)((clockMachine.getCurrentTime()/1000)%60));
                ((TextView) findViewById(R.id.clockText)).setText(formattedTime);
                if(clockMachine.soundReady()) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("step", true);
                    bundle.putInt("round", clockMachine.getRound());
                    bundle.putBoolean("isResting", clockMachine.isResting());
                    bundle.putBoolean("isActive", clockMachine.isActive());
                    Message msg = new Message();
                    msg.setData(bundle);
                    timerHandler.handleMessage(msg);
                }
                timerHandler.postDelayed(this, 50);
            }
        };

        timerHandler = new ClockHandler(this);
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void resetClock(View view){
        this.clockMachine.Reset();
        ((TextView) findViewById(R.id.clockRound)).setText("Round " + Integer.toString(this.clockMachine.getRound()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        clockMachine.Pause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        clockMachine.Resume();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    static class ClockHandler extends Handler {
        private final WeakReference<ClockActivity> mActivity;

        ClockHandler(ClockActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg){
            ClockActivity activity = mActivity.get();
            Bundle bundle = msg.getData();
            if(bundle.containsKey("isActive") && bundle.getBoolean("isActive")) {
                if (bundle.containsKey("step") && bundle.getBoolean("step")) {
                    // Play noise
                    MediaPlayer mp = MediaPlayer.create(activity, R.raw.bell);
                    mp.start();
                    activity.clockMachine.soundPlayed();
                }
                if (bundle.containsKey("isResting")) {
                    if (bundle.getBoolean("isResting")) {
                        ((TextView) activity.findViewById(R.id.clockRound)).setText("Resting");
                    } else if (bundle.containsKey("round")){
                        ((TextView) activity.findViewById(R.id.clockRound)).setText("Round " + Integer.toString(bundle.getInt("round")));
                    }
                }
            } else {
                // Play finish noise
                MediaPlayer mp = MediaPlayer.create(activity, R.raw.bell);
                mp.start();
                activity.clockMachine.soundPlayed();
                ((TextView) activity.findViewById(R.id.clockRound)).setText("Finished");
            }
        }
    }

    public void togglePause(View v){
        boolean isActive = this.clockMachine.togglePause();
        if(isActive){
            ((Button)v).setText("Pause");
        } else {
            ((Button)v).setText("Resume");
        }
    }
}