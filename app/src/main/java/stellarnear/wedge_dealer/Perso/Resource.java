package stellarnear.wedge_dealer.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jchatron on 04/01/2018.
 */

public class Resource {
    private String name;
    private String id;
    private int max = 0;
    private int current;
    private SharedPreferences settings;

    public Resource(String name,String id, Context mC) {
        this.name = name;
        this.id = id;
        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
    }

    public String getName() {
        return this.name;
    }


    public String getId() {
        return this.id;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Integer getMax() {
        return this.max;
    }

    public Integer getCurrent() {
        return this.current;
    }

    public void spend(Integer cost) {
        if (this.current - cost <= 0) {
            this.current = 0;
        } else {
            this.current -= cost;
        }
        saveCurrentToSettings();
    }

    public void resetCurrent() {
        this.current = this.max;
        saveCurrentToSettings();
    }

    public void loadCurrentFromSettings() {
        int currentVal = settings.getInt(this.id + "_current", -99);
        if (currentVal != -99) {
            this.current = currentVal;
        } else {
            resetCurrent();
        }
    }

    private void saveCurrentToSettings() {
        settings.edit().putInt(this.id + "_current", this.current).apply();
    }
}

