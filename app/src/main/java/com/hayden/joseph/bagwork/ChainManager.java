package com.hayden.joseph.bagwork;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Joseph on 7/14/2016.
 */
public class ChainManager {
    private HashMap<Integer, Shot> checkToShot;
    private ArrayList<Shot> chain;
    private ArrayList<Shot> leftShots;
    private ArrayList<Shot> rightShots;
    private ArrayList<Shot> moves;
    private Ruleset rules;

    public ChainManager(Context ctx, Profile profile, Ruleset rules){
        Resources res = ctx.getResources();
        Handedness.Side handedness = profile.getHandedness();
        this.leftShots = new ArrayList<>();
        this.rightShots = new ArrayList<>();
        this.moves = new ArrayList<>();
        this.checkToShot = new HashMap<>();
        this.rules = rules;
        Shot temp = new Shot(new String[]{res.getString(R.string.short_jab_text)},
                            ctx.getResources().getString(R.string.full_jab_text),
                            Handedness.getOppositeHandedness(handedness),
                            handedness, ctx);
        this.checkToShot.put(R.id.jabCheck, temp);
        temp = new Shot(new String[]{res.getString(R.string.short_cross_text)},
                        res.getString(R.string.full_cross_text),
                        handedness,
                        handedness, ctx);
        this.checkToShot.put(R.id.crossCheck, temp);
        temp = new Shot(new String[]{res.getString(R.string.short_hook_n), res.getString(R.string.short_hook_d)},
                        res.getString(R.string.full_hook_text),
                        Handedness.Side.LEFT,
                        handedness, ctx);
        this.checkToShot.put(R.id.lHookCheck, temp);
        temp = new Shot(new String[]{res.getString(R.string.short_hook_n), res.getString(R.string.short_hook_d)},
                        res.getString(R.string.full_hook_text),
                        Handedness.Side.RIGHT,
                        handedness, ctx);
        this.checkToShot.put(R.id.rHookCheck, temp);
        temp = new Shot(new String[]{res.getString(R.string.short_body_n), res.getString(R.string.short_body_d)},
                        res.getString(R.string.full_body_text),
                        Handedness.Side.LEFT,
                        handedness, ctx);
        this.checkToShot.put(R.id.lBodyCheck, temp);
        temp = new Shot(new String[]{res.getString(R.string.short_body_n), res.getString(R.string.short_body_d)},
                        res.getString(R.string.full_body_text),
                        Handedness.Side.RIGHT,
                        handedness, ctx);
        this.checkToShot.put(R.id.rBodyCheck, temp);
        temp = new Shot(new String[]{res.getString(R.string.short_upper_n), res.getString(R.string.short_upper_d)},
                        res.getString(R.string.full_upper_text),
                        Handedness.Side.LEFT,
                        handedness, ctx);
        this.checkToShot.put(R.id.lUpperCheck, temp);
        temp = new Shot(new String[]{res.getString(R.string.short_upper_n), res.getString(R.string.short_upper_d)},
                        res.getString(R.string.full_upper_text),
                        Handedness.Side.RIGHT,
                        handedness, ctx);
        this.checkToShot.put(R.id.rUpperCheck, temp);
        temp = new Shot(new String[]{res.getString(R.string.full_roll_text)},
                        res.getString(R.string.full_roll_text),
                        Handedness.Side.NONBIASED,
                        handedness, ctx);
        this.checkToShot.put(R.id.rollCheck, temp);
        temp = new Shot(new String[]{res.getString(R.string.full_slip_text)},
                        res.getString(R.string.full_slip_text),
                        Handedness.Side.NONBIASED,
                        handedness, ctx);
        this.checkToShot.put(R.id.slipCheck, temp);
        temp = new Shot(new String[]{res.getString(R.string.full_catch_text)},
                        res.getString(R.string.full_catch_text),
                        Handedness.Side.NONBIASED,
                        handedness, ctx);
        this.checkToShot.put(R.id.catchCheck, temp);
        temp = new Shot(new String[]{res.getString(R.string.full_block_text)},
                        res.getString(R.string.full_block_text),
                        Handedness.Side.NONBIASED,
                        handedness, ctx);
        this.checkToShot.put(R.id.blockCheck, temp);
        for(Integer n : rules.getPermittedShots()){
            try {
                addToHandednessBin(this.checkToShot.get(n));
            } catch (Exception e) {
                Log.e("ChainManager()", "Shot was not in list of shots.");
            }
        }
        this.chain = new ArrayList<>();
        if(!this.leftShots.isEmpty() && !this.rightShots.isEmpty()){
            if(this.moves.isEmpty()) {
                rules.setMode(Ruleset.Mode.RANDOM_WITHOUT_MOVES);
            } else {
                rules.setMode(Ruleset.Mode.RANDOM);
            }
        }
    }

    public ArrayList<Shot> getChain(){
        return this.chain;
    }

    public ArrayList<Shot> getChain(int chainLength){
        this.setChain(new ArrayList<Shot>());
        for(int i=0; i < chainLength; i++){
            this.addElement();
        }
        return this.getChain();
    }

    public void setChain(ArrayList<Shot> newChain){
        this.chain = newChain;
    }

    public ArrayList<Shot> addElement(){
        this.chain.add(getNewElement());
        return this.chain;
    }

    private Shot getNewElement(){
        Shot ret = null;
        if(rules.getMode() == Ruleset.Mode.RANDOM){
            Random rand = new Random();
            float prob = rand.nextFloat();
            if(prob < .33){
                ret = leftShots.get((int)(rand.nextFloat()*leftShots.size()));
            } else if (prob < .66){
                ret = rightShots.get((int)(rand.nextFloat()*rightShots.size()));
            } else {
                ret = moves.get((int)(rand.nextFloat()*moves.size()));
            }
        } else if(rules.getMode() == Ruleset.Mode.RANDOM_WITHOUT_MOVES){
            Random rand = new Random();
            float prob = rand.nextFloat();
            if(prob < .5){
                ret = leftShots.get((int)(rand.nextFloat()*leftShots.size()));
            } else {
                ret = rightShots.get((int)(rand.nextFloat()*rightShots.size()));
            }
        } else if(rules.getMode() == Ruleset.Mode.SWAY){
            ArrayList<Shot> shots = getChain();
            Handedness.Side prevSide;
            Random rand = new Random();
            if(shots.size() > 0) {
                prevSide = shots.get(shots.size() - 1).getSide();
            } else {
                if(rand.nextFloat() < 0.5){
                    prevSide = Handedness.Side.LEFT;
                } else {
                    prevSide = Handedness.Side.RIGHT;
                }
            }
            float prob = rand.nextFloat();
            if(prob < .5){
                Handedness.Side nextSide = Handedness.getOppositeHandedness(prevSide);
                if(prevSide == Handedness.Side.NONBIASED){
                    // Always treat nonbiased as side opposite penultimate shot.
                    nextSide = shots.get(shots.size() - 2).getSide();
                }
                if(nextSide == Handedness.Side.LEFT){
                    ret = leftShots.get((int)rand.nextFloat()*leftShots.size());
                } else {
                    ret = rightShots.get((int)rand.nextFloat()*rightShots.size());
                }
            } else {
                ret = moves.get((int)rand.nextFloat()*moves.size());
            }
        }
        return ret;
    }

    private void addToHandednessBin(Shot shot){
        if(shot.getSide() == Handedness.Side.LEFT){
            this.leftShots.add(shot);
        } else if(shot.getSide() == Handedness.Side.RIGHT){
            this.rightShots.add(shot);
        } else {
            this.moves.add(shot);
        }
    }
}

class Ruleset {
    private Integer permittedShots[];
    private Mode mode;
    public enum Mode {RANDOM, RANDOM_WITHOUT_MOVES, MOVES, SWAY}

    public Ruleset(Integer[] permittedShots, Mode mode){
        this.permittedShots = permittedShots;
        this.mode = mode;
    }

    public Integer[] getPermittedShots(){
        return this.permittedShots;
    }

    public Mode getMode(){
        return this.mode;
    }

    public void setMode(Mode mode){
        this.mode = mode;
    }
}