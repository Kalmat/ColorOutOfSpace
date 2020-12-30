package dev.alef.coloroutofspace.render;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.bots.CalcVector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.common.ForgeConfig;

@SuppressWarnings("resource")
public class ColorOutOfSpaceRender {

	@SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger();
	
	private static World clientWorld;
	private static PlayerEntity clientPlayer;
	
	private static boolean playerInfected = false;
	private static int cureLevel = 0;
	
    private static boolean optifineChecked = false;
    private static boolean optifinePresent = false;
    
    public static final int KEY_UNDO = GLFW.GLFW_KEY_Z;
    public static final int KEY_ROTATE = GLFW.GLFW_KEY_R;
    
 	public ColorOutOfSpaceRender() {
    }
 	
	public static void setClientWorld(World worldIn) {
		ColorOutOfSpaceRender.clientWorld = worldIn;
	}
	
	public static World getClientWorld() {
		return ColorOutOfSpaceRender.clientWorld;
	}

	public static void setClientPlayer(PlayerEntity player) {
		ColorOutOfSpaceRender.clientPlayer = player;
	}
	
	public static PlayerEntity getClientPlayer() {
		return ColorOutOfSpaceRender.clientPlayer;
	}
	
    public static boolean isPlayerInfected() {
		return ColorOutOfSpaceRender.playerInfected;
	}

	public static void setPlayerInfected(boolean infected) {
		ColorOutOfSpaceRender.playerInfected = infected;
		if (!infected) {
			ColorOutOfSpaceRender.setCureLevel(0);
		}
	}

	public static int getCureLevel() {
		return ColorOutOfSpaceRender.cureLevel;
	}

	public static void setCureLevel(int level) {
		ColorOutOfSpaceRender.cureLevel = level;
	}

	public static void showText(MatrixStack matrixStack) {
    	
    	if (ColorOutOfSpaceRender.isPlayerInfected()) {
    		String[] msg = { Refs.soulsCollectedMsg, ColorOutOfSpaceRender.getCureLevel() + "/" + Refs.cureMaxLevel };
    		ColorOutOfSpaceRender.drawCollectedSouls(msg, matrixStack, Refs.alignUpRight, 0xFFFF0000, false, false);
    	}
    }
    
	private static void drawCollectedSouls(String[] text, MatrixStack ms, int alignTo, int color, boolean shadow, boolean transparent) {
        
    	float scaleA = 0.7F;
		float scaleB = 1.0F;
		int xGap = 5;
		int yGap = 5;

		//Matrix4f mat = ms.getLast().getMatrix();
		//IRenderTypeBuffer.Impl buffers = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		FontRenderer fr = Minecraft.getInstance().fontRenderer;

		ms.push();
		ms.scale(scaleA, scaleA, scaleA);
		int x = calcX(alignTo, xGap, text, 0, scaleA);
		int y = calcY(alignTo, yGap, text, 0, scaleA);
		fr.func_238406_a_(ms, text[0], x, y, color, false);
		//fr.renderString(text[0], (float) x, (float) y, color, shadow, mat, buffers, transparent, 0, 0xF000F0);
		ms.pop();

		ms.push();
		ms.scale(scaleB, scaleB, scaleB);
		x = calcX(alignTo, xGap, text, 1, scaleB);
		y = calcY(alignTo, yGap, text, 1, scaleB);
		fr.func_238406_a_(ms, text[1], x, y, color, false);
		//fr.renderString(text[1], (float) x, (float) y, color, shadow, mat, buffers, transparent, 0, 0xF000F0);
		ms.pop();
		
		//buffers.finish();
    }
	
	private static int calcX(int alignTo, int xGap, String[] text, int index, float scale) {
		
		float ratio = 1.0F / scale;
		
		int x = (int) (xGap * ratio);
    	
		if (index >= 0 && index < text.length) {
			
	    	if (alignTo % 10 != Refs.alignLeft) {
	    		
	    		int screenWidth = Minecraft.getInstance().getMainWindow().getScaledWidth();
	    		int textWidth = Minecraft.getInstance().fontRenderer.getStringWidth(text[index]);
	    		
	    		if (alignTo % 10 == Refs.alignHCenter) {
	    			x = (int) (((screenWidth * ratio) - textWidth) / 2);
	    		}
	    		else if (alignTo % 10 == Refs.alignRight) {
	    			x = (int) ((screenWidth * ratio) - textWidth - x);
	    		}
	    	}
		}
    	return x;
	}
	
	private static int calcY(int alignTo, int yGap, String[] text, int index, float scale) {
		
		float ratio = 1.0F / scale;
		
		int y = yGap;
		int lineHeight = 0;
		
		if (index >= 0 && index < text.length ) {

			lineHeight =  Minecraft.getInstance().fontRenderer.FONT_HEIGHT * index;

			if (alignTo >= Refs.alignVCenter) {
				
	    		int screenHeight = Minecraft.getInstance().getMainWindow().getScaledHeight();
	    		int textHeight =  Minecraft.getInstance().fontRenderer.FONT_HEIGHT * text.length;

	    		if (alignTo < Refs.alignDown) {
	    			y = (int) ((screenHeight - textHeight) / 2);
	    		}
	    		else if (alignTo >= Refs.alignDown) {
	    			y = screenHeight - textHeight;
	    		}
			}
		}
		y = (int) ((y + lineHeight) * ratio);
		return y;
	}
                    
    public static void drawTextCentered(List<String> text, MatrixStack matrixStack, int color, boolean shadow, boolean transparent) {
    	
		int screenWidth = Minecraft.getInstance().getMainWindow().getScaledWidth();
		int screenHeight = Minecraft.getInstance().getMainWindow().getScaledHeight();
		int lineHeight = (int) (Minecraft.getInstance().fontRenderer.FONT_HEIGHT * 1.2);
		int textHeight = (int) (lineHeight * text.size());
		int textWidth;
		int x;
		int y = (int) (screenHeight / 2) - textHeight - (lineHeight * 2);
		String line;
		
		Matrix4f mat = matrixStack.getLast().getMatrix();
		FontRenderer fr = Minecraft.getInstance().fontRenderer;
		IRenderTypeBuffer.Impl buffers = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
		
		for (int i = 0; i < text.size(); ++i) {
			line = text.get(i);
			textWidth = fr.getStringWidth(line);
			x = (int) (screenWidth - textWidth) / 2;
			y += (lineHeight * i);
			fr.renderString(line, (float) x, (float) y, color, shadow, mat, buffers, transparent, 0, 0xF000F0);
		}
		buffers.finish();
	}
    
    public static boolean showMetGlint(MatrixStack ms, World worldIn, BlockPos metPos) {
		
		PlayerEntity player = ColorOutOfSpaceRender.getClientPlayer();
		BlockPos playerPos = new BlockPos(player.getPositionVec());
		
		if (metPos != null && metPos.manhattanDistance(playerPos) <= 32) {
			
			Vector3d metPosVec = new Vector3d(metPos.getX(), metPos.getY(), metPos.getZ());
			
			IRenderTypeBuffer.Impl buffers = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());

			int radius = 2;
			if (metPos.manhattanDistance(playerPos) <= 16) {
				radius = (int) ((worldIn.getDayTime() / 10) % 3);
			}
			
			ColorOutOfSpaceRender.drawSphere(ms, buffers, metPosVec, radius, 36, 18, Refs.glintColor, false);
			
			buffers.finish();
		}
		return true;
	}
    
    public static void drawSphere(MatrixStack ms, IRenderTypeBuffer buffers, Vector3d center, double radius, int lats, int longs, Color color, boolean solid) {
        	
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		int a = color.getAlpha();
		Vector3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
		
		ms.push();
		ms.translate(0.5 - projectedView.x, 0.5 - projectedView.y, 0.5 - projectedView.z);
		
		IVertexBuilder buffer = buffers.getBuffer(RenderTypeMod.SPHERE);;
		if (solid) {
			buffer = buffers.getBuffer(RenderTypeMod.SOLID_SPHERE);
		}
		Matrix4f mat = ms.getLast().getMatrix();
		
		double lat0, xz0, y0, lat1, xz1, y1, lng, x, z;
        
        for(int i = 1; i <= lats; ++i) {
        	
            lat0 = Math.PI * (-0.5 + (double) (i - 1) / lats);
            y0  = radius * Math.sin(lat0);
            xz0 = radius * Math.cos(lat0);

            lat1 = Math.PI * (-0.5 + (double) i / lats);
            y1 = radius * Math.sin(lat1);
            xz1 = radius * Math.cos(lat1);

            for(int j = 1; j <= longs; ++j) {
            	
                lng = 2 * Math.PI * (double) (j - 1) / longs;
                x = Math.cos(lng);
                z = Math.sin(lng);
                
                buffer.pos(mat, (float) (center.x + x * xz0), (float) (center.y + y0), (float) (center.z + z * xz0)).color(r, g, b, a).endVertex();
                buffer.pos(mat, (float) (center.x + x * xz1), (float) (center.y + y1), (float) (center.z + z * xz1)).color(r, g, b, a).endVertex();
            }
        }
        ms.pop();
    }
    
    public static boolean isLookingAtDirection(PlayerEntity player, BlockPos pos) {
		
		BlockPos playerPos = new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ());
		
		if (CalcVector.getDirection(playerPos, pos) == null) {
			if(player.rotationPitch > 0 && CalcVector.getVDirection(playerPos, pos).equals(Direction.DOWN) || 
					player.rotationPitch < 0 && CalcVector.getVDirection(playerPos, pos).equals(Direction.UP)) {
				playerPos = new BlockPos(player.getPosX(), pos.getY(), player.getPosZ());
			}
		}
		if (playerPos.equals(pos) || player.getHorizontalFacing().equals(CalcVector.getDirection(playerPos, pos)) && playerPos.manhattanDistance(pos) < 16) {
			return true;
		}
		return false;
	}

	public static boolean isLookingAtPos(World worldIn, BlockPos pos) {
		BlockPos blockpos = getMousePos();
		return blockpos.equals(pos);
	}
	
	public static BlockPos getMousePos() {
		return ((BlockRayTraceResult) Minecraft.getInstance().objectMouseOver).getPos();
	}
	
	public static Screen getCurrentScreen() {
    	return Minecraft.getInstance().currentScreen;
    }
	
	public static boolean hasOptifine() {
		
		if (!ColorOutOfSpaceRender.optifineChecked) {
			ColorOutOfSpaceRender.optifineChecked = true;
			for (Map<String, String> mod : FMLLoader.modLauncherModList()) {
				if (mod.get("name").toString().toLowerCase().equals("optifine")) {
					ColorOutOfSpaceRender.optifinePresent = true;
					break;
				}
			}
		}
		return ColorOutOfSpaceRender.optifinePresent;
	}
	
	public static boolean isShadersActive() {
		return (ColorOutOfSpaceRender.hasOptifine() || (!ForgeConfig.CLIENT.forgeLightPipelineEnabled.get() && !ForgeConfig.CLIENT.experimentalForgeLightPipelineEnabled.get()));
	}

	public static void stopSound() {
		Minecraft.getInstance().getSoundHandler().stop();
	}
	
	public static void resumeSound() {
		Minecraft.getInstance().getSoundHandler().resume();
	}
	
	public static ResourceLocation cloneSkin(World worldIn, PlayerEntity player, Entity mob, BlockPos pos) {
		ResourceLocation skin1 = Minecraft.getInstance().player.getLocationSkin();
		//GameProfile playerProfile = Minecraft.getInstance().player.getGameProfile();
		//Map<Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager().loadSkinFromCache(playerProfile);
		//ResourceLocation skin2 = Minecraft.getInstance().getSkinManager().loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
		//mob.copyDataFromOld(player);
		return skin1;
	}
}

