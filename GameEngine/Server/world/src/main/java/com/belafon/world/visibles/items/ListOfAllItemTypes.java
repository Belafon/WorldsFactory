package com.belafon.world.visibles.items;

import java.util.Hashtable;
import java.util.Map;

import com.belafon.world.time.Time;
import com.belafon.world.visibles.items.typeItem.ClothesTypeItem;
import com.belafon.world.visibles.items.typeItem.FoodTypeItem;
import com.belafon.world.visibles.items.typeItem.QuestTypeItem;
import com.belafon.world.visibles.items.typeItem.SpaceTypeItem;
import com.belafon.world.visibles.items.typeItem.ToolTypeItem;

/**
 * Setup all types of items in the world
 */
public class ListOfAllItemTypes{
    
    /**
     * Setup all types of items in the world
     */
    public static void setUpItems(){
        setUpTool();
        setUpFood();
        setUpQuest();
        setUpClothes();
        setUpSpaceItems();
    }


    public static Map<NamesOfSpaceItemTypes, SpaceTypeItem> spaceItems = new Hashtable<>(); 

    public enum NamesOfSpaceItemTypes{
        leather_bag,
        back_space
    }
    
    private static void setUpSpaceItems(){
        spaceItems.put(NamesOfSpaceItemTypes.leather_bag, new SpaceTypeItem(NamesOfSpaceItemTypes.leather_bag, 320, 0, 35, "Huge leather bag", 300)); // speedOfDecay will be 0 after 1 month
        spaceItems.put(NamesOfSpaceItemTypes.back_space, new SpaceTypeItem(NamesOfSpaceItemTypes.back_space, 0, 0, 0, "Back space", 300)); // speedOfDecay will be 0 after 1 month
    }


    public static Map<NamesOfFoodItemTypes, FoodTypeItem> foodTypes = new Hashtable<>(); 
    public enum NamesOfFoodItemTypes{
        apple
    }

    private static void setUpFood(){
        foodTypes.put(NamesOfFoodItemTypes.apple, new FoodTypeItem(NamesOfFoodItemTypes.apple, 120, 1, 15, "Kulaté jablko", 25, Time.monthsToTicks(1) / 100)); // speedOfDecay will be 0 after 1 month
    }


    public static Map<NamesOfClothesItemTypes, ClothesTypeItem> clothesTypes = new Hashtable<>(); 
    public enum NamesOfClothesItemTypes{
    }
    private static void setUpClothes(){
        
    }


    public static Map<NamesOfQuestItemTypes, QuestTypeItem> questTypes = new Hashtable<>(); 
    public enum NamesOfQuestItemTypes{
        coin
    }
    private static void setUpQuest(){
        questTypes.put(NamesOfQuestItemTypes.coin, new QuestTypeItem(NamesOfQuestItemTypes.coin, 1, 1, 15, "Mince"));
    }



    public static Map<NamesOfToolItemTypes, ToolTypeItem> ToolTypes = new Hashtable<>(); 
    public enum NamesOfToolItemTypes{
    }
    private static void setUpTool(){
        
    }
}
