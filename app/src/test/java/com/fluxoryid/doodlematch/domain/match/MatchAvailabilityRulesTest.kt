package com.fluxoryid.doodlematch.domain.match

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchAvailabilityRulesTest {

    @Test
    fun `match unavailable below the minimum completed count`() {
        assertFalse(MatchAvailabilityRules.isMatchAvailable(0))
        assertFalse(MatchAvailabilityRules.isMatchAvailable(1))
    }

    @Test
    fun `match available at or above the minimum completed count`() {
        assertTrue(MatchAvailabilityRules.isMatchAvailable(2))
        assertTrue(MatchAvailabilityRules.isMatchAvailable(5))
    }
}
