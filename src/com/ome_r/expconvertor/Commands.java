package com.ome_r.expconvertor;

import com.ome_r.expconvertor.utils.ExpManager;
import com.ome_r.expconvertor.utils.ItemBuilder;
import com.ome_r.expconvertor.utils.Messages;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Commands implements CommandExecutor {

    private Main plugin;

    public Commands(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("§cOnly players can perfrom this command.");
            return false;
        }

        Player p = (Player) sender;

        if(!p.hasPermission("expconvertor.use")){
            p.sendMessage(Messages.NO_PERMISSION.getMessage());
            return false;
        }

        if(args.length != 0 && args.length != 1){
            String arg = p.hasPermission("expconvertor.reload") ? " [reload]" : "";
            p.sendMessage(Messages.COMMAND_USAGE.getMessage("/xpbottle [#]" + arg));
            return false;
        }

        int totalExp = new ExpManager(p).getTotalExp(), exp = totalExp;

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("reload") && p.hasPermission("expconvertor.reload")){
                if(plugin.loadMessages())
                    p.sendMessage(Messages.RELOAD_SUCCEED.getMessage());
                else p.sendMessage(Messages.RELOAD_FAILED.getMessage());
                return false;
            }

            if(!isNumber(args[0])){
                p.sendMessage(Messages.INVALID_NUMBER.getMessage(args[0]));
                return false;
            }

            exp = Integer.valueOf(args[0]);
        }

        if(exp == 0){
            p.sendMessage(Messages.NO_EXP.getMessage());
            return false;
        }

        if(exp > totalExp){
            p.sendMessage(Messages.NOT_ENOUGH_EXP.getMessage());
            return false;
        }

        p.getInventory().addItem(getExpBottle(exp));

        p.setLevel(0);
        p.setExp(0);
        p.giveExp(totalExp - exp);

        p.sendMessage(Messages.CONVERT_SUCCEED.getMessage());

        return false;
    }

    private ItemStack getExpBottle(int exp){
        return new ItemBuilder(Material.EXP_BOTTLE)
                .withName(Messages.BOTTLE_NAME.getMessage())
                .withLore(Messages.BOTTLE_LORE.getMessage(exp).split("\n"))
                .withExpValue(exp).build();
    }

    private boolean isNumber(String str){
        try{
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}