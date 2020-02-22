package com.example.jmemo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.jmemo.database.Memo
import com.example.jmemo.R
import com.example.jmemo.adapter.MemoGridRecycleAdapter
import com.example.jmemo.adapter.MemoLinearRecycleAdapter
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    private val realm = Realm.getDefaultInstance()
    private val GRIDTYPE = true
    private val LINEARTYPE = false
    private var currLayout = GRIDTYPE

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
            R.id.show_grid ->{
                setViewGridFromRealm()
                currLayout = GRIDTYPE
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
            GRIDTYPE-> setViewGridFromRealm()
            LINEARTYPE-> setViewLinearFromRealm()
        }
    }
    fun setViewGridFromRealm(){
        val realmResult = realm.where<Memo>().findAll().sort("lastDate", Sort.DESCENDING)
        memoListRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val adapter = MemoGridRecycleAdapter(realmResult, this)
        memoListRecyclerView.adapter = adapter
        realmResult.addChangeListener { _-> adapter.notifyDataSetChanged() }
    }
    fun setViewLinearFromRealm(){
        val realmResult = realm.where<Memo>().findAll().sort("lastDate", Sort.DESCENDING)
        memoListRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MemoLinearRecycleAdapter(realmResult, this)
        memoListRecyclerView.adapter = adapter
        realmResult.addChangeListener { _-> adapter.notifyDataSetChanged() }
    }
    fun setEventFab(){
        addMemoFab.setOnClickListener {
            //Anko 라이브러리
            //출처 : https://github.com/Kotlin/anko
            startActivity<EditActivity>()
        }
    }
}
