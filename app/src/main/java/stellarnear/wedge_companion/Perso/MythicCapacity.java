package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jchatron on 26/12/2017.
 */

public class MythicCapacity {
    private Context mC;
    private String name;
    private String descr;
    private String type;
    private String id;
    public MythicCapacity(String name, String descr,String type, String id,Context mC)
    {
        this.mC=mC;
        this.name=name;
        this.descr=descr;
        this.type=type;
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public String getDescr() {
        return descr;
    }

    public String getType() {
        return type;
    }

    public String getId(){return id;}

    public boolean isActive(){
        boolean active=false;
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            if (settings.getBoolean("switch_"+this.id,true)) {
                active = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return active;
    }
}
