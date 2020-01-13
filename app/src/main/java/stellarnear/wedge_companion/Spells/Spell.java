package stellarnear.wedge_companion.Spells;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;


public class Spell {
    private String  name;
    private String id;
    private Boolean mythic;
    private String normalSpellId; // pour les sorts mythic

    private String  descr;
    private String shortDescr;
    private String type;
    private String  dice_type;
    private Double  n_dice_per_lvl;
    private int     cap_dice;
    private String flat_dmg;
    private int flat_cap;

    private DmgType  dmg_type;
    private String  range;
    private String contact;
    private boolean contactFailed;
    private String area;
    private String  cast_time;
    private String  duration;

    private List<String> compoList=new ArrayList<>();
    private String compoM;

    private String rm;
    private String  save_type;

    private int     rank;
    private boolean perfect;
    private String perfectMetaId="";

    private boolean fromMystery;

    private int     n_sub_spell;

    private MetaList metaList;

    private boolean binded=false;
    private transient Spell bindedParent =null;
    private Cast cast;
    private boolean crit=false;

    private transient SpellProfile spellProfile;

    private int dmgResult=0;

    public Spell(Spell spell){ //copying spell
        this.id=spell.id;
        this.mythic=spell.mythic;
        this.normalSpellId=spell.normalSpellId;
        this.name=spell.name;
        this.descr=spell.descr;
        this.shortDescr=spell.shortDescr;
        this.type=spell.type;
        this.dice_type=spell.dice_type;
        this.n_dice_per_lvl=spell.n_dice_per_lvl;
        this.cap_dice=spell.cap_dice;
        this.flat_dmg=spell.flat_dmg;
        this.flat_cap=spell.flat_cap;
        this.range=spell.range;
        this.contact=spell.contact;
        this.area=spell.area;
        this.cast_time=spell.cast_time;
        this.duration=spell.duration;
        this.compoList=spell.compoList;
        this.compoM=spell.compoM;
        this.rm=spell.rm;
        this.save_type=spell.save_type;
        this.rank=spell.rank;
        this.perfect=spell.perfect;
        this.perfectMetaId=spell.perfectMetaId;
        this.fromMystery=spell.fromMystery;
        this.n_sub_spell=spell.n_sub_spell;
        this.dmg_type=new DmgType(spell.dmg_type);
        this.metaList=new MetaList(spell.metaList);
        this.cast =new Cast();
    }

    public Spell(String id,boolean mythic,boolean fromMystery,String normalSpellId, String name, String descr, String shortDescr,String type,Integer n_sub_spell, String dice_type, Double n_dice_per_lvl, int cap_dice, String dmg_type,String flat_dmg,int flat_cap, String range,String contact,String area, String cast_time, String duration, String compo,String compoM, String rm, String save_type, int rank,Context mC){
        if(id.equalsIgnoreCase("")){
            this.id=name;
        } else {
            this.id=id;
        }
        this.mythic=mythic;
        this.fromMystery=fromMystery;
        this.normalSpellId=normalSpellId;
        this.name=name;
        this.descr=descr;
        this.shortDescr=shortDescr;
        this.type=type;
        this.n_sub_spell=n_sub_spell;
        this.dice_type=dice_type;
        this.n_dice_per_lvl=n_dice_per_lvl;
        this.cap_dice=cap_dice;
        this.dmg_type=new DmgType(dmg_type);
        this.flat_dmg=flat_dmg;
        this.flat_cap=flat_cap;
        this.range=range;
        this.contact=contact;
        this.area=area;
        this.cast_time=cast_time;
        this.duration=duration;
        calcCompo(compo);
        this.compoM=compoM;
        this.rm=rm;
        this.save_type=save_type;
        this.rank=rank;
        if (this.id.equalsIgnoreCase("intense_cure")) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
            if (settings.getBoolean("intense_cure_perfect_spell_switch", mC.getResources().getBoolean(R.bool.intense_cure_perfect_spell_switch_def))) {
                this.perfect=true;
            }
        }
        this.metaList= BuildMetaList.getInstance(mC).getMetaList();
        this.cast =new Cast();
    }

    private void calcCompo(String compo) {
        compoList.addAll(Arrays.asList(compo.split(",")));
    }


    public String getID() {
        return id;
    }

    public String getNormalSpellId() {
        return normalSpellId;
    }

    public boolean isMyth(){
        return this.mythic;
    }

    public String getDuration() {
        return duration;
    }

    public boolean hasCompo(){
        return compoList.size()>0;
    }

    public List<String> getCompoList() {
        return compoList;
    }

    public String getCompoM() {
        return compoM==null?"":compoM;
    }

    public Integer getRank(){
        return this.rank;
    }
    public String  getName(){
        return this.name;
    }
    public String getShortDescr(){
        return (this.shortDescr==null||this.shortDescr.equalsIgnoreCase(""))? this.descr : this.shortDescr;  //le null pour les sorts venant de chez yfa
    }

    public String getDescr() {
        return descr;
    }

    public String getType() {
        return type;
    }

    public boolean isFromMystery() {
        return fromMystery;
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

    public String  getSave_type(){
        return this.save_type;
    }
    public String  getDmg_type(){
        return dmg_type.getDmgType();
    }
    public String getFlat_dmg() {
        return flat_dmg;
    }

    public int getFlat_cap() {
        return flat_cap;
    }

    public String getRange(){
        return this.range;
    }
    public String getContact(){
        return this.contact;
    }

    public boolean contactFailed(){
        return contactFailed;
    }

    public void setContactFailed(){
        this.contactFailed=true;
    }

    public String getArea(){ return this.area; }

    public String getCast_time() {
        return cast_time;
    }

    public boolean isPerfect() {
        return this.perfect;
    }

    public String getPerfectMetaId() {
        return perfectMetaId;
    }

    public int getNSubSpell() {
        return this.n_sub_spell;
    }

    public boolean isHighscore(int val,Context mC){
        Tools tools=new Tools();
        boolean returnVal=false;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        int highscore=settings.getInt(this.id+"_highscore",0);
        if(val>highscore){
            returnVal=true;
            settings.edit().putInt(this.id+"_highscore",val).apply();
            int highscoreAllSpells=tools.toInt(settings.getString("highscore"+PersoManager.getPJSuffix(),"0"));
            if(val>highscoreAllSpells){
                settings.edit().putString("highscore"+PersoManager.getPJSuffix(),String.valueOf(val)).apply();
            }
        }
        return returnVal;
    }

    public int getHighscore(Context mC){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        int highscore=settings.getInt(this.id+"_highscore",0);
        return highscore;
    }

    public boolean hasRM() {
        return Boolean.valueOf(this.rm);
    }

    public MetaList getMetaList() {
        return metaList;
    }

    public CheckBox getCheckboxeForMetaId(final Activity mA,final Context mC, final String metaId){

        final CheckBox check = metaList.getMetaByID(metaId).getCheckBox(mA, mC);

        if(metaId.equalsIgnoreCase("meta_extend") && !this.dmg_type.getDmgType().equalsIgnoreCase("") &&  this.dice_type.equalsIgnoreCase("lvl")){
            check.setEnabled(false);
        }

        int maxLevelWedge=PersoManager.getWedgeMaxSpellTier();
        if(this.rank> maxLevelWedge && metaId.equalsIgnoreCase("meta_arrow")){
            check.setEnabled(false);
        }

        List<String> rangesAccepted = Arrays.asList("contact","courte","moyenne");
        if(!rangesAccepted.contains(this.range) && metaId.equalsIgnoreCase("meta_range")){
            check.setEnabled(false);
        }
        if(!this.cast_time.equalsIgnoreCase("simple") && metaId.equalsIgnoreCase("meta_quicken")){
            check.setEnabled(false);
        }
        if(!this.compoList.contains("V") && metaId.equalsIgnoreCase("meta_silent")){
            check.setEnabled(false);
        }
        if(!this.compoList.contains("G") && metaId.equalsIgnoreCase("meta_static")){
            check.setEnabled(false);
        }
        if(this.rank==0 && metaId.equalsIgnoreCase("meta_echo")){
            check.setEnabled(false);
        }
        if(this.perfect && this.rank+metaList.getMetaByID(metaId).getUprank()<=9){
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
            if(check.isChecked()) {
                        if (perfect) { //pour recheck sur les autre meta afficher que le sort est toujours parfait
                            new AlertDialog.Builder(mA)
                                    .setTitle("Demande de confirmation")
                                    .setMessage("Veux-tu utiliser ta perfection magique sur cette métamagie ?")
                                    .setIcon(android.R.drawable.ic_menu_help)
                                    .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            metaList.getMetaByID(metaId).active();
                                            perfectMetaId = metaId;
                                            check.setEnabled(false);
                                            perfect = false;
                                            metaList.getMetaByID(metaId).getmListener().onEvent();
                                        }
                                    })
                                    .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                        }
                                    }).show();
                        }
                    }
                }
            });
        }
        return check;
    }

    public void setSubName(int i) {
        this.name=this.name+" "+i;
    }

    public boolean isBinded() {
        return binded;
    }

    public void bindTo(Spell previousSpellToBind) {  //pour les sub spell pour que les meta et conversion s'applique partout
        //this.dmg_type=previousSpellToBind.dmg_type; pour yfa il fallait bind ca pour conversion arcanique ici pas necessaire
        this.metaList=previousSpellToBind.metaList;
        this.cast =previousSpellToBind.cast;
        this.bindedParent =previousSpellToBind;
        this.binded=true;
    }

    public Spell getBindedParent() {
        return bindedParent;
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

    public void makeCrit() {
        this.crit=true;
    }

    public boolean isCrit(){
        return this.crit;
    }

    public void refreshProfile() {
        this.spellProfile.refreshProfile();
    }

    public SpellProfile getProfile() {
        if(this.spellProfile==null){
            this.spellProfile=new SpellProfile(this);
        }
        return this.spellProfile;
    }

    public void setRmPassed() {
        cast.setRmPassed();
    }

    public boolean hasPassedRM(){
        return cast.hasPassedRM();
    }

    public void setDmgResult(int dmgResult) {
        this.dmgResult = dmgResult;
    }

    public int getDmgResult() {
       return this.dmgResult;
    }

}
