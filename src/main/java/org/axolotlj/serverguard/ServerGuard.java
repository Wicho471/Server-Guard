package org.axolotlj.serverguard;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import org.axolotlj.serverguard.command.ServerGuardCommand;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ServerGuard.MODID)
public class ServerGuard
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "serverguard";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    
    private static boolean isDedicatedServer;

    public ServerGuard()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }
    
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        isDedicatedServer = event.getServer().isDedicatedServer();

        if (!isDedicatedServer) {
            LOGGER.warn("[ServerGuard] Mod disabled on integrated server.");
            return;
        }

        LOGGER.info("[ServerGuard] Starting on dedicated server.");
    }

    
    private void onRegisterCommands(RegisterCommandsEvent event) {
        ServerGuardCommand.register(event.getDispatcher());
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
    
    public static boolean isDedicatedServer() {
		return isDedicatedServer;
	}
    
}
