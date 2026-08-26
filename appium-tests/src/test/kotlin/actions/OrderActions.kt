package actions

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import pages.MapPage
import pages.OrderHistoryPage

object OrderActions {
    /** Asserts each expected order price on the history screen ("29.70 €" - no tilde). */
    fun assertHistoryPrices(expected: Map<Int, String>) {
        OrderHistoryPage.title.waitFor(15)
        expected.forEach { (id, price) ->
            assertEquals(price, OrderHistoryPage.orderPrice(id).text, "price of order $id")
        }
    }

    fun assertHistoryRoutes(expected: Map<Int, String>) {
        OrderHistoryPage.title.waitFor(15)
        expected.forEach { (id, route) ->
            assertEquals(route, OrderHistoryPage.orderRoute(id).text, "route of order $id")
        }
    }

    /** Asserts the inline history load error (shown instead of the list; no retry control here). */
    fun assertHistoryError(expected: String) {
        OrderHistoryPage.title.waitFor(15)
        assertEquals(expected, OrderHistoryPage.errorLabel.text, "order history error")
    }

    fun assertOrderPrice(
        orderId: Int,
        expected: String,
    ) {
        OrderHistoryPage.title.waitFor(15)
        assertEquals(expected, OrderHistoryPage.orderPrice(orderId).text, "price of order $orderId")
    }

    fun assertOrderAbsent(orderId: Int) {
        OrderHistoryPage.title.waitFor(15)
        assertFalse(OrderHistoryPage.orderPrice(orderId).isPresent(), "order $orderId should be absent")
    }

    /**
     * MOB-1006: a ride completed during the session must show up in history as the newest order.
     *
     * "Newest" is asserted by rendered position rather than by list index, because the screen
     * exposes one test tag per order id and nothing that states the ordering. Comparing the y
     * coordinates of the new row and the previously-first row checks what the user actually sees:
     * a backend that appended instead of prepending would still render both rows, and only the
     * position tells them apart.
     */
    fun assertNewOrderOnTop(
        newOrderId: Int,
        previousFirstOrderId: Int,
        expectedRoute: String,
    ) {
        OrderHistoryPage.title.waitFor(15)
        val newOrderRoute = OrderHistoryPage.orderRoute(newOrderId).waitFor(15)
        val previousFirstRoute = OrderHistoryPage.orderRoute(previousFirstOrderId).waitFor(15)

        assertEquals(expectedRoute, newOrderRoute.text, "route of the completed order $newOrderId")
        assertTrue(
            newOrderRoute.location.y < previousFirstRoute.location.y,
            "order $newOrderId should render above order $previousFirstOrderId",
        )
    }

    fun returnToRideForm() {
        OrderHistoryPage.backButton.click()
        MapPage.destinationField.waitFor()
        MapPage.pullToRefresh.waitFor()
    }
}
