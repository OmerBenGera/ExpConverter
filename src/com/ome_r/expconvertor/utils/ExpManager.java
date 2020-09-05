package com.ome_r.expconvertor.utils;

import com.ome_r.expconvertor.Main;
import org.bukkit.entity.Player;

public class ExpManager {

    private Player pl;

    public ExpManager(Player pl){
        this.pl = pl;
    }

    public int getTotalExp(){
        return getExpToLevel(pl.getLevel()) + getExpToNextLevel();
    }

    private int getExpToLevel(int level){
        if(Main.isOnePointSeven()){
            if(level <= 14) {
                return level * 17;
            }

            else if(level <= 29){
                return (int) ((1.5 * level * level) - (29.5 * level) + 360);
            }

            else{
                return (int) ((3.5 * level * level) - (151.5 * level) + 2220);
            }
        }

        else{
            if(level <= 16){
                return (level * level) + (6 * level);
            }

            else if(level <= 31){
                return (int) ((2.5 * level * level) - (40.5 * level) + 360);
            }

            else{
                return (int) ((4.5 * level * level) - (162.5 * level) + 2220);
            }
        }

    }

    private int getExpToNextLevel(){
        return Math.round(pl.getExp() * (getExpToLevel(pl.getLevel() + 1) - getExpToLevel(pl.getLevel())));
    }

}