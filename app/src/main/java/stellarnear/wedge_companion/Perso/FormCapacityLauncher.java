package stellarnear.wedge_companion.Perso;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import stellarnear.wedge_companion.FormCapacityAlertDialog;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.TestAlertDialog;
import stellarnear.wedge_companion.Tools;

/**
 * Created by jchatron on 11/12/2019.
 */

public class FormCapacityLauncher {
    private Activity mA;
    private Context mC;
    private FormCapacity capa;
    private Perso pj= PersoManager.getCurrentPJ();
    private Tools tools=Tools.getTools();
    public FormCapacityLauncher(Activity mA, Context mC,FormCapacity capa){
        this.mA=mA;
        this.mC=mC;
        this.capa=capa;
        if (capa.getSaveType().equalsIgnoreCase("dmd")) {
            Ability bmo=pj.getAllAbilities().getAbi("ability_bmo");
            final TestAlertDialog testAlertDialog = new TestAlertDialog(mA, mC, bmo);
            testAlertDialog.setRefreshEventListener(new TestAlertDialog.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    askForConfirm();
                }
            });
        } else {
            directDealDamage();
        }

    }

    private void askForConfirm() {
        new AlertDialog.Builder(mA)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Confirmation de succès")
                .setMessage("As tu réussi le jet BMO ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        directDealDamage();
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void directDealDamage() {
        final FormCapacityAlertDialog tooltipAlert = new FormCapacityAlertDialog(mA, mC, capa);
    }



}
