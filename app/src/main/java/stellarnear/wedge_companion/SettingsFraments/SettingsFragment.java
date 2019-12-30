package stellarnear.wedge_companion.SettingsFraments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stellarnear.wedge_companion.Activities.MainActivity;
import stellarnear.wedge_companion.Activities.PetActivity;
import stellarnear.wedge_companion.Activities.SaveSharedPreferencesActivity;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Spells.BuildMetaList;
import stellarnear.wedge_companion.Spells.BuildSpellList;
import stellarnear.wedge_companion.Tools;

public class SettingsFragment extends PreferenceFragment {
    private Activity mA;
    private Context mC;
    private List<String> histoPrefKeys = new ArrayList<>();
    private List<String> histoTitle = new ArrayList<>();

    private String currentPageKey;
    private String currentPageTitle;

    private Tools tools = new Tools();
    private SharedPreferences settings;
    private PrefAllInventoryFragment prefAllInventoryFragment;
    private PrefXpFragment prefXpFragment;
    private PrefInfoScreenFragment prefInfoScreenFragment;
    private PrefSkillFragment prefSkillFragment;
    private PrefSpellgemScreenFragment prefSpellgemScreenFragment;
    private PrefSpellRankFragment prefSpellRankFragment;
    private PrefFeatFragment prefFeatFragment;
    private PrefCapaFragment prefCapaFragment;
    private PrefMythicFeatFragment prefMythicFeatFragment;
    private PrefMythicCapaFragment prefMythicCapaFragment;
    private Perso pj = PersoManager.getCurrentPJ();

    private OnSharedPreferenceChangeListener listener =
            new OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                    if (key.contains("highest_tier_spell")) {
                        prefSpellRankFragment.refresh();
                    }
                    if (key.contains("spell_rank_")){
                        if(pj.getAllResources().getRankManager()!=null){pj.getAllResources().getRankManager().refreshMax();}
                    }
                    if (key.contains("switch_capacity_")){pj.getAllResources().refreshCapaListResources();}
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        settings.registerOnSharedPreferenceChangeListener(listener);
        this.mA=getActivity();
        this.mC=getContext();
        try {
            int xmlID = getResources().getIdentifier(PersoManager.getPJPrefix()+"pref", "xml", getContext().getPackageName());
            addPreferencesFromResource(xmlID);
            this.histoPrefKeys.add(PersoManager.getPJPrefix()+"pref");
        } catch (Exception e) {
            e.printStackTrace();
            addPreferencesFromResource(R.xml.pref);
            this.histoPrefKeys.add("pref");
        }

        findPreference(PersoManager.getPJPrefix()+"pref_stats").setSummary("Record actuel : "+settings.getString("highscore"+PersoManager.getPJSuffix(),"0"));

        this.histoTitle.add("Paramètres");
        this.prefAllInventoryFragment =new PrefAllInventoryFragment(mA,mC);
        this.prefAllInventoryFragment.setRefreshEventListener(new PrefAllInventoryFragment.OnRefreshEventListener() {
            @Override
            public void onEvent() {
                navigate();
            }
        });
        this.prefXpFragment = new PrefXpFragment(mA,mC);
        this.prefXpFragment.checkLevel(tools.toBigInt(settings.getString("current_xp", String.valueOf(getContext().getResources().getInteger(R.integer.current_xp_def)))));
        this.prefInfoScreenFragment=new PrefInfoScreenFragment(mA,mC);
        this.prefSpellgemScreenFragment=new PrefSpellgemScreenFragment(mA,mC);
        this.prefSpellRankFragment=new PrefSpellRankFragment(mA,mC);
        this.prefSkillFragment=new PrefSkillFragment(mA,mC);
        this.prefFeatFragment=new PrefFeatFragment(mA,mC);
        this.prefCapaFragment =new PrefCapaFragment(mA,mC);
        this.prefMythicFeatFragment =new PrefMythicFeatFragment(mA,mC);
        this.prefMythicCapaFragment =new PrefMythicCapaFragment(mA,mC);
    }




    // will be called by SettingsActivity (Host Activity)
    public void onUpButton() {
        if (histoPrefKeys.get(histoPrefKeys.size() - 1).equalsIgnoreCase(PersoManager.getPJPrefix()+"pref") || histoPrefKeys.size() <= 1) // in top-level
        {
            pj.refresh();
            Intent intent;
            if(mA.getIntent() != null && mA.getIntent().getExtras() != null && mA.getIntent().hasExtra("fromActivity")
                    && mA.getIntent().getStringExtra("fromActivity").equalsIgnoreCase("PetActivity")){
                intent = new Intent(mA, PetActivity.class);
            } else {
                intent = new Intent(mA, MainActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            mA.startActivity(intent);
        } else // in sub-level
        {
            currentPageKey=histoPrefKeys.get(histoPrefKeys.size() - 2);
            currentPageTitle=histoTitle.get(histoTitle.size() - 2);
            navigate();
            histoPrefKeys.remove(histoPrefKeys.size() - 1);
            histoTitle.remove(histoTitle.size() - 1);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().contains("pref_")) {
            histoPrefKeys.add(preference.getKey());
            histoTitle.add(preference.getTitle().toString());
            this.currentPageKey =preference.getKey();
            this.currentPageTitle =preference.getTitle().toString();
            navigate();
        } else {
            action(preference);
        }
        return true;
    }

    private void navigate() {
        if (currentPageKey.equalsIgnoreCase(PersoManager.getPJPrefix()+"pref")) {
            displayMainPage();
        } else if (currentPageKey.contains("pref_")) {
            loadPage();
            switch (currentPageKey) {
                case "pref_inventory_equipment":
                    PreferenceCategory otherList = (PreferenceCategory) findPreference("other_slot_equipment_list_category");
                    PreferenceCategory spareList = (PreferenceCategory) findPreference("spare_equipment_list_category");
                    prefAllInventoryFragment.addEditableEquipment(otherList,spareList);
                    break;
                case "pref_inventory_bag":
                    PreferenceCategory bagList = (PreferenceCategory) findPreference("bag_list_category");
                    prefAllInventoryFragment.addBagList(bagList);
                    break;
                case "pref_character_xp":
                    BigInteger xp = tools.toBigInt(settings.getString("current_xp", String.valueOf(getContext().getResources().getInteger(R.integer.current_xp_def))));
                    prefXpFragment.checkLevel(xp);
                    break;
                case "halda_pref_character_spells":
                case "pref_character_spells":
                    PreferenceCategory spellCat = (PreferenceCategory) findPreference("tier_spell");
                    prefSpellRankFragment.addSpellRanks(spellCat);
                    setHasOptionsMenu(true);
                    break;
                case "halda_pref_character_feat":
                case "pref_character_feat":
                    BuildMetaList.resetMetas();
                    BuildSpellList.resetSpellList();
                    PreferenceCategory magic = (PreferenceCategory) findPreference("Magie");
                    PreferenceCategory atk = (PreferenceCategory) findPreference("Attaque");
                    PreferenceCategory def = (PreferenceCategory) findPreference("Défense");
                    PreferenceCategory otherFeat = (PreferenceCategory) findPreference("Autre");
                    prefFeatFragment.addFeatsList(magic,atk,def,otherFeat);
                    setHasOptionsMenu(true);
                    break;
                case "pref_character_capa":
                    prefCapaFragment.addCapaList(getPreferenceScreen());
                    setHasOptionsMenu(true);
                    break;
                case "pref_mythic_feat":
                    PreferenceCategory magicMythFeat = (PreferenceCategory) findPreference("Magie");
                    PreferenceCategory atkMythFeat = (PreferenceCategory) findPreference("Attaque");
                    PreferenceCategory defMythFeat = (PreferenceCategory) findPreference("Défense");
                    PreferenceCategory otherMythFeat = (PreferenceCategory) findPreference("Autre");
                    prefMythicFeatFragment.addMythicFeatsList(magicMythFeat,atkMythFeat,defMythFeat,otherMythFeat);
                    setHasOptionsMenu(true);
                    break;
                case "pref_mythic_capa":
                    prefMythicCapaFragment.addMythicCapaList(getPreferenceScreen());
                    setHasOptionsMenu(true);
                    break;
                case "pref_character_skill":
                    PreferenceCategory rank = (PreferenceCategory) findPreference(getString(R.string.skill_mastery));
                    PreferenceCategory bonus = (PreferenceCategory) findPreference(getString(R.string.skill_bonus));
                    prefSkillFragment.addSkillsList(rank,bonus);
                    setHasOptionsMenu(true);
                    break;
            }
        }
    }

    private void displayMainPage() {
        getPreferenceScreen().removeAll();
        int xmlID = R.xml.pref;
        try {
            xmlID = getResources().getIdentifier(PersoManager.getPJPrefix()+"pref" , "xml", getContext().getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        addPreferencesFromResource(xmlID);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentPageTitle);
        findPreference(PersoManager.getPJPrefix()+"pref_stats").setSummary("Record actuel : "+settings.getString("highscore"+PersoManager.getPJSuffix(),"0"));
    }

    private void loadPage() {
        try {
            getPreferenceScreen().removeAll();
            int xmlID = getResources().getIdentifier(currentPageKey, "xml", getContext().getPackageName());
            addPreferencesFromResource(xmlID);
        } catch (Exception e) {
            e.printStackTrace();
            displayMainPage();
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentPageTitle);
    }

    private void action(Preference preference) {
        switch (preference.getKey()) {
            case "infos":
                prefInfoScreenFragment.showInfo();
                break;
            case "show_equipment":
                pj.getInventory().showEquipment(getActivity(), getContext(), true);
                break;
            case "add_gold":
                preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                        int idValGoldDef=  mC.getResources().getIdentifier("money_gold_def"+ PersoManager.getPJSuffix(), "integer", mC.getPackageName());
                        int gold = tools.toInt(settings.getString("money_gold"+PersoManager.getPJSuffix(), String.valueOf(getContext().getResources().getInteger(idValGoldDef))));
                        settings.edit().putString("money_gold"+PersoManager.getPJSuffix(), String.valueOf(gold + tools.toInt(o.toString()))).apply();
                        settings.edit().putString("add_gold", String.valueOf(0)).apply();
                        getPreferenceScreen().removeAll();
                        addPreferencesFromResource(R.xml.pref_inventory_money); //pour refresh le current
                        return true;
                    }
                });
                break;
            case "add_current_xp":
                AlertDialog.Builder alert = new AlertDialog.Builder(mC);
                final EditText edittext = new EditText(mA);
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                alert.setMessage("Nombre de points d'experiences à ajouter");
                alert.setTitle("Ajout d'experience");
                alert.setView(edittext);
                alert.setIcon(R.drawable.ic_upgrade);
                alert.setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                        BigInteger xp = tools.toBigInt(settings.getString("current_xp", String.valueOf(getContext().getResources().getInteger(R.integer.current_xp_def))));
                        BigInteger addXp = tools.toBigInt(edittext.getText().toString());
                        settings.edit().putString("current_xp", xp.add(addXp).toString()).apply();
                        prefXpFragment.checkLevel(xp, addXp);
                        navigate();
                    }
                });

                alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // rien
                    }
                });
                alert.show();
                edittext.post(new Runnable() {
                    public void run() {
                        edittext.setFocusableInTouchMode(true);
                        edittext.requestFocusFromTouch();
                        InputMethodManager lManager = (InputMethodManager) mA.getSystemService(Context.INPUT_METHOD_SERVICE);
                        lManager.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
                break;
            case "ability_lvl_druid":
                preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        prefXpFragment.setDruidLevel(tools.toInt(o.toString()));
                        navigate();
                        return true;
                    }
                });
                break;
            case "ability_lvl_archer":
                preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        prefXpFragment.setArcherLevel(tools.toInt(o.toString()));
                        navigate();
                        return true;
                    }
                });
                break;
            case "create_bag_item":
                prefAllInventoryFragment.createBagItem();
                break;
            case "create_equipment":
                prefAllInventoryFragment.createEquipment();
                break;
            case "reset_temp":
                pj.resetTemp();
                navigate();
                break;
            case "reset_temp_global":
                resetGlobal();
                navigate();
                break;
            case "spend_myth_point":
                if( pj.getCurrentResourceValue("resource_mythic_points")>0) {
                    new AlertDialog.Builder(mC)
                            .setTitle("Demande de confirmation")
                            .setMessage("Confirmes-tu la dépense d'un point mythique ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    pj.getAllResources().getResource("resource_mythic_points").spend(1);
                                    new PostData(mC,new PostDataElement("Utilisation d'un pouvoir mythique","Dépense d' un point mythique"));
                                    tools.customToast(mC,"Il te reste "+pj.getCurrentResourceValue("resource_mythic_points")+" point(s) mythique(s)","center");
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    tools.customToast(mC,"Tu n'as plus de point mythique","center");
                }
                break;
            case "spellgem":
                prefSpellgemScreenFragment.showSpellgem();
                break;
            case "appli_refresh":
                new AlertDialog.Builder(mC)
                        .setTitle("Demande de confirmation")
                        .setMessage("Confirmes-tu l'action de réinitialiser les attributs du personnage ?")
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                pj.reset();
                                tools.customToast(mC,"Rafraîchissement éffectué","center");
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case "appli_reset_stuff":
                new AlertDialog.Builder(mC)
                        .setTitle("Demande de confirmation")
                        .setMessage("Confirmes-tu l'action de réinitialiser l'équipement du personnage ?")
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                pj.getInventory().getAllEquipments().reset();
                                pj.getAllResources().reset();
                                tools.customToast(mC,"Rafraîchissement éffectué","center");
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case "appli_reset_bag":
                new AlertDialog.Builder(mC)
                        .setTitle("Demande de confirmation")
                        .setMessage("Confirmes-tu l'action de réinitialiser le sac du personnage ?")
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                pj.getInventory().getBag().reset();
                                tools.customToast(mC,"Rafraîchissement éffectué","center");
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case "appli_reset_stats":
                new AlertDialog.Builder(mC)
                        .setTitle("Demande de confirmation")
                        .setMessage("Confirmes-tu l'action de réinitialiser les statistiques du personnage ?")
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                pj.getStats().reset();
                                tools.customToast(mC,"Reset éffectué","center");
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case "appli_reset_hall":
                new AlertDialog.Builder(mC)
                        .setTitle("Demande de confirmation")
                        .setMessage("Confirmes-tu l'action de réinitialiser le Panthéon personnage ?")
                        .setIcon(android.R.drawable.ic_menu_help)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                pj.getHallOfFame().reset();
                                tools.customToast(mC,"Reset éffectué","center");
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case "export_save":
                Intent intentSave = new Intent(mC, SaveSharedPreferencesActivity.class);
                intentSave.putExtra("ACTION_TYPE","SAVE");
                intentSave.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mC.startActivity(intentSave);
                break;
            case "import_save":
                Intent intentLoad = new Intent(mC, SaveSharedPreferencesActivity.class);
                intentLoad.putExtra("ACTION_TYPE","LOAD");
                intentLoad.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mC.startActivity(intentLoad);
                break;
        }

    }

    private void resetGlobal() {
        List<String> allTempList = Arrays.asList("bonus_global_dmg_temp","bonus_global_atk_temp","bonus_global_temp_ca","bonus_global_temp_save","bonus_global_temp_rm");
        for (String temp : allTempList) {
            settings.edit().putString(temp, "0").apply();
        }
    }

    /*
    // Top level PreferenceScreen
    if (key.equals("top_key_0")) {         changePrefScreen(R.xml.pref_general, preference.getTitle().toString()); // descend into second level    }

    // Second level PreferenceScreens
    if (key.equals("second_level_key_0")) {        // do something...    }       */
    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mC);
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
        listener=null;
    }
}