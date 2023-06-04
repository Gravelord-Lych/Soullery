package lych.soullery.gui.container;

import lych.soullery.Soullery;
import lych.soullery.block.ModBlockNames;
import lych.soullery.item.ModItemNames;
import lych.soullery.util.ProgressArray;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;
import java.util.function.Consumer;

import static lych.soullery.Soullery.make;
import static net.minecraftforge.common.extensions.IForgeContainerType.create;

@Mod.EventBusSubscriber(modid = Soullery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContainers {
    public static final ContainerType<DepthSEGeneratorContainer> DEPTH_SEGEN = createSimply(DepthSEGeneratorContainer::new);
    public static final ContainerType<ExtraAbilityContainer> EXA = create((id, inventory, data) -> new ExtraAbilityContainer(id, inventory, data.readVarInt()));
    public static final ContainerType<HeatSEGeneratorContainer> HEAT_SEGEN = createSimply(HeatSEGeneratorContainer::new);
    public static final ContainerType<NetherSEGeneratorContainer> NETHER_SEGEN = createSimply(NetherSEGeneratorContainer::new);
    public static final ContainerType<SEGeneratorContainer> SEGEN = createSimply(SEGeneratorContainer::new);
    public static final ContainerType<SkySEGeneratorContainer> SKY_SEGEN = createSimply(SkySEGeneratorContainer::new);
    public static final ContainerType<SolarSEGeneratorContainer> SOLAR_SEGEN = createSimply(SolarSEGeneratorContainer::new);
    public static final ContainerType<SEStorageContainer> SOUL_ENERGY_STORAGE = createSimply(SEStorageContainer::new);
    public static final ContainerType<SoulReinforcementTableContainer> SOUL_REINFORCEMENT_TABLE = create((id, inventory, data) -> new SoulReinforcementTableContainer(id, inventory, new IntArray(5)));

    private static <T extends Container> ContainerType<T> createSimply(ContainerMaker<T> maker) {
        return create((id, inventory, data) -> {
            BlockPos pos = data.readBlockPos();
            return maker.make(id, pos, inventory, Objects.requireNonNull(Minecraft.getInstance().level), new ProgressArray(), IWorldPosCallable.create(Objects.requireNonNull(Minecraft.getInstance().level), pos));
        });
    }

    private interface ContainerMaker<T> {
        T make(int id, BlockPos pos, PlayerInventory inventory, World world, IIntArray seProgress, IWorldPosCallable access);
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        registry.register(make(DEPTH_SEGEN, ModBlockNames.DEPTH_SEGEN));
        registry.register(make(EXA, ModItemNames.EXTRA_ABILITY_WAND));
        registry.register(make(HEAT_SEGEN, ModBlockNames.HEAT_SEGEN));
        registry.register(make(NETHER_SEGEN, ModBlockNames.NETHER_SEGEN));
        registry.register(make(SEGEN, ModBlockNames.SEGEN));
        registry.register(make(SKY_SEGEN, ModBlockNames.SKY_SEGEN));
        registry.register(make(SOLAR_SEGEN, ModBlockNames.SOLAR_SEGEN));
        registry.register(make(SOUL_ENERGY_STORAGE, ModBlockNames.SOUL_ENERGY_STORAGE));
        registry.register(make(SOUL_REINFORCEMENT_TABLE, ModBlockNames.SOUL_REINFORCEMENT_TABLE));
    }

    public static void addInventory(IInventory inventory, int leftCol, int topRow, Consumer<? super Slot> slotAdder) {
        // Player inventory
        addSlotBox(inventory, 9, leftCol, topRow, 9, 18, 3, 18, slotAdder);
        // Hotbar
        topRow += 58;
        addSlotRange(inventory, 0, leftCol, topRow, 9, 18, slotAdder);
    }

    private static int addSlotRange(IInventory inventory, int index, int x, int y, int amount, int dx, Consumer<? super Slot> slotAdder) {
        for (int i = 0; i < amount; i++) {
            slotAdder.accept(new Slot(inventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private static int addSlotBox(IInventory inventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy, Consumer<? super Slot> slotAdder) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(inventory, index, x, y, horAmount, dx, slotAdder);
            y += dy;
        }
        return index;
    }
}
