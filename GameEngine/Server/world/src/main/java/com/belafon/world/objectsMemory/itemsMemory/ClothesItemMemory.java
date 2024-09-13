package com.belafon.world.objectsMemory.itemsMemory;

import java.util.ArrayList;
import java.util.List;

import com.belafon.world.objectsMemory.ObjectsMemoryCell;

import java.util.Collections;

public class ClothesItemMemory {
    public final List<ObjectsMemoryCell<Integer>> dirty = Collections.synchronizedList(new ArrayList<>());
}
