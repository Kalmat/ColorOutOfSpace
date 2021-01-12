package dev.alef.coloroutofspace.entity;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import dev.alef.coloroutofspace.Refs;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PiglinModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class ColorEntityRenderer extends BipedRenderer<MobEntity, PiglinModel<MobEntity>> {
   private static final Map<Class<?>, ResourceLocation> field_243503_a = ImmutableMap.of(ColorBruteEntity.class, new ResourceLocation("textures/entity/piglin/zombified_piglin.png"), 
		   																			 	 AntiPlayerEntity.class, new ResourceLocation(Refs.MODID, "textures/entity/color_anti_player.png"));

   @SuppressWarnings("rawtypes")
   public ColorEntityRenderer(EntityRendererManager p_i232472_1_, boolean p_i232472_2_) {
      super(p_i232472_1_, func_239395_a_(p_i232472_2_), 0.5F, 1.0019531F, 1.0F, 1.0019531F);
      this.addLayer(new BipedArmorLayer<>(this, new BipedModel(0.5F), new BipedModel(1.02F)));
   }

   private static PiglinModel<MobEntity> func_239395_a_(boolean p_239395_0_) {
      PiglinModel<MobEntity> piglinmodel = new PiglinModel<>(0.0F, 64, 64);
      if (p_239395_0_) {
         piglinmodel.field_239116_b_.showModel = false;
      }
      return piglinmodel;
   }

   public ResourceLocation getEntityTexture(MobEntity entity) {
    	
    	ResourceLocation resourcelocation = field_243503_a.get(entity.getClass());
        if (resourcelocation == null) {
        	throw new IllegalArgumentException("I don't know what texture to use for " + entity.getClass().getSimpleName());
        } else {
        	return resourcelocation;
        }
    }
    
    protected boolean func_230495_a_(MobEntity p_230495_1_) {
       return p_230495_1_ instanceof AbstractColorEntity && ((AbstractColorEntity)p_230495_1_).func_242336_eL();
    }
   
	public static class RenderFactory implements IRenderFactory<AbstractColorEntity> {
		
		@Override
		public EntityRenderer<? super AbstractColorEntity> createRenderFor(EntityRendererManager manager) {
			return new ColorEntityRenderer(manager, true);
		}
	}
}
