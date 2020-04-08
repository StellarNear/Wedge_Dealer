package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;

import stellarnear.wedge_companion.R;

/**
 * Created by jchatron on 04/01/2018.
 */

public class Resource {
    private String name;
    private String shortname;
    private String id;
    private int max = 0;
    private int current;
    private int shield = 0;//pour les hps
    private boolean testable;
    private boolean hide;
    private boolean fromCapacity = false;
    private boolean fromSpell = false;
    private boolean infinite = false;
    private Capacity cap = null;
    private int imgId;
    private SharedPreferences settings;
    private String pjID = "";
    private String capaDescr = "";
    private Context mC;

    public Resource(String name, String shortname, Boolean testable, Boolean hide, String id, Context mC, String pjID) {
        this.name = name;
        this.shortname = shortname;
        if (shortname.equalsIgnoreCase("")) {
            this.shortname = name;
        }
        this.testable = testable;
        this.hide = hide;
        this.id = id;
        this.settings = PreferenceManager.getDefaultSharedPreferences(mC);
        int imgId = mC.getResources().getIdentifier(id, "drawable", mC.getPackageName());
        int imgIdCapa = mC.getResources().getIdentifier(id.replace("resource_", "capacity_"), "drawable", mC.getPackageName()); // pour les resoruces issue de capa
        if (imgId != 0) {
            this.imgId = imgId;
        } else this.imgId = imgIdCapa;
        this.pjID = pjID;
        this.mC = mC;
    }

    public String getName() {
        return this.name;
    }


    public String getId() {
        return this.id;
    }

    public void refreshFromCapacity() {
        if (!cap.isInfinite()) {
            setMax(cap.getDailyUse());
        }
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void setFromSpell() {
        this.fromSpell = true;
    }

    public boolean isSpellResource() {
        return this.fromSpell;
    }

    public String getCapaDescr() {
        String res = capaDescr;
        if (this.cap.getValue() > 0) {
            res += "\n\nValeur : " + this.cap.getValue();
        }
        return res;
    }

    public boolean isFromCapacity() {
        return fromCapacity;
    }

    public void setFromCapacity(Capacity cap) {
        this.fromCapacity = true;
        this.cap = cap;
        this.capaDescr = cap.getDescr();
        if (cap.isInfinite()) {
            this.infinite = true;
        } else {
            setMax(cap.getDailyUse());
        }
    }

    public Integer getMax() {
        return this.max;
    }

    public void setMax(int max) {
        if (this.id.equalsIgnoreCase("resource_hp") && this.max != 0 && max > this.max) {
            this.current += max - this.max;
            saveCurrentToSettings();
        }

        this.max = max;
        if (this.current > this.max) {
            this.current = this.max;
            saveCurrentToSettings();
        }
    }

    public Integer getCurrent() {
        return this.current;
    }

    public void setCurrent(int val) {
        this.current = val;
        saveCurrentToSettings();
    }

    public void spend(Integer cost) {
        if (this.id.equalsIgnoreCase("resource_hp")) {
            if (this.shield <= cost) {
                this.current -= (cost - this.shield);
                this.shield = 0;
            } else {
                this.shield -= cost;
            }
        } else {
            if (this.current - cost <= 0) {
                this.current = 0;
            } else {
                this.current -= cost;
            }
        }
        saveCurrentToSettings();
    }

    public void earn(Integer gain) {
        if (this.current + gain >= this.max) {
            this.current = this.max;
        } else {
            this.current += gain;
        }
        saveCurrentToSettings();
    }

    public void shield(int amount) {
        if (this.id.equalsIgnoreCase("resource_hp")) {
            this.shield += amount;
        }
    }

    public int getShield() {
        int val = 0;
        if (this.id.equalsIgnoreCase("resource_hp")) {
            val = this.shield;
        }
        return val;
    }

    public void resetCurrent() {
        if (!this.id.equalsIgnoreCase("resource_hp")) {
            this.current = this.max;
        }
        saveCurrentToSettings();
    }

    public void loadCurrentFromSettings() {
        String extendID = pjID.equalsIgnoreCase("") ? "" : "_" + pjID;
        int currentVal = settings.getInt(this.id + "_current" + extendID, -99);
        if (currentVal != -99) {
            this.current = currentVal;
        } else {
            resetCurrent();
        }
    }

    private void saveCurrentToSettings() {
        String extendID = pjID.equalsIgnoreCase("") ? "" : "_" + pjID;
        settings.edit().putInt(this.id + "_current" + extendID, this.current).apply();
    }

    public boolean isHidden() {
        return this.hide;
    }

    public boolean isTestable() {
        return testable;
    }

    public Drawable getImg() {
        if (imgId != 0) {
            return mC.getDrawable(imgId);
        } else {
            return mC.getDrawable(R.drawable.mire_test);
        }
    }

    public String getShortname() {
        return shortname;
    }

    public void fullHeal() {
        if (this.id.equalsIgnoreCase("resource_hp")) {
            this.current = this.max;
            saveCurrentToSettings();
        }
    }


    public Capacity getCapa() {
        return cap;
    }
}

