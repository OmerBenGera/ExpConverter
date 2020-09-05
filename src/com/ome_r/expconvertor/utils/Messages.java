package com.ome_r.expconvertor.utils;

import com.ome_r.expconvertor.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public enum Messages {

    NO_PERMISSION, COMMAND_USAGE, NOT_ENOUGH_EXP, NO_EXP, INVALID_NUMBER, CONVERT_SUCCEED, RELOAD_SUCCEED,
    RELOAD_FAILED, BOTTLE_NAME, BOTTLE_LORE;

    private String message;

    public void setMessage(String message) {
        this.message = ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage() {
        return message;
    }

    public String getMessage(Object... objects) {
        String msg = new String(message);
        int counter = 0;

        for(Object obj : objects) {
            msg = msg.replace("{" + counter + "}", obj.toString());
            counter++;
        }

        return msg;
    }

    public static void loadMessages(Main pl){
        File file = new File(pl.getDataFolder(), "messages.yml");
        if(!file.exists())
            pl.saveResource("messages.yml", false);

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        for(String str : cfg.getConfigurationSection("").getKeys(true)) {
            if (str.equals("NBT_SUPPORT"))
                ItemBuilder.isNBTSupport = cfg.getBoolean("NBT_SUPPORT");
            else if (!str.equals("BOTTLE_LORE"))
                Messages.valueOf(str).setMessage(translateColor(cfg.getString(str)));
            else {
                String lore = new String();
                for (String line : cfg.getStringList("BOTTLE_LORE"))
                    if (line.equals(""))
                        lore += "\n" + "§f";
                    else lore += "\n" + line;
                Messages.BOTTLE_LORE.setMessage(translateColor(lore).substring(1));
            }
        }
    }

    private static String translateColor(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
