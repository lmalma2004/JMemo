package com.example.jmemo.activity

import android.content.Intent
import androidx.core.view.get
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.jmemo.R
import com.example.jmemo.adapter.MemoGridRecycleAdapter
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.item_memo_stagger_grid.view.*
import org.jetbrains.anko.contentView
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class clearAndAddTestMainActivity {
    @Rule
    @JvmField
    var mainActivityTestRule = IntentsTestRule(MainActivity::class.java)
    @Test
    fun startTest() {
        //메모비우기
        while(mainActivityTestRule.activity.memoListRecyclerView.childCount != 0) {
            Thread.sleep(500)
            onView(withId(R.id.memoListRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition<MemoGridRecycleAdapter.ViewHolderOfGridRecycleView>(
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

        //메모추가하기
        val testTitleText = "테스트입니다.(제목)"
        val testBodyText = "테스트입니다.(본문)"
        val memoAddCnt = 10
        for(memoNum in 0..memoAddCnt) {
            Thread.sleep(500)
            onView(withId(R.id.addMemoFab)).perform(click())
            Thread.sleep(500)
            onView(withId(R.id.titleEditText)).perform(ViewActions.replaceText(testTitleText + memoNum.toString()))
            onView(withId(R.id.bodyEditText)).perform(ViewActions.replaceText(testBodyText + memoNum.toString()))
            onView(withId(R.id.saveMenuItem)).perform(click())
            onView(withText("확인")).inRoot(isDialog()).check(matches(isDisplayed()))
                .perform(click())
        }

        //메모확인하기
        for(memoNum in 0..memoAddCnt) {
            Thread.sleep(500)
            onView(withId(R.id.memoListRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition<MemoGridRecycleAdapter.ViewHolderOfGridRecycleView>(
                    memoNum,
                    click()
                )
            )
            Thread.sleep(500)
            onView(withId(R.id.titleEditText))
                .check(matches(withText(testTitleText + (memoAddCnt - memoNum).toString())))
            onView(withId(R.id.bodyEditText))
                .check(matches(withText(testBodyText + (memoAddCnt - memoNum).toString())))
            pressBack()
        }

    }
}

