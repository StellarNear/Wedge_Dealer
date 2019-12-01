package stellarnear.wedge_companion.FormSpell;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Spells.DisplayedText;
import stellarnear.wedge_companion.Tools;

public class FormSpellProfile {
    private Activity mA;
    private Context mC;
    private FormPower spell;
    private View profile;
    private FormCalculationSpell calculationSpell =new FormCalculationSpell();
    private DisplayedText displayText =new DisplayedText();
    private FormSpellProfileManager profileManager;
    private OnRefreshEventListener mListener;
    private Perso pj = PersoManager.getCurrentPJ();
    private Tools tools=new Tools();

    public FormSpellProfile(FormPower spell){
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
        profileManager = new FormSpellProfileManager(mA,mC,spell,profile);
        profileManager.setRefreshEventListener(new FormSpellProfileManager.OnRefreshEventListener() {
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
        if (spell.getDmg_type().equals("aucun")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_void);
        }else  if (spell.getDmg_type().equals("feu")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_fire);
        }else if (spell.getDmg_type().equals("foudre")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_thunder);
        }else if (spell.getDmg_type().equals("froid")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_cold);
        } else if (spell.getDmg_type().equals("acide")) {
            gd=mC.getDrawable(R.drawable.round_corner_title_acid);
        } else {
            gd=mC.getDrawable(R.drawable.round_corner_title);
        }
        ((RelativeLayout)profile.findViewById(R.id.title_background)).setBackground(gd);
    }

    private String printInfo() {
        String text = "";
        if (spell.getFlat_dmg()>0) {
            text += "Dégats:" + spell.getFlat_dmg() + ", ";
        }
        if (!spell.getDmg_type().equals("")) {
            text += "Typ:" + spell.getDmg_type() + ", ";
        }
        if (!spell.getRange().equals("")) {
            text += "Portée:" + spell.getRange()+ ", ";
        }
        if (!spell.getArea().equals("")) {
            text += "Zone:" + spell.getArea()+ ", ";
        }
        String resistance;
        if (spell.getSave_type().equals("aucun") || spell.getSave_type().equals("")) {
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
