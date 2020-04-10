package stellarnear.wedge_companion.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stellarnear.wedge_companion.AllGeneralHelpInfos;
import stellarnear.wedge_companion.GeneralHelpInfo;
import stellarnear.wedge_companion.Perso.Ability;
import stellarnear.wedge_companion.Perso.Capacity;
import stellarnear.wedge_companion.Perso.Feat;
import stellarnear.wedge_companion.Perso.MythicCapacity;
import stellarnear.wedge_companion.Perso.MythicFeat;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Perso.Skill;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Spells.BuildPreparedSpellList;
import stellarnear.wedge_companion.Spells.BuildSpontaneousSpellList;
import stellarnear.wedge_companion.Spells.Spell;
import stellarnear.wedge_companion.Spells.SpellList;


/**
 * Created by jchatron on 26/12/2017.
 */

public class HelpActivity extends AppCompatActivity {
    private Perso pj = PersoManager.getCurrentPJ();
    private Context mC;
    private Map<Button, String> mapButtonCat = new HashMap<>();
    private ViewFlipper flipper;
    private View titleLayout;
    private View menuCategories;
    private TextView titleCatText;
    private ImageView infoImg;
    private GestureDetector mGestureDetector;
    private Activity mA;
    private Map<String, SpellList> mapsElemSPellList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (settings.getBoolean("switch_fullscreen_mode", getApplicationContext().getResources().getBoolean(R.bool.switch_fullscreen_mode_DEF))) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);
        this.mA = this;
        this.mC = getApplicationContext();
        setContentView(R.layout.help_activity);
        flipper = findViewById(R.id.help_activity_flipper);
        infoImg = new ImageView(mC);
        infoImg.setImageDrawable(getDrawable(R.drawable.ic_info_outline_24dp));
        infoImg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        flipper.addView(infoImg);
        titleCatText = findViewById(R.id.help_activity_title_category_text);
        menuCategories = findViewById(R.id.help_activity_button_linear);
        titleLayout = findViewById(R.id.help_activity_title_category_relat);
        ImageButton backToCatMenu = findViewById(R.id.help_activity_button_category_exit);
        backToCatMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipper.removeAllViews();
                flipper.addView(infoImg);
                switchMenu(titleLayout, menuCategories);
            }
        });
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);

        Toolbar mActionBarToolbarhelp = findViewById(R.id.toolbarHelp);
        setSupportActionBar(mActionBarToolbarhelp);

        buildCategories();
    }

    private void buildCategories() {
        List<String> categories;
        if (pj.getID().equalsIgnoreCase("") || pj.getID().equalsIgnoreCase("halda")) {
            categories = new ArrayList<>(Arrays.asList("Général", "Caractéristiques", "Compétences", "Dons", "Capacités", "Dons Mythiques", "Capacités Mythiques"));
            SpellList spellsToDisplay = new SpellList();
            if (pj.getID().equalsIgnoreCase("")) {
                spellsToDisplay.add(BuildPreparedSpellList.getInstance(mC).getAllSpells());
            } else {
                spellsToDisplay.add(BuildSpontaneousSpellList.getInstance(mC).getSpellList());
            }
            for (Spell spell : spellsToDisplay.asList()) {
                if (mapsElemSPellList.get(spell.getDmg_type()) == null) {
                    String nameCat = "";
                    switch (spell.getDmg_type()) {
                        case "":
                            nameCat = "utilitaires";
                            break;
                        case "heal":
                            nameCat = "de soin";
                            break;
                        case "none":
                            nameCat = "de dégât";
                            break;
                        case "fire":
                            nameCat = "de feu";
                            break;
                        case "shock":
                            nameCat = "de foudre";
                            break;
                        case "frost":
                            nameCat = "de froid";
                            break;
                        case "acid":
                            nameCat = "d'acide";
                            break;
                    }
                    categories.add("Sorts " + nameCat);
                    mapsElemSPellList.put(spell.getDmg_type(), new SpellList());
                    mapsElemSPellList.get(spell.getDmg_type()).add(spell);
                } else {
                    mapsElemSPellList.get(spell.getDmg_type()).add(spell);
                }
            }
        } else {
            categories = new ArrayList<>(Arrays.asList("Général", "Caractéristiques", "Compétences", "Dons", "Capacités"));
        }

        LinearLayout buttons = findViewById(R.id.help_activity_button_linear);
        buttons.removeAllViews();

        boolean firstButton = true;
        for (String cat : categories) {
            Button button = new Button(mC);
            LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.MATCH_PARENT);
            int pixelMarging = mC.getResources().getDimensionPixelSize(R.dimen.general_margin);
            if (firstButton) {
                para.setMargins(pixelMarging, pixelMarging, pixelMarging, pixelMarging);
                firstButton = false;
            } else {
                para.setMargins(0, pixelMarging, pixelMarging, pixelMarging);
            }
            button.setLayoutParams(para);
            button.setText(cat);
            button.setTextSize(13);
            button.setAllCaps(false);
            button.setBackground(getDrawable(R.drawable.button_basic_gradient));
            setButtonListner(button);
            mapButtonCat.put(button, cat);
            buttons.addView(button);
        }
    }

    private void setButtonListner(final Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setBackground(getDrawable(R.drawable.button_ok_gradient));
                unselectOthers(button);
                fillFlipper(button);
                titleCatText.setText(mapButtonCat.get(button));
                switchMenu(menuCategories, titleLayout);
            }
        });
    }

    private void switchMenu(final View out, final View in) {
        final Animation inFromTop = AnimationUtils.loadAnimation(mC, R.anim.infromtop);
        final Animation outToTop = AnimationUtils.loadAnimation(mC, R.anim.outtotop);
        outToTop.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                out.setVisibility(View.GONE);
                in.setVisibility(View.VISIBLE);
                in.startAnimation(inFromTop);
            }
        });
        out.startAnimation(outToTop);
    }

    private void unselectOthers(Button button) {
        for (Button buttonToUnselect : mapButtonCat.keySet()) {
            if (!buttonToUnselect.equals(button)) {
                buttonToUnselect.setBackground(getDrawable(R.drawable.button_basic_gradient));
            }
        }
    }

    private void fillFlipper(Button button) {
        flipper.removeAllViews();
        ViewGroup vg = findViewById(R.id.help_info_RelativeLayout);
        if (mapButtonCat.get(button).equalsIgnoreCase("Général")) {
            List<GeneralHelpInfo> listHelp = new AllGeneralHelpInfos(mC).getListGeneralHelpInfos();
            for (GeneralHelpInfo help : listHelp) {
                View view = getLayoutInflater().inflate(R.layout.custom_help_info_flipper, vg, false);
                changeFields(view, help.getId(), help.getName(), "", help.getDescr());
                flipper.addView(view);
            }
        } else if (mapButtonCat.get(button).equalsIgnoreCase("Caractéristiques")) {
            for (Ability abi : pj.getAllAbilities().getAbilitiesList()) {
                View view = getLayoutInflater().inflate(R.layout.custom_help_info_flipper, vg, false);
                changeFields(view, abi.getId(), abi.getName(), "Type : " + abi.getType(), abi.getDescr());
                flipper.addView(view);
            }
        } else if (mapButtonCat.get(button).equalsIgnoreCase("Compétences")) {
            for (Skill skill : pj.getAllSkills().getSkillsList()) {
                View view = getLayoutInflater().inflate(R.layout.custom_help_info_flipper, vg, false);
                changeFields(view, skill.getId(), skill.getName(), "", skill.getDescr());
                flipper.addView(view);
            }
        } else if (mapButtonCat.get(button).equalsIgnoreCase("Dons")) {
            for (Feat feat : pj.getAllFeats().getFeatsList()) {
                View view = getLayoutInflater().inflate(R.layout.custom_help_info_flipper, vg, false);
                String type = feat.getType().equalsIgnoreCase("feat_magic") ? "Magie" : feat.getType().equalsIgnoreCase("feat_def") ? "Défensif" : "Autre";
                changeFields(view, feat.getId(), feat.getName(), "Type : " + type, feat.getDescr());
                flipper.addView(view);
            }
        } else if (mapButtonCat.get(button).equalsIgnoreCase("Capacités")) {
            for (Capacity capa : pj.getAllCapacities().getAllCapacitiesList()) {
                View view = getLayoutInflater().inflate(R.layout.custom_help_info_flipper, vg, false);
                changeFields(view, capa.getId(), capa.getName(), "", capa.getDescr());
                flipper.addView(view);
            }
        } else if (mapButtonCat.get(button).equalsIgnoreCase("Dons Mythiques")) {
            for (MythicFeat mythicFeat : pj.getAllMythicFeats().getMythicFeatsList()) {
                View view = getLayoutInflater().inflate(R.layout.custom_help_info_flipper, vg, false);
                changeFields(view, mythicFeat.getId(), mythicFeat.getName(), "", mythicFeat.getDescr());
                flipper.addView(view);
            }
        } else if (mapButtonCat.get(button).equalsIgnoreCase("Capacités Mythiques")) {
            for (MythicCapacity mythicCapacity : pj.getAllMythicCapacities().getAllMythicCapacitiesList()) {
                View view = getLayoutInflater().inflate(R.layout.custom_help_info_flipper, vg, false);
                changeFields(view, mythicCapacity.getId(), mythicCapacity.getName(), "Catégorie : " + mythicCapacity.getType(), mythicCapacity.getDescr());
                flipper.addView(view);
            }
        } else if (mapButtonCat.get(button).contains("Sorts")) {
            SpellList spellsToDisplay = new SpellList();
            switch (mapButtonCat.get(button)) {
                case "Sorts utilitaires":
                    spellsToDisplay = mapsElemSPellList.get("");
                    break;
                case "Sorts de soin":
                    spellsToDisplay = mapsElemSPellList.get("heal");
                    break;
                case "Sorts de dégât":
                    spellsToDisplay = mapsElemSPellList.get("none");
                    break;
                case "Sorts de feu":
                    spellsToDisplay = mapsElemSPellList.get("fire");
                    break;
                case "Sorts de foudre":
                    spellsToDisplay = mapsElemSPellList.get("shock");
                    break;
                case "Sorts de froid":
                    spellsToDisplay = mapsElemSPellList.get("frost");
                    break;
                case "Sorts d'acide":
                    spellsToDisplay = mapsElemSPellList.get("acid");
                    break;
            }
            for (Spell spell : spellsToDisplay.asList()) {
                View view = getLayoutInflater().inflate(R.layout.custom_help_info_flipper, vg, false);
                String dmgTxt = "Dégat : " + spell.getDmg_type();
                if (spell.getDmg_type().equalsIgnoreCase("")) {
                    dmgTxt = "Utilitaire";
                }
                changeFields(view, spell.getID(), spell.getName(), dmgTxt, spell.getDescr());
                flipper.addView(view);
            }
        }
    }

    private void changeFields(View view, String id, String titleTxt, String typeTxt, String descrTxt) {
        ImageView img = view.findViewById(R.id.help_info_image);
        int imgId = getResources().getIdentifier(id, "drawable", getPackageName());
        try {
            img.setImageDrawable(mC.getDrawable(imgId));
        } catch (Exception e) {
            img.setVisibility(View.GONE);
            e.printStackTrace();
        }
        TextView title = view.findViewById(R.id.help_info_textName);
        title.setText(titleTxt);
        if (!typeTxt.equalsIgnoreCase("")) {
            TextView type = view.findViewById(R.id.help_info_textType);
            type.setVisibility(View.VISIBLE);
            type.setText(typeTxt);
        }
        TextView descr = view.findViewById(R.id.help_info_textDescr);
        descr.setText(descrTxt);
    }

    private void flipNext() {
        flipper.clearAnimation();
        Animation in = AnimationUtils.loadAnimation(mC, R.anim.infromright);
        Animation out = AnimationUtils.loadAnimation(mC, R.anim.outtoleft);
        flipper.setInAnimation(in);
        flipper.setOutAnimation(out);
        flipper.showNext();
    }

    private void flipPrevious() {
        flipper.clearAnimation();
        Animation in = AnimationUtils.loadAnimation(mC, R.anim.infromleft);
        Animation out = AnimationUtils.loadAnimation(mC, R.anim.outtoright);
        flipper.setInAnimation(in);
        flipper.setOutAnimation(out);
        flipper.showPrevious();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOrientStart(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
    }

    @Override
    protected void onDestroy() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        finish();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        final Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        if (display.getRotation() == Surface.ROTATION_0) {
            Intent intent = new Intent(mA, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
        if (display.getRotation() == Surface.ROTATION_90) {
            Intent intent = new Intent(mA, PetActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
    }

    private void checkOrientStart(int screenOrientation) {
        if (getRequestedOrientation() != screenOrientation) {
            setRequestedOrientation(screenOrientation);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }
            }, 1000);
        }
    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() > e2.getX()) { // Swipe left (next)
                flipNext();
            }
            if (e1.getX() < e2.getX()) {// Swipe right (previous)
                flipPrevious();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
