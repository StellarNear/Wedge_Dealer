package stellarnear.wedge_companion.FormSpell;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.CompositeListner;
import stellarnear.wedge_companion.Spells.BuildMetaList;
import stellarnear.wedge_companion.Spells.Cast;
import stellarnear.wedge_companion.Spells.DmgType;
import stellarnear.wedge_companion.Spells.MetaList;
import stellarnear.wedge_companion.Spells.SpellProfile;
import stellarnear.wedge_companion.Tools;


public class FormPower {
    private String  name;
    private String id;
    private String  descr;
    private String  dice_type;
    private Double  n_dice_per_lvl;
    private int cap_dice;
    private int flat_dmg=0;
    private String effect;
    private DmgType dmg_type;
    private String  range;
    private String area;
    private String  cast_time;
    private String  save_type;
    private SharedPreferences settings;
    private Cast cast;
    private Tools tools=new Tools();
    private FormSpellProfile spellProfile;
    private int dmgResult=0;

    public FormPower(String id, String name, String descr, String effect, String dmg_type, String dice_type, Double n_dice_per_lvl, int cap_dice, int flat_dmg, String range, String area, String cast_time, String save_type, Context mC){
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if(id.equalsIgnoreCase("")){
            this.id=name;
        } else {
            this.id=id;
        }
        this.name=name;
        this.descr=descr;
        this.effect=effect;
        this.dmg_type=new DmgType(dmg_type);
        this.dice_type=dice_type;
        this.n_dice_per_lvl=n_dice_per_lvl;
        this.cap_dice=cap_dice;
        this.flat_dmg=flat_dmg;
        this.range=range;
        this.area=area;
        this.cast_time=cast_time;
        this.save_type=save_type;
        this.cast =new Cast();
    }


    public String getID() {
        return id;
    }
    public String  getName(){
        return this.name;
    }
    public String  getDescr(){
        return this.descr;
    }


    public String  getSave_type(){
        return this.save_type;
    }
    public String  getDmg_type(){
        return dmg_type.getDmgType();
    }
    public int getFlat_dmg() {
        return flat_dmg;
    }
    public String getDice_type(){
        return this.dice_type;
    }

    public Double getN_dice_per_lvl() {
        return n_dice_per_lvl;
    }
    public int getCap_dice() {
        return this.cap_dice;
    }
    public String getRange(){
        return this.range;
    }

    public String getArea(){ return this.area; }

    public String getCast_time() {
        return cast_time;
    }

    public String getEffect() {
        return effect;
    }

    public void cast(){
        cast.cast();
    }

    public void setFailed(){
        cast.setFailed();
    }

    public boolean isCast(){
        return cast.isCast();
    }

    public boolean isFailed() {
        return cast.isFailed();
    }

    public void refreshProfile() {
        this.spellProfile.refreshProfile();
    }

    public FormSpellProfile getProfile() {
        if(this.spellProfile==null){
            this.spellProfile=new FormSpellProfile(this);
        }
        return this.spellProfile;
    }

    public void setDmgResult(int dmgResult) {
        this.dmgResult = dmgResult;
    }

    public int getDmgResult() {
       return this.dmgResult;
    }
}
