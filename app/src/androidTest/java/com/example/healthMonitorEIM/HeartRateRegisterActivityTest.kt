package com.example.healthMonitorEIM

import com.example.healthMonitorEIM.Model.HeartRate
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.Mockito.mock

class HeartRateRegisterActivityTest {
    private val testHearRateRegisterActivity: HeartRateRegisterActivity = mock(HeartRateRegisterActivity::class.java)

    @Test
    fun addHeartRate() {
        val response = testHearRateRegisterActivity.addHeartRate(HeartRate("120", System.currentTimeMillis(), "test"))

        assertNotNull(response)
        assertNotEquals(response, "")
    }

    @Test
    fun getHeartRateLevel() {
        val response1 = testHearRateRegisterActivity.heartRateLevel("120")
        assert(response1.first)
        assert(response1.second == "Pulsul este peste limita normala!")

        val response2 = testHearRateRegisterActivity.heartRateLevel("90")
        assert(!response2.first)
        assert(response2.second == "Pulsul se afla in limite normale!")

        val response3 = testHearRateRegisterActivity.heartRateLevel("45")
        assert(response3.first)
        assert(response3.second == "Pulsul este sub limita normala!")
    }
}