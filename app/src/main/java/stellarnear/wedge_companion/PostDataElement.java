package stellarnear.wedge_companion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.FormSpell.FormPower;
import stellarnear.wedge_companion.Perso.CanalisationCapacity;
import stellarnear.wedge_companion.Perso.Capacity;
import stellarnear.wedge_companion.Perso.Form;
import stellarnear.wedge_companion.Perso.FormCapacity;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;
import stellarnear.wedge_companion.Spells.CalculationSpell;
import stellarnear.wedge_companion.Spells.Metamagic;
import stellarnear.wedge_companion.Spells.Spell;


public class PostDataElement {
    private String targetSheet= PersoManager.getCurrentNamePJ();
    private String date="-";
    private String detail ="-";
    private String typeEvent="-";
    private String result="-";

    /* jet  attaque et jet degat de l'attaque  */
    public PostDataElement(RollList rolls, String mode) {
        if(mode.equalsIgnoreCase("atk")) {
            initAtk(rolls);
        } else {
            initDmg(rolls);
        }
    }

    public PostDataElement forceTargetSheet(String forcedSheet){
        targetSheet=forcedSheet;
        return this;
    }


    private void initAtk(RollList atkRolls){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());

        this.typeEvent="Jets d'attaques";
        String detailTxt="";
        for (Roll roll : atkRolls.getList()) {
            if (!detailTxt.equalsIgnoreCase("")) {
                detailTxt += " / ";
            }

            if(roll.isInvalid()){
                detailTxt +="-";
            } else if(roll.isCrit()) {
                detailTxt += "crit";
            } else  if(roll.isFailed()) {
                detailTxt += "fail";
            } else {
                detailTxt += "normal";
            }
            if(!roll.isInvalid()){
                detailTxt += "("+roll.getAtkDice().getRandValue();
                if(roll.getAtkDice().getMythicDice()!=null){detailTxt +=","+roll.getAtkDice().getMythicDice().getRandValue();}
                detailTxt +=")";
            }
        }

        this.detail=detailTxt;
        String resultTxt="";

        for (Roll roll : atkRolls.getList()) {
            if (!resultTxt.equalsIgnoreCase("")) {
                resultTxt += " / ";
            }
            int resultAtk=roll.getAtkValue();
            if(roll.getAtkDice().getMythicDice()!=null){
                resultAtk+=roll.getAtkDice().getMythicDice().getRandValue();
            }
            resultTxt += resultAtk;
        }
        this.result=resultTxt;
    }

    /* dégat d'une attaque */
    private void initDmg(RollList atkRolls){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());

        this.typeEvent="Dégats de l'attaque";

        String detailTxt="";
        int nAtk=0;
        int nCrit=0;
        for (Roll roll : atkRolls.getList()) {
            if(roll.isHitConfirmed()){
                nAtk++;
                if (roll.isCritConfirmed()){
                    nCrit++;
                }
            }
        }
        detailTxt=nAtk+" hits, "+nCrit+" crits";
        this.detail=detailTxt;

        String resultTxt="";

        int totalSum = 0;
        ElemsManager elems = ElemsManager.getInstance();
        if(elems!=null) {
            for (String elem : elems.getListKeysWedgeDamage()) {
                int sumElem = atkRolls.getDmgSumFromType(elem);
                if (sumElem > 0) {
                    if (!resultTxt.equalsIgnoreCase("")) {
                        resultTxt += ", ";
                    }
                    totalSum += sumElem;
                    resultTxt += sumElem + " " + elems.getName(elem);
                }
            }
            if (!resultTxt.equalsIgnoreCase("")) {
                resultTxt += " || ";
            }
            if (totalSum > 0) {
                resultTxt += "Total:" + totalSum;
            }
            this.result = resultTxt;
        }
    }

    /* autre posts */
    public PostDataElement(String typeEvent,int result){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        this.typeEvent=typeEvent;
        this.result=String.valueOf(result);
    }

    public PostDataElement(String typeEvent,String resultTxt){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        this.typeEvent=typeEvent;
        this.result=resultTxt;
    }


    public PostDataElement(String typeEvent,String detail,String resultTxt){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        this.typeEvent=typeEvent;
        this.detail=detail;
        this.result=resultTxt;
    }

    public PostDataElement(String typeEvent, Dice oriDice, int result){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());

        this.typeEvent=typeEvent;
        this.result=String.valueOf(result);

        String detailTxt = String.valueOf(oriDice.getRandValue());
        if(oriDice.getMythicDice()!=null){detailTxt +=","+oriDice.getMythicDice().getRandValue();}
        this.detail =detailTxt;
    }

    /* lancement d'un sort */
    public PostDataElement(Spell spell){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());

        this.typeEvent="Lancement sort "+spell.getName() +" (rang:"+new CalculationSpell().currentRank(spell)+")";

        if(spell.getMetaList().hasAnyMetaActive()){
            this.detail="Métamagies:";
            for(Metamagic meta : spell.getMetaList().getAllActivesMetas().asList()){
                this.detail+=meta.getName()+"(+"+meta.getUprank()*meta.getnCast()+")";
            }
        }
        if(spell.isFailed()||spell.contactFailed()){
            String failPostData="-";
            if(spell.isFailed()){
                failPostData="Test de RM raté";
            } else if(spell.contactFailed()){
                failPostData="Test de contact raté";
            }
            this.result=failPostData;
        } else {
            if(spell.getDmg_type().equalsIgnoreCase("")){
                this.result="Lancé !";
            } else {
                if(spell.getDmg_type().equalsIgnoreCase("heal")){
                    this.result = "Soins : " + spell.getDmgResult();
                } else {
                    this.result = "Dégâts : " + spell.getDmgResult();
                }
            }
        }
    }

    public PostDataElement(Form form){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        if(form==null){
            this.typeEvent = "Retour à la forme normale";
            this.result = "Druide Archer-sylvestre !" ;
        } else {
            this.typeEvent = "Changement de forme en " + form.getName();
            Perso pj = PersoManager.getCurrentPJ();
            this.result = pj.getAllForms().getFormAbiModText(form);
            String detailTxt = "";
            for (FormCapacity capa : form.getListActivesCapacities()) {
                if (!detailTxt.equalsIgnoreCase("")) {
                    detailTxt += ", ";
                }
                detailTxt += capa.getName();
            }
            for (FormCapacity capa : form.getListPassiveCapacities()) {
                if (!detailTxt.equalsIgnoreCase("")) {
                    detailTxt += ", ";
                }
                detailTxt += capa.getName();
            }
            if (form.getResistsValueLongFormat().length() > 0) {
                if (!detailTxt.equalsIgnoreCase("")) {
                    detailTxt += ", ";
                }
                detailTxt += form.getResistsValueLongFormat();
            }
            if (form.getVulnerability().length() > 0) {
                if (!detailTxt.equalsIgnoreCase("")) {
                    detailTxt += ", ";
                }
                detailTxt += "Vulnérabilité : " + form.getVulnerability();
            }
            this.detail = detailTxt;
        }
    }

    public PostDataElement(Capacity capa) {
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        this.typeEvent="Lancement capacité "+capa.getName();
        this.detail=capa.getDescr();
        if(capa.getValue()>0) {
            this.result = "Valeur : " + capa.getValue();
        }
    }

    public PostDataElement(FormCapacity capa, int sumResult) {
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        this.typeEvent="Lancement capacité "+capa.getName();
        this.detail=capa.getDescr();
        this.result="Dégâts : "+sumResult;
    }

    public PostDataElement(FormPower spell){
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        this.typeEvent="Lancement sort de forme animale "+spell.getName();
        this.detail=spell.getDescr();
        if(spell.getDmg_type().equalsIgnoreCase("")){
            this.result="Lancé !";
        } else {
            this.result="Dégâts : "+spell.getDmgResult();
        }
    }


    public PostDataElement(CanalisationCapacity canalCapaSelected) {
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        this.typeEvent="Lancement capacité d'energie "+canalCapaSelected.getName();
        this.detail="Cout : "+canalCapaSelected.getCost()+" canalisation";
        this.result=canalCapaSelected.getShortdescr();
        if(PersoManager.getCurrentPJ().getAllCapacities().capacityIsActive("capacity_epic_revelation_canal")){
            this.result=result.replace("9m","13m");
        }
    }

    public PostDataElement(CanalisationCapacity canal, int sumResult) {
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.FRANCE);
        this.date=formater.format(new Date());
        this.typeEvent="Lancement capacité "+canal.getName();
        this.detail=canal.getShortdescr();
        if(PersoManager.getCurrentPJ().getAllCapacities().capacityIsActive("capacity_epic_revelation_canal")){
            this.detail=detail.replace("9m","13m");
        }
        this.result="Résultat : "+sumResult;
    }


    public String getDetail() {
        return detail;
    }

    public String getDate() {
        return date;
    }

    public String getResult() {
        return result;
    }

    public String getTargetSheet() {
        return targetSheet;
    }

    public String getTypeEvent() {
        return typeEvent;
    }

}
