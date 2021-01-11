package dev.alef.coloroutofspace.lists;

import dev.alef.coloroutofspace.entity.AntiPlayerEntity;
import dev.alef.coloroutofspace.entity.renderers.AntiPlayerRenderer;
import dev.alef.coloroutofspace.Refs;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityList {
	
	public static EntityType<? extends AntiPlayerEntity> color_anti_player = EntityType.Builder.<AntiPlayerEntity>create(AntiPlayerEntity::new, EntityClassification.MONSTER).setCustomClientFactory(AntiPlayerEntity::new).size(0.6F, 1.95F).trackingRange(8).build(Refs.MODID+":color_anti_player");
     
    public static final DeferredRegister<EntityType<?>> ENTITY_LIST = DeferredRegister.create(ForgeRegistries.ENTITIES, Refs.MODID);
    public static final RegistryObject<EntityType<?>> ANTIPLAYER_ENTITY = ENTITY_LIST.register("color_anti_player", () -> color_anti_player);
    
	public static void putAttributes() {
    	GlobalEntityTypeAttributes.put(color_anti_player, AntiPlayerEntity.setCustomAttributes().create());
    }
	
	public static void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(color_anti_player, new AntiPlayerRenderer.RenderFactory());
	}
}
