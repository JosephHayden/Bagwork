package com.hayden.joseph.bagwork;

/**
 * Created by Joseph on 7/14/2016.
 */
public class Handedness {
    public static enum Side { LEFT, RIGHT, NONBIASED };

    /**
     * Given a side, returns the opposite side.
     *
     * @param side a handedness, left or right. Behavior is undefined when this is nonbiased.
     * @return the side opposite to the <code>side</side>.
     */
    public static Side getOppositeHandedness(Side side){
        Side ret = Side.LEFT;
        if(side == Side.LEFT){
            ret = Side.RIGHT;
        }
        return ret;
    }

    public static int getString(Side side){
        switch(side){
            case LEFT:
                return R.string.left;
            case RIGHT:
                return R.string.right;
            case NONBIASED:
                return R.string.nonbiased;
            default:
                return R.string.nonbiased;
        }
    }

    public static Side getSide(String string){
        switch(string){
            case "Left":
                return Side.LEFT;
            case "Right":
                return Side.RIGHT;
            case "Non-Biased":
                return Side.NONBIASED;
            default:
                return Side.NONBIASED;
        }
    }
}
