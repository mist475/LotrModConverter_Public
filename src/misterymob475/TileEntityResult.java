package misterymob475;

import de.piegames.nbt.CompoundMap;

import java.util.Optional;

public class TileEntityResult {
    private final TileEntityFixerReturnType type;
    private final Optional<CompoundMap> content;

    /**
     * The Result of blockEntityFixer, should be a Tuple, but I don't want extra libraries
     * @param content {@link Optional} of type {@link CompoundMap} of the result
     * @param type {@link TileEntityFixerReturnType} containing the type, used in chunkFixer for edge cases
     */
    public TileEntityResult(Optional<CompoundMap> content, TileEntityFixerReturnType type) {
        this.content = content;
        this.type = type;
    }

    @Override
    public String toString() {
        return "TileEntityResult{" + "content=" + content + ", type=" + type + '}';
    }

    public TileEntityFixerReturnType getType() {
        return type;
    }

    public Optional<CompoundMap> getContent() {
        return content;
    }
}
