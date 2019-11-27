package stellarnear.wedge_companion.Perso;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.CalculationAtk;
import stellarnear.wedge_companion.CalculationSpell;
import stellarnear.wedge_companion.HallOfFame;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.Spells.Spell;
import stellarnear.wedge_companion.Stats.Stats;
import stellarnear.wedge_companion.Tools;


/**
 * Created by jchatron on 26/12/2017.
 */

public class Perso {

    private Inventory inventory;
    private AllResources allResources;
    private Stats stats;
    private HallOfFame hallOfFame;

    private AllAbilities allAbilities;
    private AllFeats allFeats;
    private AllCapacities allCapacities;
    private AllSkills allSkills;
    private AllMythicFeats allMythicFeats;
    private AllMythicCapacities allMythicCapacities;

    private String pjID;

    private Tools tools=new Tools();
    private Context mC;
    private SharedPreferences prefs;
    private CalculationSpell calculationSpell =new CalculationSpell();

    public Perso(Context mC,String pjID) {
        this.mC=mC;
        this.prefs= PreferenceManager.getDefaultSharedPreferences(mC);
        inventory = new Inventory(mC,pjID);
        stats = new Stats(mC,pjID);
        hallOfFame=new HallOfFame(mC,pjID);
        allFeats = new AllFeats(mC,pjID);
        allCapacities = new AllCapacities(mC,pjID);
        allMythicFeats = new AllMythicFeats(mC,pjID);
        allMythicCapacities = new AllMythicCapacities(mC,pjID);
        allAbilities = new AllAbilities(mC,pjID);
        allSkills = new AllSkills(mC,pjID);
        allResources = new AllResources(mC,allAbilities,allMythicCapacities,pjID);
        this.pjID=pjID;
    }

    public void refresh() {
        allAbilities.refreshAllAbilities();
        allSkills.refreshAllVals();
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

    public Integer getResourceValue(String resId){
        Resource res = allResources.getResource(resId);
        Integer value=0;
        if(res!=null){
            value=res.getCurrent();
        }
        return value;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void castSpell(Integer selected_rank) {
        allResources.castSpell(selected_rank);
    }

    public void castSpell(Spell spell) {
        if (!spell.isCast()){
            spell.cast();
            allResources.castSpell(calculationSpell.currentRank(spell));
            new PostData(mC,new PostDataElement(spell));
        }
    }

    public int getCasterLevel() {
        int val= getAbilityScore("ability_lvl");
        if (prefs.getBoolean("karma_switch",false)){
            val+=4;
        }
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


    public HallOfFame getHallOfFame() {
        return hallOfFame;
    }

    public AllSkills getAllSkills() {
        return allSkills;
    }

    public Integer getSkillBonus(Context mC,String skillId) {
        int bonusTemp = allSkills.getSkill(skillId).getBonus();
        String suffix=pjID.equalsIgnoreCase("") ? "" : "_"+pjID;
        if(inventory.getAllEquipments().testIfNameItemIsEquipped("Pierre porte bonheur")){
            bonusTemp+=1;
        }
        if(skillId.equalsIgnoreCase("skill_estimate")) {
            Equipment gant = inventory.getAllEquipments().getEquipmentsEquiped("hand_slot");
            if (gant != null && gant.getName().equalsIgnoreCase("Gant d'estimation")) {
                bonusTemp += 3;
            }
        }
        if(skillId.equalsIgnoreCase("skill_stealth")) {
            Equipment boot = inventory.getAllEquipments().getEquipmentsEquiped("equipment_feet");
            if (boot != null && boot.getName().equalsIgnoreCase("Bottes elfiques")) {
                bonusTemp += 5;
            }
        }
        List<String> listSkillPredil = Arrays.asList("skill_geo","skill_nature","skill_stealth","skill_percept","skill_survival");

        if(listSkillPredil.contains(skillId)) {
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

        return bonusTemp;
    }


    public Integer getAbilityScore(String abiId) {
        int abiScore = 0;
        String suffix=pjID.equalsIgnoreCase("") ? "" : "_"+pjID;

        if (allAbilities.getAbi(abiId) != null) {
            abiScore = allAbilities.getAbi(abiId).getValue();

            if (abiId.equalsIgnoreCase("ability_ca")) {
                abiScore += tools.toInt(prefs.getString("bonus_global_temp_ca",String.valueOf(0)));
                abiScore += tools.toInt(prefs.getString("bonus_temp_ca"+suffix,String.valueOf(0)));
                int dexMod=getAbilityMod("ability_dexterite");
                if(pjID.equalsIgnoreCase("") && inventory.getAllEquipments().testIfNameItemIsEquipped("Chemise du veilleur") && dexMod>=10){
                    abiScore+=10;
                } else {
                    abiScore+=dexMod;
                }
            }

            if (abiId.equalsIgnoreCase("ability_equipment")) {
                abiScore= inventory.getAllItemsCount();
            }

            if (abiId.equalsIgnoreCase("ability_rm")) {
                int bonusRmGlobal = tools.toInt(prefs.getString("bonus_global_temp_rm", String.valueOf(0)));
                int bonusRm = tools.toInt(prefs.getString("bonus_temp_rm"+suffix, String.valueOf(0)));
                if (bonusRm>abiScore) { abiScore = bonusRm; }
                if (bonusRmGlobal>abiScore) { abiScore = bonusRmGlobal; }
            }

            if (abiId.equalsIgnoreCase("ability_init")) {
                if(getAllMythicCapacities().mythicCapacityIsActive("mythiccapacity_init")) {
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
            }

            if (abiId.equalsIgnoreCase("ability_ref")||abiId.equalsIgnoreCase("ability_vig")||abiId.equalsIgnoreCase("ability_vol")) {
                abiScore += tools.toInt(prefs.getString("bonus_global_temp_save",String.valueOf(0)));
                abiScore += tools.toInt(prefs.getString("bonus_temp_save"+suffix,String.valueOf(0)));
                int valDefId = mC.getResources().getIdentifier("epic_save_def"+suffix, "integer", mC.getPackageName());
                abiScore += tools.toInt(prefs.getString("epic_save"+suffix,String.valueOf(mC.getResources().getInteger(valDefId))));
                int valDefPermaId = mC.getResources().getIdentifier("switch_perma_resi_DEF"+suffix, "bool", mC.getPackageName());
                if (prefs.getBoolean("switch_perma_resi"+suffix,mC.getResources().getBoolean(valDefPermaId))) {
                    abiScore+=1;
                }
                Equipment torse= inventory.getAllEquipments().getEquipmentsEquiped("armor_slot") ;
                if (torse!=null && torse.getName().equalsIgnoreCase("Robe d'archimage grise")) {  //TODO la cape de halda
                    abiScore+=4;
                }

                if (abiId.equalsIgnoreCase("ability_ref")){
                    abiScore+=getAbilityMod("ability_dexterite");
                    if(getAllFeats().featIsActive("feat_inhuman_reflexes")){
                        abiScore+=2;
                    }
                }
                if (abiId.equalsIgnoreCase("ability_vig")){
                    abiScore+=getAbilityMod("ability_constitution");

                }
                if (abiId.equalsIgnoreCase("ability_vol")){
                    abiScore+=getAbilityMod("ability_sagesse");
                }
            }
            if(abiId.equalsIgnoreCase("ability_bmo")||abiId.equalsIgnoreCase("ability_dmd")){
                abiScore+=getBaseAtk();
            }
        }
        return abiScore;
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
        this.allMythicFeats.reset();
        this.allMythicCapacities.reset();
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