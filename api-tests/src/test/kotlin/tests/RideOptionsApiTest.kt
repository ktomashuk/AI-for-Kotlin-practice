package tests

import client.RidesApi
import io.qameta.allure.AllureId
import io.qameta.allure.Feature
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import rule.ApiTestCase
import testdata.ApiTestData

@Feature("API: Ride options")
class RideOptionsApiTest : ApiTestCase() {
    @Test
    @DisplayName("Ride options return the seeded tariffs in order")
    @AllureId("2004")
    fun testRideOptionsReturnSeededTariffs() {
        val token = obtainToken()

        step("Request the tariffs for the seeded route") {
            val actual = RidesApi.options(token, ApiTestData.FROM, ApiTestData.TO)
            assertThat(actual.statusCode).isEqualTo(200)

            // Compared as (name, price) pairs rather than whole objects: the assertion then fails
            // on the thing the lesson is about - which tariffs are offered and what they cost -
            // instead of on an unrelated field such as seats or availability.
            val actualTariffs = actual.body.options.map { it.name to it.priceCents }
            val expectedTariffs = ApiTestData.TARIFFS.map { it.name to it.priceCents }

            // containsExactlyElementsOf, not containsExactlyInAnyOrder: the catalog order is part
            // of the contract, because the app renders the list in the order the API returns.
            assertThat(actualTariffs).containsExactlyElementsOf(expectedTariffs)
        }
    }
}
