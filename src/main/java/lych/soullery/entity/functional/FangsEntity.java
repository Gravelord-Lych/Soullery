package lych.soullery.entity.functional;

import lych.soullery.Soullery;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.EvokerFangsEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An extension of Evoker Fangs.
 */
public class FangsEntity extends EvokerFangsEntity {
    private static final ResourceLocation DEFAULT_LOCATION = new ResourceLocation("textures/entity/illager/evoker_fangs.png");
    @NotNull
    private ResourceLocation textureLocation = DEFAULT_LOCATION;
    private float damage = 6;
    private final Set<EffectInstance> effects = new HashSet<>();

    public FangsEntity(EntityType<? extends FangsEntity> fangs, World world) {
        super(fangs, world);
    }

    /**
     * @param yRot <b>Radian</b>
     */
    public FangsEntity(World world, double x, double y, double z, float yRot, int warmupDelayTicks, LivingEntity owner) {
        super(world, x, y, z, yRot, warmupDelayTicks, owner);
    }

    @Override
    protected void dealDamageTo(LivingEntity entity) {
        LivingEntity owner = this.getOwner();
        if (entity.isAlive() && !entity.isInvulnerable() && entity != owner) {
            if (owner == null) {
                entity.hurt(DamageSource.MAGIC, damage);
            } else {
                if (owner.isAlliedTo(entity)) {
                    return;
                }
                entity.hurt(DamageSource.indirectMagic(this, owner), damage);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putFloat("Damage", damage);
        compoundNBT.putString("TextureLocation", textureLocation.toString());
        ListNBT effectsNBT = new ListNBT();
        for (EffectInstance effect : effects) {
            CompoundNBT effectNBT = new CompoundNBT();
            effect.save(effectNBT);
            effectsNBT.add(effectNBT);
        }
        compoundNBT.put("EffectsToAdd", effectsNBT);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        damage = compoundNBT.getFloat("Damage");
        if (compoundNBT.contains("TextureLocation", Constants.NBT.TAG_STRING)) {
            try {
                textureLocation = new ResourceLocation(compoundNBT.getString("TextureLocation"));
            } catch (ResourceLocationException e) {
                Soullery.LOGGER.warn("Caught exception when parsing texture location, used default", e);
                textureLocation = DEFAULT_LOCATION;
            }
        }
        if (compoundNBT.contains("EffectsToAdd", Constants.NBT.TAG_LIST)) {
            effects.clear();
            ListNBT effectsNBT = compoundNBT.getList("EffectsToAdd", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < effectsNBT.size(); i++) {
                CompoundNBT effectNBT = effectsNBT.getCompound(i);
                effects.add(EffectInstance.load(effectNBT));
            }
        }
    }

    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    public void setTextureLocation(ResourceLocation textureLocation) {
        Objects.requireNonNull(textureLocation, "TextureLocation should be non-null");
        this.textureLocation = textureLocation;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public Set<EffectInstance> getEffects() {
        return effects;
    }

    public void addEffect(EffectInstance effect) {
        effects.add(effect);
    }
}
