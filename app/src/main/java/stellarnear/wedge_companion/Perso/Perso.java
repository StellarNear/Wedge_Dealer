package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.CalculationAtk;
import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.HallOfFame;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.Spells.CalculationSpell;
import stellarnear.wedge_companion.Spells.EchoList;
import stellarnear.wedge_companion.Spells.GuardianList;
import stellarnear.wedge_companion.Spells.Spell;
import stellarnear.wedge_companion.Stats.SpellStats.SpellStats;
import stellarnear.wedge_companion.Stats.Stats;
import stellarnear.wedge_companion.Tools;


/**
 * Created by jchatron on 26/12/2017.
 */

public class Perso {

    private Inventory inventory;
    private AllResources allResources;
    private Stats stats;
    private SpellStats spellStats;
    private HallOfFame hallOfFame;

    private AllAbilities allAbilities;
    private AllFeats allFeats;
    private AllForms allForms;
    private AllCanalisationCapacities allCanalisationCapacities;
    private AllCapacities allCapacities;
    private AllSkills allSkills;
    private AllMythicFeats allMythicFeats;
    private AllMythicCapacities allMythicCapacities;

    private String pjID;

    private Tools tools=new Tools();
    private Context mC;
    private SharedPreferences prefs;

    public Perso(Context mC,String pjID) {
        this.mC=mC;
        this.prefs= PreferenceManager.getDefaultSharedPreferences(mC);
        stats = new Stats(mC,pjID);
        hallOfFame=new HallOfFame(mC,pjID);
        allCapacities = new AllCapacities(mC,pjID);
        allSkills = new AllSkills(mC,pjID);
        if(pjID.equalsIgnoreCase("")|| pjID.equalsIgnoreCase("halda")){
            allMythicFeats = new AllMythicFeats(mC,pjID);
            allMythicCapacities = new AllMythicCapacities(mC,pjID);
            spellStats = new SpellStats(mC,pjID);
            if(pjID.equalsIgnoreCase("")){ //wedge
                allForms=new AllForms(mC);
            } else {  //halda
                allCanalisationCapacities=new AllCanalisationCapacities(mC);
            }
        }
        inventory = new Inventory(mC,pjID);
        allFeats = new AllFeats(mC,pjID);
        allAbilities = new AllAbilities(mC,inventory,pjID);
        allResources = new AllResources(mC,allFeats,allAbilities,allCapacities,pjID);
        this.pjID=pjID;
    }

    public void refresh() {
        allSkills.refreshAllVals();
        allAbilities.refreshAllAbilities(); //abi d'abord car resource depend de abi
        allResources.refresh();
    }

    public String getID() {
        return pjID;
    }

    public AllAbilities getAllAbilities() {
        return allAbilities;
    }

    public AllCapacities getAllCapacities() {
        return allCapacities;
    }

    public AllMythicCapacities getAllMythicCapacities() {
        return allMythicCapacities;
    }

    public AllMythicFeats getAllMythicFeats() {
        return allMythicFeats;
    }

    public AllResources getAllResources() {
        return this.allResources;
    }

    public AllForms getAllForms() {
        return allForms;
    }

    public AllCanalisationCapacities getAllCanalisationCapacities() {
        return allCanalisationCapacities;
    }

    public Integer getCurrentResourceValue(String resId){
        Resource res = allResources.getResource(resId);
        Integer value=0;
        if(res!=null){
            value=res.getCurrent();
            if(resId.equalsIgnoreCase("resource_regen")&&allForms!=null&&allForms.hasActiveForm()&&allForms.getCurrentForm().hasCapacity("form_capacity_regen")){
                value+=5;
            }
        }
        return value;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void castSpell(Integer selected_rank) {
        allResources.castSpell(selected_rank);
    }

    public void castSpell(Spell spell,Context context) {
        if (!spell.isCast()){
            spell.cast(context);
            int currentRankSpell=new CalculationSpell().currentRank(spell);
            if(currentRankSpell>0) {
                allResources.castSpell(currentRankSpell);
            }
            new PostData(mC,new PostDataElement(spell));
        }
    }

    public int getCasterLevel() {
        int val= getAbilityScore("ability_lvl");
        String extendID = pjID.equalsIgnoreCase("") ? "" : "_"+pjID;
        val+=tools.toInt(prefs.getString("NLS_bonus"+extendID,String.valueOf(0)));
        return val;
    }

    public void resetTemp() {
        List<String> allTempList = Arrays.asList("NLS_bonus","bonus_dmg_temp","bonus_atk_temp","bonus_temp_ca","bonus_temp_save","bonus_temp_rm");
        String extendID = pjID.equalsIgnoreCase("") ? "" : "_"+pjID;
        for (String temp : allTempList) {
            prefs.edit().putString(temp+extendID, "0").apply();
        }
        if(pjID.equalsIgnoreCase("")){
            prefs.edit().putBoolean("switch_predil_env", false).apply();
        }
    }

    public Stats getStats() {
        return stats;
    }

    public SpellStats getSpellStats() {
        return spellStats;
    }

    public HallOfFame getHallOfFame() {
        return hallOfFame;
    }

    public AllSkills getAllSkills() {
        return allSkills;
    }

    public Integer getSkillRank(String skillId) {
        int rank=allSkills.getSkill(skillId).getRank();
        if(skillId.equalsIgnoreCase("skill_folklore") && inventory.getAllEquipments().getEquipmentsEquiped("face_slot") !=null
                && inventory.getAllEquipments().getEquipmentsEquiped("face_slot").getDescr().contains("folklore")){
            rank=getAbilityScore("ability_lvl");
        } else if(skillId.equalsIgnoreCase("skill_intimidate") && inventory.getAllEquipments().getEquipmentsEquiped("face_slot") !=null
                && inventory.getAllEquipments().getEquipmentsEquiped("face_slot").getDescr().contains("intimidation")){
            rank=getAbilityScore("ability_lvl");
        }
        return rank;
    }

    public Integer getSkillBonus(String skillId) {
        int bonusTemp = allSkills.getSkill(skillId).getBonus();
        bonusTemp+= inventory.getAllEquipments().getSkillBonus(skillId);

        String suffix=pjID.equalsIgnoreCase("") ? "" : "_"+pjID;
        List<String> listSkillPredil = Arrays.asList("skill_geo","skill_nature","skill_stealth","skill_percept","skill_survival");
        if(pjID.equalsIgnoreCase("") && listSkillPredil.contains(skillId)) {
            int valDefPredilID = mC.getResources().getIdentifier("switch_predil_env_def" + suffix, "bool", mC.getPackageName());
            if (prefs.getBoolean("switch_predil_env" + suffix, mC.getResources().getBoolean(valDefPredilID))) {
                switch (prefs.getString("switch_predil_env_val" + suffix, "-").toLowerCase()) {
                    case "foret":
                        bonusTemp += 4;
                        break;
                    case "montagne":
                        bonusTemp += 2;
                        break;
                    case "sous-terrain":
                        bonusTemp += 4;
                        break;
                }
            }
        }
        if((skillId.equalsIgnoreCase("skill_nature")||skillId.equalsIgnoreCase("skill_survival")) && getAllCapacities().capacityIsActive("capacity_natural_instinct")){
            bonusTemp+=2;
        }

        return bonusTemp;
    }

    public Integer getAbilityScore(String abiId) {
        int abiScore = 0;
        String suffix=pjID.equalsIgnoreCase("") ? "" : "_"+pjID;

        if (allAbilities.getAbi(abiId) != null) {
            abiScore = allAbilities.getAbi(abiId).getValue();
            if(allForms!=null && allForms.hasActiveForm()){abiScore+=allForms.getFormAbilityModif(abiId);}

            if(abiId.equalsIgnoreCase("ability_dexterite")) {
                try {
                    int isilDefId = mC.getResources().getIdentifier("isillirit_switch_def" + suffix, "bool", mC.getPackageName());
                    if (prefs.getBoolean("isillirit_switch" + suffix, mC.getResources().getBoolean(isilDefId))) {
                        abiScore += 2;
                    }
                } catch (Exception e) { }
            }else if (abiId.equalsIgnoreCase("ability_ca")) {
                abiScore = 10;
                if(pjID.equalsIgnoreCase("sylphe")|| pjID.equalsIgnoreCase("rana")){
                    int defNatArmorID = mC.getResources().getIdentifier("natural_armor_def" + suffix, "integer", mC.getPackageName());
                    abiScore += tools.toInt(prefs.getString("natural_armor"+suffix,String.valueOf(mC.getResources().getInteger(defNatArmorID))));
                    int bonusNatArmorID = mC.getResources().getIdentifier("bonus_natural_armor_def" + suffix, "integer", mC.getPackageName());
                    abiScore += tools.toInt(prefs.getString("bonus_natural_armor"+suffix,String.valueOf(mC.getResources().getInteger(bonusNatArmorID))));
                    if(allFeats.featIsActive("feat_natural_armor_up")){abiScore+=1;}
                }
                abiScore += tools.toInt(prefs.getString("bonus_global_temp_ca",String.valueOf(0)));
                abiScore += tools.toInt(prefs.getString("bonus_temp_ca"+suffix,String.valueOf(0)));
                int abiMod=0;
                if(allCapacities.capacityIsActive("capacity_revelation_prophetic_armor")){
                    abiMod = getAbilityMod("ability_charisme");
                } else {
                    abiMod = getAbilityMod("ability_dexterite");
                }
                if (inventory.getAllEquipments().hasArmorDexLimitation() && inventory.getAllEquipments().getArmorDexLimitation() < abiMod) {
                    abiScore += inventory.getAllEquipments().getArmorDexLimitation();
                } else {
                    abiScore += abiMod;
                }
                abiScore+=inventory.getAllEquipments().getArmorBonus();
            } else if (abiId.equalsIgnoreCase("ability_equipment")) {
                abiScore= inventory.getAllItemsCount();
            } else if (abiId.equalsIgnoreCase("ability_rm")) {
                int bonusRmGlobal = tools.toInt(prefs.getString("bonus_global_temp_rm", String.valueOf(0)));
                int bonusRm = tools.toInt(prefs.getString("bonus_temp_rm"+suffix, String.valueOf(0)));
                if (bonusRm>abiScore) { abiScore = bonusRm; }
                if (bonusRmGlobal>abiScore) { abiScore = bonusRmGlobal; }
            } else  if (abiId.equalsIgnoreCase("ability_init")) {
                abiScore=getAbilityMod("ability_dexterite");
                if(getAllMythicCapacities()!=null && getAllMythicCapacities().mythicCapacityIsActive("mythiccapacity_init")) {
                    int valDefTierID = mC.getResources().getIdentifier("mythic_tier_def" + suffix, "integer", mC.getPackageName());
                    int currentTier = tools.toInt(prefs.getString("mythic_tier"+suffix, String.valueOf(mC.getResources().getInteger(valDefTierID))));
                    if(getAllMythicFeats().mythicFeatsIsActive("mythicfeat_parangon")){
                        currentTier+=2;
                    }
                    abiScore += currentTier;
                }
                if(getAllFeats().featIsActive("feat_init_sup")){
                    abiScore+=4;
                }
                if(getAllCapacities().capacityIsActive("capacity_old_warrior")){
                    abiScore+=2;
                }

                try {
                    int valDefPredilID = mC.getResources().getIdentifier("switch_predil_env_def" + suffix, "bool", mC.getPackageName());
                    if (prefs.getBoolean("switch_predil_env" + suffix, mC.getResources().getBoolean(valDefPredilID))) {
                        switch (prefs.getString("switch_predil_env_val" + suffix, "-").toLowerCase()) {
                            case "foret":
                                abiScore += 4;
                                break;
                            case "montagne":
                                abiScore += 2;
                                break;
                            case "sous-terrain":
                                abiScore += 4;
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (abiId.equalsIgnoreCase("ability_ref")||abiId.equalsIgnoreCase("ability_vig")||abiId.equalsIgnoreCase("ability_vol")) {
                abiScore += tools.toInt(prefs.getString("bonus_global_temp_save",String.valueOf(0)));
                abiScore += tools.toInt(prefs.getString("bonus_temp_save"+suffix,String.valueOf(0)));
                int valDefId = mC.getResources().getIdentifier("epic_save_def"+suffix, "integer", mC.getPackageName());
                abiScore += tools.toInt(prefs.getString("epic_save"+suffix,String.valueOf(mC.getResources().getInteger(valDefId))));
                int valDefPermaId = mC.getResources().getIdentifier("switch_perma_resi_DEF"+suffix, "bool", mC.getPackageName());
                if (prefs.getBoolean("switch_perma_resi"+suffix,mC.getResources().getBoolean(valDefPermaId))) {
                    abiScore+=1;
                }

                if (abiId.equalsIgnoreCase("ability_ref")){
                    if(allCapacities.capacityIsActive("capacity_revelation_prophetic_armor")){
                        abiScore += getAbilityMod("ability_charisme");
                    } else {
                        abiScore += getAbilityMod("ability_dexterite");
                    }
                    if(getAllFeats().featIsActive("feat_inhuman_reflexes")){
                        abiScore+=2;
                    }
                } else if (abiId.equalsIgnoreCase("ability_vig")){
                    abiScore+=getAbilityMod("ability_constitution");
                    if(getAllFeats().featIsActive("feat_inhuman_sta")){
                        abiScore+=2;
                    }
                } else if (abiId.equalsIgnoreCase("ability_vol")){
                    abiScore+=getAbilityMod("ability_sagesse");
                    if(getAllFeats().featIsActive("feat_iron_will")){
                        abiScore+=2;
                    }
                }
            } else if(abiId.equalsIgnoreCase("ability_bmo")||abiId.equalsIgnoreCase("ability_dmd")){
                abiScore=getBaseAtk();
                if(abiId.equalsIgnoreCase("ability_bmo")){
                    abiScore+=getAbilityMod("ability_force");
                    if(allForms!=null && allForms.hasActiveForm() && allForms.getCurrentForm().hasCapacity("form_capacity_constriction")){
                        abiScore+=4;
                    }
                }
                if(abiId.equalsIgnoreCase("ability_dmd")){
                    abiScore+=getAbilityMod("ability_force");
                    abiScore+=getAbilityMod("ability_dexterite");
                    abiScore+=10;
                }
            } else if(abiId.equalsIgnoreCase("ability_reduc") && allForms!=null && allForms.hasActiveForm() && allForms.getCurrentForm().hasCapacity("form_capacity_elemental_body")){
                abiScore+=5;
            } else if(abiId.equalsIgnoreCase("ability_reduc_elem") && allForms!=null){
                abiScore+=allForms.getMaxResistBonus();
            }
        }
        return abiScore;
    }

    public Spanned getResistsValueLongFormat() {
        Spanned result= new SpannableString("");
        ElemsManager elems=ElemsManager.getInstance(mC);
        for(String elemId : elems.getResistElems()){
            if(result.length()>0){result=(Spanned) TextUtils.concat(result,"/");}
            int valueFromAbi = ((ElementalReducAbility)allAbilities.getAbi("ability_reduc_elem")).getMapElemReduc().get(elemId);
            int valueFromForm = allForms!=null? allForms.getResistBonus(elemId) : 0;
            Spannable span = new SpannableString(String.valueOf(valueFromAbi+valueFromForm));
            span.setSpan(new ForegroundColorSpan(elems.getColorIdDark(elemId)),0,span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            result=(Spanned)TextUtils.concat(result,span);
        }
        return result;
    }

    private int getBaseAtk() {
        CalculationAtk calcAtk= new CalculationAtk(mC);
        return calcAtk.getBaseAtk()+calcAtk.getBonusAtk();
    }

    public Integer getAbilityMod(String abiId) {
        int abiMod = 0;
        Ability abi = allAbilities.getAbi(abiId);
        if (abi != null && abi.getType().equalsIgnoreCase("base")) {
            int abiScore = getAbilityScore(abiId);

            float modFloat = (float) ((abiScore - 10.) / 2.0);
            if (modFloat >= 0) {
                abiMod = (int) modFloat;
            } else {
                abiMod = -1 * Math.round(Math.abs(modFloat));
            }
        }
        return abiMod;
    }

    public AllFeats getAllFeats() {
        return allFeats;
    }

    public void sleep() {
        resetTemp();
        refresh();
        EchoList.getInstance(mC).resetEcho(mC);
        GuardianList.getInstance(mC).resetGuardian(mC);
        allResources.resetCurrent();
    }
    public void halfSleep(){
        resetTemp();
        refresh();
        allResources.halfSleepReset();
    }

    public void loadFromSave(){
        inventory.loadFromSave();
        stats.loadFromSave();
        hallOfFame.loadFromSave();
        refresh();
    }

    public void reset() {
        this.allFeats.reset();
        this.allCapacities.reset();
        if(allMythicFeats!=null){
            this.allMythicFeats.reset();
        }
        if(allMythicCapacities!=null){
            this.allMythicCapacities.reset();
        }
        this.allAbilities.reset();
        this.allResources.reset();
        this.allSkills.reset();
        resetTemp();
        refresh();
        allResources.resetCurrent();
    }

    public void hardReset(){
        this.stats.reset();
        this.hallOfFame.reset();
        this.inventory.reset();
        reset();
    }
}
