package stellarnear.wedge_companion.Spells.ListingElements;

import android.content.Context;
import android.graphics.drawable.Drawable;

import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class DrawableSymbolsSingleton {

    public static DrawableSymbolsSingleton instance = null;
    private Drawable mystSymbol;
    private Drawable utilSymbol;
    private Drawable buffSymbol;
    private Drawable combatBuffSymbol;
    private Drawable healSymbol;
    private Drawable dmgSymbol;
    private Drawable debuffSymbol;
    private Drawable contactRange;
    private Drawable shortRange;
    private Drawable averageRange;
    private Drawable longRange;
    private Tools tools = Tools.getTools();

    private DrawableSymbolsSingleton(Context mC) {
        //, mC.getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        mystSymbol = mC.getDrawable(R.drawable.ic_myst_symbol_draw);
        utilSymbol = mC.getDrawable(R.drawable.ic_util_symbol_draw);
        buffSymbol = mC.getDrawable(R.drawable.ic_buff_symbol_draw);
        combatBuffSymbol = mC.getDrawable(R.drawable.ic_combatbuff_symbol_draw);
        healSymbol = mC.getDrawable(R.drawable.ic_heal_symbol_draw);
        dmgSymbol = mC.getDrawable(R.drawable.ic_dmg_symbol_draw);
        debuffSymbol = mC.getDrawable(R.drawable.ic_debuff_symbol_draw);

        contactRange = mC.getDrawable(R.drawable.ic_contact_range_symbol_draw);
        shortRange = mC.getDrawable(R.drawable.ic_short_range_symbol_draw);
        averageRange = mC.getDrawable(R.drawable.ic_average_range_symbol_draw);
        longRange = mC.getDrawable(R.drawable.ic_long_range_symbol_draw);
    }

    public static DrawableSymbolsSingleton getInstance(Context mC) {  //pour eviter de relire le xml à chaque fois
        if (instance == null) {
            instance = new DrawableSymbolsSingleton(mC);
        }
        return instance;
    }

    public Drawable getMystSymbol() {
        return mystSymbol;
    }

    public Drawable getUtilSymbol() {
        return utilSymbol;
    }

    public Drawable getBuffSymbol() {
        return buffSymbol;
    }

    public Drawable getCombatBuffSymbol() {
        return combatBuffSymbol;
    }

    public Drawable getHealSymbol() {
        return healSymbol;
    }

    public Drawable getDebuffSymbol() {
        return debuffSymbol;
    }

    public Drawable getDmgSymbol() {
        return dmgSymbol;
    }

    public Drawable getContactRange() {
        return contactRange;
    }

    public Drawable getShortRange() {
        return shortRange;
    }

    public Drawable getAverageRange() {
        return averageRange;
    }

    public Drawable getLongRange() {
        return longRange;
    }


}
