package dev.alef.coloroutofspace.lists;

import dev.alef.coloroutofspace.entities.AntiPlayerEntity;
import dev.alef.coloroutofspace.Refs;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityList {
	
	public static EntityType<?> color_anti_player = EntityType.Builder.<AntiPlayerEntity>create(AntiPlayerEntity::new, EntityClassification.MONSTER).setCustomClientFactory(AntiPlayerEntity::new).size(0.95F, 2.95F).trackingRange(8).build(Refs.MODID+":color_anti_player");
     
    public static final DeferredRegister<EntityType<?>> ENTITY_LIST = DeferredRegister.create(ForgeRegistries.ENTITIES, Refs.MODID);
    public static final RegistryObject<EntityType<?>> ANTIPLAYER_ENTITY = ENTITY_LIST.register("color_anti_player", () -> color_anti_player);
}
