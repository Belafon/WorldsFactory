package com.belafon.world.mobileClient.game;

import com.belafon.world.mobileClient.game.behaviours.BehavioursFragment;
import com.belafon.world.mobileClient.game.bodyStats.CreatureStatisticsFragment;
import com.belafon.world.mobileClient.game.inventory.fragments.InventoryFragment;
import com.belafon.world.mobileClient.game.maps.SurroundingPlacesFragment;
import com.belafon.world.mobileClient.game.visibles.VisiblesFragment;
import com.belafon.world.mobileClient.gameActivity.GameActivity;
import com.belafon.world.mobileClient.gameActivity.StoryFragment;
import com.belafon.world.mobileClient.gameActivity.ViewFragment;

/**
 * List of all main fragments in the game,
 * that can be reachible from the main menu of the game.
 */
public class Fragments {
    public final CreatureStatisticsFragment bodyStatistics;
    public final SurroundingPlacesFragment surroundingPlaces;
    public final VisiblesFragment visibles;
    public final BehavioursFragment behaviours;
    public final ViewFragment view;
    public final Stats stats;
    public final InventoryFragment inventory;
    public final StoryFragment story;

    GameActivity gameActivity;

    public Fragments(Stats stats, GameActivity gameActivity, Game game) {
        this.stats = stats;
        this.gameActivity = gameActivity;
        this.bodyStatistics = new CreatureStatisticsFragment(stats.body);
        this.surroundingPlaces = stats.maps.getSurroundingPlacesFragment(null, gameActivity.getGameFragmentContainerID());
        this.visibles = new VisiblesFragment(stats.visibles);
        this.view = new ViewFragment();
        this.behaviours = stats.behaviours.getNewBehaviourListFragment(gameActivity.getGameFragmentContainerID());
        this.inventory = new InventoryFragment(stats.inventory, gameActivity.getGameFragmentContainerID(), this);
        this.story = new StoryFragment(game.story);
    }
}
