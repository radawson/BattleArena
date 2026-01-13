package org.clockworx.battlearena.module.hologram.fancyholograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.TextHologramData;
import net.kyori.adventure.text.Component;
import org.clockworx.battlearena.competition.LiveCompetition;
import org.clockworx.battlearena.feature.PluginFeature;
import org.clockworx.battlearena.feature.hologram.Hologram;
import org.clockworx.battlearena.feature.hologram.HologramFeature;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FancyHologramsFeature extends PluginFeature<HologramFeature> implements HologramFeature {
    private final List<Hologram> holograms = new ArrayList<>();

    public FancyHologramsFeature() {
        super(FancyHologramsPlugin.get().getPlugin());
    }

    @Override
    public Hologram createHologram(LiveCompetition<?> competition, Location location, Component... lines) {
        de.oliver.fancyholograms.api.hologram.Hologram impl = FancyHologramsPlugin.get().getHologramManager().create(new TextHologramData(UUID.randomUUID().toString(), location));
        impl.getData().setPersistent(false);

        FancyHologram hologram = new FancyHologram(competition, impl);
        hologram.setLines(lines);
        FancyHologramsPlugin.get().getHologramManager().addHologram(impl);

        this.holograms.add(hologram);
        return hologram;
    }

    @Override
    public void removeHologram(Hologram hologram) {
        this.holograms.remove(hologram);

        FancyHologram fancyHologram = (FancyHologram) hologram;
        FancyHologramsPlugin.get().getHologramManager().removeHologram(fancyHologram.getImpl());
    }

    @Override
    public List<Hologram> getHolograms() {
        return List.copyOf(this.holograms);
    }
}
