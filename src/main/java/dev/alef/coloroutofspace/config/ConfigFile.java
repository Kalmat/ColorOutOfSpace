package dev.alef.coloroutofspace.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ConfigFile {
	
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    public static class General {
        public final ForgeConfigSpec.ConfigValue<Integer> Difficulty;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            Difficulty = builder
                    .comment("Difficulty [0 - Normal / 1 - Hardcore | default: 0]")
                    .define("Difficulty", 0);
            builder.pop();
        }
    }
    
    public static void registerConfig() {         
    	ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigFile.spec);
    }
    
//    @SubscribeEvent
//    public static void onLoad(final ModConfig.Loading configEvent) {
//    }
//
//    @SubscribeEvent
//    public static void onReload(final ConfigReloading configEvent) {
//    }
}