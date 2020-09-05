package com.ome_r.expconvertor.utils;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ItemBuilder {

    public static boolean isNBTSupport;
    public static String version;
    private static int expRow, expColumn;
    private int exp;
    private ItemStack is;
    private ItemMeta meta;

    public static boolean loadExpData(){
        if(!isNBTSupport){
            String[] lore = Messages.BOTTLE_LORE.getMessage().split("\n");

            for(int i = 0; i < lore.length; i++)
                if (lore[i].contains("{0}")) {
                    String[] split = lore[i].split(" ");
                    for (int j = 0; j < split.length; j++) {
                        if (split[j].contains("{0}")) {
                            expRow = i;
                            expColumn = j;
                            return true;
                        }
                    }
                }

            return false;
        }
        return true;
    }

    public ItemBuilder(Material type){
        this(type, 1);
    }

    public ItemBuilder(Material type, int amount){
        this(new ItemStack(type, amount));
    }

    public ItemBuilder(ItemStack is){
        this.is = is;
        this.meta = this.is.getItemMeta();
        this.exp = -1;
    }

    public ItemBuilder withName(String name){
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder withLore(String... lines){
        meta.setLore(Arrays.asList(lines));
        return this;
    }

    public ItemBuilder withExpValue(int exp){
        this.exp = exp;
        return this;
    }

    public ItemStack build(){
        is.setItemMeta(meta);

        if(exp >= 0 && isNBTSupport)
            setNBTExpValue(exp);

        return is;
    }

    public int getExpValue(){
        return isNBTSupport ? getNBTExpValue() : getLoreExpValue();
    }

    private int getLoreExpValue(){
        int exp = -1;

        if(!meta.hasLore())
            return exp;

        String expString = meta.getLore().get(expRow).split(" ")[expColumn];
        expString = ChatColor.stripColor(expString);

        if(!NumberUtils.isNumber(expString))
            return exp;

        exp = Integer.valueOf(expString);

        return exp;
    }

    private int getNBTExpValue() {
        int exp = -1;

        try {
            Object tag = getTag(), expValue = null;

            if(tag != null)
                expValue = tag.getClass().getMethod("getInt", String.class).invoke(tag, "EXP_VALUE");

            if(expValue != null)
                exp = Integer.valueOf(expValue.toString());

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return exp;
    }

    private void setNBTExpValue(int value) {
        try {
            Class tagClass = getNMSClass("NBTTagCompound");
            Object tag = getTag();

            if(tag == null)
                tag = tagClass.newInstance();

            tagClass.getMethod("setInt", String.class, int.class)
                    .invoke(tag, "EXP_VALUE", value);

            Object itemStack = getNMSStack();

            itemStack.getClass().getMethod("setTag", tagClass).invoke(itemStack, tag);

            Class<?> craftItemStack = getBukkitClass("inventory.CraftItemStack");
            is = (ItemStack) craftItemStack.getMethod("asBukkitCopy", itemStack.getClass())
                    .invoke(craftItemStack, itemStack);

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                InstantiationException e) {
            e.printStackTrace();
        }
    }

    private Object getNMSStack() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> craftItemStack = getBukkitClass("inventory.CraftItemStack");
        return craftItemStack.getMethod("asNMSCopy", ItemStack.class).invoke(craftItemStack, is);
    }

    private Object getTag() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object itemStack = getNMSStack();
        return itemStack.getClass().getMethod("getTag").invoke(itemStack);
    }

    private Class<?> getNMSClass(String name){
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Class<?> getBukkitClass(String name){
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
