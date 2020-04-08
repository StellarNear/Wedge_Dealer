package stellarnear.wedge_companion.Spells;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Metamagic {
    private String id;
    private String name;
    private String description;
    private transient CheckBox check = null;
    private transient OnCheckedChangeListener changeListener;
    private int uprank;
    private boolean multicast = false;
    private int nCast;
    private transient OnRefreshEventListener mListener;

    public Metamagic(Metamagic meta) { //pour cloner
        this.id = meta.id;
        this.name = meta.name;
        this.description = meta.description;
        this.uprank = meta.uprank;
        this.multicast = meta.multicast;
        this.nCast = meta.nCast;
    }

    public Metamagic(String id, String name, String description, int uprank, boolean multicast) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.uprank = uprank;
        this.multicast = multicast;
        this.nCast = 0;
    }

    public CheckBox getCheckBox(final Activity mA, final Context mC) {
        if (check == null) {
            check = new CheckBox(mC);
            check.setText(name + " (+" + uprank + ")");
            setOnChangeListenr(mA);
            check.setOnCheckedChangeListener(changeListener);
        }
        return check;
    }

    private void setOnChangeListenr(final Activity mA) {
        changeListener = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    nCast += 1;
                } else {
                    if (multicast) {
                        new AlertDialog.Builder(mA)
                                .setTitle("Demande de confirmation")
                                .setMessage("Veux-tu utiliser " + name + " une fois de plus ?")
                                .setIcon(android.R.drawable.ic_menu_help)
                                .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        check.setChecked(true);
                                        if (mListener != null) {
                                            mListener.onEvent();
                                        }
                                    }
                                })
                                .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        check.setChecked(false);
                                        nCast = 0;
                                        if (mListener != null) {
                                            mListener.onEvent();
                                        }
                                    }
                                }).show();
                    } else {
                        nCast = 0;
                    }
                }
                if (mListener != null) {
                    mListener.onEvent();
                }
            }
        };
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getUprank() {
        return uprank;
    }

    public String getDescription() {
        return description;
    }

    public boolean canBeMultiCast() {
        return multicast;
    }

    public int getnCast() {
        return nCast;
    }

    public boolean isActive() {
        return nCast > 0;
    }

    public void activateFromConversion() {
        this.nCast = 1;
        if (!(check == null)) {
            check.setOnClickListener(null);
            check.setOnCheckedChangeListener(null);
            check.setChecked(true);
            check.setEnabled(false);
        }
    }

    public void active() {
        this.nCast = 1;
        if (mListener != null) {
            mListener.onEvent();
        }
    }

    public void desactive() {
        this.check.setChecked(false);
        this.nCast = 0;
        if (mListener != null) {
            mListener.onEvent();
        }
    }

    public OnCheckedChangeListener getOnChangeListener() {
        return changeListener;
    }

    public OnRefreshEventListener getmListener() {
        return mListener;
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }
}
