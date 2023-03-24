package lych.soullery.entity.ai;

import lych.soullery.entity.monster.voidwalker.AbstractVoidLasererEntity;
import lych.soullery.entity.monster.voidwalker.ComputerScientistEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.ToIntFunction;

public final class ComputerOperation implements AbstractVoidLasererEntity.ILaserProvider<ComputerScientistEntity> {
    private static final ResourceLocation TEXTURE = AbstractVoidLasererEntity.prefixTex("computer_scientist_operate_beam.png");
    public static final ComputerOperation SHIELD = new ComputerOperation(TEXTURE, 0xA3D5FF, 0xA3D5FF);
    public static final ComputerOperation MUTATE = new ComputerOperation(TEXTURE, 0x4B7013, 0x7EC414);
    public static final ComputerOperation SHUFFLE = new ComputerOperation(TEXTURE, 0x1111EE, 0x3333CC);
    public static final ComputerOperation CYBERATTACK = new ComputerOperation(TEXTURE, ComputerScientistEntity::getAttackColor, ComputerScientistEntity::getAttackColor);
    private static ComputerOperation[] operations;
    private final ResourceLocation textureLocation;
    private final ToIntFunction<? super ComputerScientistEntity> srcColor;
    private final ToIntFunction<? super ComputerScientistEntity> destColor;
    private final int id;

    public ComputerOperation(ResourceLocation textureLocation, int srcColor, int destColor) {
        this(textureLocation, cs -> srcColor, cs -> destColor);
    }

    public ComputerOperation(ResourceLocation textureLocation, ToIntFunction<? super ComputerScientistEntity> srcColor, ToIntFunction<? super ComputerScientistEntity> destColor) {
        this.textureLocation = textureLocation;
        int id;
        if (operations == null) {
            id = 0;
            operations = new ComputerOperation[1];
        } else {
            id = operations.length;
            operations = Arrays.copyOf(operations, operations.length + 1);
        }
        operations[id] = this;
        this.srcColor = srcColor;
        this.destColor = destColor;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Nullable
    public static ComputerOperation byId(int id) {
        if (id >= 0 && id < operations.length) {
            return operations[id];
        }
        return null;
    }

    @Override
    public ResourceLocation getTextureLocation(ComputerScientistEntity armorer, Entity target) {
        return textureLocation;
    }

    @Override
    public int getSrcColor(ComputerScientistEntity cs, Entity target) {
        return srcColor.applyAsInt(cs);
    }

    @Override
    public int getDestColor(ComputerScientistEntity cs, Entity target) {
        return destColor.applyAsInt(cs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComputerOperation operation = (ComputerOperation) o;
        return srcColor == operation.srcColor && destColor == operation.destColor && getId() == operation.getId() && Objects.equals(textureLocation, operation.textureLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textureLocation, srcColor, destColor, getId());
    }
}
