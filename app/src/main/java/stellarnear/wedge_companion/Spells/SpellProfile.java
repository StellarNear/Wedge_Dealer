package stellarnear.wedge_companion.Spells;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Tools;

public class SpellProfile {
    private Activity mA;
    private Context mC;
    private Spell spell;
    private View profile;
    private CalculationSpell calculationSpell =new CalculationSpell();
    private DisplayedText displayText =new DisplayedText();
    private SpellProfileManager profileManager;
    private OnRefreshEventListener mListener;
    private Perso pj = PersoManager.getCurrentPJ();
    private Tools tools=new Tools();

    public SpellProfile(Spell spell){
        this.spell=spell;
    }

    public View getProfile(Activity mA,Context mC){
        if(this.profile==null){
            this.mA=mA;
            this.mC=mC;
            init();
        }
        buildProfile();
        return this.profile;
    }

    private void init() {
        LayoutInflater inflater = mA.getLayoutInflater();
        profile = inflater.inflate(R.layout.spell_profile, null);
        profileManager = new SpellProfileManager(mA,mC,spell,profile);
        profileManager.setRefreshEventListener(new SpellProfileManager.OnRefreshEventListener() {
            @Override
            public void onEvent() {
                buildProfile();
                if(mListener!=null){mListener.onEvent();} //on communique le refresh
            }
        });
    }

    private void buildProfile() {
        ((TextView)profile.findViewById(R.id.spell_name)).setText(spell.getName());
        ((TextView)profile.findViewById(R.id.spell_name)).postDelayed(new Runnable() {
            @Override
            public void run() {
                ((TextView)profile.findViewById(R.id.description)).setSelected(true);
            }
        }, 1500);
        ((TextView)profile.findViewById(R.id.spell_name)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tools.customToast(mC,spell.getDescr(),"center");
            }
        });

        testSpellForColorTitle();

        ((TextView)profile.findViewById(R.id.current_rank)).setText("(rang : "+ calculationSpell.currentRank(spell)+")");

        if(!pj.getAllResources().checkSpellAvailable(calculationSpell.currentRank(spell))){
            ((TextView)profile.findViewById(R.id.current_rank)).setTextColor(Color.RED);
        }else {
            ((TextView)profile.findViewById(R.id.current_rank)).setTextColor(Color.BLACK);
        }

        ((TextView)profile.findViewById(R.id.description)).setText(spell.getDescr());
        ((TextView)profile.findViewById(R.id.description)).postDelayed(new Runnable() {
            @Override
            public void run() {
                ((TextView)profile.findViewById(R.id.description)).setSelected(true);
            }
        }, 1500);

        ((TextView)profile.findViewById(R.id.infos)).setText(printInfo());
    }

    private void testSpellForColorTitle() {
        Drawable gd =null;
        if (spell.getDmg_type().equals("none")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_void);
        }else  if (spell.getDmg_type().equals("fire")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_fire);
        }else if (spell.getDmg_type().equals("shock")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_thunder);
        }else if (spell.getDmg_type().equals("frost")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_cold);
        } else if (spell.getDmg_type().equals("acid")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_acid);
        } else {
            gd=mC.getDrawable(R.drawable.round_corner_title);
        }
        ((RelativeLayout)profile.findViewById(R.id.title_background)).setBackground(gd);
    }

    private String printInfo() {
        String text = "";
        if (calculationSpell.nDice(spell)>0) {
            text += "Dégats:" + displayText.damageTxt(spell) + ", ";
        }
        if (!spell.getDmg_type().equals("")) {
            text += "Type:" + spell.getDmg_type() + ", ";
        }
        if (!spell.getRange().equals("")) {
            text += "Portée:" + displayText.rangeTxt(spell)+ ", ";
        }
        if (!spell.getArea().equals("")) {
            text += "Zone:" + spell.getArea()+ ", ";
        }
        if (!displayText.compoTxt(mC,spell).equalsIgnoreCase("")) {
            text += "Compos:" + displayText.compoTxt(mC,spell) + ", ";
        }
        if (!spell.getCast_time().equals("")) {
            text += "Cast:" + calculationSpell.getCastTimeTxt(spell) + ", ";
        }
        if (!spell.getDuration().equals("") && !spell.getDuration().equalsIgnoreCase("instant")) {
            text += "Durée:" + displayText.durationTxt(spell) + ", ";
        }
        if (!spell.hasRM()||spell.hasRM()) {
            text += "RM:" + (spell.hasRM() ? "oui":"non") + ", ";
        }
        String resistance;
        if (spell.getSave_type().equals("none") || spell.getSave_type().equals("")) {
            resistance = spell.getSave_type();
        } else {
            resistance = spell.getSave_type() + "(" + calculationSpell.saveVal(spell) + ")";
        }
        if (!resistance.equals("")) {
            text += "Sauv:" + resistance + ", ";
        }
        text = text.substring(0, text.length() - 2);
        return text;
    }

    public boolean isDone() {
        return this.profileManager.isDone();
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }

    public void refreshProfile(){
        buildProfile();
        this.profileManager.refreshProfileMechanisms();
    }
}
