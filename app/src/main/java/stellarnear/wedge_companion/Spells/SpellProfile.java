package stellarnear.wedge_companion.Spells;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.CustomAlertDialog;
import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class SpellProfile {
    private Activity mA;
    private Context mC;
    private Spell spell;
    private View profile;
    private CalculationSpell calculationSpell;
    private DisplayedText displayText;
    private SpellProfileManager profileManager;
    private OnRefreshEventListener mListener;
    private Perso pj = PersoManager.getCurrentPJ();
    private Tools tools = Tools.getTools();

    public SpellProfile(Spell spell) {
        this.spell = spell;
    }

    public View getProfile(Activity mA, Context mC) {
        displayText = new DisplayedText();
        calculationSpell = new CalculationSpell();
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
        profile = inflater.inflate(R.layout.spell_profile, null);
        profileManager = new SpellProfileManager(mA, mC, spell, profile);
        profileManager.setRefreshEventListener(new SpellProfileManager.OnRefreshEventListener() {
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
                profile.findViewById(R.id.spell_name).setSelected(true);
                profile.findViewById(R.id.description).setSelected(true);
            }
        }, 1500);
        profile.findViewById(R.id.spell_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupLongText(spell.getDescr());
            }
        });

        testSpellForColorTitle();

        ((TextView) profile.findViewById(R.id.current_rank)).setText("(rang : " + calculationSpell.currentRank(spell) + ")");
        setMetaPopup((TextView) profile.findViewById(R.id.current_rank));
        if (!pj.getAllResources().checkSpellAvailable(calculationSpell.currentRank(spell))) {
            ((TextView) profile.findViewById(R.id.current_rank)).setTextColor(Color.RED);
        } else {
            ((TextView) profile.findViewById(R.id.current_rank)).setTextColor(Color.BLACK);
        }

        ((TextView) profile.findViewById(R.id.description)).setText(spell.getShortDescr());
        profile.findViewById(R.id.description).postDelayed(new Runnable() {
            @Override
            public void run() {
                profile.findViewById(R.id.description).setSelected(true);
            }
        }, 1500);

        printInfo();
    }

    private void setMetaPopup(TextView viewById) {
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spell.getMetaList().hasAnyMetaActive()) {
                    String text = " Liste des métamagies :";
                    for (Metamagic meta : spell.getMetaList().asList()) {
                        if (meta.isActive()) {
                            text += "\n" + meta.getName() + " (" + meta.getUprank() + ")";
                        }
                    }
                    tools.customToast(mC, text, "center");
                }
            }
        });
    }

    private void popupLongText(String descr) {
        View tooltip = mA.getLayoutInflater().inflate(R.layout.custom_toast_info_lone_text, null);
        final CustomAlertDialog tooltipAlert = new CustomAlertDialog(mA, mC, tooltip);
        tooltipAlert.setPermanent(true);
        ((TextView) tooltip.findViewById(R.id.toast_textDescr)).setText(descr);
        tooltipAlert.clickToHide(tooltip.findViewById(R.id.toast_LinearLayout));
        tooltipAlert.showAlert();
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
        } else if (spell.getDmg_type().equals("heal")) {
            gd = mC.getDrawable(R.drawable.round_corner_title_heal);
        } else {
            gd = mC.getDrawable(R.drawable.round_corner_title);
        }
        profile.findViewById(R.id.title_background).setBackground(gd);
    }

    private void printInfo() {
        GridLayout grid = profile.findViewById(R.id.infos);
        grid.removeAllViews();
        if (calculationSpell.nDice(spell) > 0) {
            if (spell.getDmg_type().equalsIgnoreCase("heal")) {
                grid.addView(infoElement("Soins:" + displayText.damageTxt(spell)));
            } else {
                grid.addView(infoElement("Dégats:" + displayText.damageTxt(spell)));
            }
        }
        if (!spell.getDmg_type().equals("")) {
            grid.addView(infoElement("Type:" + ElemsManager.getInstance(mC).getName(spell.getDmg_type())));
        }
        if (!spell.getRange().equals("")) {
            grid.addView(infoElement("Portée:" + displayText.rangeTxt(spell)));
        }
        if (!spell.getArea().equals("")) {
            grid.addView(infoElement("Zone:" + displayText.areaTxt(spell)));
        }
        if (!displayText.compoTxt(spell).equalsIgnoreCase("")) {
            TextView compos = infoElement("Compos:" + displayText.compoTxt(spell));
            if (displayText.compoTxt(spell).contains("M")) {
                compos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tools.customToast(mC, "Composant : " + spell.getCompoM(), "center");
                    }
                });
            }
            grid.addView(compos);
        }
        if (!spell.getCast_time().equals("")) {
            grid.addView(infoElement("Cast:" + calculationSpell.getCastTimeTxt(spell)));
        }
        if (!spell.getDuration().equals("") && !spell.getDuration().equalsIgnoreCase("instant")) {
            grid.addView(infoElement("Durée:" + displayText.durationTxt(spell)));
        }
        if (!spell.hasRM() || spell.hasRM()) {
            grid.addView(infoElement("RM:" + (spell.hasRM() ? "oui" : "non")));
        }
        String resistance;
        if (spell.getSave_type().equals("aucun") || spell.getSave_type().equals("")) {
            resistance = spell.getSave_type();
        } else {
            resistance = spell.getSave_type() + "(" + calculationSpell.saveVal(spell) + ")";
        }
        if (!resistance.equals("")) {
            grid.addView(infoElement("Sauv:" + resistance));
        }
    }

    private TextView infoElement(String txt) {
        TextView textview = new TextView(mC);
        textview.setText(txt);
        textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        GridLayout.LayoutParams para = new GridLayout.LayoutParams();
        para.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        para.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        textview.setLayoutParams(para);
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
