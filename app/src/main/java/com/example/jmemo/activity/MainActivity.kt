package com.example.jmemo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.jmemo.database.Memo
import com.example.jmemo.database.MemoGridAdapter
import com.example.jmemo.R
import io.realm.Realm
import io.realm.Sort
import io.realm.kotlin.where

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {
    private val realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setViewFromRealm()
        setEventGridView()
        setEventFab()
    }
    override fun onRestart() {
        super.onRestart()
        setViewFromRealm()
        setEventGridView()
        setEventFab()
    }
    override fun onResume() {
        super.onResume()
        setViewFromRealm()
        setEventGridView()
        setEventFab()
    }
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    fun setViewFromRealm(){
        val realmResult = realm.where<Memo>().findAll().sort("date", Sort.DESCENDING)
        val adapter = MemoGridAdapter(realmResult)
        memoListGridView.adapter = adapter
        realmResult.addChangeListener { _-> adapter.notifyDataSetChanged() }
    }
    fun setEventGridView(){
        memoListGridView.setOnItemClickListener { parent, view, position, id ->
            startActivity<EditActivity>("id" to id)
        }
    }
    fun setEventFab(){
        addMemoFab.setOnClickListener {
            //Anko 라이브러리
            //출처 : https://github.com/Kotlin/anko
            startActivity<EditActivity>()
        }
    }
}
