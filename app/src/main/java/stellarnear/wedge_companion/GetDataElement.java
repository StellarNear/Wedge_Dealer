package stellarnear.wedge_companion;

public class GetDataElement {
    private String date;
    private String uuid;
    private String caster;
    private String spelljson;

    /* jet  attaque et jet degat de l'attaque  */
    public GetDataElement(String date,String uuid,String caster,String spelljson) {
       this.date=date;
       this.uuid=uuid;
       this.caster=caster;
       this.spelljson=spelljson;
    }

    public String getDate() {
        return date;
    }

    public String getUuid() {
        return uuid;
    }

    public String getCaster() {
        return caster;
    }

    public String getSpelljson() {
        return spelljson;
    }
}
