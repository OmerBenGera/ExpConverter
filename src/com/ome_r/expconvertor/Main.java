package com.ome_r.expconvertor;

import com.ome_r.expconvertor.utils.ItemBuilder;
import com.ome_r.expconvertor.utils.Messages;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public class Main extends JavaPlugin{

    private static boolean isOnePointSeven;

    @Override
    public void onEnable() {
        loadCommand();
        loadListener();
        loadMessages();
        ItemBuilder.version = getServer().getClass().getPackage().getName().split("\\.")[3];

        isOnePointSeven = ItemBuilder.version.contains("1_7");

        if(ItemBuilder.isNBTSupport && isOnePointSeven){
            getLogger().log(Level.INFO, "Found out that NBT_Support is true but your server's version is 1.7");
            getLogger().log(Level.INFO, "NBT_Support isn't supported on 1.7 for now, make sure you change it.");
            getServer().getPluginManager().disablePlugin(this);
        }

    }

    private void loadCommand(){
        getLogger().log(Level.INFO, "Loading commands...");
        getCommand("xpbottle").setExecutor(new Commands(this));
    }

    private void loadListener(){
        getLogger().log(Level.INFO, "Loading events...");
        getServer().getPluginManager().registerEvents(new Listeners(this), this);
    }

    public boolean loadMessages(){
        getLogger().log(Level.INFO, "Loading messages...");
        Messages.loadMessages(this);

        if(!ItemBuilder.loadExpData()){
            getLogger().log(Level.INFO, "Error while loading messages: couldn't find {0} in lore!");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }

        return true;
    }

    public static boolean isOnePointSeven(){
        return isOnePointSeven;
    }

}