package com.jrtserver.util;

import java.util.LinkedList;
import java.util.Queue;

public final class IDGenerator {
    private static int id = 1;
    private static final Queue<Integer> reusableIds = new LinkedList<Integer>();

    public static Integer nextId() {
        if (!reusableIds.isEmpty()) {
            return reusableIds.remove();
        } else {
            return id++;
        }
    }

    public static void addReusableId(Integer id) {
        reusableIds.add(id);
    }
}
