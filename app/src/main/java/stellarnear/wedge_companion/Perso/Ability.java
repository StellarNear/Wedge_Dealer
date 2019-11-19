package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.graphics.drawable.Drawable;

import stellarnear.wedge_companion.R;


/**
 * Created by jchatron on 04/01/2018.
 */

public class Ability {
    private String name;
    private String type;
    private String descr;
    private String id;
    private Context mC;
    private Drawable img;
    private String shortname;
    private int value=0;
    private boolean testable;
    private boolean focusable;

    public Ability(String name, String shortname, String type, String descr,Boolean testable,Boolean focusable, String id, Context mC) {
        this.name = name;
        this.type = type;
        this.descr = descr;
        this.testable=testable;
        this.focusable=focusable;
        this.id = id;
        this.shortname = shortname;
        this.mC = mC;
        try {
            int imgId=mC.getResources().getIdentifier(id, "drawable", mC.getPackageName());
            this.img = mC.getDrawable(imgId);
        } catch (Exception e) {
            this.img = mC.getDrawable(R.drawable.mire_test);
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getShortname() {
        String returnVal="";
        if (this.shortname.equalsIgnoreCase("")){
            returnVal=this.name;
        } else {
            returnVal=this.shortname;
        }
        return returnVal;
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

    public Drawable getImg(){
        return this.img;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public int getMod() {
        int mod;
        if (this.type.equalsIgnoreCase("base")) {
            float modFloat = (float) ((this.value - 10.) / 2.0);
            if (modFloat >= 0) {
                mod = (int) modFloat;
            } else {
                mod = -1 * Math.round(Math.abs(modFloat));
            }
        } else {
            mod = 0;
        }
        return mod;
    }

    public boolean isTestable(){
        return this.testable;
    }

    public boolean isFocusable(){
        return this.focusable;
    } //c'est pour les jet à+10 et 20 en réussite passive quand les test peuvent etre fait au calme

}

