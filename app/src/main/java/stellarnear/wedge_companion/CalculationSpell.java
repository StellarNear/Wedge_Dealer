package stellarnear.wedge_companion;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Spells.Metamagic;
import stellarnear.wedge_companion.Spells.Spell;
import stellarnear.wedge_companion.Spells.SpellList;

public class CalculationSpell {
    private Perso pj = PersoManager.getCurrentPJ();
    private Tools tools=new Tools();

    public CalculationSpell(){  }

    public int saveVal(Spell spell){
        int val=10;
        val+= pj.getAbilityMod("ability_charisme");
        val+=spell.getRank();
        if(spell.getMetaList().metaIdIsActive("meta_heighten")){
            val+=spell.getMetaList().getMetaByID("meta_heighten").getnCast() * spell.getMetaList().getMetaByID("meta_heighten").getUprank();
        }
        if(spell.getMetaList().metaIdIsActive("meta_focus")){
            val+=2;
        }

        return val;
    }


    public int casterLevel(Spell spell){
        int val= pj.getCasterLevel();
        if(spell.getMetaList().metaIdIsActive("meta_heighten")){
            val+=spell.getMetaList().getMetaByID("meta_heighten").getnCast() * spell.getMetaList().getMetaByID("meta_heighten").getUprank();
        }

        return val;
    }


    public int casterLevelSR(Spell spell){
        int val= pj.getCasterLevel();
        if(spell.getMetaList().metaIdIsActive("meta_heighten")){
            val+=spell.getMetaList().getMetaByID("meta_heighten").getnCast() * spell.getMetaList().getMetaByID("meta_heighten").getUprank();
        }

        return val;
    }

    public int nDice(Spell spell){
        int val=0;
        if(spell.getN_dice_per_lvl()==0.0){
            val = spell.getCap_dice();
        }else {
            if ((casterLevel(spell) * spell.getN_dice_per_lvl() > spell.getCap_dice()) && (spell.getCap_dice() != 0)) {
                val = spell.getCap_dice();

            } else {
                val = (int) (casterLevel(spell) * spell.getN_dice_per_lvl());
            }
        }
        if(spell.getMetaList().metaIdIsActive("meta_enhance") && tools.toInt(spell.getDice_type())==8){ //car on passe à 2d6
            val = val * 2;
        }
        if(spell.getMetaList().metaIdIsActive("meta_extend")){
            val = (int)(val * 1.5);
        }
        return val;
    }

    public int diceType(Spell spell){
        int val = tools.toInt(spell.getDice_type());
        if(spell.getMetaList().metaIdIsActive("meta_enhance")){
            if(val==4){
                val=6;
            } else if(val==6){
                val=8;
            } else if(val==8) {
                val = 6;
            }
        }
        return val;
    }

    public int currentRank(Spell spell){
        int val=spell.getRank();
        for(Metamagic meta : spell.getMetaList().filterActive().asList()){
            //une méta peut etre gratuite venant de conversion arcanique ou de perfectin magique
            boolean metaFreePerfect = spell.getPerfectMetaId().equalsIgnoreCase(meta.getId());
            if( !(  metaFreePerfect ) ){
                val += meta.getUprank()*meta.getnCast();
            }
        }
        return val;
    }

    public Double range(Spell spell) {
        String range=spell.getRange();
        List<String> rangesLvl = Arrays.asList("contact", "courte", "moyenne", "longue" );

        Double distDouble =-1.0;
        int indexRange = rangesLvl.indexOf(range);
        if(spell.getMetaList().metaIdIsActive("meta_range") && rangesLvl.contains(range) && !range.equalsIgnoreCase("longue")){
            indexRange+=spell.getMetaList().getMetaByID("meta_range").getnCast();
        }
        if (indexRange>=0) {
            Integer lvl = casterLevel(spell);
            if(indexRange>=rangesLvl.size()){indexRange=rangesLvl.size()-1;}
            switch(rangesLvl.get(indexRange)) {
                case ("contact"):
                    distDouble=0.0;
                    break;
                case ("courte"):
                    distDouble=7.5+1.5*(lvl/2.0);
                    break;
                case ("moyenne"):
                    distDouble=30.0+3.0*lvl;
                    break;
                case ("longue"):
                    distDouble=120.0+lvl*12.0;
                    break;
            }
        }
        return distDouble;
    }

    public int calcRounds(Context mC, SpellList spellList) {
        int sumAction = 0;
        int nComplexe = 0;
        int nSimple = 0;
        int nRapide = 0;
        int nSpells = 0;
        int nRound = 0;
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);

        for (Spell spell : spellList.asList()) {
            nSpells += 1;
            switch (getCastTimeTxt(spell)) {
                case "complexe":
                    nComplexe += 1;
                    break;
                case "simple":
                    sumAction += 2;
                    nSimple += 1;
                    break;
                case "rapide":
                    sumAction += 1;
                    nRapide += 1;
                    break;
                default:
                    nComplexe+=tools.toInt(getCastTimeTxt(spell).replace("min",""))*60/6;  //converti le nombre de minute en round
                    break;
            }
        }
        nRound = (int) (Math.ceil(sumAction / 3.0));
        nRound += nComplexe;
        if ((int) (Math.ceil(nRapide / 2.0)) > nRound) {
            nRound = (int) (Math.ceil(nRapide / 2.0));
        }
        if (nSimple > nRound) {
            nRound = nSimple;
        }
        if ((int) (Math.ceil(nSpells / 2.0)) > nRound) {
            nRound = (int) (Math.ceil(nSpells / 2.0));
        }

        return nRound;
    }
    public String getCastTimeTxt(Spell spell) {
        String val=spell.getCast_time();
        if(val.equalsIgnoreCase("simple")&&spell.getMetaList().metaIdIsActive("meta_quicken")){
            val="rapide";
        }
        return val;
    }
    public String getContact(Spell spell){
        String contact=spell.getContact();
        if(contact.equalsIgnoreCase("melee")&&spell.getMetaList().metaIdIsActive("meta_range")){contact="distance";}
        return contact;
    }

}
