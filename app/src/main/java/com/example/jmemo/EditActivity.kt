package com.example.jmemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.jmemo.R.id.deleteMenuItem
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import java.util.*

class EditActivity : AppCompatActivity() {

    val realm = Realm.getDefaultInstance()
    val calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        if(menu == null)
            return true;

        val deleteMemoItem = menu.findItem(deleteMenuItem)
        val id = intent.getLongExtra("id", -1L)
        if(id == -1L)
            deleteMemoItem.setVisible(false)
        else {
            val memo = realm.where<Memo>().equalTo("id", id).findFirst()!!
            titleEditText.setText(memo.title)
            bodyEditText.setText(memo.body)
            //imageView.setImage~~
            deleteMemoItem.setVisible(true)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = intent.getLongExtra("id", -1L)
        when(item?.itemId){
            R.id.cameraMenuItem ->{
                return true
            }
            R.id.albumMenuItem ->{
                return true
            }
            R.id.urlMenuItem ->{
                return true
            }
            R.id.saveMenuItem ->{
                if(id == -1L)
                    insertMemo()
                else
                    updateMemo(id)
                return true
            }
            R.id.deleteMenuItem ->{
                deleteMemo(id)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun insertMemo(){
        realm.beginTransaction()

        val newMemo = realm.createObject<Memo>(nextId())
        newMemo.title = titleEditText.text.toString()
        newMemo.date = calendar.timeInMillis
        newMemo.body = bodyEditText.text.toString()
        //newMemo.images =

        realm.commitTransaction()

        alert("메모가 추가되었습니다."){
            yesButton { finish() }
        }.show()
    }

    private fun updateMemo(id: Long){
        realm.beginTransaction()

        val updateMemo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        updateMemo.title = titleEditText.text.toString()
        updateMemo.date = calendar.timeInMillis

        realm.commitTransaction()

        alert("메모가 변경되었습니다"){
            yesButton { finish() }
        }.show()
    }

    private fun deleteMemo(id: Long){
        realm.beginTransaction()

        val deleteMemo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        deleteMemo.deleteFromRealm()
        realm.commitTransaction()

        alert("메모가 삭제되었습니다."){
            yesButton { finish() }
        }.show()
    }

    private fun nextId(): Int{
        val maxId = realm.where<Memo>().max("id")
        if(maxId != null)
            return maxId.toInt() + 1
        return 0
    }
}
