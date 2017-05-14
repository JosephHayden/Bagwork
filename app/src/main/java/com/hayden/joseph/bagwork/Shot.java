package com.hayden.joseph.bagwork;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.hayden.joseph.bagwork.Handedness.Side;

import java.io.IOException;
import java.io.ObjectStreamException;

/**
 * Holds a shot, its full name, and its short (callout) name, as well as the side (left or right)
 * the shot is thrown from and retains information about whether the shot is thrown from the user's
 * dominant hand (necessary because short names are dependent on whether the shot is dominant or
 * not).
 */
public class Shot implements Parcelable {
    private String shortName;
    private String fullName;
    private Side side;
    private boolean isDominant;

    public Shot(String[] possibleShortNames, String fullName, Side side, Side userHandedness, Context context) {
        this.isDominant = (userHandedness == side);
        if(possibleShortNames.length > 1) {
            this.fullName = context.getString(Handedness.getString(side)) + " " + fullName;
            if(side != Side.NONBIASED){
                if(!this.isDominant) {
                    this.shortName = possibleShortNames[0];
                } else {
                    this.shortName = possibleShortNames[1];
                }
            } else {
                this.shortName = possibleShortNames[0];
            }
        } else {
            this.fullName = fullName;
            this.shortName = possibleShortNames[0];
        }
        this.side = side;
    }

    public Shot(String shortName, String fullName, Side side, boolean isDominant) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.side = side;
        this.isDominant = isDominant;
    }

    public String getShortName(){
        return this.shortName;
    }

    public String getFullName(){
        return this.fullName;
    }

    public Side getSide(){
        return this.side;
    }

    public String getName(Boolean verbose){
        if(verbose){
            return this.fullName;
        } else {
            return this.shortName;
        }
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeChars(this.getShortName());
        out.writeChars(this.getFullName());
        out.writeObject(this.getSide());
        out.writeBoolean(this.isDominant);
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        String shortName = (String)in.readObject();
        String fullName = (String)in.readObject();
        Side side = (Side)in.readObject();
        boolean isDominant = in.readBoolean();

        Shot retShot = new Shot(shortName, fullName, side, isDominant);
    }
    private void readObjectNoData() throws ObjectStreamException {

    }

    public Shot(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Shot> CREATOR = new Parcelable.Creator<Shot>() {
        public Shot createFromParcel(Parcel in) {
            return new Shot(in);
        }

        public Shot[] newArray(int size) {
            return new Shot[size];
        }
    };

    public void readFromParcel(Parcel in) {
        this.shortName = in.readString();
        this.fullName = in.readString();
        this.side = Side.values()[in.readInt()];
        this.isDominant = in.readInt() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.getShortName());
        parcel.writeString(this.getFullName());
        parcel.writeInt(this.getSide().ordinal());
        parcel.writeInt(this.isDominant ? 0 : 1);
    }
}
