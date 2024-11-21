package com.example.demo;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StreamTest {

    private static final LocalDateTime DEFAULT_DATE = LocalDateTime.of(2024, 1, 1, 12, 0);

    @Test
    void testOldCodeThrowsExceptionWhenIndexOutOfBounds() {
        // Given
        List<Region> regions = new ArrayList<>(); // 빈 리스트
        LocalDateTime updateDateTime = LocalDateTime.of(2024, 1, 1, 0, 0);

        // When & Then
        assertThrows(IndexOutOfBoundsException.class, () -> {
            originCode(regions, updateDateTime);
        });
    }

    private LocalDateTime originCode(List<Region> regions, LocalDateTime updateDateTime) {
        return regions.get(0) == null ? updateDateTime : regions.get(0).defaultLastCutOffTime();
    }

    @Test
    void testNewCodeHandlesEmptyListGracefully() {
        // Given
        List<Region> regions = new ArrayList<>(); // 빈 리스트
        LocalDateTime updateDateTime = LocalDateTime.of(2024, 1, 1, 0, 0);

        // When
        LocalDateTime result = improveCode(regions, updateDateTime);

        // Then
        assertEquals(updateDateTime, result);
    }

    private LocalDateTime improveCode(List<Region> regions, LocalDateTime updateDateTime) {
        return regions.stream()
                .findFirst()
                .map(Region::defaultLastCutOffTime)
                .orElse(updateDateTime);
    }

    record Region(LocalDateTime defaultLastCutOffTime) { }

}
