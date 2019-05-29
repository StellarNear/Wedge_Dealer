package stellarnear.wedge_dealer.Stats;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_dealer.Rolls.Roll;
import stellarnear.wedge_dealer.Rolls.RollList;

public class Stat {

    private Map<String,Integer> elemSumDmg=new HashMap<>();
    private Integer nAtk=0;
    private Integer nCrit=0;
    private Integer nCritNat=0;

    public Stat(){

    }

    public void feedStat(RollList rolls){
        List<String> elems = Arrays.asList("", "fire", "shock", "frost");
        for (String elem : elems){
            elemSumDmg.put(elem,rolls.getDmgSumFromType(elem));
        }

        for (Roll roll:rolls.getList()){
            if(roll.isCritConfirmed()){
                nCrit++;
                if(roll.getAtkRoll().getAtkDice().getRandValue()==20){
                    nCritNat++;
                }
            }
            if( roll.isHitConfirmed()){
               nAtk++;
            }

        }
    }

    public Integer getnAtk() {
        return nAtk;
    }

    public Integer getnCrit() {
        return nCrit;
    }

    public Integer getnCritNat() {
        return nCritNat;
    }

    public Map<String, Integer> getElemSumDmg() {
        return elemSumDmg;
    }
}
