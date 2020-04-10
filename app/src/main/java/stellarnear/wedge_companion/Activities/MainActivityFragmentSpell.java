package stellarnear.wedge_companion.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormatSymbols;
import java.util.Iterator;

import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Spells.EchoList;
import stellarnear.wedge_companion.Spells.GuardianList;
import stellarnear.wedge_companion.Spells.ListingElements.LinearLineSpell;
import stellarnear.wedge_companion.Spells.SelectedSpells;
import stellarnear.wedge_companion.Spells.Spell;
import stellarnear.wedge_companion.Spells.SpellList;
import stellarnear.wedge_companion.Tools;

public class MainActivityFragmentSpell extends Fragment {
    private SpellList selectedSpells = new SpellList();
    private Perso pj = PersoManager.getCurrentPJ();

    private View returnFragView;

    private SpellList listAllSpell = null;

    private Tools tools = Tools.getTools();

    private Drawable mystSymbol;
    private Drawable utilSymbol;
    private Drawable buffSymbol;
    private Drawable combatBuffSymbol;
    private Drawable healSymbol;

    private Drawable contactRange;
    private Drawable shortRange;
    private Drawable averageRange;
    private Drawable longRange;


    public MainActivityFragmentSpell() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }

        returnFragView = inflater.inflate(R.layout.fragment_main_cast, container, false);

        mystSymbol = tools.resize(getContext(), R.drawable.ic_myst_symbol, getContext().getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        utilSymbol = tools.resize(getContext(), R.drawable.ic_util_symbol, getContext().getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        buffSymbol = tools.resize(getContext(), R.drawable.ic_buff_symbol, getContext().getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        combatBuffSymbol = tools.resize(getContext(), R.drawable.ic_combat_buff_symbol, getContext().getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        healSymbol = tools.resize(getContext(), R.drawable.ic_heal_symbol, getContext().getResources().getDimensionPixelSize(R.dimen.logo_spell_type));

        contactRange = tools.resize(getContext(), R.drawable.ic_contact_range_symbol, getContext().getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        shortRange = tools.resize(getContext(), R.drawable.ic_short_range_symbol, getContext().getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        averageRange = tools.resize(getContext(), R.drawable.ic_average_range_symbol, getContext().getResources().getDimensionPixelSize(R.dimen.logo_spell_type));
        longRange = tools.resize(getContext(), R.drawable.ic_long_range_symbol, getContext().getResources().getDimensionPixelSize(R.dimen.logo_spell_type));

        buildPage1();

        ImageButton fab = returnFragView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!selectedSpells.isEmpty()) {
                    testSpellSelection();
                } else {
                    Toast toast = Toast.makeText(getContext(), "Sélectionnes au moins un sort ...", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        returnFragView.findViewById(R.id.back_main_from_spell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToMain();
            }
        });
        animate(((ImageButton) returnFragView.findViewById(R.id.back_main_from_spell)));
        return returnFragView;
    }

    private void backToMain() {
        Fragment fragment = new MainActivityFragment();
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.infadefrag, R.animator.outtorightfrag);
        fragmentTransaction.replace(R.id.fragment_main_frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void animate(final ImageButton buttonMain) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Animation anim = new ScaleAnimation(1f, 1.25f, 1f, 1.25f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setRepeatCount(1);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setDuration(666);

                buttonMain.startAnimation(anim);
            }
        }, getResources().getInteger(R.integer.translationFragDuration));


    }

    private void buildPage1() {
        if (!pj.getID().equalsIgnoreCase("")) {
            testEchosAndGuardians();
        }

        listAllSpell = pj.getAllSpells();

        int max_tier = pj.getAllResources().getRankManager().getHighestTier();
        final ScrollView scroll_tier = returnFragView.findViewById(R.id.main_scroll_relat);
        LinearLayout tiers = returnFragView.findViewById(R.id.linear1);
        SpellList spellListDisplayable = pj.getSpellsForCastList();
        if(pj.getID().equalsIgnoreCase("halda")
                && pj.getAllFeats().featIsActive("feat_interconnected_truth")
                && (spellListDisplayable.getNFreeMystSpells()<pj.getAbilityMod("ability_charisme"))){
            tools.customToast(getContext(),"Il manque "+(pj.getAbilityMod("ability_charisme")-spellListDisplayable.getNFreeMystSpells())+" sort(s) de mystère(s) gratuit(s) pour Halda !" +
                    "\nGrace au don Vérités interconnectées","center");
        }
        for (int i = 0; i <= max_tier; i++) {
            final TextView tier = new TextView(getContext());
            LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int pixelMarging = getContext().getResources().getDimensionPixelSize(R.dimen.general_margin);
            para.setMargins(pixelMarging, pixelMarging, pixelMarging, pixelMarging);
            tier.setLayoutParams(para);
            tier.setBackground(getContext().getDrawable(R.drawable.background_tier_title));

            String tier_txt = "Tier " + i;

            String titre_tier = tier_txt + " [" + pj.getCurrentResourceValue("spell_rank_" + i) + " restant(s)]";
            if (i == 0) {
                titre_tier = tier_txt + " [illimité]";
            }
            SpannableString titre = new SpannableString(titre_tier);
            titre.setSpan(new RelativeSizeSpan(0.65f), tier_txt.length(), titre_tier.length(), 0);
            tier.setText(titre);

            tier.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tier.setTextColor(Color.BLACK);
            tier.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tiers.addView(tier);

            // side bar
            LinearLayout side = returnFragView.findViewById(R.id.side_bar);
            final TextView side_txt = new TextView(getContext());
            side_txt.setGravity(Gravity.CENTER);
            side_txt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1));

            int orientation = getResources().getConfiguration().orientation;  //1 pour portrait et 2 paysage
            side_txt.setTextColor(Color.DKGRAY);
            if (orientation == 1) {
                side_txt.setText("T" + i + "\n(" + pj.getCurrentResourceValue("spell_rank_" + i) + ")");
                if (i == 0) {
                    side_txt.setText("T" + i + "\n(" + DecimalFormatSymbols.getInstance().getInfinity() + ")");
                }
            } else {
                side_txt.setText("T" + i + " (" + pj.getCurrentResourceValue("spell_rank_" + i) + ")");
                if (i == 0) {
                    side_txt.setText("T" + i + " (" + DecimalFormatSymbols.getInstance().getInfinity() + ")");
                }
            }

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
            final int rank = i;
            side_txt.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (pj.getCurrentResourceValue("resource_mythic_points") > 0) {

                        new AlertDialog.Builder(getContext())
                                .setTitle("Sort Inspiré")
                                .setMessage("Veux tu lancer utiliser un sort inspiré pour lancer un sort de rang " + rank + " ?")
                                .setIcon(android.R.drawable.ic_menu_help)
                                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        Snackbar.make(side_txt, "Sort inspiré de rang " + rank + " lancé.\n(+2 NLS sur ce sort)", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();

                                        pj.getAllResources().getResource("resource_mythic_points").spend(1);
                                        new PostData(getContext(), new PostDataElement("Lancement sort inspiré", "-1pt mythique"));
                                        Toast toast = Toast.makeText(getContext(), "Il te reste " + pj.getCurrentResourceValue("resource_mythic_points") + " point(s) mythique(s)", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                                        toast.show();
                                    }
                                })
                                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                }).show();
                    } else {
                        Toast toast = Toast.makeText(getContext(), "Tu n'as plus de point mythique ...", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    }
                    return true;
                }
            });

            side.addView(side_txt);

            SpellList spellToDisplayRankList = spellListDisplayable.filterByRank(i);

            if (spellToDisplayRankList.size() == 0) {
                continue;
            }

            for (final Spell spell : spellToDisplayRankList.asList()) {
                final LinearLineSpell spellLine = new LinearLineSpell(spell, getContext());
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
                if (spellLine.hasMythSpell()) {
                    spellLine.setAddMythicSpellEventListener(new LinearLineSpell.OnAddMythicSpellEventListener() {
                        @Override
                        public void onEvent() {
                            addSpellToList(spellLine.getSpellMyth());
                        }
                    });

                    spellLine.setRemoveMythicSpellEventListener(new LinearLineSpell.OnRemoveMythicSpellEventListener() {
                        @Override
                        public void onEvent() {
                            removeSpellFromSelection(spellLine.getSpellMyth());
                        }
                    });
                }
                tiers.addView(spellLine.getSpellLine());
            }
        }
    }

    private void testEchosAndGuardians() {
        if (EchoList.getInstance(getContext()).hasEcho() || GuardianList.getInstance(getContext()).hasGuardian()) {
            returnFragView.findViewById(R.id.special_spellslists_bar).setVisibility(View.VISIBLE);
            addEchosAndGuardians((LinearLayout) returnFragView.findViewById(R.id.special_spellslists_bar));
        } else {
            returnFragView.findViewById(R.id.special_spellslists_bar).setVisibility(View.GONE);
        }
    }

    private void addEchosAndGuardians(LinearLayout linear) {
        linear.removeAllViews();
        if (EchoList.getInstance(getContext()).hasEcho()) {
            TextView echo = new TextView(getContext());
            String title = EchoList.getInstance(getContext()).getEchoList().size() + " Écho";
            if (EchoList.getInstance(getContext()).getEchoList().size() > 1) {
                title += "s magiques";
            } else {
                title += " magique";
            }
            echo.setText(title);
            echo.setTextSize(18);
            echo.setTypeface(null, Typeface.BOLD);
            echo.setTextColor(getContext().getColor(R.color.colorPrimaryDarkhalda));
            echo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EchoList.getInstance(getContext()).popupList(getActivity(), getContext());
                }
            });
            EchoList.getInstance(getContext()).setRefreshEventListener(new EchoList.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    testEchosAndGuardians();
                }
            });
            linear.addView(echo);
        }
        if (GuardianList.getInstance(getContext()).hasGuardian()) {
            TextView guardian = new TextView(getContext());
            if (EchoList.getInstance(getContext()).hasEcho()) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(getResources().getDimensionPixelSize(R.dimen.general_margin), 0, 0, 0);
                guardian.setLayoutParams(params);
            }
            String title = GuardianList.getInstance(getContext()).getGuardianList().size() + " Sort";
            if (GuardianList.getInstance(getContext()).getGuardianList().size() > 1) {
                title += "s gardiens";
            } else {
                title += " gardien";
            }
            guardian.setText(title);
            guardian.setTextSize(18);
            guardian.setTypeface(null, Typeface.BOLD);
            guardian.setTextColor(getContext().getColor(R.color.colorPrimaryDarkhalda));
            guardian.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GuardianList.getInstance(getContext()).popupList(getActivity(), getContext());
                }
            });
            GuardianList.getInstance(getContext()).setRefreshEventListener(new GuardianList.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    testEchosAndGuardians();
                }
            });
            linear.addView(guardian);
        }
    }

    private void addSpellToList(Spell spell) {
        if (spell.getNSubSpell() > 0) {
            Spell parentSpellToBind = null;
            for (int i = 1; i <= spell.getNSubSpell(); i++) {
                SpellList subSpellN = listAllSpell.getSubSpellsByID(spell.getID() + "_sub");
                subSpellN.setSubName(i);
                if (subSpellN.asList().size() > 0 && parentSpellToBind == null) {
                    parentSpellToBind = subSpellN.asList().get(0);
                }
                if (parentSpellToBind != null) {
                    subSpellN.bindTo(parentSpellToBind);
                }
                selectedSpells.add(subSpellN);
            }
        } else {
            if (pj.getID().equalsIgnoreCase("")) {
                selectedSpells.add(spell); //pour wedge les sort sont enlevé directement
            } else {
                selectedSpells.add(new Spell(spell));  //pour halda on a x instance du meme sort qui sont possiible sans compter
            }
        }
        currentSelectionDisplay(getContext());
    }

    private void removeSpellFromSelection(Spell spell) {
        Iterator<Spell> s = selectedSpells.iterator();
        SpellList parentSpellRemovedList = new SpellList();
        while (s.hasNext()) {
            Spell spellList = s.next();
            boolean spellInList = spellList.getID().equalsIgnoreCase(spell.getID());
            boolean subSpellInList = spell.getNSubSpell() > 0 && spellList.getID().equalsIgnoreCase(spell.getID() + "_sub");

            if (spellInList || subSpellInList) {
                s.remove();
                if (spellInList) {
                    parentSpellRemovedList.add(spellList);
                } else if (subSpellInList && !parentSpellRemovedList.contains(spellList.getBindedParent())) {
                    parentSpellRemovedList.add(spellList.getBindedParent());
                }
            }
        }
        currentSelectionDisplay(getContext());
    }

    private void currentSelectionDisplay(Context mC) {
        if (selectedSpells.isEmpty()) {
            Toast toast = Toast.makeText(mC, "Aucun sort séléctionné", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            String display = "Sort séléctionnés :\n";
            for (Spell spell : selectedSpells.asList()) {
                display += spell.getName() + "\n";
            }
            display = display.substring(0, display.length() - 1);
            Toast toast = Toast.makeText(mC, display, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void testSpellSelection() {
        if (!selectedSpells.isEmpty()) {
            SelectedSpells.getInstance().setSelection(selectedSpells);
            buildPage2();
        } else {
            startActivity(new Intent(getContext(), MainActivityFragmentSpell.class));
        }
    }

    private void buildPage2() {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.infromrightfrag, R.animator.outfadefrag);
        fragmentTransaction.replace(R.id.fragment_main_frame_layout, new MainActivityFragmentSpellCast(), "frag_spell_cast");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}

