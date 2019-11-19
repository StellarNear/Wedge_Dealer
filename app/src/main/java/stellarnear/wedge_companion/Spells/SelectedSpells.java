package stellarnear.wedge_companion.Spells;

public class SelectedSpells {

    private static SelectedSpells instance = new SelectedSpells();
    private SpellList selectedSpells =new SpellList();

    private SelectedSpells() {
    }

    public static SelectedSpells getInstance() {
        return instance;
    }

    public SpellList getSelection() {
        return selectedSpells;
    }

    public void setSelection(SpellList selectedSpells) {
        this.selectedSpells=selectedSpells;
    }
}
