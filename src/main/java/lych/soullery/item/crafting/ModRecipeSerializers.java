package lych.soullery.item.crafting;

import lych.soullery.Soullery;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModRecipeSerializers {
    public static final DeferredRegister<IRecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Soullery.MOD_ID);
    public static final RegistryObject<SpecialRecipeSerializer<SoulContainerRecipe>> SOUL_CONTAINER = SERIALIZERS.register(ModRecipeNames.SOUL_CONTAINER,
            () -> new SpecialRecipeSerializer<>(SoulContainerRecipe::new));

    private ModRecipeSerializers() {}
}
