package mctmods.immersivefabrication.core.registration;

import mctmods.immersivefabrication.ImmersiveFabrication;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import java.nio.file.Path;

public final class IFabWiresPack {
    public static final String FOLDER = "wires_data";

    private IFabWiresPack() {}

    public static void register(IEventBus bus) { bus.addListener(IFabWiresPack::addPack); }

    private static void addPack(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA) { return; }
        Path path = ModList.get().getModFileById(ImmersiveFabrication.MODID).getFile().findResource(FOLDER);
        Pack pack = Pack.readMetaAndCreate(ImmersiveFabrication.MODID + ":" + FOLDER, Component.literal("IFab wires data"), true, id -> new PathPackResources(id, path, true), PackType.SERVER_DATA, Pack.Position.TOP, PackSource.BUILT_IN);
        event.addRepositorySource(accepter -> accepter.accept(pack));
    }
}
