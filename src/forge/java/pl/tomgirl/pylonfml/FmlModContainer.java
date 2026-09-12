package pl.tomgirl.pylonfml;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

@SuppressWarnings("unused")
public class FmlModContainer extends DummyModContainer {
    public FmlModContainer() {
        this(new ModMetadata());
    }

    public FmlModContainer(ModMetadata md) {
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
