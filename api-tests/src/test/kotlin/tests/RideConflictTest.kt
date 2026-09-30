package tests

import client.RidesApi
import io.qameta.allure.AllureId
import io.qameta.allure.Feature
import model.ActiveRide
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import rule.ApiTestCase
import testdata.ApiTestData

@Feature("API: Ride conflicts")
class RideConflictTest : ApiTestCase() {
    @Test
    @DisplayName("A conflicting order leaves the original active ride untouched")
    @AllureId("2007")
    fun testConflictingOrderPreservesActiveRide() {
        val token = obtainToken()
        lateinit var firstRide: ActiveRide

        step("Order the Yellow tariff: it becomes the active ride") {
            val actual = RidesApi.create(token, ApiTestData.FROM, ApiTestData.TO, ApiTestData.YELLOW_TARIFF.id)
            assertThat(actual.statusCode).isEqualTo(201)
            firstRide = actual.body
        }
        step("A second order is rejected while a ride is already active") {
            val actual = RidesApi.create(token, ApiTestData.FROM, ApiTestData.TO, ApiTestData.TURQUOISE_TARIFF.id)
            assertThat(actual.statusCode).isEqualTo(409)
        }
        // The point of the repair: the original ride must still be active and unchanged. Cancelling
        // it here would destroy the very state under test, so the rejected order is followed by a
        // read, not a cleanup.
        step("The original ride is still active on its original tariff") {
            val actual = RidesApi.active(token)
            assertThat(actual.statusCode).isEqualTo(200)
            val activeRide = actual.body
            assertThat(activeRide.id).isEqualTo(firstRide.id)
            assertThat(activeRide.option.id).isEqualTo(firstRide.option.id)
        }
    }
}
