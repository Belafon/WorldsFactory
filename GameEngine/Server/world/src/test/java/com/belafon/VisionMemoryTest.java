package com.belafon;

import org.junit.Test;

import com.belafon.settings.TestingCreature;
import com.belafon.world.World;
import com.belafon.world.maps.place.ListOfAllTypesOfPlaces;
import com.belafon.world.visibles.items.ListOfAllItemTypes;
import com.belafon.world.visibles.resources.ListOfAllTypesOfResources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class VisionMemoryTest {
    @Test
    public void testVisibleObjectSpotted() {
        ListOfAllTypesOfResources.setUpAllResources();
        ListOfAllTypesOfPlaces.setUpAllTypesOfPlaces();
        ListOfAllItemTypes.setUpItems();

        World world = World.testingWorld();
        final TestingCreature creature_1 = new TestingCreature(world, "creature_1", world.maps.maps[0].places[0][0],
                "appearenceTest", 100);
        final TestingCreature creature_2 = new TestingCreature(world, "creature_2", world.maps.maps[0].places[0][0],
                "appearenceTest", 100);
        world.creatures.add(creature_1);
        world.creatures.add(creature_2);

        creature_1.addVisible(creature_2);
        creature_1.addVisible(creature_2);

        creature_1.getCurrentlyVisibleObjectSpotted(
                (visibles) -> assertTrue(visibles.contains(creature_2)));

        creature_2.getWatchers(
                watchers -> assertEquals(1, watchers.size()));

        creature_1.memory.getVisibleObjectSpotted(
                (visibles) -> {
                    assertTrue(
                            visibles.stream().anyMatch(memoryCell -> memoryCell.object() == creature_2));
                });

        creature_1.removeVisible(creature_2);

        creature_1.getCurrentlyVisibleObjectSpotted(
                (visibles) -> assertFalse(visibles.contains(creature_2)));

        creature_2.getWatchers(
                watchers -> assertEquals(0, watchers.size()));

        creature_1.memory.getVisibleObjectSpotted(
                (visibles) -> {
                    assertTrue(
                            visibles.stream().anyMatch(memoryCell -> memoryCell.object() == creature_2));
                });

    }
}
