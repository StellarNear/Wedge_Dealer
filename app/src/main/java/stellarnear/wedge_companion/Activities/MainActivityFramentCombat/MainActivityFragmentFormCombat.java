package stellarnear.wedge_companion.Activities.MainActivityFramentCombat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import stellarnear.wedge_companion.Activities.MainActivityFragment;
import stellarnear.wedge_companion.DisplayRolls;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.FormRollFactory;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;
import stellarnear.wedge_companion.TextFilling.Damages;
import stellarnear.wedge_companion.TextFilling.PostRandValues;
import stellarnear.wedge_companion.TextFilling.PreRandValues;
import stellarnear.wedge_companion.TextFilling.RangesAndProba;
import stellarnear.wedge_companion.TextFilling.SetupCheckboxes;
import stellarnear.wedge_companion.Tools;

public class MainActivityFragmentFormCombat extends Fragment {
    public Perso pj = PersoManager.getCurrentPJ();
    private View mainPage;
    private Drawable ori_background;
    private FloatingActionButton fabAtk;
    private FloatingActionButton fabDmg;
    private FloatingActionButton fabDmgDet;
    private ImageButton simpleAtk;
    private ImageButton barrageShot;

    private RollList rollList;
    private RollList selectedRolls;
    private boolean firstDmgRoll = true;
    private boolean firstAtkRoll = true;

    private String mode;

    private PreRandValues preRandValues;
    private PostRandValues postRandValues;
    private SetupCheckboxes setupCheckboxes;
    private Damages damages;
    private RangesAndProba rangesAndProba;
    private DisplayRolls displayRolls;

    private Tools tools = Tools.getTools();


    public MainActivityFragmentFormCombat() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }
        mainPage = inflater.inflate(R.layout.fragment_main_attack, container, false);
        ImageButton buttonMain = mainPage.findViewById(R.id.button_frag_combat_to_main);
        animate(buttonMain);
        View.OnClickListener listnerBackToMain = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MainActivityFragment();
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.infadefrag, R.animator.outtobotfrag);
                fragmentTransaction.replace(R.id.fragment_main_frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        };
        buttonMain.setOnClickListener(listnerBackToMain);

        setupScreen();

        if (pj.getAllForms().hasActiveForm()) {
            try {
                int imgId = getResources().getIdentifier(pj.getAllForms().getCurrentForm().getId(), "drawable", getContext().getPackageName());
                ((ImageView) mainPage.findViewById(R.id.background_blank_combat)).setImageDrawable(getResources().getDrawable(imgId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mainPage;
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

    public void setupScreen() {
        mode = "fullround";
        firstDmgRoll = true;
        firstAtkRoll = true;
        mainPage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                mainPage.setBackground(ori_background);
                hideButtons(3);
                startPreRand();
                mainPage.setOnTouchListener(null);
                return true;//always return true to consume event
            }
        });

        // boutons d'attaques
        simpleAtk = mainPage.findViewById(R.id.button_simple_atk);
        simpleAtk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = "simple";
                tools.customToast(getContext(), "Lancement de l'attaque primaire uniquement !", "center");
                hideButtons(1);
                startPreRand();
            }
        });
        barrageShot = mainPage.findViewById(R.id.button_barrage_shot);
        barrageShot.setVisibility(View.GONE);

        fabAtk = mainPage.findViewById(R.id.fabAtk);
        setListenerFabAtk();
        fabDmg = mainPage.findViewById(R.id.fab_damage);
        setListenerFabDmg();
        fabDmgDet = mainPage.findViewById(R.id.fab_damage_detail);
        setListenerFabDmgDet();
    }

    private void setListenerFabAtk() {
        fabAtk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode.equalsIgnoreCase("")) {
                    mode = "fullround";
                }
                if (firstAtkRoll) {
                    hideButtons(0);
                    startRandAtk();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Demande de confirmation")
                            .setMessage("Voulez vous relancer les jets d'attaque ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    rollList = new FormRollFactory(getActivity(), getContext(), mode).getRollList();
                                    preRandValues = new PreRandValues(getContext(), mainPage, rollList);
                                    hideButtons(0);
                                    startRandAtk();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });
    }

    private void clearStep(Integer step) {
        switch (step) {
            case 0:
                if (mainPage.getBackground() != null) {
                    mainPage.setBackground(null);
                }
                mainPage.setOnTouchListener(null);
                if (preRandValues != null) {
                    preRandValues.hideViews();
                }
            case 1:
                if (postRandValues != null) {
                    postRandValues.hideViews();
                }
                if (setupCheckboxes != null) {
                    setupCheckboxes.hideViews();
                    displayRolls = null;
                }
            case 2:
                if (damages != null) {
                    damages.hideViews();
                }
            case 3:
                if (rangesAndProba != null) {
                    rangesAndProba.hideViews();
                }
            case 4:
                if (displayRolls == null || displayRolls.size() == 0) {
                    fabDmgDet.setEnabled(false);
                }
        }
    }

    private void startPreRand() {
        clearStep(0);
        ((ImageView) mainPage.findViewById(R.id.background_blank_combat)).setImageDrawable(null);
        rollList = new FormRollFactory(getActivity(), getContext(), mode).getRollList();
        preRandValues = new PreRandValues(getContext(), mainPage, rollList);

        // ajout du panneau additionel (lynx_eye etc)
        if (damages != null) {
            damages.hideViews();
        }
    }

    private void hideButtons(int buttonClicked) {
        simpleAtk.setOnClickListener(null);
        barrageShot.setOnClickListener(null);
        switch (buttonClicked) {
            case 0:
            case 3:
                simpleAtk.animate().translationXBy(-200).setDuration(1000).start();
                barrageShot.animate().translationXBy(200).setDuration(1000).start();
                break;
            case 1:
                simpleAtk.animate().scaleX(2).scaleY(2).alpha(0).setDuration(1000).start();
                barrageShot.animate().translationXBy(200).setDuration(1000).start();
                break;
            case 2:
                simpleAtk.animate().translationXBy(-200).setDuration(1000).start();
                barrageShot.animate().scaleX(2).scaleY(2).alpha(0).setDuration(1000).start();
                break;
        }
    }

    private void startRandAtk() {
        firstAtkRoll = false;
        firstDmgRoll = true;
        showDivider();
        fabAtk.animate().setDuration(1000).translationX(-400).start();       //decale le bouton à gauche pour l'apparition du suivant
        Snackbar.make(mainPage, "Lancement des dés en cours... ", Snackbar.LENGTH_SHORT).show();
        if (preRandValues == null) {
            startPreRand();
        }
        if (postRandValues != null) {
            postRandValues.hideViews();
        }
        if (setupCheckboxes != null) {
            setupCheckboxes.hideViews();
        }
        postRandValues = new PostRandValues(getContext(), mainPage, rollList);
        setupCheckboxes = new SetupCheckboxes(getContext(), mainPage, rollList);
    }

    private void showDivider() {
        mainPage.findViewById(R.id.bar_sep).setVisibility(View.VISIBLE);
    }

    private void setListenerFabDmg() {
        fabDmg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabDmg.animate().setDuration(1000).translationX(+400).start();
                Snackbar.make(view, "Calcul des dégâts en cours... ", Snackbar.LENGTH_SHORT).show();
                AlphaAnimation anim = new AlphaAnimation(0, 1);
                anim.setDuration(2000);
                checkSelectedRolls();
                if (firstDmgRoll) {
                    startDamage();
                    firstDmgRoll = false;
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Demande de confirmation")
                            .setMessage("Voulez vous relancer des jets de dégâts pour l'attaque en cours ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    startDamage();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });
    }

    private void startDamage() {
        displayRolls = null;
        clearStep(2);
        damages = new Damages(getActivity(), getContext(), mainPage, selectedRolls);  //calcul et affiche les degats
        if (selectedRolls.getDmgDiceList().getList().size() > 0) {
            rangesAndProba = new RangesAndProba(getContext(), mainPage, selectedRolls);
            fabDmgDet.setVisibility(View.VISIBLE);
            fabDmgDet.setEnabled(true);
        }
        postRandValues.refreshPostRandValues();
        new PostData(getContext(), new PostDataElement(selectedRolls, "dmg"));
    }

    private void checkSelectedRolls() {
        selectedRolls = new RollList();
        for (Roll roll : rollList.getList()) {
            if (roll.isInvalid() || (!roll.isHitConfirmed() && !roll.isMissed())) {
                continue;
            }
            selectedRolls.add(roll);
        }
    }

    private void setListenerFabDmgDet() {
        fabDmgDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (displayRolls == null) {
                    displayRolls = new DisplayRolls(getActivity(), getContext(), selectedRolls);
                }
                displayRolls.showPopup();
            }
        });

    }
}
