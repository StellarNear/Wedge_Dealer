package stellarnear.wedge_companion.Spells;

import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Tools;

public class CalculationSpell {
    private Perso pj = PersoManager.getCurrentPJ();
    private Tools tools = Tools.getTools();

    public CalculationSpell() {
    }

    public int saveVal(Spell spell) {
        int val = 10;
        if (pj.getID().equalsIgnoreCase("")) {
            val += pj.getAbilityMod("ability_sagesse");
        } else {
            val += pj.getAbilityMod("ability_charisme");
            if(spell.isFromMystery()){
                val+=pj.getAllCapacities().getCapacity("capacity_unraveled_mystery").getValue();
            }
        }

        val += spell.getRank();
        return val;
    }


    public int casterLevel() {
        return pj.getCasterLevel();
    }

    public int nDice(Spell spell) {
        int val = 0;
        if (spell.getN_dice_per_lvl() == 0.0) {
            val = spell.getCap_dice();
        } else {
            if ((casterLevel() * spell.getN_dice_per_lvl() > spell.getCap_dice()) && (spell.getCap_dice() != 0) && !pj.getAllCapacities().capacityIsActive("capacity_revelation_improved_heal")) {
                val = spell.getCap_dice();
            } else {
                val = (int) (casterLevel() * spell.getN_dice_per_lvl());
            }
        }
        if (spell.getMetaList().metaIdIsActive("meta_moon") && !spell.getDice_type().contains("lvl") && !spell.getDice_type().equalsIgnoreCase("")) {
            val = (int) (val * 1.5);
        }
        return val;
    }

    public int diceType(Spell spell) {
        int val = tools.toInt(spell.getDice_type());
        return val;
    }

    public int currentRank(Spell spell) {
        int val = spell.getRank();
        int upRankFromMeta = 0;
        for (Metamagic meta : spell.getMetaList().filterActive().asList()) {
            //une méta peut etre gratuite venant de conversion arcanique ou de perfectin magique
            boolean metaFreePerfect = spell.getPerfectMetaId().equalsIgnoreCase(meta.getId());
            if (!metaFreePerfect) {
                upRankFromMeta += meta.getUprank() * meta.getnCast();
            }
        }

        if (spell.isFromMystery() && pj.getAllCapacities().capacityIsActive("capacity_unraveled_mystery")) {
            int valReduc = pj.getAllCapacities().getCapacity("capacity_unraveled_mystery").getValue();
            if (upRankFromMeta - valReduc > 0) {
                val += upRankFromMeta - valReduc;
            }
        } else {
            val += upRankFromMeta;
        }
        return val;
    }

    public Double range(Spell spell) {
        String range = spell.getRange();
        List<String> rangesLvl = Arrays.asList("contact", "courte", "moyenne", "longue");

        Double distDouble = -1.0;
        int indexRange = rangesLvl.indexOf(range);
        if (spell.getMetaList().metaIdIsActive("meta_range") && rangesLvl.contains(range) && !range.equalsIgnoreCase("longue")) {
            indexRange += spell.getMetaList().getMetaByID("meta_range").getnCast();
        }
        if (indexRange >= 0) {
            Integer lvl = casterLevel();
            if (indexRange >= rangesLvl.size()) {
                indexRange = rangesLvl.size() - 1;
            }
            switch (rangesLvl.get(indexRange)) {
                case ("contact"):
                    distDouble = 0.0;
                    break;
                case ("courte"):
                    distDouble = 7.5 + 1.5 * (lvl / 2.0);
                    break;
                case ("moyenne"):
                    distDouble = 30.0 + 3.0 * lvl;
                    break;
                case ("longue"):
                    distDouble = 120.0 + lvl * 12.0;
                    break;
            }
        }
        return distDouble;
    }

    public int calcRounds(SpellList spellList) {
        int sumAction = 0;
        int nComplexe = 0;
        int nSimple = 0;
        int nRapide = 0;
        int nSpells = 0;
        int nRound = 0;
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
                    if (getCastTimeTxt(spell).contains("round")) {
                        nComplexe += tools.toInt(getCastTimeTxt(spell).replace("rounds", "").replace("round", ""));
                    } else if (getCastTimeTxt(spell).contains("min")) {
                        nComplexe += tools.toInt(getCastTimeTxt(spell).replace("min", "")) * 60 / 6;  //converti le nombre de minute en round
                    }
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
        String val = spell.getCast_time();
        if (val.equalsIgnoreCase("simple") && spell.getMetaList().metaIdIsActive("meta_quicken")) {
            val = "rapide";
        }
        return val;
    }

    public String getContact(Spell spell) {
        String contact = spell.getContact();
        if (contact.equalsIgnoreCase("melee") && spell.getMetaList().metaIdIsActive("meta_range")) {
            contact = "distance";
        }
        return contact;
    }

    public int getFlatDmg(Spell spell) {
        int val = 0;
        if (spell.getFlat_dmg().contains("/lvl")) {
            Double valFlatLeveled = tools.toDouble(spell.getFlat_dmg().split("/lvl")[0]) * casterLevel();
            if (valFlatLeveled > spell.getFlat_cap() && spell.getFlat_cap() > 0 && !pj.getAllCapacities().capacityIsActive("capacity_revelation_improved_heal")) {
                valFlatLeveled = 1.0 * spell.getFlat_cap();
            }
            val = valFlatLeveled.intValue();
        } else if (spell.getFlat_dmg().equalsIgnoreCase("for")) {
            val = pj.getAbilityMod("ability_force");
        } else if (spell.getFlat_dmg().equalsIgnoreCase("cha")) {
            val = pj.getAbilityMod("ability_charisme");
        }  else {
            val = tools.toInt(spell.getFlat_dmg());
        }
        return val;
    }
}
