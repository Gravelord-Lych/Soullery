package lych.soullery.entity.iface;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Objects;

public interface ISpellCastable {
    boolean isCastingSpell();

    SpellType getCurrentSpell();

    void setCastingSpell(SpellType type);

    SoundEvent getCastingSoundEvent();

    int getSpellCastingTickCount();

    void setSpellCastingTickCount(int spellCastingTickCount);

    default void renderParticles() {
        LivingEntity entity = (LivingEntity) this;
        World level = entity.level;
        if (level.isClientSide() && isCastingSpell()) {
            SpellType spell = getCurrentSpell();
            double r = spell.getR();
            double g = spell.getG();
            double b = spell.getB();
            float rot = entity.yBodyRot * ((float) Math.PI / 180F) + MathHelper.cos(entity.tickCount * 0.6662F) * 0.25F;
            float xOffset = MathHelper.cos(rot);
            float zOffset = MathHelper.sin(rot);
            level.addParticle(ParticleTypes.ENTITY_EFFECT, entity.getX() + (double) xOffset * 0.6D, entity.getY() + 1.8D, entity.getZ() + (double) zOffset * 0.6D, r, g, b);
            level.addParticle(ParticleTypes.ENTITY_EFFECT, entity.getX() - (double) xOffset * 0.6D, entity.getY() + 1.8D, entity.getZ() - (double) zOffset * 0.6D, r, g, b);
        }
    }

    class SpellType implements Comparable<SpellType> {
        public static final SpellType NONE = new SpellType(0, 0, 0, 0);

        private final int id;
        private final double r;
        private final double g;
        private final double b;

        private SpellType(int id, double r, double g, double b) {
            this.id = id;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public static SpellType create(int id, double r, double g, double b) {
            SpellType type = new SpellType(id, r, g, b);
            Preconditions.checkArgument(type.id > 0, "Id should be greater than 0");
            return type;
        }

        public double getR() {
            return r;
        }

        public double getG() {
            return g;
        }

        public double getB() {
            return b;
        }

        public int getId() {
            return id;
        }

        @Override
        public int compareTo(SpellType o) {
            return Integer.compare(id, o.id);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SpellType spellType = (SpellType) o;
            return getId() == spellType.getId() && Double.compare(spellType.getR(), getR()) == 0 && Double.compare(spellType.getG(), getG()) == 0 && Double.compare(spellType.getB(), getB()) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId(), getR(), getG(), getB());
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("id", id)
                    .add("r", r)
                    .add("g", g)
                    .add("b", b)
                    .toString();
        }
    }
}
