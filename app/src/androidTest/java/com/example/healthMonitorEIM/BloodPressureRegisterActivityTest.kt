package com.example.healthMonitorEIM

import com.example.healthMonitorEIM.Model.BloodPressure
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

internal class BloodPressureRegisterActivityTest {
    private val testBloodPressureRegisterActivity: BloodPressureActivity =
        Mockito.mock(BloodPressureActivity::class.java)

    @Test
    fun addHeartRate() {
        val response = testBloodPressureRegisterActivity.addBloodPressure(
            BloodPressure(
                "110",
                "75",
                System.currentTimeMillis(),
                "test"
            )
        )

        Assert.assertNotNull(response)
        Assert.assertNotEquals(response, "")
    }

    @Test
    fun getBloodPressureLevel() {
        val response1 = testBloodPressureRegisterActivity.getBloodPressureLevel("150", "70")
        assert(response1.first)
        assert(response1.second == "Tensiunea dvs este prea ridicata!")

        val response2 = testBloodPressureRegisterActivity.getBloodPressureLevel("102", "65")
        assert(!response2.first)
        assert(response2.second == "Tensiunea dvs este in limite normale!")

        val response3 = testBloodPressureRegisterActivity.getBloodPressureLevel("95", "55")
        assert(response3.first)
        assert(response3.second == "Tensiunea dvs este prea scazuta!")
    }
}