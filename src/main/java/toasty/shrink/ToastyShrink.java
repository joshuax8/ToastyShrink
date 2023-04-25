package toasty.shrink;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.io.FileWriter;
import java.io.BufferedWriter;

public class ToastyShrink implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("toastyshrink");
    CommandDispatcher<ServerCommandSource> dispatcher;
    @Override
    public void onInitialize() {
        LOGGER.info("ToastyShrink init!");
        File createConfigFile = new File("config/toastyshrink.cfg");
        if (!createConfigFile.exists()) {
            ToastyShrink.createConfigFile(createConfigFile);
        }
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            registerCommands(dispatcher);
        });
    }
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        
        LiteralArgumentBuilder<ServerCommandSource> myCommand = LiteralArgumentBuilder.<ServerCommandSource>literal("toastyshrink")
                .requires((source) -> source.hasPermissionLevel(2))
                    .executes((context) -> {
                            ToastyShrink.run(context);
                            return 1;
                        });

        dispatcher.register(myCommand);
    }
    
    public static int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource serverCommandSource = context.getSource();
        MinecraftServer server = serverCommandSource.getMinecraftServer();
        CommandManager commandManager = server.getCommandManager();
        File configFile = new File("config/toastyshrink.cfg");
        List<String> configLines = ToastyShrink.readLinesFromFile(configFile);
        for (String configFileString : configLines) {
            commandManager.execute(serverCommandSource, configFileString);
        }
        LOGGER.info("ToastyShrink started!");
        return 1;
    }

    public static List<String> readLinesFromFile(File configFile) {
        List<String> lines = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void createConfigFile(File configFile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            writer.write("say No ToastyShrink commands configured!\n");
            writer.write("say Replace these lines in the config file with your commands (without the leading \\)\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}