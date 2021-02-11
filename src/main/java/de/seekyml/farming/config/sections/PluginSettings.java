package de.seekyml.farming.config.sections;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;

import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public final class PluginSettings implements SettingsHolder {

    @Comment("# Color codes: https://minecraft.gamepedia.com/Formatting_codes ##")
    public static final Property<String> PREFIX = newProperty("general.prefix", "&8[&6&lADVANCEDFARMING&8]");


    public static final Property<String> NO_PERMISSION = newProperty("general.mesages.nopermissions", "&cYou don't have enough permissions to do that.");

}