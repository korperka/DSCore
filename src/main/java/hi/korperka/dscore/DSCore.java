package hi.korperka.dscore;

import hi.korperka.dscore.extensions.ExtensionLoader;
import hi.korperka.dscore.files.ExtensionsConfig;
import hi.korperka.dscore.files.PropertiesConfig;
import hi.korperka.dscore.files.config.ConfigLoader;
import hi.korperka.dscore.logging.DSLogger;
import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import org.fusesource.jansi.AnsiConsole;

import java.nio.file.Path;
import java.util.Scanner;

public class DSCore {
    @Getter
    private final static Path rootPath = Path.of("");
    @Getter
    private final static EventNode<Event> eventNode = EventNode.all("ds_core");
    @Getter
    private static PropertiesConfig propertiesConfig;

    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        MinecraftServer server = MinecraftServer.init();

        propertiesConfig = ConfigLoader.initConfig(rootPath.resolve("server.properties"), PropertiesConfig.class);
        propertiesConfig.applyServerSettings();

        ExtensionLoader.setExtensionsConfig(ConfigLoader.initConfig(rootPath.resolve("extensions.conf"), ExtensionsConfig.class));

        MinecraftServer.getGlobalEventHandler().addChild(eventNode);

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        ExtensionLoader.load(instanceContainer);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> ExtensionLoader.unload(instanceContainer)));

        DSLogger.info("Starting Minecraft server...");
        server.start(propertiesConfig.getIp(), propertiesConfig.getPort());
//        CommandLoader.init();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            MinecraftServer.getCommandManager().executeServerCommand(input);
        }
    }
}