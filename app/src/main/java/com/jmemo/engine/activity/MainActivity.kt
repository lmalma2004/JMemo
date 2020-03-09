package com.jmemo.engine.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jmemo.engine.database.Memo
import com.jmemo.engine.R
import com.jmemo.engine.adapter.MemoGridRecyclerAdapter
import com.jmemo.engine.adapter.MemoLinearRecycleAdapter
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity

/*
* 라이브러리 출처
* 1. Anko [출처 : https://github.com/Kotlin/anko]
* 2. Glide [출처 : https://github.com/bumptech/glide]
* 3. jsoup [출처 : https://github.com/jhy/jsoup/]
* 4. Realm [출처 : https://github.com/realm]
*
* */
class MainActivity : AppCompatActivity() {
    private val realm = Realm.getDefaultInstance()
    private val STAGGERGRIDTYPE = 0
    private val LINEARTYPE      = 1
    private var currLayout = STAGGERGRIDTYPE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setView()
        setEventFab()
    }
    override fun onResume() {
        super.onResume()
        setView()
    }
    override fun onRestart() {
        super.onRestart()
        setView()
    }
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_staggergrid ->{
                setViewStaggeredGridFromRealm()
                currLayout = STAGGERGRIDTYPE
            }
            R.id.show_linear->{
                setViewLinearFromRealm()
                currLayout = LINEARTYPE
            }
        }
        return super.onOptionsItemSelected(item);
    }
    fun setView(){
        when(currLayout){
            STAGGERGRIDTYPE-> {
                setViewStaggeredGridFromRealm()
            }
            LINEARTYPE-> {
                setViewLinearFromRealm()
            }
        }
    }
    fun setViewStaggeredGridFromRealm(){
        val realmResult = realm.where<Memo>().findAll().sort("lastDate", Sort.DESCENDING)
        memoListRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val adapter = MemoGridRecyclerAdapter(realmResult, this)
        memoListRecyclerView.adapter = adapter
        realmResult.addChangeListener { _->
            val adapter = memoListRecyclerView.adapter
            adapter!!.notifyDataSetChanged()
        }
        //realmResult.addChangeListener { _-> adapter.notifyDataSetChanged() }
   }
    fun setViewLinearFromRealm(){
        val realmResult = realm.where<Memo>().findAll().sort("lastDate", Sort.DESCENDING)
        memoListRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MemoLinearRecycleAdapter(realmResult, this)
        memoListRecyclerView.adapter = adapter
        realmResult.addChangeListener { _->
            val adapter = memoListRecyclerView.adapter
            adapter!!.notifyDataSetChanged()
        }
        //realmResult.addChangeListener { _-> adapter.notifyDataSetChanged() }
   }
    fun setEventFab(){
        addMemoFab.setOnClickListener {
            startActivity<EditActivity>()
        }
    }
}