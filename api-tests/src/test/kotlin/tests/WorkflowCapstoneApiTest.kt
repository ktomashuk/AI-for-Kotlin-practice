package tests

import client.OrdersApi
import client.RidesApi
import io.qameta.allure.AllureId
import io.qameta.allure.Feature
import model.Order
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import rule.ApiTestCase
import testdata.ApiTestData

@Feature("API: Workflow capstone")
class WorkflowCapstoneApiTest : ApiTestCase() {
    @Test
    @DisplayName("Completing the same ride twice is rejected and leaves history intact")
    @AllureId("2010")
    fun testRepeatedCompletionIsRejectedAndHistoryPreserved() {
        val token = obtainToken()
        var rideId = 0
        lateinit var completedOrder: Order

        step("Order the Yellow tariff: a driver is found") {
            val actual = RidesApi.create(token, ApiTestData.FROM, ApiTestData.TO, ApiTestData.YELLOW_TARIFF.id)
            assertThat(actual.statusCode).isEqualTo(201)
            assertThat(actual.body.status).isEqualTo(ApiTestData.DRIVER_FOUND_STATUS)
            rideId = actual.body.id
        }
        step("Complete the ride: it becomes the newest order") {
            val actual = RidesApi.complete(token, rideId)
            assertThat(actual.statusCode).isEqualTo(200)
            assertThat(actual.body.ride.status).isEqualTo(ApiTestData.COMPLETED_STATUS)
            completedOrder = actual.body.order
        }
        // The capstone's point: completing an already-completed ride must be refused rather than
        // silently appending a duplicate order. A backend that re-ran the completion would still
        // answer 200 here, so the status assertion is what separates the two behaviours.
        step("Completing the same ride again is refused") {
            val actual = RidesApi.complete(token, rideId)
            assertThat(actual.statusCode).isNotEqualTo(200)
        }
        step("History still holds exactly one new order, newest first") {
            val actual = OrdersApi.orders(token)
            assertThat(actual.statusCode).isEqualTo(200)
            assertThat(actual.body.orders)
                .containsExactlyElementsOf(listOf(completedOrder) + ApiTestData.SEEDED_ORDERS)
        }
    }
}
