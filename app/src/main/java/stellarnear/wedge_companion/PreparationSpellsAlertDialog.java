package stellarnear.wedge_companion;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

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
    private View mainView;
    private Context mC;
    private boolean positiveButton = false;
    private OnAcceptEventListener mListener;

    private BuildPreparedSpellList buildPreparedSpellList;
    private String mode;

    private SpellList preparedSpells;
    private SpellList oldSaveSpells;
    private SpellList allSpells;
    private Perso wedge = PersoManager.getPJ("Wedge");
    private Tools tools = Tools.getTools();

    private SpellsRanksManager manager;
    private Map<Integer, Integer> mapSpellToPickForRank = new HashMap<>();
    private Map<Integer, TextView> mapTiersTitles = new HashMap<>();
    private Map<Integer, TextView> mapSideTiers = new HashMap<>();
    private Map<String, LinearLineSpell> mapSpellIDLinearLine = new HashMap<>();

    public PreparationSpellsAlertDialog(final Context mC, final BuildPreparedSpellList buildPreparedSpellList, String mode) {
        // Set the toast and duration
        LayoutInflater inflater = LayoutInflater.from(mC);
        mainView = inflater.inflate(R.layout.custom_pick_prepared_spell, null);
        this.mC = mC;
        this.buildPreparedSpellList = buildPreparedSpellList;
        this.mode = mode;
        if (mode.equalsIgnoreCase("halfsleep")) {
            this.oldSaveSpells = buildPreparedSpellList.getPreparedSpellList();
        } else if (mode.equalsIgnoreCase("sleep")) {
            this.oldSaveSpells = buildPreparedSpellList.getOldFullSleepPreparedSpellList();
        }
        this.preparedSpells = new SpellList();
        this.allSpells = buildPreparedSpellList.getAllSpells();

        dialogBuilder = new AlertDialog.Builder(mC, R.style.CustomDialog);
        dialogBuilder.setView(mainView);
        dialogBuilder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) { /*on le refais apres le show pour overload*/ }
        });

        manager = wedge.getAllResources().getRankManager();
        initPickRemaningList();
        addSpellsAndBoxesToLin((LinearLayout) mainView.findViewById(R.id.custom_mainlin));

        loadPreviousSpells();

        positiveButton = true;
        alert = dialogBuilder.create();
    }

    private void loadPreviousSpells() {
        for (Spell spell : oldSaveSpells.asList()) {
            try {
                mapSpellIDLinearLine.get(spell.getID()).forceAddOneCast();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean setupFinished() {
        boolean val = true;
        for (int i = 1; i <= manager.getHighestTier(); i++) {
            if (mapSpellToPickForRank.get(i) > 0) {
                val = false;
                break;
            }
        }
        return val;
    }

    private void initPickRemaningList() {
        mapSpellToPickForRank = new HashMap<>();
        for (int i = 1; i <= manager.getHighestTier(); i++) {
            mapSpellToPickForRank.put(i, wedge.getCurrentResourceValue("spell_rank_" + i));
        }
    }

    private void addSpellsAndBoxesToLin(LinearLayout linear) {
        final ScrollView scroll_tier = mainView.findViewById(R.id.main_scroll_relat);
        LinearLayout side = mainView.findViewById(R.id.side_bar);
        for (int i = 1; i <= manager.getHighestTier(); i++) {

            final TextView tier = new TextView(mC);
            LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int pixelMarging = mC.getResources().getDimensionPixelSize(R.dimen.general_margin);
            para.setMargins(pixelMarging, pixelMarging, pixelMarging, pixelMarging);
            tier.setLayoutParams(para);
            tier.setBackground(mC.getDrawable(R.drawable.background_tier_title));

            String tier_txt = "Tier " + i;
            String titre_tier = tier_txt + " [" + mapSpellToPickForRank.get(i) + " restant(s)]";
            SpannableString titre = new SpannableString(titre_tier);
            titre.setSpan(new RelativeSizeSpan(0.65f), tier_txt.length(), titre_tier.length(), 0);
            tier.setText(titre);

            tier.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tier.setTextColor(Color.BLACK);
            tier.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            mapTiersTitles.put(i, tier);
            linear.addView(tier);

            // side bar

            final TextView side_txt = new TextView(mC);
            side_txt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1));
            side_txt.setGravity(Gravity.CENTER);
            side_txt.setTextColor(Color.DKGRAY);
            side_txt.setText("T" + i + "\n(" + mapSpellToPickForRank.get(i) + ")");
            side_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    scroll_tier.post(new Runnable() {
                        @Override
                        public void run() {
                            scroll_tier.scrollTo(0, tier.getTop());
                        }
                    });
                }
            });
            mapSideTiers.put(i, side_txt);
            side.addView(side_txt);

            for (final Spell spell : allSpells.filterByRank(i).filterDisplayable().asList()) {
                final LinearLineSpell spellLine = new LinearLineSpell(spell, mC);
                mapSpellIDLinearLine.put(spell.getID(), spellLine);
                spellLine.setAddSpellEventListener(new LinearLineSpell.OnAddSpellEventListener() {
                    @Override
                    public void onEvent() {
                        if (mapSpellToPickForRank.get(spell.getRank()) > 0) {
                            addSpellToList(spell);
                        } else {
                            spellLine.forceBoxUncheck();
                            tools.customToast(mC, "Tu as choisi tous les sorts de rang " + spell.getRank() + "\nEnlève en un pour ajouter celui ci!", "center");
                        }

                    }
                });
                spellLine.setRemoveSpellEventListener(new LinearLineSpell.OnRemoveSpellEventListener() {
                    @Override
                    public void onEvent() {
                        removeSpellFromSelection(spellLine);
                    }
                });
                linear.addView(spellLine.getSpellLine());
            }
        }
    }

    private void addSpellToList(Spell spellToAdd) {
        int rank = spellToAdd.getRank();
        preparedSpells.add(new Spell(spellToAdd));
        mapSpellToPickForRank.put(rank, mapSpellToPickForRank.get(rank) - 1);
        refreshPickNumbers();
    }

    private void refreshPickNumbers() {
        for (int i = 1; i <= manager.getHighestTier(); i++) {
            String tier_txt = "Tier " + i;
            String titre_tier = tier_txt + " [" + mapSpellToPickForRank.get(i) + " restant(s)]";
            SpannableString titre = new SpannableString(titre_tier);
            titre.setSpan(new RelativeSizeSpan(0.65f), tier_txt.length(), titre_tier.length(), 0);
            mapTiersTitles.get(i).setText(titre);

            String ratioPicked_All = (wedge.getCurrentResourceValue("spell_rank_" + i) - mapSpellToPickForRank.get(i)) + "/" + wedge.getCurrentResourceValue("spell_rank_" + i);
            mapSideTiers.get(i).setText("T" + i + "\n(" + ratioPicked_All + ")");
        }
    }

    private void removeSpellFromSelection(LinearLineSpell spellLineToRemove) {
        Spell spellToRemove = spellLineToRemove.getSpell();
        int rank = spellToRemove.getRank();
        SpellList removedSpellList = new SpellList(preparedSpells);
        for (Spell spell : preparedSpells.asList()) {
            if (spell.getID().equalsIgnoreCase(spellToRemove.getID())) {
                removedSpellList.remove(spell);
                mapSpellToPickForRank.put(rank, mapSpellToPickForRank.get(rank) + 1);
            }
        }
        this.preparedSpells = removedSpellList;
        refreshPickNumbers();
    }

    // retour aux methode de l'alert

    public void showAlert() {
        alert.show();
        if (positiveButton) {
            applyStyleToOkButton();
        }
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        alert.getWindow().setLayout(width, height);
        alert.getWindow().setGravity(Gravity.CENTER);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mC);
        if (settings.getBoolean("switch_fullscreen_mode", mC.getResources().getBoolean(R.bool.switch_fullscreen_mode_DEF))) {
            alert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        alert.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setupFinishedTestOnButton();
    }

    private void setupFinishedTestOnButton() {
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setupFinished()) {
                    buildPreparedSpellList.saveListFromPreparationAlert(preparedSpells, mode);
                    if (mListener != null) {
                        mListener.onEvent();
                    }
                    alert.dismiss();
                } else {
                    tools.customToast(mC, "Il te reste encore de sorts à préparer !", "center");
                }
            }
        });
    }

    private void applyStyleToOkButton() {
        Button button = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) button.getLayoutParams();
        positiveButtonLL.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        positiveButtonLL.setMargins(mC.getResources().getDimensionPixelSize(R.dimen.general_margin), 0, 0, 0);
        button.setLayoutParams(positiveButtonLL);
        button.setTextColor(mC.getColor(R.color.colorBackground));
        button.setBackground(mC.getDrawable(R.drawable.button_ok_gradient));
    }

    public void setAcceptEventListener(OnAcceptEventListener eventListener) {
        mListener = eventListener;
    }

    public interface OnAcceptEventListener {
        void onEvent();
    }
}
