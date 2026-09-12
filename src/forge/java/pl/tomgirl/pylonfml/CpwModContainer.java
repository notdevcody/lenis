package pl.tomgirl.pylonfml;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

@SuppressWarnings("unused")
public class CpwModContainer extends DummyModContainer {
    public CpwModContainer() {
        this(new ModMetadata());
    }

    public CpwModContainer(ModMetadata md) {
        super(md);
        md.modId = "pylon";
        md.name = "Pylon";
        md.description = "LWJGL3 compatibility layer for legacy Minecraft.";
        md.url = "https://github.com/notdevcody/pylon";
        String version = getClass().getPackage().getImplementationVersion();
        md.version = version == null ? "dev" : version;
        md.authorList.add("notdevcody");
        md.logoFile = "/assets/pylon/icon.png";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }
}
