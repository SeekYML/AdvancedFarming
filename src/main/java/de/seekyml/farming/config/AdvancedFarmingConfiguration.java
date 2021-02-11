package de.seekyml.farming.config;

import ch.jalu.configme.SettingsManagerImpl;
import ch.jalu.configme.configurationdata.ConfigurationDataBuilder;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.resource.YamlFileResource;
import de.seekyml.farming.config.sections.PluginSettings;
import de.seekyml.farming.config.sections.enchants.ReplenishSettings;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Optional;

public final class AdvancedFarmingConfiguration extends SettingsManagerImpl {

    public AdvancedFarmingConfiguration(final Plugin plugin) {
        super(new YamlFileResource(new File(plugin.getDataFolder(), "config.yml").toPath()),

                ConfigurationDataBuilder.createConfiguration(PluginSettings.class, ReplenishSettings.class),

                new PlainMigrationService());
    }


    public <T> T get(final Property<T> property)
    {
        return Optional.ofNullable(getProperty(property)).orElse(property.getDefaultValue());
    }

}
