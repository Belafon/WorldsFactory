package com.belafon.world.mobileClient.game;

import com.belafon.world.mobileClient.game.behaviours.Behaviours;
import com.belafon.world.mobileClient.game.bodyStats.BodyStats;
import com.belafon.world.mobileClient.game.inventory.Inventory;
import com.belafon.world.mobileClient.game.maps.PlayersMaps;
import com.belafon.world.mobileClient.game.visibles.Visibles;

public class Stats {
    private Game game;

    public synchronized Game getGame(){
        return game;
    }

    public synchronized void setGame(Game game){
        this.game = game;
    }
    public final BodyStats body = new BodyStats();
    public final PlayersMaps maps = new PlayersMaps();
    public final Inventory inventory = new Inventory();
    public final Visibles visibles = new Visibles();
    public final Behaviours behaviours = new Behaviours();
}
