package com.hayden.joseph.bagwork;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.hayden.joseph.bagwork.Handedness.Side;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Joseph on 7/14/2016.
 */
public class Profile implements Parcelable {
    private static Profile SingletonInstance;
    private Side handedness;
    private boolean verboseShots;
    private boolean sound;

    public Profile(){}

    public static Profile getSingletonInstance(Context ctx){
        if(SingletonInstance == null) {
            SingletonInstance = new Profile();
            SingletonInstance.loadFromFile(ctx);
        }
        return SingletonInstance;
    }

    public Profile(Side handedness, boolean verbose, boolean sound){
        this.handedness = handedness;
        this.verboseShots = verbose;
        this.sound = sound;
    }

    public Side getHandedness(){
        return this.handedness;
    }

    public void setHandedness(Side side){
        this.handedness = side;
    }

    public Boolean getVerbose(){
        return this.verboseShots;
    }

    public void setVerbose(Boolean verbose){
        this.verboseShots = verbose;
    }

    public void setSound(Boolean sound){
        this.sound = sound;
    }

    public boolean getSound(){
        return this.sound;
    }

    public void loadFromFile(Context ctx) {
        File file = new File(ctx.getFilesDir(), ctx.getString(R.string.profile_name));
        if(file.exists()){
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                String handStr = doc.getDocumentElement().getElementsByTagName("handedness").item(0).getFirstChild().getTextContent();
                this.handedness = handStr.equalsIgnoreCase("left") ? Side.LEFT : Side.RIGHT;
                this.verboseShots = Boolean.parseBoolean(doc.getElementsByTagName("verbose").item(0).getFirstChild().getTextContent());
                this.sound = Boolean.parseBoolean(doc.getElementsByTagName("sound").item(0).getFirstChild().getTextContent());
            } catch (Exception e) {
                Log.e("loadFromFile()", "Error loading profile. XML may be corrupted.");
                // Try creating new default profile.
                createDefaultProfile(ctx);
                loadFromFile(ctx);
            }
        } else {
            createDefaultProfile(ctx);
            loadFromFile(ctx);
        }
    }

    public void createDefaultProfile(Context ctx){
        File file = new File(ctx.getFilesDir(), ctx.getString(R.string.profile_name));
        try {
            file.createNewFile();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element rootElem = document.createElement("profile");
            document.appendChild(rootElem);
            document.setXmlVersion("1.0");

            Element hand = document.createElement("handedness");
            rootElem.appendChild(hand);
            hand.appendChild(document.createTextNode("right"));
            Element verbose = document.createElement("verbose");
            rootElem.appendChild(verbose);
            verbose.appendChild(document.createTextNode("false"));
            Element sound = document.createElement("sound");
            rootElem.appendChild(sound);
            sound.appendChild(document.createTextNode("true"));

            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            StreamResult result = new StreamResult(file);
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
        } catch (Exception e) {
            Log.e("createDefaultProfile()", "Error saving profile.");
        }
    }

    public void saveToFile(Context ctx) {
        File file = new File(ctx.getFilesDir(), ctx.getString(R.string.profile_name));
        if (file.canWrite()) {
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = dbFactory.newDocumentBuilder();
                Document document = builder.newDocument();
                Element rootElem = document.createElement("profile");
                document.appendChild(rootElem);
                document.setXmlVersion("1.0");
                Element hand = document.createElement("handedness");
                rootElem.appendChild(hand);
                hand.appendChild(document.createTextNode(this.handedness.toString()));
                Element verbose = document.createElement("verbose");
                rootElem.appendChild(verbose);
                verbose.appendChild(document.createTextNode(Boolean.toString(this.verboseShots)));
                Element sound = document.createElement("sound");
                rootElem.appendChild(sound);
                sound.appendChild(document.createTextNode(Boolean.toString(this.sound)));

                TransformerFactory tFactory = TransformerFactory.newInstance();
                Transformer transformer = tFactory.newTransformer();
                StreamResult result = new StreamResult(new PrintWriter(new FileOutputStream(file, false)));
                DOMSource source = new DOMSource(document);
                transformer.transform(source, result);
            } catch (Exception e) {
                Log.e("saveToFile()", "Error saving profile.");
            }
        }
    }

    public Profile(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        this.handedness = Handedness.getSide(data[0]);
        this.verboseShots = Boolean.getBoolean(data[1]);
        this.sound = Boolean.getBoolean(data[2]);
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                Integer.toString(Handedness.getString(this.handedness)),
                Boolean.toString(this.verboseShots),
                Boolean.toString(this.sound)});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}
