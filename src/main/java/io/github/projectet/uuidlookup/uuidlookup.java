package io.github.projectet.uuidlookup;

import io.github.projectet.uuidlookup.command.LookupCommand;
import io.github.projectet.uuidlookup.util.HTTPGet;
import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class uuidlookup implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "uuidlookup";
    public static final String MOD_NAME = "UUIDLookup";

    @Override
    public void onInitialize() {
        LookupCommand.init();
        log(Level.INFO, "Initializing");
        //TODO: Initializer
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

}