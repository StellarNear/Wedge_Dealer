//package pathfinder.pathfinder_dealer.Rolls;
//
//import android.util.Log;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.math.RoundingMode;
//import java.util.HashMap;
//import java.util.Map;
//
//import pathfinder.pathfinder_dealer.R;
//
//
///**
// * Created by Utilisateur on 13/02/2018.
// */
//
//public class ProbaFromDiceRand {
//    private RollList selectedRollList;
//    private int maxPhy;
//    private int minPhy;
//    private int sumPhy;
//    private int maxFire;
//    private int minFire;
//    private int sumFire;
//    private Map<Integer, Integer> mapTypeDiceNumberDice = new HashMap<>();
//
//    public ProbaFromDiceRand(RollList selectedRollList) {
//        this.selectedRollList = selectedRollList;
//        buildParameters();
//    }
//
//    private void buildParameters() {
//        maxPhy = selectedRollList.getMaxDmgFromType();
//        minPhy = selectedRollList.getMinDmgFromType();
//        sumPhy = selectedRollList.getDmgSumFromType();
//        maxFire = selectedRollList.getMaxDmgFromType("fire");
//        minFire = selectedRollList.getMinDmgFromType("fire");
//        sumFire = selectedRollList.getDmgSumFromType("fire");
//    }
//
//    private Double getProba(DiceList diceList, int sum) {
//        int nd10 = diceList.filterWithNface(10).size();
//        int nd8 = diceList.filterWithNface(8).size();
//        int nd6 = diceList.filterWithNface(6).size();
//        mapTypeDiceNumberDice.put(10, nd10);
//        mapTypeDiceNumberDice.put(8, nd8);
//        mapTypeDiceNumberDice.put(6, nd6);
//
//        Integer total = 10 * nd10 + 8 * nd8 + 6 * nd6;
//
//        Log.d("STATE (table)total", String.valueOf(total));
//        BigInteger[] combi_old = new BigInteger[total];          // table du nombre de combinaison pour chaque valeur somme
//        BigInteger[] combi_new = new BigInteger[total];
//        for (int i = 1; i <= total; i++) {
//            combi_old[i - 1] = BigInteger.ZERO;
//            combi_new[i - 1] = BigInteger.ZERO;
//        }
//
//        for (Integer diceType : mapTypeDiceNumberDice.keySet()) {
//            if (mapTypeDiceNumberDice.get(diceType) != 0) {
//                for (int i = 1; i <= diceType; i++) {                     //on rempli la premiere itération
//                    combi_old[i - 1] = BigInteger.ONE;
//                }
//                mapTypeDiceNumberDice.put(diceType, mapTypeDiceNumberDice.get(diceType) - 1);
//                break;
//            }
//        }
//
//        for (Integer diceType : mapTypeDiceNumberDice.keySet()) {
//            for (int i = 1; i <= mapTypeDiceNumberDice.get(diceType); i++) {              //pour chaque nouveau dès on ajoute la somme(cf https://wizardofodds.com/gambling/dice/2/)
//                for (int j = 1; j <= total; j++) {
//                    for (int k = diceType; k >= 1; k--) {
//                        if (j - 1 - k >= 0) {
//                            combi_new[j - 1] = combi_new[j - 1].add(combi_old[j - 1 - k]);
//                        }
//                    }
//                }
//                for (int l = 1; l <= total; l++) {
//                    combi_old[l - 1] = combi_new[l - 1];
//                    combi_new[l - 1] = BigInteger.ZERO;
//                }
//            }
//        }
//
//        BigInteger sum_combi, sum_combi_tot;
//        sum_combi = BigInteger.ZERO;
//        sum_combi_tot = BigInteger.ZERO;
//        for (int i = 1; i <= total; i++) {
//            sum_combi_tot = sum_combi_tot.add(combi_old[i - 1]);
//            if (i == sum) {
//                sum_combi = sum_combi_tot;
//            }
//        }
//
//        BigDecimal temp_sum = new BigDecimal(sum_combi);
//        BigDecimal temp_sum_tot = new BigDecimal(sum_combi_tot);
//        BigDecimal result_percent;
//        result_percent = temp_sum.divide(temp_sum_tot, 4, RoundingMode.HALF_UP);
//        return result_percent.doubleValue();
//    }
//
//    private String getRange(String mode) {
//        int max = 0, min = 0, sum = 0;
//        if (mode.equalsIgnoreCase("phy")) {
//            max = maxPhy;
//            min = minPhy;
//            sum = sumPhy;
//        }
//        if (mode.equalsIgnoreCase("fire")) {
//            max = maxFire;
//            min = minFire;
//            sum = sumFire;
//        }
//        Integer ecart = max - min;
//        Double percentage = 0d;
//        if (ecart != 0) {
//            percentage = 100d * (sum - min) / ecart;
//        }
//        String rangeTxt = "[" + min + " - " + max + "]\n(" + String.valueOf(percentage.intValue()) + "%)";
//        return rangeTxt;
//    }
//
//    private String getProba(String mode) {
//        Double percentage = 0d;
//        if (mode.equalsIgnoreCase("phy")) {
//            percentage = 100d - (100 * getProba(selectedRollList.getDmgDiceList().filterWithElement(""), selectedRollList.getDmgDiceList().filterWithElement("").getSum()));
//        }
//        if (mode.equalsIgnoreCase("fire")) {
//            percentage = 100d - (100 * getProba(selectedRollList.getDmgDiceList().filterWithElement("fire"), selectedRollList.getDmgDiceList().filterWithElement("fire").getSum()));
//        }
//        if (mode.equalsIgnoreCase("phyCrit")) {
//            percentage = 100d - (100 * getProba(selectedRollList.getCritDmgDiceList().filterWithElement("").filterCritable(), selectedRollList.getCritDmgDiceList().filterWithElement("").filterCritable().getSum()));
//        }
//        String proba = "(" + String.format("%.02f", percentage) + "%)";
//        return proba;
//    }
//
//
//    public void giveLinearToFill(LinearLayout linearStats) {
//        TextView rangePhy = linearStats.findViewById(R.id.combat_dialog_phy_range_result);
//        rangePhy.setText(getRange("phy"));
//        TextView probaPhy = linearStats.findViewById(R.id.combat_dialog_phy_proba_result);
//        probaPhy.setText(getProba("phy"));
//        if (selectedRollList.getDmgDiceList().filterWithElement("fire").size() <= 0) {
//            linearStats.findViewById(R.id.combat_dialog_fire_proba).setVisibility(View.GONE);
//            linearStats.findViewById(R.id.combat_dialog_fire_range).setVisibility(View.GONE);
//        } else {
//            linearStats.findViewById(R.id.combat_dialog_fire_proba).setVisibility(View.VISIBLE);
//            linearStats.findViewById(R.id.combat_dialog_fire_range).setVisibility(View.VISIBLE);
//            TextView rangeFire = linearStats.findViewById(R.id.combat_dialog_fire_range_result);
//            rangeFire.setText(getRange("fire"));
//            TextView probaFire = linearStats.findViewById(R.id.combat_dialog_fire_proba_result);
//            probaFire.setText(getProba("fire"));
//        }
//        if (selectedRollList.getDmgDiceList().filterWithElement("").size() <= 0) {
//            linearStats.findViewById(R.id.combat_dialog_phy_crit).setVisibility(View.GONE);
//        } else {
//            linearStats.findViewById(R.id.combat_dialog_phy_crit).setVisibility(View.VISIBLE);
//            TextView probaPhyCrit = linearStats.findViewById(R.id.combat_dialog_phy_crit_proba_result);
//            probaPhyCrit.setText(getProba("phyCrit"));
//        }
//    }
//}
//
