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
    private String cooldown;

    private String powerId;
    private String damage;
    private String saveType;

    public FormCapacity(String name, String shortname, String type, String descr, String id, int dailyUse, String cooldown,String damage,String saveType,String powerid, Context mC){
        this.name=name;
        this.shortname=shortname;
        this.type=type;
        this.descr=descr;
        this.dailyUse=dailyUse;
        this.cooldown=cooldown;
        this.damage=damage;
        this.saveType=saveType;
        this.powerId=powerid;
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

    public boolean hasPower(){
        return !powerId.equalsIgnoreCase("");
    }

    public String getPowerId() {
        return powerId;
    }

    public boolean hasDamage(){
        return !damage.equalsIgnoreCase("");
    }

    public String getDamage() {
        return damage;
    }
}
