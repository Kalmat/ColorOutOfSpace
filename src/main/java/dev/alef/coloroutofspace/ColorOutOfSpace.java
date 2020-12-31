package dev.alef.coloroutofspace;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.alef.coloroutofspace.entities.AntiPlayerEntity;
import dev.alef.coloroutofspace.entities.renderers.AntiPlayerRenderer;
import dev.alef.coloroutofspace.lists.BlockList;
import dev.alef.coloroutofspace.lists.EntityList;
import dev.alef.coloroutofspace.lists.ItemList;
import dev.alef.coloroutofspace.network.Networking;
import dev.alef.coloroutofspace.network.PacketAddCure;
import dev.alef.coloroutofspace.playerdata.PlayerData;
import dev.alef.coloroutofspace.playerdata.PlayerDataList;
import dev.alef.coloroutofspace.bots.MetBot;
import dev.alef.coloroutofspace.items.MeteoriteSwordTier;
import dev.alef.coloroutofspace.lists.BlockItemList;
import dev.alef.coloroutofspace.render.ColorOutOfSpaceRender;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

//The value here should match an entry in the META-INF/mods.toml file
@Mod(Refs.MODID)
public class ColorOutOfSpace {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
    public static PlayerDataList playerDataList;
	private static long serverPrevTime = 0;
    
    private static boolean debug = false;
    
	public ColorOutOfSpace() {

		// Register modloading events
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		
		// Register other events we use
        MinecraftForge.EVENT_BUS.register(new onPlayerLoggedInListener());
        MinecraftForge.EVENT_BUS.register(new onPlayerRespawnListener());
        MinecraftForge.EVENT_BUS.register(new onWorldTickListener());
        MinecraftForge.EVENT_BUS.register(new onBlockBreakListener());
        MinecraftForge.EVENT_BUS.register(new onPlayerSleepListener());
        MinecraftForge.EVENT_BUS.register(new onPlayerWakeUpListener());
        MinecraftForge.EVENT_BUS.register(new onPlayerHitListener());
        MinecraftForge.EVENT_BUS.register(new onHitEntityListener());
        MinecraftForge.EVENT_BUS.register(new onLivingDeathListener());
        MinecraftForge.EVENT_BUS.register(new onPlayerLoggedOutListener());
        MinecraftForge.EVENT_BUS.register(new onRenderGameOverlayListener());
        
		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
		
		// Register our custom blocks and items
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		BlockList.BLOCK_LIST.register(modEventBus);
		BlockItemList.BLOCKITEM_LIST.register(modEventBus);
		ItemList.ITEM_LIST.register(modEventBus);
		EntityList.ENTITY_LIST.register(modEventBus);
				
		// Register custom server --> client messages
		Networking.registerMessages();
		
		// Create the player data lists we will use on client and server sides
		ColorOutOfSpace.playerDataList = new PlayerDataList();
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	private void setup(final FMLCommonSetupEvent event) {
	    // some preinit code
		if (ColorOutOfSpace.debug) { LOGGER.info("HELLO from PREINIT"); }
	    DeferredWorkQueue.runLater(() -> {
            GlobalEntityTypeAttributes.put((EntityType<? extends LivingEntity>) EntityList.color_anti_player, AntiPlayerEntity.setCustomAttributes().func_233813_a_());
        });
	}
	 
	@SuppressWarnings({ "resource", "unchecked" })
	private void doClientStuff(final FMLClientSetupEvent event) {
		// do something that can only be done on the client
		if (ColorOutOfSpace.debug) { LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings); }
		RenderTypeLookup.setRenderLayer(BlockList.color_grass, RenderType.getCutout());
		//RenderTypeLookup.setRenderLayer(BlockList.color_leaves_block, RenderType.getSolid());
    	RenderingRegistry.registerEntityRenderingHandler((EntityType<? extends AntiPlayerEntity>) EntityList.color_anti_player, new AntiPlayerRenderer.RenderFactory());
	}
	
	private void enqueueIMC(final InterModEnqueueEvent event) {
		// some example code to dispatch IMC to another mod
		if (ColorOutOfSpace.debug) { InterModComms.sendTo(Refs.MODID, "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world"; }); }
	}

	private void processIMC(final InterModProcessEvent event) {
		// some example code to receive and process InterModComms from other mods
		if (ColorOutOfSpace.debug) { LOGGER.info("Got IMC {}", event.getIMCStream().map(m->m.getMessageSupplier().get()).collect(Collectors.toList())); }
	}

	// CLIENT & SERVER
    public class onPlayerLoggedInListener {
        
		@SubscribeEvent
        public void PlayerLogIn(final PlayerLoggedInEvent event) throws FileNotFoundException, CommandSyntaxException {
			
			PlayerEntity player = event.getPlayer();
			World world = player.world;

			if (!world.isRemote) {

				Random rand = new Random();
				PlayerData playerData = ColorOutOfSpace.playerDataList.read(world, player);

				if (playerData.getFirstJoin() == 0L) {
					playerData.reset(true);
					playerData.setFirstJoin(world.getGameTime() + 1L);
					playerData.setFallDay(Refs.daysToFall + rand.nextInt(Refs.graceDaysToFall));
				}
				else if (playerData.isPlayerInfected()) {
					Utils.applyInfectedEffects(player, false);
					Networking.sendToClient(new PacketAddCure(playerData.getPlayerUUID(), playerData.getCureLevel()), (ServerPlayerEntity) playerData.getPlayer());
				}
			}
			else {
				ColorOutOfSpaceRender.setClientWorld(world);
				ColorOutOfSpaceRender.setClientPlayer(player);
			}
        }
		
    }

    // SERVER
    public class onPlayerRespawnListener {
        
		@SubscribeEvent
        public void PlayerRespawn(final PlayerRespawnEvent event) throws FileNotFoundException, CommandSyntaxException {
			
			PlayerEntity player = event.getPlayer();
			World world = player.world;

			if (!world.isRemote) {

				PlayerData playerData = ColorOutOfSpace.playerDataList.read(world, player);
				
				if (playerData.isPlayerInfected()) {
					Utils.applyInfectedEffects(player, false);
				}
			}
		}
	}
    
	// SERVER
    public class onWorldTickListener {
        
		@SuppressWarnings("unused")
		@SubscribeEvent
        public void WorldTick(final WorldTickEvent event) {
			
			World world = event.world;
			long time = event.world.getDayTime();
			
			if (time % Refs.timeIncrease == 0 && time != ColorOutOfSpace.serverPrevTime) {
				
				ColorOutOfSpace.serverPrevTime = time;

				for (PlayerData playerData : ColorOutOfSpace.playerDataList.getList()) {

					if (playerData.isMetFallen() && playerData.isMetActive()) {

						PlayerEntity player = playerData.getPlayer();
						
						if (playerData.getPrevRadius() < Refs.infectRadiusLimit) {
							
							int radius = playerData.getPrevRadius() + Refs.radiusIncrease;
							MetBot metBot = new MetBot();
							metBot.increaseInfectedArea(world, player, playerData.getMetPos(), playerData.getPrevRadius(), radius);
							playerData.setPrevRadius(radius);
							
							if (playerData.isPlayerInfected()) {
								Utils.applyInfectedEffects(player, false);
							}
						}
						else if (!playerData.isPlayerCured()) {
							Random rand = new Random();
							playerData.setMetFallen(false);
							playerData.setFallDay(Refs.daysToFall + rand.nextInt(Refs.graceDaysToFall));
							playerData.setMetActive(false);
							if (Refs.limitCureTime || Refs.hardcoreMode) {
								playerData.setPlayerInfected(false);
								playerData.setCureLevel(0);
								world.destroyBlock(playerData.getMetPos(), false);
								MetBot metBot = new MetBot();
								metBot.uninfectArea(world, player, playerData.getMetPos(), playerData.getPrevRadius(), false);
							}
						}
					}
					if ((world.getGameTime() - playerData.getFirstJoin()) / 24000 == 0) {
						if (playerData.getFallDay() > 0) {
							playerData.setFallDay(playerData.getFallDay() - 1);
						}
					}
				}
			}
		}
    }
    
	// CLIENT & SERVER
    public class onPlayerSleepListener {
        
		@SubscribeEvent
        public void PlayerSleep(final PlayerSleepInBedEvent event) {
			
			PlayerEntity player = event.getPlayer();
			World world = player.world;
			
			if (!world.isRemote) {

				BlockPos bedPos = new BlockPos(player.getPositionVec());
				PlayerData playerData = ColorOutOfSpace.playerDataList.get(world, player);
				
				playerData.setBedPos(bedPos);
				playerData.setFallPos(bedPos, true);
			}
		}
    }
    
	// CLIENT & SERVER
    public class onPlayerWakeUpListener {
        
		@SubscribeEvent
        public void PlayerWakeUp(final PlayerWakeUpEvent event) {
			
			PlayerEntity player = event.getPlayer();
			World world = player.world;
			
			if (!world.isRemote) {

				PlayerData playerData = ColorOutOfSpace.playerDataList.get(world, player);
				
				if (!playerData.isMetFallen() && playerData.getFallDay() == 0) {
					MetBot metBot = new MetBot();
					metBot.metFall(world, player, playerData);
				}
			}
		}
    }
    
    // SERVER
    public class onPlayerHitListener {
        
		@SubscribeEvent
        public void PlayerHit(final LivingHurtEvent event) {
			
			if (event.getEntityLiving() instanceof PlayerEntity) {
				
				PlayerEntity playerAttacked = (PlayerEntity) event.getEntityLiving();
				
				if (event.getSource().getTrueSource() instanceof PlayerEntity) {

					PlayerData playerData = ColorOutOfSpace.playerDataList.get(playerAttacked.world, playerAttacked);
					PlayerEntity playerAttacking = (PlayerEntity) event.getSource().getTrueSource();

					if (playerData.isPlayerInfected()) {
						playerAttacking.addPotionEffect(new EffectInstance(Effects.NAUSEA, 100));
					}

					playerData = ColorOutOfSpace.playerDataList.get(playerAttacking.world, playerAttacking);
					
					if (playerData.isPlayerInfected()) {
						playerAttacked.addPotionEffect(new EffectInstance(Effects.NAUSEA, 100));
					}
				}
			}
		}
    }

    // CLIENT & SERVER
	public class onHitEntityListener {
		
	    @SubscribeEvent
        public void onHitEntity(AttackEntityEvent event) {
        	
        	Entity target = event.getTarget();
        	PlayerEntity player = (PlayerEntity) event.getPlayer();
        	
        	Item itemInMainHand = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND).getStack().getItem();
        	
    		if (itemInMainHand.equals(ItemList.meteorite_sword)) {
    			Utils.knockback(player, target, MeteoriteSwordTier.getKnockback());
        	}
		}
	}

    // CLIENT & SERVER
    public class onLivingDeathListener {
        
		@SubscribeEvent
        public void LivingDeath(final LivingDeathEvent event) throws IOException {
			
			LivingEntity entity = event.getEntityLiving();
			Entity attacker = event.getSource().getTrueSource();
			World world = entity.world;

			if (!world.isRemote && attacker != null) {

				if (attacker instanceof PlayerEntity && Refs.souls.contains(entity.getType())) {
						
					PlayerData playerData = ColorOutOfSpace.playerDataList.get(world, (PlayerEntity) attacker);
					
					if (playerData.isPlayerInfected() && playerData.isMetActive()) {

						playerData.setCureLevel(playerData.getCureLevel() + 1);
						Networking.sendToClient(new PacketAddCure(playerData.getPlayerUUID(), playerData.getCureLevel()), (ServerPlayerEntity) playerData.getPlayer());

						if (playerData.getCureLevel() == Refs.cureMaxLevel) {
							world.setBlockState(playerData.getMetPos(), Refs.curedMetState);
							playerData.setMetActive(false);
						}
					}
				}
				else if (!(entity instanceof PlayerEntity) && !(attacker instanceof PlayerEntity) &&
						entity.world.func_230315_m_().func_242725_p().equals(Refs.overworld) &&
						Refs.infectedDupEntities.contains(attacker.getType())) {
					
					Random rand = new Random();
					EntityType<?> spawnEntity = attacker.getType();
					int chance = Refs.dupEntityChance;
					
					if (spawnEntity.equals(EntityType.field_233590_aW_)) {
						chance = Refs.dupZoglinChance;
					}
					if (rand.nextInt(chance) == 0 || Refs.hardcoreMode) {
						Utils.spawnEntity((ServerWorld) entity.world, null, new BlockPos(entity.getPositionVec()), spawnEntity, true, null);
					}
				}
	        }
		}
    }

    // SERVER
    public class onPlayerLoggedOutListener {
        
		@SubscribeEvent
        public void PlayerLogOut(final PlayerEvent.PlayerLoggedOutEvent event) throws IOException {

			PlayerEntity player = event.getPlayer();
			World world = player.world;
			
			ColorOutOfSpace.playerDataList.write(world, player, true);
        }
    }
    
 	// SERVER
	public class onBlockBreakListener {
		
		@SubscribeEvent
		public void BlockBreak(final BlockEvent.BreakEvent  event) {

			PlayerEntity player = event.getPlayer();
			World world = player.world;
			
			PlayerData playerData = ColorOutOfSpace.playerDataList.get(world, player);
			
    		if (playerData != null && playerData.isPlayerInfected()) {
    			world.destroyBlock(event.getPos(), false);
    		}
		}
	}
	
	// CLIENT
	public class onRenderGameOverlayListener {
		
		@SubscribeEvent
		public void RenderGameOverlay(final RenderGameOverlayEvent.Text event) {
	    	ColorOutOfSpaceRender.showText(event.getMatrixStack());
		}
	}
}
