package misterymob475;

import com.github.bsideup.jabel.Desugar;
import de.piegames.nbt.CompoundMap;

import java.util.Optional;

@Desugar //Jabel peculiarity
public record TileEntityResult(Optional<CompoundMap> content, TileEntityFixerReturnType type) {
    /**
     * The Result of blockEntityFixer
     *
     * @param content {@link Optional} of type {@link CompoundMap} of the result
     * @param type    {@link TileEntityFixerReturnType} containing the type, used in chunkFixer for edge cases
     */
    public TileEntityResult {
    }

    @Override
    public String toString() {
        return "TileEntityResult{" + "content=" + content + ", type=" + type + '}';
    }
}
