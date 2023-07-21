package org.juhanir;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    @DisplayName("App tester")
    void doesImportantTests() {
        assertEquals(2, 1 + 1, "You broke math");
    }
}
