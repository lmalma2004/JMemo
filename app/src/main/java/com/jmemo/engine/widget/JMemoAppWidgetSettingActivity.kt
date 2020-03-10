package com.jmemo.engine.widget

import android.appwidget.AppWidgetManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jmemo.engine.R
import com.jmemo.engine.database.Memo
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.content_main.*

class JMemoAppWidgetSettingActivity : AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()
    private val appWidgetId = intent?.extras?.getInt(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_j_memo_app_widget_setting)
        setViewStaggeredGridFromRealmWidget()
    }

    fun setViewStaggeredGridFromRealmWidget(){
        val realmResult = realm.where<Memo>().findAll().sort("lastDate", Sort.DESCENDING)
        memoListRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val adapter = MemoGridRecyclerAdapterWidget(realmResult, this)
        memoListRecyclerView.adapter = adapter
        realmResult.addChangeListener { _->
            val adapter = memoListRecyclerView.adapter
            adapter!!.notifyDataSetChanged()
        }
        //realmResult.addChangeListener { _-> adapter.notifyDataSetChanged() }
    }
}
