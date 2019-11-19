package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jchatron on 04/01/2018.
 */

public class MythicFeat {
    private String name;
    private String type;
    private String descr;
    private String id;
    private Context mC;

    public MythicFeat(String name, String type, String descr, String id, Context mC){
        this.name=name;
        this.type=type;
        this.descr=descr;
        this.id=id;
        this.mC=mC;
    }

    public String getName() {
        return name;
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

    public boolean isActive(){
        boolean active=false;
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            int switchDefId = mC.getResources().getIdentifier("switch_"+this.id+"_def", "bool", mC.getPackageName());
            boolean switchDef = mC.getResources().getBoolean(switchDefId);
            active = settings.getBoolean("switch_"+this.id, switchDef);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return active;
    }
}

