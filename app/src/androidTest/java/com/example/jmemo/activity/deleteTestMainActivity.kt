package com.example.jmemo.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.jmemo.R
import com.example.jmemo.adapter.MemoGridRecyclerAdapter
import kotlinx.android.synthetic.main.content_main.*
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class deleteTestMainActivity {
    @Rule
    @JvmField
    var mainActivityTestRule = IntentsTestRule(MainActivity::class.java)
    @Test
    fun startTest() {
        //메모 삭제 테스트
        while(mainActivityTestRule.activity.memoListRecyclerView.childCount != 0) {
            Thread.sleep(50)
            onView(withId(R.id.memoListRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition<MemoGridRecyclerAdapter.ViewHolderOfGridRecycleView>(
                    0,
                    click()
                )
            )
            onView(withId(R.id.deleteMenuItem)).perform(click())
            onView(withText("확인")).inRoot(isDialog()).check(matches(isDisplayed()))
                .perform(click())
            onView(withText("확인")).inRoot(isDialog()).check(matches(isDisplayed()))
                .perform(click())
        }
    }
}

