package dev.alef.coloroutofspace.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.network.Networking;
import dev.alef.coloroutofspace.network.PacketMetFall;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

@SuppressWarnings("resource")
@OnlyIn(Dist.CLIENT)
public class ColorOutOfSpaceRender {

	@SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger();
	
	private static boolean playerInfected = false;
	private static int metDisableLevel = 0;
	
    public static final int KEY_FALL_MET = GLFW.GLFW_KEY_M;
	
    public static boolean isPlayerInfected() {
		return ColorOutOfSpaceRender.playerInfected;
	}

	public static void setPlayerInfected(boolean isInfected, int metDisableLevel, boolean metJustDisabled) {
		ColorOutOfSpaceRender.playerInfected = isInfected;
		if (isInfected) {
			ColorOutOfSpaceRender.metDisableLevel = metDisableLevel;
			if (metJustDisabled) {
				ColorOutOfSpaceRender.playSound(Refs.curedMetSound);
			}
		}
		else {
			ColorOutOfSpaceRender.metDisableLevel = 0;
		}
	}

	public static void showText(MatrixStack matrixStack) {
    	
    	if (ColorOutOfSpaceRender.isPlayerInfected()) {
    		
    		List<String> msg = null;
    		int textColor = 0xFFFFFFFF;

    		if (ColorOutOfSpaceRender.metDisableLevel < Refs.cureMaxLevel) {
    			msg = Arrays.asList(Refs.soulsCollectedMsg, ColorOutOfSpaceRender.metDisableLevel + "/" + Refs.cureMaxLevel);
    			textColor = 0xFFFF0000;
    		}
    		else {
    			msg = Arrays.asList(Refs.allSoulsCollectedMsg, Refs.mineMetMsg);
    			textColor = 0xFF00FF00;
    		}
    		ColorOutOfSpaceRender.drawCollectedSouls(msg, matrixStack, Refs.alignUpRight, textColor, false, false);
    	}
    }
    
	private static void drawCollectedSouls(List<String> text, MatrixStack ms, int alignTo, int color, boolean shadow, boolean transparent) {
        
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
		fr.func_238406_a_(ms, text.get(0), x, y, color, false);
		//fr.renderString(text[0], (float) x, (float) y, color, shadow, mat, buffers, transparent, 0, 0xF000F0);
		ms.pop();

		ms.push();
		ms.scale(scaleB, scaleB, scaleB);
		x = calcX(alignTo, xGap, text, 1, scaleB);
		y = calcY(alignTo, yGap, text, 1, scaleB);
		fr.func_238406_a_(ms, text.get(1), x, y, color, false);
		//fr.renderString(text[1], (float) x, (float) y, color, shadow, mat, buffers, transparent, 0, 0xF000F0);
		ms.pop();
		
		//buffers.finish();
    }
	
	private static int calcX(int alignTo, int xGap, List<String> text, int index, float scale) {
		
		float ratio = 1.0F / scale;
		
		int x = (int) (xGap * ratio);
    	
		if (index >= 0 && index < text.size()) {
			
	    	if (alignTo % 10 != Refs.alignLeft) {
	    		
	    		int screenWidth = Minecraft.getInstance().getMainWindow().getScaledWidth();
	    		int textWidth = Minecraft.getInstance().fontRenderer.getStringWidth(text.get(index));
	    		
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
	
	private static int calcY(int alignTo, int yGap, List<String> text, int index, float scale) {
		
		float ratio = 1.0F / scale;
		
		int y = yGap;
		int lineHeight = 0;
		
		if (index >= 0 && index < text.size()) {

			lineHeight =  Minecraft.getInstance().fontRenderer.FONT_HEIGHT * index;

			if (alignTo >= Refs.alignVCenter) {
				
	    		int screenHeight = Minecraft.getInstance().getMainWindow().getScaledHeight();
	    		int textHeight =  Minecraft.getInstance().fontRenderer.FONT_HEIGHT * text.size();

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
    
    public static boolean showMetGlint(MatrixStack ms, BlockPos metPos) {
		
		ClientWorld world = Minecraft.getInstance().world;
    	PlayerEntity player = Minecraft.getInstance().player;
		BlockPos playerPos = player.getPosition();

		if (metPos != null && metPos.manhattanDistance(playerPos) <= 32) {
			
			Vector3d metPosVec = new Vector3d(metPos.getX(), metPos.getY(), metPos.getZ());
			
			IRenderTypeBuffer.Impl buffers = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());

			int radius = 2;
			if (metPos.manhattanDistance(playerPos) <= 16) {
				radius = (int) ((world.getDayTime() / 10) % 3);
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
    
	public static void playSound(int sound) {

		ClientWorld world = Minecraft.getInstance().world;
		ClientPlayerEntity player = Minecraft.getInstance().player;
		BlockPos pos = player.getPosition();
		
		if (sound == Refs.curedMetSound) {
			Random rand = new Random();
	        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + rand.nextFloat() * 0.2F, false);
	        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + rand.nextFloat() * 0.2F, false);
		}
	}
	
    public static void registerKeybindings() {
    	
		List<KeyBinding> KEYBINDS = new ArrayList<KeyBinding>();
	    KEYBINDS.add(new KeyBinding("key.lazybuilder.undo", ColorOutOfSpaceRender.KEY_FALL_MET, "key.lazybuilder.general"));
	    
	    for (KeyBinding keyBind : KEYBINDS) {
	        ClientRegistry.registerKeyBinding(keyBind);
	    }
	}
    
	public static boolean isValidMetFallKey(int action, int modifiers, int key) {
		if (action == GLFW.GLFW_PRESS && modifiers == GLFW.GLFW_MOD_CONTROL && key == ColorOutOfSpaceRender.KEY_FALL_MET &&
				getCurrentScreen() == null && Minecraft.getInstance().player.isCreative()) {
			return true;
		}
		return false;
	}
	
	public static void sendMetFallToServer() {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		BlockPos pos = ((BlockRayTraceResult) Minecraft.getInstance().objectMouseOver).getPos();
        Networking.sendToServer(new PacketMetFall(player.getUniqueID(), pos));
	}
	
	public static Screen getCurrentScreen() {
    	return Minecraft.getInstance().currentScreen;
    }
}

