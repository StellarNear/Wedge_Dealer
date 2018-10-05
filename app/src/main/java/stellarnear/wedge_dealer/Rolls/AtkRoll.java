package stellarnear.wedge_dealer.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import stellarnear.wedge_dealer.R;
import stellarnear.wedge_dealer.Rolls.Dices.Dice;
import stellarnear.wedge_dealer.Tools;

public class AtkRoll {
    private Dice atkDice;

    private Integer preRandValue = 0;
    private Integer atk = 0;
    private Integer base = 0;

    private String mode;

    private Boolean hitConfirmed = false;
    private Boolean crit = false;
    private Boolean critConfirmed = false;
    private Boolean fail = false;
    private Boolean invalid = false;
    private Context mC;

    private SharedPreferences settings;

    private CheckBox hitCheckbox;
    private CheckBox critCheckbox;
    private Tools tools=new Tools();

    public AtkRoll(Activity mA,Context mC, Integer base) {
        this.mC = mC;
        this.base=base;
        this.atkDice = new Dice(mA,mC,20);
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        constructCheckboxes();
    }

    private void constructCheckboxes() {
        hitCheckbox = new CheckBox(mC);
        hitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hitConfirmed = false;
                if (isChecked) {
                    hitConfirmed = true;
                }
            }
        });
        critCheckbox = new CheckBox(mC);
        critCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                critConfirmed = false;
                if (isChecked) {
                    hitCheckbox.setChecked(true);
                    critConfirmed = true;
                }
            }
        });
    }

    private int getBonusAtk() {
       int bonusAtk=0;
        if (settings.getBoolean("thor_switch", mC.getResources().getBoolean(R.bool.thor_switch_def))) {
            bonusAtk+= 3;
        }
        if (settings.getBoolean("isillirit_switch", mC.getResources().getBoolean(R.bool.isillirit_switch_def))) {
            bonusAtk+= 1;
        }
        if (settings.getBoolean("predil_switch", mC.getResources().getBoolean(R.bool.predil_switch_def))) {
            bonusAtk+= 1;
        }
        if (settings.getBoolean("predil_sup_switch", mC.getResources().getBoolean(R.bool.predil_sup_switch_def))) {
            bonusAtk+= 1;
        }
        if (settings.getBoolean("predil_epic_switch", mC.getResources().getBoolean(R.bool.predil_epic_switch_def))) {
            bonusAtk+= 1;
        }
        if (settings.getBoolean("neuf_m_switch", mC.getResources().getBoolean(R.bool.neuf_m_switch_def))) {
            bonusAtk+= 1;
        }
        if (settings.getBoolean("magic_switch", mC.getResources().getBoolean(R.bool.magic_switch_def))) {
            bonusAtk+= tools.toInt(settings.getString("magic_val", String.valueOf(mC.getResources().getInteger(R.integer.magic_val_def))));
        }
        if (this.mode.equalsIgnoreCase("fullround") && settings.getBoolean("tir_rapide", mC.getResources().getBoolean(R.bool.tir_rapide_switch_def))) {
            bonusAtk-=2;
        }
        if (settings.getBoolean("viser", mC.getResources().getBoolean(R.bool.viser_switch_def))) {
            bonusAtk-=tools.toInt(settings.getString("viser_val", String.valueOf(mC.getResources().getInteger(R.integer.viser_val_def))));
        }
        bonusAtk+= tools.toInt(settings.getString("mod_dex", String.valueOf(mC.getResources().getInteger(R.integer.mod_dex_def))));

        bonusAtk+= tools.toInt(settings.getString("epic_val", String.valueOf(mC.getResources().getInteger(R.integer.epic_val_def))));

        bonusAtk+= tools.toInt(settings.getString("att_buff", String.valueOf(mC.getResources().getInteger(R.integer.att_buff_def))));

        if(mode.equalsIgnoreCase("barrage_shot")){
            bonusAtk+=tools.toInt(settings.getString("mythic_tier", String.valueOf(mC.getResources().getInteger(R.integer.mythic_tier_def))));
        }

        return bonusAtk;
    }
    //setters
    public void setMode(String mode){
        this.mode=mode;
    }

    //getters
    public Dice getAtkDice(){
        return this.atkDice;
    }

    public ImageView getImgAtk() {
        return atkDice.getImg();
    }

    public Integer getPreRandValue() {
        this.preRandValue = this.base + getBonusAtk();
        return preRandValue;
    }
    public void setAtkRand() {
        atkDice.rand();
        setCritAndFail();
    }

    private void setCritAndFail() {
        if (atkDice.getRandValue() == 1 && !settings.getBoolean("chance_switch", mC.getResources().getBoolean(R.bool.chance_switch_def))) {
            this.fail = true;
            atkDice.getImg().setOnClickListener(null);
        }
        int critMin;
        if (settings.getBoolean("improved_crit_switch", mC.getResources().getBoolean(R.bool.improved_crit_switch_def))) {
            critMin = 18;
        } else {
            critMin = 20;
        }
        if (atkDice.getRandValue() >= critMin) {
            this.crit = true;
        }
    }

    private void calculAtk() {
        this.atk = this.preRandValue + atkDice.getRandValue();
        if(this.atkDice.getMythicDice()!=null){
            this.atk+=this.atkDice.getMythicDice().getRandValue();
        }
    }

    public Integer getValue() {
        calculAtk();
        return atk;
    }

    public Boolean isInvalid() {
        return invalid;
    }

    public void invalidated() {
        this.invalid = true;
        atkDice.getImg().setImageDrawable(tools.resize(mC,R.drawable.d20_fail, mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_combat_launcher_size)));
        atkDice.getImg().setOnClickListener(null);
    }

    public boolean isFailed() {
        return this.fail;
    }

    public boolean isCrit() {
        return this.crit;
    }

    public void isDelt() {
        invalid = true;
        hitCheckbox.setEnabled(false);
        critCheckbox.setEnabled(false);
        atkDice.delt();
    }

    public boolean isHitConfirmed() {
        return this.hitConfirmed;
    }

    public boolean isCritConfirmed() {
        return this.critConfirmed;
    }

    public CheckBox getHitCheckbox() {
        return this.hitCheckbox;
    }

    public CheckBox getCritCheckbox() {
        return this.critCheckbox;
    }

}
