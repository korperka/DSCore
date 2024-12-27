package hi.korperka.dscore.files;

import hi.korperka.dscore.files.config.Config;
import hi.korperka.dscore.files.serializable.ExtensionSector;
import hi.korperka.dscore.files.serializable.PositionsExtensionSector;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ExtensionsConfig implements Config {
    private ExtensionSector lighting = new ExtensionSector();
    private ExtensionSector plugins = new ExtensionSector();
    private PositionsExtensionSector positions = new PositionsExtensionSector();
    private ExtensionSector world = new ExtensionSector();
    private ExtensionSector blocks = new ExtensionSector();
    private ExtensionSector blockUpdate = new ExtensionSector();
}
