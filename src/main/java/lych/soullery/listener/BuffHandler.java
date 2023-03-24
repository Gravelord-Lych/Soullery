package lych.soullery.listener;

import lych.soullery.Soullery;
import lych.soullery.api.event.PostLivingHurtEvent;
import lych.soullery.api.exa.MobDebuff;
import lych.soullery.extension.soulpower.buff.DamageBuff;
import lych.soullery.extension.soulpower.buff.DefenseBuff;
import lych.soullery.extension.soulpower.buff.PlayerBuffMap;
import lych.soullery.extension.soulpower.debuff.MobDebuffMap;
import lych.soullery.util.Utils;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID)
public final class BuffHandler {
    private BuffHandler() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void handleMobDebuffs(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof MobEntity) {
            MobEntity mob = (MobEntity) event.getEntity();
            World world = event.getWorld();
            for (MobDebuff debuff : MobDebuffMap.values()) {
                long applyTimes = getApplyTimes(event.getWorld(), debuff);
                for (long i = 0; i < applyTimes; i++) {
                    debuff.doWhenMobJoinWorld(mob, world);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntityLiving() instanceof MobEntity) || event.getEntityLiving().level.isClientSide()) {
            return;
        }
        MobEntity mob = (MobEntity) event.getEntityLiving();
        for (MobDebuff debuff : MobDebuffMap.values().stream().filter(debuff -> getApplyTimes(event.getEntity().level, debuff) >= 1).collect(Collectors.toList())) {
            debuff.serverTick(mob, (ServerWorld) event.getEntityLiving().level);
        }
    }

    private static long getApplyTimes(World world, MobDebuff debuff) {
        return Utils.clamp(world.players()
                .stream()
                .filter(MobDebuffMap.getAbility(debuff).orElseThrow(NullPointerException::new)::isOn)
                .count(), 0, Math.max(1, debuff.getMaxStackSize()));
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            PlayerBuffMap.values().stream()
                    .filter(buff -> buff instanceof DamageBuff)
                    .map(buff -> (DamageBuff) buff)
                    .filter(buff -> PlayerBuffMap.hasBuff(player, buff))
                    .forEach(buff -> buff.onPlayerAttack(player, event));
        }
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            PlayerBuffMap.values().stream()
                    .filter(buff -> buff instanceof DefenseBuff)
                    .map(buff -> (DefenseBuff) buff)
                    .filter(buff -> PlayerBuffMap.hasBuff(player, buff))
                    .forEach(buff -> buff.onEntityAttackPlayer(player, event));
        }
    }


    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            PlayerBuffMap.values().stream()
                    .filter(buff -> buff instanceof DamageBuff)
                    .map(buff -> (DamageBuff) buff)
                    .filter(buff -> PlayerBuffMap.hasBuff(player, buff))
                    .forEach(buff -> buff.onLivingHurt(player, event));
        }
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            PlayerBuffMap.values().stream()
                    .filter(buff -> buff instanceof DefenseBuff)
                    .map(buff -> (DefenseBuff) buff)
                    .filter(buff -> PlayerBuffMap.hasBuff(player, buff))
                    .forEach(buff -> buff.onPlayerHurt(player, event));
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getSource().getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            PlayerBuffMap.values().stream()
                    .filter(buff -> buff instanceof DamageBuff)
                    .map(buff -> (DamageBuff) buff)
                    .filter(buff -> PlayerBuffMap.hasBuff(player, buff))
                    .forEach(buff -> buff.onLivingDamage(player, event));
        }
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            PlayerBuffMap.values().stream()
                    .filter(buff -> buff instanceof DefenseBuff)
                    .map(buff -> (DefenseBuff) buff)
                    .filter(buff -> PlayerBuffMap.hasBuff(player, buff))
                    .forEach(buff -> buff.onPlayerDamaged(player, event));
        }
    }

    @SubscribeEvent
    public static void onPostHurt(PostLivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            PlayerBuffMap.values().stream()
                    .filter(buff -> buff instanceof DamageBuff)
                    .map(buff -> (DamageBuff) buff)
                    .filter(buff -> PlayerBuffMap.hasBuff(player, buff))
                    .forEach(buff -> buff.onPostHurt(player, event));
        }
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            PlayerBuffMap.values().stream()
                    .filter(buff -> buff instanceof DefenseBuff)
                    .map(buff -> (DefenseBuff) buff)
                    .filter(buff -> PlayerBuffMap.hasBuff(player, buff))
                    .forEach(buff -> buff.onPostHurt(player, event));
        }
    }
}
