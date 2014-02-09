package org.bukkit.craftbukkit.block;


import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.NoteBlock;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

public class CraftNoteBlock extends CraftBlockState implements NoteBlock {
    private final CraftWorld world;
    private final net.minecraft.tileentity.TileEntityNote tileNote;

    public CraftNoteBlock(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        tileNote = (net.minecraft.tileentity.TileEntityNote) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public Note getNote() {
        return new Note(tileNote.note);
    }

    public byte getRawNote() {
        return tileNote.note;
    }

    public void setNote(Note n) {
        tileNote.note = n.getId();
    }

    public void setRawNote(byte n) {
        tileNote.note = n;
    }

    public boolean play() {
        Block block = getBlock();

        if (block.getType() == Material.NOTE_BLOCK) {
            tileNote.triggerNote(world.getHandle(), getX(), getY(), getZ());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean play(byte instrument, byte note) {
        Block block = getBlock();

        if (block.getType() == Material.NOTE_BLOCK) {
            world.getHandle().addBlockEvent(getX(), getY(), getZ(), CraftMagicNumbers.getBlock(block), instrument, note);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean play(Instrument instrument, Note note) {
        Block block = getBlock();

        if (block.getType() == Material.NOTE_BLOCK) {
            world.getHandle().addBlockEvent(getX(), getY(), getZ(), CraftMagicNumbers.getBlock(block), instrument.getType(), note.getId());
            return true;
        } else {
            return false;
        }
    }
}
