package stellarnear.wedge_companion.SettingsFraments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import stellarnear.wedge_companion.Perso.Ability;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Perso.Skill;
import stellarnear.wedge_companion.Tools;

public class PrefAllInventoryFragmentAddSelection {

    private Activity mA;
    private Context mC;
    private Tools tools = Tools.getTools();
    private Map<String, Integer> mapAbiUp = new HashMap<>();
    private Map<String, Integer> mapSkillUp = new HashMap<>();
    private Perso pj = PersoManager.getCurrentPJ();
    private LinearLayout listAbi;
    private LinearLayout listSkill;

    public PrefAllInventoryFragmentAddSelection(Activity mA, Context mC, LinearLayout addAbi, LinearLayout addSkill, LinearLayout listAbi, LinearLayout listSkill) {
        this.mA = mA;
        this.mC = mC;
        this.listAbi = listAbi;
        this.listSkill = listSkill;

        setAddAbiButton(addAbi);
        setAddSkillButton(addSkill);
        listAbi.removeAllViews();
        listSkill.removeAllViews();
        mapAbiUp = new HashMap<>();
        mapSkillUp = new HashMap<>();
    }

    /*  Partie abi */
    private void setAddAbiButton(final LinearLayout mainAddLinear) {
        mainAddLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupSelectAbi();
            }
        });
    }

    private void popupSelectAbi() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mA);
        builder.setTitle("Choix de la statistique");
        // add a radio button list

        final ArrayList<String> abilityName = new ArrayList<>();

        for (Ability abi : pj.getAllAbilities().getAbilitiesList()) {
            abilityName.add(abi.getName());
        }

        int checkedItem = -1;
        String[] abiArray = abilityName.toArray(new String[abilityName.size()]);
        builder.setSingleChoiceItems(abiArray, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                popupAbiSelectValue(which);
            }


        });

        builder.setPositiveButton("Ok", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void popupAbiSelectValue(final int which) {
        final Ability selectedAbi = pj.getAllAbilities().getAbilitiesList().get(which);
        final EditText input = new EditText(mC);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(mA)
                .setView(input)
                .setTitle("Valeur d'augmentation " + selectedAbi.getName())
                .setIcon(android.R.drawable.ic_menu_help)
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        addFinalValues(selectedAbi, tools.toInt(input.getText().toString()));
                    }
                })
                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
    }

    /*  Partie skill */
    private void setAddSkillButton(final LinearLayout mainAddLinear) {
        mainAddLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupSelectSkill();
            }
        });
    }

    private void popupSelectSkill() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mA);
        builder.setTitle("Choix de la compétence");
        // add a radio button list

        final ArrayList<String> abilityName = new ArrayList<>();

        for (Skill skill : pj.getAllSkills().getSkillsList()) {
            abilityName.add(skill.getName());
        }

        int checkedItem = -1;
        String[] abiArray = abilityName.toArray(new String[abilityName.size()]);
        builder.setSingleChoiceItems(abiArray, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                popupSkillSelectValue(which);
            }


        });

        builder.setPositiveButton("Ok", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void popupSkillSelectValue(final int which) {
        final Skill selectedSkill = pj.getAllSkills().getSkillsList().get(which);
        final EditText input = new EditText(mC);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(mA)
                .setView(input)
                .setTitle("Valeur d'augmentation " + selectedSkill.getName())
                .setIcon(android.R.drawable.ic_menu_help)
                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        addFinalValues(selectedSkill, tools.toInt(input.getText().toString()));
                    }
                })
                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }).show();
    }

    /* autre */

    private void addFinalValues(Ability ability, Integer val) {
        LinearLayout abiLine = new LinearLayout(mC);
        abiLine.setOrientation(LinearLayout.HORIZONTAL);
        abiLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        abiLine.setGravity(Gravity.CENTER);
        TextView valueAugment = new TextView(mC);
        valueAugment.setText("+" + val + " " + ability.getName());
        abiLine.addView(valueAugment);
        listAbi.addView(abiLine);
        mapAbiUp.put(ability.getId(), val);
    }

    private void addFinalValues(Skill skill, Integer val) {
        LinearLayout abiLine = new LinearLayout(mC);
        abiLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        abiLine.setOrientation(LinearLayout.HORIZONTAL);
        abiLine.setGravity(Gravity.CENTER);
        TextView valueAugment = new TextView(mC);
        valueAugment.setText("+" + val + " " + skill.getName());
        abiLine.addView(valueAugment);
        listSkill.addView(abiLine);
        mapSkillUp.put(skill.getId(), val);
    }


    public Map<String, Integer> getAbiMap() {
        return mapAbiUp;
    }

    public Map<String, Integer> getSkillMap() {
        return mapSkillUp;
    }
}
