package com.belafon.world.objectsMemory.itemsMemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.belafon.world.calendar.events.EventItemChange;
import com.belafon.world.objectsMemory.ObjectsMemoryCell;

public class FoodItemsMemory {
    public final List<ObjectsMemoryCell<Integer>> freshness = Collections.synchronizedList(new ArrayList<>());
    public final List<ObjectsMemoryCell<Integer>> warm = Collections.synchronizedList(new ArrayList<>());
    public final List<ObjectsMemoryCell<EventItemChange>> changeFreshness = Collections.synchronizedList(new ArrayList<>());
    public final List<ObjectsMemoryCell<EventItemChange>> changeWarm = Collections.synchronizedList(new ArrayList<>());
}
