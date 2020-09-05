package com.ome_r.expconvertor;

import com.ome_r.expconvertor.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Listeners implements Listener {

    private Set<UUID> shouldWait = new HashSet<>();
    private Map<UUID, Integer> exp = new HashMap<>();
    private Main plugin;

    public Listeners(Main plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void onExpBottle(ExpBottleEvent e) {
        UUID uuid = e.getEntity().getUniqueId();
        if(exp.containsKey(uuid)) {
            e.setExperience(exp.get(uuid));
            exp.remove(uuid);
        }
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack inHand = p.getItemInHand();

        if(inHand.getType() != Material.EXP_BOTTLE) return;

        final int expLevel = new ItemBuilder(inHand).getExpValue();

        if(expLevel != -1){
            if(shouldWait.contains(p.getUniqueId())){
                e.setCancelled(true);
                return;
            }

            shouldWait.add(p.getUniqueId());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e){}
                    for(Entity en : p.getNearbyEntities(2, 2, 2)) {
                        if (en instanceof ThrownExpBottle) {
                            exp.put(en.getUniqueId(), expLevel);
                            break;
                        }
                    }
                    try {
                        Thread.sleep(200);
                    }catch (InterruptedException e){}
                    shouldWait.remove(p.getUniqueId());
                }
            }).start();
        }

    }

}