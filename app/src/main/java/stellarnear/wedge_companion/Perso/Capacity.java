package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

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
    private String dailyUseString;
    private int dailyUse=0;
    private boolean infinite=false;
    private String valueString;
    private int value;

    public Capacity(String name,String shortname, String type, String descr, String id,String dailyUse,String valueString, Context mC,String pjID){
        this.name=name;
        this.shortname=shortname;
        this.type=type;
        this.descr=descr;
        this.valueString=valueString;
        this.id=id;
        this.mC=mC;
        this.pjID=pjID;
        this.dailyUseString = dailyUse;
        if(dailyUse.equalsIgnoreCase("infinite")){
            this.infinite=true;
        } else {
            computeDailyUsage();
        }
        computeValue();
    }

    private void computeValue() {
        Tools tools = new Tools();
        if(!valueString.equalsIgnoreCase("")){
            if(tools.toInt(valueString)==0){
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
                int mainPJlvl=tools.toInt(settings.getString( "ability_lvl", String.valueOf(mC.getResources().getInteger(R.integer.ability_lvl_def))));
                if(pjID.equalsIgnoreCase("halda")){
                    mainPJlvl=mainPJlvl-2;
                }
                int druidLvl=tools.toInt(settings.getString( "ability_lvl_druid", String.valueOf(mC.getResources().getInteger(R.integer.ability_lvl_druid_def))));
                int archerLvl=tools.toInt(settings.getString( "ability_lvl_archer", String.valueOf(mC.getResources().getInteger(R.integer.ability_lvl_archer_def))));
                switch (valueString){
                    case "lvl":
                        this.value =mainPJlvl;
                        break;
                    case "1+lvl_archer/2":
                        this.value=1+((int)(archerLvl/2));
                        break;
                    default:
                        this.value =0;
                        break;
                }
            } else {
                this.value=tools.toInt(valueString);
            }
        }

    }

    public void refresh(){
     computeDailyUsage();
     computeValue();
    }

    private void computeDailyUsage() {
        Tools tools = new Tools();
        if(!dailyUseString.equalsIgnoreCase("")){
            if(tools.toInt(dailyUseString)==0){
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
                int mainPJlvl=tools.toInt(settings.getString( "ability_lvl", String.valueOf(mC.getResources().getInteger(R.integer.ability_lvl_def))));
                if(pjID.equalsIgnoreCase("halda")){
                        mainPJlvl=mainPJlvl-2;
                }
                int druidLvl=tools.toInt(settings.getString( "ability_lvl_druid", String.valueOf(mC.getResources().getInteger(R.integer.ability_lvl_druid_def))));
                int archerLvl=tools.toInt(settings.getString( "ability_lvl_archer", String.valueOf(mC.getResources().getInteger(R.integer.ability_lvl_archer_def))));
                switch (dailyUseString){
                    case "lvl":
                        this.dailyUse =mainPJlvl;
                        break;
                    case "lvl_archer":
                        this.dailyUse =archerLvl;
                        break;
                    case "lvl_druid":
                        this.dailyUse =druidLvl;
                        break;
                    case "(lvl_druid/2)-1+lvl_archer/3":
                        this.dailyUse = ((int)(druidLvl/2))-1+((int)(archerLvl/3));
                        break;
                    case "1+((lvl_archer-6)/4)":
                        this.dailyUse =1+((int)((archerLvl-6)/4));
                        break;
                    case "1+((lvl_archer-8)/3)":
                        this.dailyUse =1+((int)((archerLvl-8)/3));
                        break;

                        // Partie Halda
                    case "1+(lvl-5)/6":
                        this.dailyUse =1+((int)((mainPJlvl-5)/6));
                        break;

                    default:
                        this.dailyUse =0;
                        break;
                }
            } else {
                this.dailyUse=tools.toInt(dailyUseString);
            }
        }
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

    public boolean isInfinite() {
        return infinite;
    }

    public int getDailyUse() {
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
