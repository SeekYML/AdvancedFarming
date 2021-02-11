package de.seekyml.farming.config.sections.enchants;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;

import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public final class ReplenishSettings implements SettingsHolder {


    @Override
    public void registerComments(CommentsConfiguration conf) {
        conf.setComment("enchantments.replenish", "REPLENISH: REPLANT CROPS UPON BREAKING");
    }


    @Comment("Enable replenish enchantment? Default: true")
    public static final Property<Boolean> ENABLED = newProperty("enchantments.replenish.enable", true);


    @Comment("Break not finished crops? Default: false")
    public static final Property<Boolean> BREAK_PARTIALLY_GROWN = newProperty("enchantments.replenish.breaknotfullygrown", false);


    @Comment("Make Hoe unbreakable if enchantment equipped? Default: false")
    public static final Property<Boolean> UNBREAKABLE_HOE = newProperty("enchantments.replenish.unbreakable", false);


    @Comment("Drop items directly to inventory? Default: false")
    public static final Property<Boolean> DROPS_TO_INVENTORY = newProperty("enchantments.replenish.dropstoinventory", false);


    @Comment("Which chance to get enchant? [ 1 to 99 ] Default: 20")
    public static final Property<Integer> ENCHANT_CHANCE = newProperty("enchantments.replenish.enchantmentchance", 20);


    @Comment({"Enchantmentname? Default: Replenish I", "NOTE: IF YOU CHANGE THIS, EXISTING HOE'S WILL BE USELESS"})
    public static final Property<String> ENCHANT_LORE_NAME = newProperty("enchantments.replenish.lore", "Replenish I");

}