package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jchatron on 26/12/2017.
 */

public class Capacity {
    private String name;
    private String shortname;
    private String type;
    private String descr;
    private String id;
    private Context mC;
    private String pjID;
    private String dailyUse;
    private int value;

    public Capacity(String name,String shortname, String type, String descr, String id,String dailyUse,int value, Context mC,String pjID){
        this.name=name;
        this.shortname=shortname;
        this.type=type;
        this.descr=descr;
        this.dailyUse=dailyUse;
        this.value=value;
        this.id=id;
        this.mC=mC;
        this.pjID=pjID;
    }

    public String getName() {
        return name;
    }

    public String getShortname() {
        return shortname;
    }

    public String getType() {
        return type;
    }

    public String getDescr() {
        return descr;
    }

    public String getId() {
        return id;
    }

    public String getDailyUse() {
        return dailyUse;
    }

    public boolean isActive(){
        boolean active = false;
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            active = settings.getBoolean("switch_"+this.id+pjID, true);
        } catch ( Exception e) {}
        return active;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int val) {
        this.value=val;
    }
}
