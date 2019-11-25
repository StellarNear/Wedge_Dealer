package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import stellarnear.wedge_companion.CalculationAtk;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Tools;

public class AtkRoll {
    protected Dice atkDice;

    protected Integer preRandValue = 0;
    protected Integer atk = 0;
    protected Integer base = 0;

    protected String mode; //le type d'attauqe fullround,barrrage,simple

    protected Boolean miss = false;
    protected Boolean hitConfirmed = false;
    protected Boolean crit = false;
    protected Boolean critConfirmed = false;
    protected Boolean fail = false;
    protected Boolean invalid = false;
    protected Boolean manualDice;
    protected Context mC;

    protected SharedPreferences settings;

    protected CheckBox hitCheckbox;
    protected CheckBox critCheckbox;
    protected Perso pj = PersoManager.getCurrentPJ();

    protected OnRefreshEventListener mListener;
    protected Tools tools=new Tools();

    public AtkRoll(Activity mA,Context mC, Integer base) {
        this.mC = mC;
        this.base=base;
        this.atkDice = new Dice(mA,mC,20);
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        manualDice = settings.getBoolean("switch_manual_diceroll", mC.getResources().getBoolean(R.bool.switch_manual_diceroll_def));
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

        hitCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                miss=true;
                hitCheckbox.setChecked(false);
                critCheckbox.setChecked(false);
                hitCheckbox.setEnabled(false);
                critCheckbox.setEnabled(false);
                return false;
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
        this.preRandValue = this.base + new CalculationAtk(mC).getBonusAtk();
        return preRandValue;
    }

    public void setAtkRand() {
        atkDice.rand(manualDice);
        if (manualDice) {
            atkDice.setRefreshEventListener(new Dice.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    calculAtk();
                    setCritAndFail();
                    if(mListener!=null){mListener.onEvent();}
                }
            });
        } else {
            calculAtk();
            setCritAndFail();
        }
    }

    private void setCritAndFail() {
        if (atkDice.getRandValue() == 1 ) {
            this.fail = true;
            atkDice.getImg().setOnClickListener(null);
        }
        int critMin=20;
        if (atkDice.getRandValue() >= critMin) {
            this.crit = true;
        }
    }

    public void calculAtk() {
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
    }

    public boolean isMissed(){
        return this.miss;
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

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }
}
