package com.example.healthMonitorEIM

import com.example.healthMonitorEIM.Model.OxygenSaturation
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

internal class OxygenSaturationRegisterActivityTest {
    private val testOxygenSaturationRegisterActivity: OxygenSaturationRegisterActivity =
        Mockito.mock(OxygenSaturationRegisterActivity::class.java)

    @Test
    fun addHeartRate() {
        val response = testOxygenSaturationRegisterActivity.addOxygenSaturation(OxygenSaturation("114", System.currentTimeMillis(), "test"))

        Assert.assertNotNull(response)
        Assert.assertNotEquals(response, "")
    }

    @Test
    fun getHeartRateLevel() {
        val response1 = testOxygenSaturationRegisterActivity.getSPO2level("70")
        assert(response1.first)
        assert(response1.second == "Saturatia este sub limita normala!")

        val response2 = testOxygenSaturationRegisterActivity.getSPO2level("97")
        assert(!response2.first)
        assert(response2.second == "Saturatia este in limite normale!")

    }
}