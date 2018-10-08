package stellarnear.wedge_dealer.SettingsFraments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Tools;

public class PrefXpFragment {
    private Activity mA;
    private Context mC;
    private SharedPreferences settings;
    private Tools tools=new Tools();
    public PrefXpFragment(Activity mA,Context mC) {
        this.mA=mA;
        this.mC=mC;
        this.settings=PreferenceManager.getDefaultSharedPreferences(mC);
    }

    public void checkLevel(BigInteger currentXp, BigInteger... addXpInput) {
        BigInteger addXp = addXpInput.length > 0 ? addXpInput[0] : BigInteger.ZERO;
        List<String> listLvlXp = Arrays.asList(mC.getResources().getStringArray(R.array.xp_lvl_needed));
        List<Integer> listLvl = new ArrayList<>();
        List<BigInteger> listXp = new ArrayList<>();
        for (String line : listLvlXp) {
            listLvl.add(tools.toInt(line.substring(0, line.indexOf(":"))));
            listXp.add(tools.toBigInt(line.substring(line.indexOf(":") + 1, line.length())));
        }

        int newLvl = 0;
        for (BigInteger xp : listXp) {
            if ((currentXp.add(addXp)).compareTo(xp) >= 0) {
                newLvl = listLvl.get(listXp.indexOf(xp));
            }
        }

        Integer currentLvl = tools.toInt(settings.getString("ability_lvl", String.valueOf(mC.getResources().getInteger(R.integer.ability_lvl_def))));

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        BigInteger previousLvlXp = listXp.get(listLvl.indexOf(newLvl));
        BigInteger nextLvlXp = listXp.get(listLvl.indexOf(newLvl + 1));

        settings.edit().putString("previous_level", previousLvlXp.toString()).apply();
        settings.edit().putString("next_level", nextLvlXp.toString()).apply();
        settings.edit().putString("ability_lvl", String.valueOf(newLvl)).apply();

        if (currentLvl != newLvl) {
            tools.playVideo(mA, mC, "/raw/saiyan");
            tools.customToast(mC, "Bravo tu as atteint le niveau " + String.valueOf(newLvl));
        }
    }
}
