package com.belafon.world.objectsMemory.itemsMemory;
import java.util.ArrayList;
import java.util.List;

import com.belafon.world.objectsMemory.ObjectsMemoryCell;

import java.util.Collections;


public class ToolItemsMemory {
    public final List<ObjectsMemoryCell<Integer>> quality = Collections.synchronizedList(new ArrayList<>());
}
