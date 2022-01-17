package stellarnear.wedge_companion.FormSpell;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class FormSpellProfile {
    private Activity mA;
    private Context mC;
    private FormPower spell;
    private View profile;
    private FormCalculationSpell calculationSpell = new FormCalculationSpell();
    private FormDisplayedText displayText = new FormDisplayedText();
    private FormSpellProfileManager profileManager;
    private OnRefreshEventListener mListener;
    private Perso pj = PersoManager.getCurrentPJ();
    private Tools tools = Tools.getTools();
    private int nInfos;

    public FormSpellProfile(FormPower spell) {
        this.spell = spell;
    }

    public View getProfile(Activity mA, Context mC) {
        if (this.profile == null) {
            this.mA = mA;
            this.mC = mC;
            init();
        }
        buildProfile();
        return this.profile;
    }

    private void init() {
        LayoutInflater inflater = mA.getLayoutInflater();
        profile = inflater.inflate(R.layout.formpower_profile, null);
        profileManager = new FormSpellProfileManager(mA, mC, spell, profile);
        profileManager.setRefreshEventListener(new FormSpellProfileManager.OnRefreshEventListener() {
            @Override
            public void onEvent() {
                buildProfile();
                if (mListener != null) {
                    mListener.onEvent();
                } //on communique le refreshCalculations
            }
        });
    }

    private void buildProfile() {
        ((TextView) profile.findViewById(R.id.spell_name)).setText(spell.getName());
        profile.findViewById(R.id.spell_name).postDelayed(new Runnable() {
            @Override
            public void run() {
                profile.findViewById(R.id.description).setSelected(true);
            }
        }, 1500);
        profile.findViewById(R.id.spell_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tools.customToast(mC, spell.getDescr(), "center");
            }
        });

        testSpellForColorTitle();

        ((TextView) profile.findViewById(R.id.description)).setText(spell.getDescr());
        profile.findViewById(R.id.description).postDelayed(new Runnable() {
            @Override
            public void run() {
                profile.findViewById(R.id.description).setSelected(true);
            }
        }, 1500);

        printInfo();
    }

    private void testSpellForColorTitle() {
        Drawable gd = null;
        if (spell.getDmg_type().equals("none")) {
            gd = mC.getDrawable(R.drawable.round_corner_title_void);
        } else if (spell.getDmg_type().equals("fire")) {
            gd = mC.getDrawable(R.drawable.round_corner_title_fire);
        } else if (spell.getDmg_type().equals("shock")) {
            gd = mC.getDrawable(R.drawable.round_corner_title_shock);
        } else if (spell.getDmg_type().equals("frost")) {
            gd = mC.getDrawable(R.drawable.round_corner_title_frost);
        } else if (spell.getDmg_type().equals("acid")) {
            gd = mC.getDrawable(R.drawable.round_corner_title_acid);
        } else {
            gd = mC.getDrawable(R.drawable.round_corner_title);
        }
        profile.findViewById(R.id.title_background).setBackground(gd);
    }

    private void printInfo() {
        ((LinearLayout)profile.findViewById(R.id.infos_col1)).removeAllViews();
        ((LinearLayout)profile.findViewById(R.id.infos_col2)).removeAllViews();
        ((LinearLayout)profile.findViewById(R.id.infos_col3)).removeAllViews();

        nInfos=0;
        if (calculationSpell.nDice(spell) > 0 || spell.getFlat_dmg() > 0) {
            addInfoToGrid(infoElement("Dégats:" + displayText.damageTxt(spell)));
        }
        if (!spell.getDmg_type().equals("")) {
            addInfoToGrid(infoElement("Type:" + ElemsManager.getInstance(mC).getName(spell.getDmg_type())));
        }
        if (!spell.getRange().equals("")) {
            addInfoToGrid(infoElement("Portée:" + displayText.rangeTxt(spell)));
        }
        if (!spell.getArea().equals("")) {
            addInfoToGrid(infoElement("Zone:" + spell.getArea()));
        }
        String resistance;
        if (spell.getSave_type().equals("none") || spell.getSave_type().equals("")) {
            resistance = spell.getSave_type();
        } else {
            int valDD = 10 + pj.getAbilityScore("ability_lvl") / 2 + pj.getAbilityMod("ability_constitution");
            resistance = spell.getSave_type() + "(" + valDD + ")";
        }
        if (!resistance.equals("")) {
            addInfoToGrid(infoElement("Sauv:" + resistance));
        }
    }

    private void addInfoToGrid(TextView infoElement) {
        LinearLayout line=null;
        nInfos++;
        if((nInfos % 3 )== 0){
            line = ((LinearLayout)profile.findViewById(R.id.infos_col3));
        } else if ((nInfos % 2 )== 0) {
            line = ((LinearLayout)profile.findViewById(R.id.infos_col2));
        }else {
            line = ((LinearLayout)profile.findViewById(R.id.infos_col1));
        }
        line.addView(infoElement);
    }

    private TextView infoElement(String txt) {
        TextView textview = new TextView(mC);
        textview.setText(txt);
        textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textview.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textview.setMarqueeRepeatLimit(-1);
        textview.setSingleLine(true);
        textview.setSelected(true);
        return textview;
    }

    public boolean isDone() {
        return this.profileManager.isDone();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }

    public void refreshProfile() {
        buildProfile();
        this.profileManager.refreshProfileMechanisms();
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }
}
