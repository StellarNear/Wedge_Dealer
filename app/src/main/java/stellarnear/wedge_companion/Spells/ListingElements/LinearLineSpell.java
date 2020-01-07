package stellarnear.wedge_companion.Spells.ListingElements;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import stellarnear.wedge_companion.CustomAlertDialog;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Spells.Spell;
import stellarnear.wedge_companion.Tools;

public class LinearLineSpell {
    private LinearLayout spellLine;
    private Spell spell;
    private Context mC;
    private CheckBox checkbox;
    private TextView spellName;
    private int nCast=0;

    private Tools tools=new Tools();
    private DrawableSymbolsSingleton drawableSymbolsSingleton;
    private Perso pj= PersoManager.getCurrentPJ();

    private OnAddSpellEventListener mAddListener;
    private OnRemoveSpellEventListener mRemoveListener;


    public LinearLineSpell(final Spell spell, Context mC){
        this.spell=spell;
        this.mC=mC;
        spellLine = new LinearLayout(mC);
        drawableSymbolsSingleton=DrawableSymbolsSingleton.getInstance(mC);
        spellLine.setGravity(Gravity.CENTER_VERTICAL);
        spellLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupLongTooltip();
            }
        });
        spellLine.setOrientation(LinearLayout.HORIZONTAL);
        setSpellLineColor();
        int pixelMarging = mC.getResources().getDimensionPixelSize(R.dimen.general_margin);
        LinearLayout.LayoutParams paraSpellLine= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paraSpellLine.setMargins(pixelMarging,pixelMarging,pixelMarging,0);
        spellLine.setLayoutParams(paraSpellLine);

        checkbox=new CheckBox(mC);
        setAddingSpell();
        setCheckBoxColor();
        spellLine.addView(checkbox);

        addSpellTypeIcons();
        int pxMaging = mC.getResources().getDimensionPixelSize(R.dimen.general_margin);
        spellName = new TextView(mC); spellName.setPadding(pxMaging,0,0,0);
        spellName.setText(spell.getName());
        spellName.setTextColor(Color.BLACK);
        spellName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkbox.setChecked(!checkbox.isChecked());
            }
        });
        spellLine.addView(spellName);
    }
    
    public LinearLayout getSpellLine(){
        return spellLine;
    }


    private void popupLongTooltip(){
        LayoutInflater inflater = LayoutInflater.from(mC);
        View tooltip = inflater.inflate(R.layout.custom_toast_longtooltip, null);
        final CustomAlertDialog tooltipAlert = new CustomAlertDialog(null, mC, tooltip);
        tooltipAlert.setPermanent(true);
        ((TextView)tooltip.findViewById(R.id.toast_textName)).setText(spell.getName());
        ((TextView)tooltip.findViewById(R.id.toast_textDescr)).setText(spell.getDescr());
        tooltipAlert.clickToHide(tooltip.findViewById(R.id.toast_LinearLayout));
        tooltipAlert.showAlert();
    }

    private void addSpellTypeIcons() {
        ImageView logo = new ImageView(mC);
        switch (spell.getType()){
            case "heal":
                logo.setImageDrawable(drawableSymbolsSingleton.getHealSymbol());
                break;
            case "buff":
                logo.setImageDrawable(drawableSymbolsSingleton.getBuffSymbol());
                break;
            case "combatbuff":
                logo.setImageDrawable(drawableSymbolsSingleton.getCombatBuffSymbol());
                break;
            case "util":
                logo.setImageDrawable(drawableSymbolsSingleton.getUtilSymbol());
                break;
            default:
                logo.setImageDrawable(mC.getDrawable(R.drawable.mire_test));
                break;
        }
        spellLine.addView(logo);

        ImageView range = new ImageView(mC);
        switch (spell.getRange()){
            case "contact":
                range.setImageDrawable(drawableSymbolsSingleton.getContactRange());
                break;
            case "courte":
                range.setImageDrawable(drawableSymbolsSingleton.getShortRange());
                break;
            case "moyenne":
                range.setImageDrawable(drawableSymbolsSingleton.getAverageRange());
                break;
            case "longue":
                range.setImageDrawable(drawableSymbolsSingleton.getLongRange());
                break;
        }
        spellLine.addView(range);

        if(spell.isFromMystery()){
            ImageView logoMyst = new ImageView(mC);
            logoMyst.setImageDrawable(drawableSymbolsSingleton.getMystSymbol());
            spellLine.addView(logoMyst);
        }
    }

    private void setAddingSpell() {
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    new AlertDialog.Builder(mC)
                            .setTitle("Demande de confirmation")
                            .setMessage("Veux-tu tu lancer une nouvelle fois le sort "+spell.getName()+" ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    checkbox.setChecked(true);
                                }})
                            .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    checkbox.setChecked(false);
                                    removeSpellFromSelection();
                                }}).show();
                } else {
                    prepareSpell();
                }
            }
        });
    }

    private void prepareSpell() {
        if(spell.isMyth()){
            if(pj.getCurrentResourceValue("resource_mythic_points")>0) {
                new AlertDialog.Builder(mC)
                        .setTitle("Demande de confirmation")
                        .setMessage("Point(s) mythique(s) avant lancement des sorts : " + pj.getCurrentResourceValue("resource_mythic_points") + "\n" +
                                "\nVeux tu préparer la version mythique du sort " + spell.getName() + "\n(cela coûtera 1 pt) ?")
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                checkbox.setChecked(true);
                                addSpellToList();
                            }})
                        .setNegativeButton("non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                checkbox.setChecked(false);
                            }
                        }).show();
            } else {
                Toast toast = Toast.makeText(mC, "Tu n'as plus de point mythique ...", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
                checkbox.setChecked(false);
            }
        }else {
            addSpellToList();
        }
    }

    private void addSpellToList() {
        nCast++;
        refreshName();
        if(mAddListener!=null){
            mAddListener.onEvent();
        }
    }

    private void refreshName() {
        if(nCast>0){
            spellName.setText(spell.getName()+" ("+nCast+")");
        } else {
            spellName.setText(spell.getName());
        }
    }

    private void removeSpellFromSelection() {
        nCast--;
        refreshName();
        if(mRemoveListener!=null){
            mRemoveListener.onEvent();
        }
    }

    public void setSpellLineColor() {
        if (spell.getDmg_type().equals("none")) {
            spellLine.setBackground(mC.getDrawable(R.drawable.background_spell_line_noelem));
        } else if (spell.getDmg_type().equals("fire")) {
            spellLine.setBackground(mC.getDrawable(R.drawable.background_spell_line_fire));
        } else if (spell.getDmg_type().equals("shock")) {
            spellLine.setBackground(mC.getDrawable(R.drawable.background_spell_line_shock));
        } else if (spell.getDmg_type().equals("frost")) {
            spellLine.setBackground(mC.getDrawable(R.drawable.background_spell_line_frost));
        } else if (spell.getDmg_type().equals("acid")) {
            spellLine.setBackground(mC.getDrawable(R.drawable.background_spell_line_acid));
        } else if (spell.getDmg_type().equals("heal"))  {
            spellLine.setBackground(mC.getDrawable(R.drawable.background_spell_line_heal));
        }
    }

    public void setCheckBoxColor() {
        checkbox.setTextColor(Color.BLACK);
        int[] colorClickBox=new int[]{Color.BLACK,Color.BLACK};

        ColorStateList colorStateList = new ColorStateList(
                new int[][] {
                        new int[] { -android.R.attr.state_checked }, // unchecked
                        new int[] {  android.R.attr.state_checked }  // checked
                },colorClickBox

        );
        checkbox.setButtonTintList(colorStateList);
    }


    public interface OnAddSpellEventListener {
        void onEvent();
    }

    public void setAddSpellEventListener(OnAddSpellEventListener eventListener) {
        mAddListener = eventListener;
    }

    public interface OnRemoveSpellEventListener {
        void onEvent();
    }

    public void setRemoveSpellEventListener(OnRemoveSpellEventListener eventListener) {
        mRemoveListener = eventListener;
    }
}
