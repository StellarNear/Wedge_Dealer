package stellarnear.wedge_companion.Rolls;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Utilisateur on 13/02/2018.
 */

public class ProbaFromDiceRand {
    private RollList selectedRollList;
    private int max;
    private int min;
    private int sum;
    private String element;
    private Map<Integer, Integer> mapTypeDiceNumberDice = new HashMap<>();

    public ProbaFromDiceRand(RollList selectedRollList) {
        this.selectedRollList = selectedRollList;
    }

    public String getProba(String element, boolean... critArg) {
        this.element=element;
        Boolean crit = critArg.length > 0 ? critArg[0] : false;  //parametre optionnel pour editer le contenu de l'invertaire

        buildParameters();
        Double percentage = 0d;
        if (crit) {
            percentage = 100d - (100 * getProbaPercentage(selectedRollList.getCritDmgDiceList().filterWithElement(element), selectedRollList.getCritDmgDiceList().filterWithElement(element).getSum()));
        } else {
            percentage = 100d - (100 * getProbaPercentage(selectedRollList.getDmgDiceList().filterWithElement(element), selectedRollList.getDmgDiceList().filterWithElement(element).getSum()));
        }
        String proba = String.format("%.02f", percentage) + "%";
        return proba;
    }

    public String getRange(String element) {
        this.element=element;
        buildParameters();
        Integer ecart = max - min;
        Double percentage = 0d;
        if (ecart != 0) {
            percentage = 100d * (sum - min) / ecart;
        }
        String rangeTxt = "[" + min + " - " + max + "]\n(" + String.valueOf(percentage.intValue()) + "%)";
        return rangeTxt;
    }

    private void buildParameters() {
        max = selectedRollList.getMaxDmgFromType(element);
        min = selectedRollList.getMinDmgFromType(element);
        sum = selectedRollList.getDmgSumFromType(element);
    }

    private Double getProbaPercentage(DiceList diceList, int sum) {
        int nd10 = diceList.filterWithNface(10).size();
        int nd8 = diceList.filterWithNface(8).size();
        int nd6 = diceList.filterWithNface(6).size();
        mapTypeDiceNumberDice.put(10, nd10);
        mapTypeDiceNumberDice.put(8, nd8);
        mapTypeDiceNumberDice.put(6, nd6);

        Integer total = 10 * nd10 + 8 * nd8 + 6 * nd6;

        if (total==0){return 0d;}

        //Log.d("STATE (table)total", String.valueOf(total));
        BigInteger[] combi_old = new BigInteger[total];          // table du nombre de combinaison pour chaque valeur somme
        BigInteger[] combi_new = new BigInteger[total];
        for (int i = 1; i <= total; i++) {
            combi_old[i - 1] = BigInteger.ZERO;
            combi_new[i - 1] = BigInteger.ZERO;
        }

        for (Integer diceType : mapTypeDiceNumberDice.keySet()) {
            if (mapTypeDiceNumberDice.get(diceType) != 0) {
                for (int i = 1; i <= diceType; i++) {                     //on rempli la premiere itération
                    combi_old[i - 1] = BigInteger.ONE;
                }
                mapTypeDiceNumberDice.put(diceType, mapTypeDiceNumberDice.get(diceType) - 1);
                break;
            }
        }

        for (Integer diceType : mapTypeDiceNumberDice.keySet()) {
            for (int i = 1; i <= mapTypeDiceNumberDice.get(diceType); i++) {              //pour chaque nouveau dès on ajoute la somme(cf https://wizardofodds.com/gambling/dice/2/)
                for (int j = 1; j <= total; j++) {
                    for (int k = diceType; k >= 1; k--) {
                        if (j - 1 - k >= 0) {
                            combi_new[j - 1] = combi_new[j - 1].add(combi_old[j - 1 - k]);
                        }
                    }
                }
                for (int l = 1; l <= total; l++) {
                    combi_old[l - 1] = combi_new[l - 1];
                    combi_new[l - 1] = BigInteger.ZERO;
                }
            }
        }

        BigInteger sum_combi, sum_combi_tot;
        sum_combi = BigInteger.ZERO;
        sum_combi_tot = BigInteger.ZERO;
        for (int i = 1; i <= total; i++) {
            sum_combi_tot = sum_combi_tot.add(combi_old[i - 1]);
            if (i == sum) {
                sum_combi = sum_combi_tot;
            }
        }

        BigDecimal temp_sum = new BigDecimal(sum_combi);
        BigDecimal temp_sum_tot = new BigDecimal(sum_combi_tot);
        BigDecimal result_percent;
        result_percent = temp_sum.divide(temp_sum_tot, 4, RoundingMode.HALF_UP);
        return result_percent.doubleValue();
    }


}

