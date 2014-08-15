package net.minecraftforge.cauldron.api.inventory;

public class OreDictionaryEntry {
    private int id;
    public OreDictionaryEntry(int id) {
        this.id = id;
    }

    /**
     * Get the opaque ID of this ore-dictionary entry.
     *
     * @return Opaque id number
     */
    public int getId() {
        return id;
    }
}
