package stellarnear.wedge_companion;

import stellarnear.wedge_companion.Stats.Stat;

public class FameEntry {
    private Stat stat;
    private String foeName;
    private String location;
    private String details;

    public FameEntry(Stat stat, String foeName, String location, String details){
        this.stat = stat;
        this.foeName = foeName;
        this.location = location;
        this.details = details;
    }


    public String getLocation() {
        return location;
    }

    public String getFoeName() {
        return foeName;
    }

    public Stat getStat() {
        return stat;
    }

    public String getDetails() {
        return details;
    }

    public int getSumDmg() {
        return stat==null ? 0 : stat.getSumDmg();
    }

    public void updateInfos(String foeName, String location, String details) {
        this.foeName=foeName;
        this.location=location;
        this.details=details;
    }
}
