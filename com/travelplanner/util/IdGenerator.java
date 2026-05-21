package com.travelplanner.util;

import com.travelplanner.model.BaseEntity;

import java.util.List;

public final class IdGenerator {
    private IdGenerator() {
    }

    public static int generateNextId(List<? extends BaseEntity> entities) {
        return entities.stream()
                .mapToInt(BaseEntity::getId)
                .max()
                .orElse(0) + 1;
    }
}
