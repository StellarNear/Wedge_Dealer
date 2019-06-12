package stellarnear.wedge_dealer.Stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_dealer.Rolls.Roll;
import stellarnear.wedge_dealer.Rolls.RollList;

public class Stat {
    private Map<String,Integer> elemSumDmg=new HashMap<>();
    private List<Integer> nthAtksHit =new ArrayList<>();
    private List<Integer> nthAtksMiss =new ArrayList<>();
    private Integer nCrit=0;
    private Integer nCritNat=0;
    private Date date=null;

    public Stat(){  }

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
                nthAtksHit.add(roll.getNthRoll());
            } else if (roll.isMissed()){
                nthAtksMiss.add(roll.getNthRoll());
            }
        }
        this.date=new Date();
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

    public Date getDate() {
        return date;
    }
}
