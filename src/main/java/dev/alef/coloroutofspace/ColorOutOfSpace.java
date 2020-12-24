package dev.alef.coloroutofspace;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.alef.coloroutofspace.lists.BlockList;
import dev.alef.coloroutofspace.lists.ItemList;
import dev.alef.coloroutofspace.network.Networking;
import dev.alef.coloroutofspace.network.PacketAddCure;
import dev.alef.coloroutofspace.playerdata.PlayerData;
import dev.alef.coloroutofspace.playerdata.PlayerDataList;
import dev.alef.coloroutofspace.bots.MetBot;
import dev.alef.coloroutofspace.lists.BlockItemList;
import dev.alef.coloroutofspace.render.ColorOutOfSpaceRender;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
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
	private static Random rand = new Random();
	private static long serverPrevTime = 0;
    
    private static boolean debug = false;
    
	public ColorOutOfSpace() {

		// Register modloading events
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		
		// Register other events we use
		MinecraftForge.EVENT_BUS.register(new ModColorManager());
        MinecraftForge.EVENT_BUS.register(new onPlayerLoggedInListener());
        MinecraftForge.EVENT_BUS.register(new onPlayerRespawnListener());
        MinecraftForge.EVENT_BUS.register(new onWorldTickListener());
        MinecraftForge.EVENT_BUS.register(new onBlockBreakListener());
        MinecraftForge.EVENT_BUS.register(new onPlayerSleepListener());
        MinecraftForge.EVENT_BUS.register(new onPlayerWakeUpListener());
        MinecraftForge.EVENT_BUS.register(new onPlayerHitListener());
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
		
		// Register custom server --> client messages
		Networking.registerMessages();
		
		// Create the player data lists we will use on client and server sides
		ColorOutOfSpace.playerDataList = new PlayerDataList();
	}

	private void setup(final FMLCommonSetupEvent event) {
	    // some preinit code
		if (ColorOutOfSpace.debug) {
			LOGGER.info("HELLO from PREINIT");
	 	}
	}
	 
	@SuppressWarnings("resource")
	private void doClientStuff(final FMLClientSetupEvent event) {
		// do something that can only be done on the client
		if (ColorOutOfSpace.debug) {
			LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
		}
	}
	
	private void enqueueIMC(final InterModEnqueueEvent event) {
		// some example code to dispatch IMC to another mod
		if (ColorOutOfSpace.debug) {
			InterModComms.sendTo(Refs.MODID, "helloworld", () -> { 
				LOGGER.info("Hello world from the MDK"); return "Hello world";});
		}
	}

	private void processIMC(final InterModProcessEvent event) {
		// some example code to receive and process InterModComms from other mods
		if (ColorOutOfSpace.debug) {
			LOGGER.info("Got IMC {}", event.getIMCStream().
						map(m->m.getMessageSupplier().get()).
	    				collect(Collectors.toList()));
		}
	}
	
	//@Mod.EventBusSubscriber(modid = Refs.MODID)
	public class ModColorManager {

	    @SubscribeEvent
	    public void registerBlockColorHandlers(final ColorHandlerEvent.Block event) {
	        final BlockColors blockColors = event.getBlockColors();
	        final IBlockColor grassColorHandler = (state, blockAccess, pos, tintIndex) -> {
	        	state = BlockList.color_grass_block.getDefaultState();
	        	return blockColors.getColor(state, null, null, tintIndex);
	        };
	        blockColors.register(grassColorHandler, BlockList.color_grass_block);
	    }

//	    @SubscribeEvent
//	    public void registerItemColourHandlers(final ColorHandlerEvent.Item event) {
//	        final BlockColors blockColors = event.getBlockColors();
//	        final ItemColors itemColors = event.getItemColors();
//
//	        final IItemColor itemBlockColourHandler = (stack, tintIndex) -> {
//	            @SuppressWarnings("deprecation")
//	            final BlockState state = ((BlockItem) stack.getItem()).getBlock().getDefaultState();
//	            return blockColors.getColor(state, null, null, tintIndex);
//	        };
//
//	        itemColors.register(itemBlockColourHandler, MoreStuff.MARBLEGRASS);
//	    }
	}
	
	// CLIENT & SERVER
    public class onPlayerLoggedInListener {
        
		@SubscribeEvent
        public void PlayerLogIn(final PlayerLoggedInEvent event) throws FileNotFoundException, CommandSyntaxException {
			
			PlayerEntity player = event.getPlayer();
			World world = player.world;

			if (!world.isRemote) {

				PlayerData playerData = ColorOutOfSpace.playerDataList.read(world, player);

				if (playerData.getFirstJoin() == 0L) {
					playerData.reset(true);
					playerData.setFirstJoin(world.getGameTime() + 1L);
					playerData.setFallDay(Refs.daysToFall + ColorOutOfSpace.rand.nextInt(Refs.graceDaysToFall));
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
							playerData.reset(Refs.limitCureTime);
							playerData.setFallDay(Refs.daysToFall + ColorOutOfSpace.rand.nextInt(Refs.graceDaysToFall));
						}
						if ((time - playerData.getFirstJoin()) / 24000L == 2.0) {
							Utils.spawnEntity(world, player, playerData.getMetPos(), EntityType.COW, false);
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
				int offset = ColorOutOfSpace.rand.nextInt(5) + 10;
				playerData.setFallPos(bedPos.offset(Direction.byHorizontalIndex(ColorOutOfSpace.rand.nextInt(3)), offset));
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
				
				if ((world.getGameTime() - playerData.getFirstJoin()) / 24000 == 0) {
					if (playerData.getFallDay() == 0) {
						if (!playerData.isMetFallen()) {
							Utils.metFall(world, player, playerData);
						}
					}
					playerData.setFallDay(playerData.getFallDay() - 1);
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
					
					EntityType<?> spawnEntity = attacker.getType();
					Entity spawnedEntity = spawnEntity.spawn((ServerWorld) entity.world, new CompoundNBT(), null, null, new BlockPos(entity.getPositionVec()), SpawnReason.CONVERSION, false, false);
					Utils.applyPersistence(spawnedEntity);
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
