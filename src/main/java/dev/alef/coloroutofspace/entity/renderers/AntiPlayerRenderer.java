package dev.alef.coloroutofspace.entity.renderers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.matrix.MatrixStack;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.entity.AntiPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class AntiPlayerRenderer extends ZombieRenderer {
	
	@SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger();
	
	private static ResourceLocation TEXTURE = new ResourceLocation(Refs.MODID, "textures/entity/color_anti_player.png");
	
	public AntiPlayerRenderer(EntityRendererManager renderManagerIn) {
		super(renderManagerIn);
	}

	protected void preRenderCallback(AntiPlayerEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
		matrixStackIn.scale(1.0625F, 1.0625F, 1.0625F);
		super.preRenderCallback(entitylivingbaseIn, matrixStackIn, partialTickTime);
	}
	
	public ResourceLocation getEntityTexture(ZombieEntity entity) {
		return TEXTURE;
	}
	
	public static void setEntityTexture(String skin) {
		TEXTURE = new ResourceLocation(skin);
	}
	
	public static class RenderFactory implements IRenderFactory<AntiPlayerEntity> {
		
		@Override
		public EntityRenderer<? super AntiPlayerEntity> createRenderFor(EntityRendererManager manager) {
			return new AntiPlayerRenderer(manager);
		}
	}
}