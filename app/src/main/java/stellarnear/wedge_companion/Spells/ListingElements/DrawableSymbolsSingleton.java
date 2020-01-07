package stellarnear.wedge_companion.Spells.ListingElements;

import android.content.Context;
import android.graphics.drawable.Drawable;

import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class DrawableSymbolsSingleton {

    public static DrawableSymbolsSingleton instance=null;

    public static DrawableSymbolsSingleton getInstance(Context mC) {  //pour eviter de relire le xml à chaque fois
        if (instance==null) {
            instance = new DrawableSymbolsSingleton(mC);
        }
        return instance;
    }

    private Drawable mystSymbol;
    private Drawable utilSymbol;
    private Drawable buffSymbol;
    private Drawable combatBuffSymbol;
    private Drawable healSymbol;

    private Drawable contactRange;
    private Drawable shortRange;
    private Drawable averageRange;
    private Drawable longRange;

    private Tools tools =new Tools();
    private DrawableSymbolsSingleton(Context mC){
        mystSymbol=tools.resize(mC, R.drawable.ic_myst_symbol, mC.getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        utilSymbol=tools.resize(mC, R.drawable.ic_util_symbol, mC.getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        buffSymbol=tools.resize(mC, R.drawable.ic_buff_symbol, mC.getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        combatBuffSymbol=tools.resize(mC, R.drawable.ic_combat_buff_symbol, mC.getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        healSymbol=tools.resize(mC, R.drawable.ic_heal_symbol, mC.getResources().getDimensionPixelSize(R.dimen.logo_spell_type));

        contactRange=tools.resize(mC, R.drawable.ic_contact_range_symbol, mC.getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        shortRange=tools.resize(mC, R.drawable.ic_short_range_symbol, mC.getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        averageRange=tools.resize(mC, R.drawable.ic_average_range_symbol, mC.getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        longRange=tools.resize(mC, R.drawable.ic_long_range_symbol, mC.getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
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
