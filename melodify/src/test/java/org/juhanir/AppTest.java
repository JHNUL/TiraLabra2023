package org.juhanir;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    @DisplayName("App tester")
    void doesImportantTests() {
        assertEquals(2, 1 + 1, "Did the math check out?");
    }
}
