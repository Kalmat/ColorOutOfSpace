package dev.alef.coloroutofspace.lists;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.item.CuredMetItem;
import dev.alef.coloroutofspace.item.MeteoriteSwordTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemList {
	
	public static final Item meteorite_antidote = new CuredMetItem(new Item.Properties().group(ItemGroup.FOOD));
	public static final Item meteorite_sword = new SwordItem(MeteoriteSwordTier.meteorite_sword, 0, -2, new Item.Properties().group(ItemGroup.COMBAT));

    public static final DeferredRegister<Item> ITEM_LIST = DeferredRegister.create(ForgeRegistries.ITEMS, Refs.MODID);
    public static final RegistryObject<Item> METEORITE_ITEM = ITEM_LIST.register("color_cured_met_item", () -> meteorite_antidote);
    public static final RegistryObject<Item> METEORITE_SWORD_ITEM = ITEM_LIST.register("meteorite_sword", () -> meteorite_sword);
}
