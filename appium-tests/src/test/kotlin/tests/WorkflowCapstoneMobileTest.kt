package tests

import actions.ProfileActions
import io.qameta.allure.AllureId
import io.qameta.allure.Feature
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import rule.AppiumTestCase
import testdata.TestData

@Feature("Mobile: Workflow capstone")
class WorkflowCapstoneMobileTest : AppiumTestCase() {
    /**
     * MOB-1007. Persistence is only demonstrated by leaving the screen and coming back: a profile
     * that still shows the typed names without a reload proves nothing, because the fields would
     * hold them whether or not the save reached the backend.
     *
     * Each step attaches the screen state it ended on, so the evidence reads as: the form, then
     * the saved names with their confirmation, then the same names on a freshly reopened profile
     * with no confirmation left over.
     */
    @Test
    @DisplayName("Saved profile names survive leaving and reopening the profile")
    @AllureId("1007")
    fun testSavedProfileNamesPersistAcrossReopen() {
        step("Start on the map (authorized)") {
            map.awaitReady()
        }
        step("Open the profile from the drawer") {
            ProfileActions.open()
        }
        step("Save the new first and last name") {
            ProfileActions.saveName(TestData.PROFILE_FIRST_NAME, TestData.PROFILE_LAST_NAME)
        }
        step("Leave the profile") {
            ProfileActions.leave()
        }
        step("Reopen the profile: both names persisted, with no stale confirmation") {
            ProfileActions.open()
            ProfileActions.assertName(TestData.PROFILE_FIRST_NAME, TestData.PROFILE_LAST_NAME)
            ProfileActions.assertNoSaveConfirmation()
        }
    }
}
