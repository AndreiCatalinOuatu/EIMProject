package com.example.healthMonitorEIM

import com.example.healthMonitorEIM.Model.BloodSugar
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

internal class BloodSugarRegisterActivityTest {
    private val testBloodSugarRegisterActivity: BloodSugarRegisterActivity =
        Mockito.mock(BloodSugarRegisterActivity::class.java)

    @Test
    fun addHeartRate() {
        val response = testBloodSugarRegisterActivity.addBloodSugar(BloodSugar("85", System.currentTimeMillis(), "test"))

        Assert.assertNotNull(response)
        Assert.assertNotEquals(response, "")
    }

    @Test
    fun getHeartRateLevel() {
        val response1 = testBloodSugarRegisterActivity.getBloodSugarLevel("114")
        assert(response1.first)
        assert(response1.second == "Glicemia este peste limita normala!")

        val response2 = testBloodSugarRegisterActivity.getBloodSugarLevel("92")
        assert(!response2.first)
        assert(response2.second == "Glicemia se afla in limite normale!")

        val response3 = testBloodSugarRegisterActivity.getBloodSugarLevel("65")
        assert(response3.first)
        assert(response3.second == "Glicemia este sub limita normala!")
    }
}