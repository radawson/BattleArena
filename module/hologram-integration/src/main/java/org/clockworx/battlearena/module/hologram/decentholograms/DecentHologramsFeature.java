package org.clockworx.battlearena.module.hologram.decentholograms;

import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import net.kyori.adventure.text.Component;
import org.clockworx.battlearena.competition.LiveCompetition;
import org.clockworx.battlearena.feature.PluginFeature;
import org.clockworx.battlearena.feature.hologram.Hologram;
import org.clockworx.battlearena.feature.hologram.HologramFeature;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DecentHologramsFeature extends PluginFeature<HologramFeature> implements HologramFeature {
    private final List<Hologram> holograms = new ArrayList<>();

    public DecentHologramsFeature() {
        super(DecentHologramsAPI.get().getPlugin());
    }

    @Override
    public Hologram createHologram(LiveCompetition<?> competition, Location location, Component... lines) {
        eu.decentsoftware.holograms.api.holograms.Hologram impl = new eu.decentsoftware.holograms.api.holograms.Hologram(UUID.randomUUID().toString(), location);
        impl.setSaveToFile(false);

        DecentHologramsAPI.get().getHologramManager().registerHologram(impl);

        DecentHologram hologram = new DecentHologram(competition, impl);
        hologram.setLines(lines);

        this.holograms.add(hologram);
        return hologram;
    }

    @Override
    public void removeHologram(Hologram hologram) {
        this.holograms.remove(hologram);

        DecentHologram decentHologram = (DecentHologram) hologram;
        DecentHologramsAPI.get().getHologramManager().removeHologram(decentHologram.getImpl().getName());
    }

    @Override
    public List<Hologram> getHolograms() {
        return this.holograms;
    }
}
