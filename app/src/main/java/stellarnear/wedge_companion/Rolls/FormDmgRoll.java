package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;

import stellarnear.wedge_companion.Attack.Attack;
import stellarnear.wedge_companion.Perso.FormCapacity;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Rolls.Dices.Dice;

public class FormDmgRoll extends DmgRoll {
    private Attack attack;
    public FormDmgRoll(Activity mA, Context mC, Attack atk, Boolean critConfirmed, Boolean naturalCrit) {
        super(mA, mC, critConfirmed, naturalCrit);
        this.attack=atk;
    }

    public void setDmgRand() {
        setCritMultiplier();
        addDmgDices();
        this.bonusDmg = getBonusDmg();
        for (Dice dice : allDiceList.getList()) {
            dice.rand(manualDiceDmg);
        }
        if(attack.hasPower()){
            String message;
            if(attack.getCapacities().size()==1){
                message="N'oublies pas d'utiliser la capacité "+attack.getCapacities().get(0).getName();
            } else {
                message="N'oublies pas d'utiliser les capacités ";
                for(FormCapacity capa : attack.getCapacities()){
                    message+=" "+capa.getName();
                }
            }
            tools.customToast(mC,message,"center");
        }
    }

    private void setCritMultiplier() {
        critMultiplier = 2; //de base c'est *2
    }

    private void addDmgDices() {
        for(int i=1;i<=attack.getnDice();i++) {
            Dice dice = new Dice(mA, mC, attack.getDiceType());
            if (critConfirmed) {
                dice.makeCritable();
            }
            allDiceList.add(dice);
        }
    }

    public int getBonusDmg() { //par defaut aucun bonus
        int calcBonusDmg = 0;
        calcBonusDmg+= pj.getAbilityMod("ability_force");
        calcBonusDmg += tools.toInt(settings.getString("bonus_global_dmg_temp", String.valueOf(0)));
        calcBonusDmg += tools.toInt(settings.getString("bonus_dmg_temp"+PersoManager.getPJSuffix(), String.valueOf(0)));
        return calcBonusDmg;
    }

}
