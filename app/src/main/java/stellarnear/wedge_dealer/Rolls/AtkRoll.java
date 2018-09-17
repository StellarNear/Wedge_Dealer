package stellarnear.wedge_dealer.Rolls;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import stellarnear.stellarnear.R;
import stellarnear.wedge_dealer.Tools;

public class AtkRoll {
    private Dice atkDice;

    private Integer preRandValue = 0;
    private Integer atk = 0;

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

    public AtkRoll(Context mC, Integer base) {
        this.mC = mC;
        this.atkDice = new Dice(mC,20);
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        this.preRandValue = base + getBonusAtk();
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
        if (settings.getBoolean("neuf_m_switch", mC.getResources().getBoolean(R.bool.neuf_m_switch_def))) {
            bonusAtk+= 1;
        }
        if (settings.getBoolean("magic_switch", mC.getResources().getBoolean(R.bool.magic_switch_def))) {
            bonusAtk+= tools.toInt(settings.getString("magic_val", String.valueOf(mC.getResources().getInteger(R.integer.magic_val_def))));
        }

        if (settings.getBoolean("tir_rapide", mC.getResources().getBoolean(R.bool.tir_rapide_switch_def))) {
            bonusAtk-=2;
        }

        if (settings.getBoolean("viser", mC.getResources().getBoolean(R.bool.viser_switch_def))) {
            bonusAtk-=tools.toInt(settings.getString("viser_val", String.valueOf(mC.getResources().getInteger(R.integer.viser_val_def))));
        }

        bonusAtk+= tools.toInt(settings.getString("mod_dex", String.valueOf(mC.getResources().getInteger(R.integer.mod_dex_def))));

        bonusAtk+= tools.toInt(settings.getString("epic_val", String.valueOf(mC.getResources().getInteger(R.integer.epic_val_def))));

        bonusAtk+= tools.toInt(settings.getString("att_buff", String.valueOf(mC.getResources().getInteger(R.integer.att_buff_def))));

        return bonusAtk;
    }
    //getters

    public ImageView getImgAtk() {
        return atkDice.getImg();
    }

    public Integer getPreRandValue() {
        setAtkRand();
        return preRandValue;
    }
    private void setAtkRand() {
        atkDice.rand();
        calculAtk();
    }

    private void calculAtk() {
        this.atk = this.preRandValue + atkDice.getRandValue();
        if (atkDice.getRandValue() == 1) {
            this.fail = true;
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

    public Integer getValue() {
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
