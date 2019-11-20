package stellarnear.wedge_companion.Spells;

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
import stellarnear.wedge_companion.Tools;


public class Spell {
    private String  name;
    private String id;
    private Boolean mythic;
    private String normalSpellId; // pour les sorts mythic

    private String  descr;
    private String  dice_type;
    private Double  n_dice_per_lvl;
    private int     cap_dice;
    private int flat_dmg=0;

    private DmgType  dmg_type;
    private String  range;
    private String contact;
    private boolean contactFailed;
    private String area;
    private String  cast_time;
    private String  duration;

    private List<String> compoList=new ArrayList<>();

    private String rm;
    private String  save_type;

    private int     rank;
    private boolean perfect;
    private String perfectMetaId="";

    private int     n_sub_spell;

    private SharedPreferences settings;

    private MetaList metaList;


    private boolean binded=false;
    private Spell bindedParent =null;
    private Cast cast;
    private boolean crit=false;

    private Tools tools=new Tools();

    private SpellProfile spellProfile;

    private int dmgResult=0;

    public Spell(Spell spell){ //copying spell
        this.id=spell.id;
        this.mythic=spell.mythic;
        this.normalSpellId=spell.normalSpellId;
        this.name=spell.name;
        this.descr=spell.descr;
        this.dice_type=spell.dice_type;
        this.n_dice_per_lvl=spell.n_dice_per_lvl;
        this.cap_dice=spell.cap_dice;
        this.flat_dmg=spell.flat_dmg;
        this.range=spell.range;
        this.contact=spell.contact;
        this.area=spell.area;
        this.cast_time=spell.cast_time;
        this.duration=spell.duration;
        this.compoList=spell.compoList;
        this.rm=spell.rm;
        this.save_type=spell.save_type;
        this.rank=spell.rank;
        this.perfect=spell.perfect;
        this.perfectMetaId=spell.perfectMetaId;
        this.n_sub_spell=spell.n_sub_spell;
        this.dmg_type=new DmgType(spell.dmg_type);
        this.metaList=new MetaList(spell.metaList);
        this.cast =new Cast();
        this.settings=spell.settings;
    }

    public Spell(String id,String mythic,String normalSpellId, String name, String descr,Integer n_sub_spell, String dice_type, Double n_dice_per_lvl, int cap_dice, String dmg_type,int flat_dmg, String range,String contact,String area, String cast_time, String duration, String compo, String rm, String save_type, int rank,Context mC){
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if(id.equalsIgnoreCase("")){
            this.id=name;
        } else {
            this.id=id;
        }
        this.mythic=Boolean.valueOf(mythic);
        this.normalSpellId=normalSpellId;
        this.name=name;
        this.descr=descr;
        this.n_sub_spell=n_sub_spell;
        this.dice_type=dice_type;
        this.n_dice_per_lvl=n_dice_per_lvl;
        this.cap_dice=cap_dice;
        this.dmg_type=new DmgType(dmg_type);
        this.flat_dmg=flat_dmg;
        this.range=range;
        this.contact=contact;
        this.area=area;
        this.cast_time=cast_time;
        this.duration=duration;
        calcCompo(compo);
        this.rm=rm;
        this.save_type=save_type;
        this.rank=rank;
        /* TODO perfect sort heal halda
        if (this.id.equalsIgnoreCase("Désintégration") || this.normalSpellId.equalsIgnoreCase("Désintégration")) {
            if (settings.getBoolean("perfect_desint", mC.getResources().getBoolean(R.bool.perfect_desint_def))) {
                this.perfect=true;
            }
        } */
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

    public Integer getRank(){
        return this.rank;
    }
    public String  getName(){
        return this.name;
    }
    public String  getDescr(){
        return this.descr;
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
    public int getFlat_dmg() {
        return flat_dmg;
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

    public boolean isHighscore(int val){
        boolean returnVal=false;
        int highscore=settings.getInt(this.id+"_highscore",0);
        if(val>highscore){
            returnVal=true;
            settings.edit().putInt(this.id+"_highscore",val).apply();
            int highscoreAllSpells=settings.getInt("all_spells_highscore",0);
            if(val>highscoreAllSpells){
                settings.edit().putInt("all_spells_highscore",val).apply();
            }
        }
        return returnVal;
    }

    public int getHighscore(){
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

        if(metaId.equalsIgnoreCase("meta_heighten") ){
            CompositeListner compoList = new CompositeListner();
            compoList.addOnclickListener(metaList.getMetaByID(metaId).getOnChangeListener());
            compoList.addOnclickListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if ( rank+metaList.getMetaByID("meta_heighten").getnCast()>9){
                        metaList.getMetaByID("meta_heighten").desactive();
                    }
                }
            });

            check.setOnCheckedChangeListener(compoList);
                if (this.rank>=9 || this.rank+metaList.getMetaByID("meta_heighten").getnCast()>=9 ){
            check.setEnabled(false);}
        }

        if(metaId.equalsIgnoreCase("meta_focus") && (this.save_type.equalsIgnoreCase("") || this.save_type.equalsIgnoreCase("aucun"))){
            check.setEnabled(false);
        }

        if(metaId.equalsIgnoreCase("meta_extend") && !this.dmg_type.getDmgType().equalsIgnoreCase("") &&  this.dice_type.equalsIgnoreCase("lvl")){
            check.setEnabled(false);
        }

        // TODO faire int maxLevelWedge= tools.toInt(settings.getString("wedge_max_lvl_spell",String.valueOf(mC.getResources().getInteger(R.integer.wedge_max_lvl_spell_def))));
        int maxLevelWedge=2;
        if(this.rank> maxLevelWedge && metaId.equalsIgnoreCase("meta_arrow")){
            check.setEnabled(false);
        }

        if(this.dmg_type.getDmgType().equalsIgnoreCase("") && metaId.equalsIgnoreCase("meta_enhance")){
            check.setEnabled(false);
        }

        List<String> rangesDisable = Arrays.asList("contact","personnelle");
        if(rangesDisable.contains(this.range) && metaId.equalsIgnoreCase("meta_select")){
            check.setEnabled(false);
        }

        List<String> rangesAccepted = Arrays.asList("contact","courte","moyenne");
        if(!rangesAccepted.contains(this.range) && metaId.equalsIgnoreCase("meta_range")){
            check.setEnabled(false);
        }
        List<String> durationDisable = Arrays.asList("instant","permanente");
        if(durationDisable.contains(this.duration) && metaId.equalsIgnoreCase("meta_duration")){
            check.setEnabled(false);
        }
        if(!this.cast_time.equalsIgnoreCase("simple") && metaId.equalsIgnoreCase("meta_quicken")){
            check.setEnabled(false);
        }
        if(!this.compoList.contains("V") && metaId.equalsIgnoreCase("meta_silent")){
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
        this.dmg_type=previousSpellToBind.dmg_type;
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
