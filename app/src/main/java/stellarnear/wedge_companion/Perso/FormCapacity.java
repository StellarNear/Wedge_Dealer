package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import stellarnear.wedge_companion.FormSpell.FormPower;

/**
 * Created by jchatron on 01/12/2019.
 */

public class FormCapacity {
    private String name;
    private String shortname;
    private String type;
    private String descr;
    private String id;
    private Context mC;
    private int dailyUse;
    private int value;
    private FormPower power=null;

    public FormCapacity(String name, String shortname, String type, String descr, String id, int dailyUse, int value, Context mC){
        this.name=name;
        this.shortname=shortname;
        this.type=type;
        this.descr=descr;
        this.dailyUse=dailyUse;
        this.value=value;
        this.id=id;
        this.mC=mC;
    }

    public FormCapacity(String name, String shortname, String type, String descr, String id, int dailyUse,FormPower power, int value, Context mC){
        this.name=name;
        this.shortname=shortname;
        this.type=type;
        this.descr=descr;
        this.dailyUse=dailyUse;
        this.power=power;
        this.value=value;
        this.id=id;
        this.mC=mC;
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

    public int getDailyUse() {
        return dailyUse;
    }

    public int getValue() {
        return value;
    }

    public FormPower getPower() {
        return power;
    }
}
