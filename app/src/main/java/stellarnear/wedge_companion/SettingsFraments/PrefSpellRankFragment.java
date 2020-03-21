package stellarnear.wedge_companion.SettingsFraments;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceCategory;
import android.text.InputType;

import stellarnear.wedge_companion.EditTextPreference;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Perso.Resource;
import stellarnear.wedge_companion.Spells.SpellsRanksManager;
import stellarnear.wedge_companion.Tools;


public class PrefSpellRankFragment {
    private Perso yfa = PersoManager.getCurrentPJ();
    private Activity mA;
    private Context mC;
    private SpellsRanksManager rankManager;
    private PreferenceCategory spell;
    private Tools tools=Tools.getTools();
    public PrefSpellRankFragment(Activity mA, Context mC){
        this.mA=mA;
        this.mC=mC;
        this.rankManager=yfa.getAllResources().getRankManager();
    }


    public void addSpellRanks(PreferenceCategory spell) {
        this.spell=spell;
        refreshList();
    }

    private void refreshList() {
        rankManager.refreshRanks();
        spell.removeAll();
        for (Resource res : rankManager.getSpellTiers()) {
            EditTextPreference text = new EditTextPreference(mC, InputType.TYPE_CLASS_NUMBER);
            text.setKey(res.getId()+PersoManager.getPJSuffix());
            text.setTitle(res.getShortname());
            text.setSummary(res.getName()+" : %s");
            text.setDefaultValue(String.valueOf(readDef(res.getId())));
            spell.addPreference(text);
        }
    }

    private int readDef(String key) {
        int val=0;
        try {
            int defId = mC.getResources().getIdentifier(key.toLowerCase() + "_def"+PersoManager.getPJSuffix(), "integer", mC.getPackageName());
            val=tools.toInt(String.valueOf(mC.getResources().getInteger(defId)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return val;
    }

    public void refresh() {
        if(spell!=null){refreshList();}
    }
}
