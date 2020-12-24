package dev.alef.coloroutofspace.lists;

import dev.alef.coloroutofspace.Refs;
import dev.alef.coloroutofspace.items.CuredMetItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemList {

    public static final DeferredRegister<Item> ITEM_LIST = DeferredRegister.create(ForgeRegistries.ITEMS, Refs.MODID);
    public static final RegistryObject<Item> METEORITE_ITEM = ITEM_LIST.register("color_cured_met_item", () -> new CuredMetItem(new Item.Properties().group(ItemGroup.FOOD)));
}
