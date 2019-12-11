package stellarnear.wedge_companion.Perso;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import stellarnear.wedge_companion.CustomAlertDialog;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.TestAlertDialog;

/**
 * Created by jchatron on 11/12/2019.
 */

public class FormCapacityLauncher {
    private Activity mA;
    private Context mC;
    private FormCapacity capa;
    private Perso pj= PersoManager.getCurrentPJ();
    public FormCapacityLauncher(Activity mA, Context mC,FormCapacity capa){
        this.mA=mA;
        this.mC=mC;
        this.capa=capa;
        if(capa.hasPower()){
            customPowerAlert();
        } else if (capa.getId().equalsIgnoreCase("form_capacity_constriction")) {
            Ability bmo=pj.getAllAbilities().getAbi("ability_bmo");
            final TestAlertDialog testAlertDialog = new TestAlertDialog(mA, mC, bmo);
            testAlertDialog.setRefreshEventListener(new TestAlertDialog.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    askForConfirm();
                }
            });
        } else { //todo lancer direct les degats
            popupAlert();
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
                        //todo lancer les degat capa
                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void customPowerAlert() { //todo affiche avec cooldown et le spell
    }

    private void popupAlert() {
        View tooltip = mA.getLayoutInflater().inflate(R.layout.custom_toast_info_form_capacity, null);
        final CustomAlertDialog tooltipAlert = new CustomAlertDialog(mA, mC, tooltip);
        tooltipAlert.setPermanent(true);
        ((TextView)tooltip.findViewById(R.id.toast_textName)).setText(capa.getName());
        ((TextView)tooltip.findViewById(R.id.toast_textDescr)).setText(capa.getDescr());
    }


}
