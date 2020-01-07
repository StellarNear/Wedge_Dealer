package stellarnear.wedge_companion;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Spells.BuildPreparedSpellList;
import stellarnear.wedge_companion.Spells.ListingElements.LinearLineSpell;
import stellarnear.wedge_companion.Spells.Spell;
import stellarnear.wedge_companion.Spells.SpellList;
import stellarnear.wedge_companion.Spells.SpellsRanksManager;

public class PreparationSpellsAlertDialog {
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog alert;
    private Context mC;
    private boolean positiveButton=false;
    private OnAcceptEventListener mListener;

    private SpellList preparedSpells;
    private SpellList allSpells;
    private Perso wedge=PersoManager.getPJ("Wedge");
    private Tools tools=new Tools();


    public PreparationSpellsAlertDialog(Context mC, final BuildPreparedSpellList buildPreparedSpellList) {
        // Set the toast and duration
        LayoutInflater inflater = LayoutInflater.from(mC);
        View mainView = inflater.inflate(R.layout.custom_pick_prepared_spell,null);
        this.mC=mC;
        this.preparedSpells=buildPreparedSpellList.getPreparedSpells();
        this.allSpells=buildPreparedSpellList.getAllSpells();

        dialogBuilder  = new AlertDialog.Builder(mC, R.style.CustomDialog);
        dialogBuilder.setView(mainView);
        dialogBuilder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                buildPreparedSpellList.saveList();
                if(mListener!=null) {
                    mListener.onEvent();
                }
            }
        });

        addSpellsAndBoxesToLin((LinearLayout)mainView.findViewById(R.id.custom_mainlin));

        positiveButton=true;
        alert = dialogBuilder.create();
    }

    private void addSpellsAndBoxesToLin(LinearLayout linear) {
        SpellsRanksManager manager = wedge.getAllResources().getRankManager();

        for(int i=1;i<=manager.getHighestTier();i++){
            final TextView tier= new TextView(mC);
            LinearLayout.LayoutParams para= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int pixelMarging = mC.getResources().getDimensionPixelSize(R.dimen.general_margin);
            para.setMargins(pixelMarging,pixelMarging,pixelMarging,pixelMarging);
            tier.setLayoutParams(para);
            tier.setBackground(mC.getDrawable(R.drawable.background_tier_title));

            String tier_txt="Tier "+i;

            String titre_tier=tier_txt +" ["+ wedge.getCurrentResourceValue("spell_rank_"+i)+" restant(s)]";
            if (i==0){titre_tier=tier_txt +" [illimité]";}
            SpannableString titre=  new SpannableString(titre_tier);
            titre.setSpan(new RelativeSizeSpan(0.65f), tier_txt.length(),titre_tier.length(), 0);
            tier.setText(titre);

            tier.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tier.setTextColor(Color.BLACK);
            tier.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            linear.addView(tier);
            
            
            for(final Spell spell : allSpells.filterByRank(i).filterDisplayable().asList()){
                LinearLineSpell spellLine =  new LinearLineSpell(spell,mC);
                spellLine.setAddSpellEventListener(new LinearLineSpell.OnAddSpellEventListener() {
                    @Override
                    public void onEvent() {
                        addSpellToList(spell);
                    }
                });
                spellLine.setRemoveSpellEventListener(new LinearLineSpell.OnRemoveSpellEventListener() {
                    @Override
                    public void onEvent() {
                        removeSpellFromSelection(spell);
                    }
                });
                linear.addView(spellLine.getSpellLine());
            }
        }
    }

    private void addSpellToList(Spell spellToAdd) {
        preparedSpells.add(new Spell(spellToAdd));
        toastList();
    }

    private void removeSpellFromSelection(Spell spellToRemove) {
        for(Spell spell : preparedSpells.asList()){
            if(spell.getID().equalsIgnoreCase(spellToRemove.getID())){
                preparedSpells.remove(spell);
                toastList();
                break;
            }
        }
    }



    private void toastList() {
        String text="Sorts:";
        for(Spell spell :preparedSpells.asList()){
            text+="\n"+spell.getName();
        }
        new Tools().customToast(mC,text,"center");
    }



    
    
    // retour aux methode de l'alert
    
    

    public void showAlert() {
        alert.show();
        if(positiveButton){applyStyleToOkButton();}
        int height=LinearLayout.LayoutParams.MATCH_PARENT;
        int width=LinearLayout.LayoutParams.MATCH_PARENT;
        alert.getWindow().setLayout(width,height);
        alert.getWindow().setGravity(Gravity.CENTER);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (settings.getBoolean("switch_fullscreen_mode", mC.getResources().getBoolean(R.bool.switch_fullscreen_mode_DEF))) {
            alert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        alert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private void applyStyleToOkButton() {
        Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) button.getLayoutParams();
        positiveButtonLL.width= ViewGroup.LayoutParams.WRAP_CONTENT;
        positiveButtonLL.setMargins(mC.getResources().getDimensionPixelSize(R.dimen.general_margin),0,0,0);
        button.setLayoutParams(positiveButtonLL);
        button.setTextColor(mC.getColor(R.color.colorBackground));
        button.setBackground(mC.getDrawable(R.drawable.button_ok_gradient));
    }

    public interface OnAcceptEventListener {
        void onEvent();
    }

    public void setAcceptEventListener(OnAcceptEventListener eventListener) {
        mListener = eventListener;
    }
}
