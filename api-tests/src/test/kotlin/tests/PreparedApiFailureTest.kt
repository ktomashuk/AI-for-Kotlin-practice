package tests

import client.OrdersApi
import io.qameta.allure.AllureId
import io.qameta.allure.Feature
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import rule.ApiTestCase
import testdata.ApiTestData

@Feature("API: Order history")
class PreparedApiFailureTest : ApiTestCase() {
    @Test
    @DisplayName("Order history returns a successful status with the seeded orders")
    @AllureId("2006")
    fun testOrderHistoryReturnsSuccessfulStatus() {
        val token = obtainToken()

        step("Order history answers 200 with exactly the seeded orders") {
            val actual = OrdersApi.orders(token)
            assertThat(actual.statusCode).isEqualTo(200)
            // Sized against the fixture rather than a literal, so seeding changes surface here as
            // a data change instead of a silently wrong magic number.
            assertThat(actual.body.orders).hasSize(ApiTestData.SEEDED_ORDERS.size)
        }
    }
}
