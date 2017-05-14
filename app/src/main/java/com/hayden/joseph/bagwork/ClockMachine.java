package com.hayden.joseph.bagwork;

/**
 * Created by Joseph on 7/27/2016.
 */
public class ClockMachine {
    private int round;
    private int maxRounds;
    private long restLength;
    private long roundLength;
    private boolean isResting;
    private boolean isActive;
    private long roundTime;
    private long lastUpdateTime;
    private boolean playSound;

    public ClockMachine(){
        Setup();
    }

    public ClockMachine(long restLength, long roundLength, int maxRounds){
        Setup();
        this.maxRounds = maxRounds;
        this.restLength = restLength;
        this.roundLength = roundLength;
    }

    private void Setup(){
        this.round = 1;
        this.isResting = false;
        this.lastUpdateTime = System.currentTimeMillis();
        this.roundTime = this.roundLength;
    }

    public void Step(){
        if(isResting){
            if(this.round >= this.maxRounds){
                Stop();
            } else {
                this.round++;
                this.roundTime = this.roundLength;
            }
        } else {
            if(this.round < this.maxRounds) {
                this.roundTime = this.restLength;
            } else {
                // If final round, don't rest after round.
                Stop();
            }
        }
        this.isResting = !this.isResting;
    }

    public void Start(){
        this.isActive = true;
        this.isResting = false;
        this.roundTime = this.roundLength;
    }

    public void Stop(){
        this.isActive = false;
    }

    public long getCurrentTime(){
        return this.roundTime;
    }

    public void Pause(){
        this.isActive = false;
    }

    public void Resume(){
        this.isActive = true;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public boolean togglePause(){
        if(this.isActive){
            this.Pause();
        } else {
            this.Resume();
        }
        return isActive;
    }

    public void updateRoundClock(){
        if(this.isActive) {
            long delta = System.currentTimeMillis() - this.lastUpdateTime;
            this.roundTime -= delta;
            this.lastUpdateTime = System.currentTimeMillis();
            if (this.roundTime <= 0) {
                this.playSound = true;
                this.Step();
            }
        }
    }

    public boolean soundReady(){
        return this.playSound;
    }

    public void soundPlayed(){
        this.playSound = false;
    }

    public void setMaxRounds(int maxRounds){
        this.maxRounds = maxRounds;
    }

    public void setRestLength(long restLength){
        this.restLength = restLength;
    }

    public void setRoundLength(long roundLength){
        this.roundLength = roundLength;
    }

    public int getRound(){
        return this.round;
    }

    public boolean isResting(){
        return this.isResting;
    }

    public boolean isActive(){
        return this.isActive;
    }

    public void Reset(){
        this.Setup();
    }
}
