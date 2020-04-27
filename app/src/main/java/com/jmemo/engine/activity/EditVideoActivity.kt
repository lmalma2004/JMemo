package com.jmemo.engine.activity

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.jmemo.engine.R
import com.jmemo.engine.database.Memo
import com.jmemo.engine.fragment.DeleteDialogFragment
import com.jmemo.engine.fragment.YouTubeDialogFragment
import com.jmemo.engine.widget.JMemoAppWidget
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit_video.*
import kr.co.prnd.YouTubePlayerView
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.Exception
import java.util.*

class EditVideoActivity : YouTubeDialogFragment.OnYouTubeDialogFragmentInteractionListener, AppCompatActivity() {
    private val realm = Realm.getDefaultInstance()
    private var deleteMenu: MenuItem?      = null
    private var saveMenu: MenuItem?        = null
    private var youtubeUrlMenu: MenuItem?  = null
    private val menuItems: ArrayList<MenuItem> = arrayListOf()
    private var addImage: String   = ""
    private var addVideoId: String = ""
    private val calendar: Calendar = Calendar.getInstance()
    private val YOUTUBE_URL        = "v="
    private val YOUTUBE_MOBILE_URL = ".be/"
    var id: Long = -1L //PrimaryKey

    inner class JMetaData(val url:String?, var imageUrl:String?, var youTubeVideoId: String?)
    inner class JMetadataTask(val url:String = "") : Runnable{
        override fun run() {
            val jMetaData = getVideoIdFromUrl(url)
            if(jMetaData == null){
                toast("URL 정보를 가져올 수 없어요. 인터넷 연결을 확인해주세요.")
                return
            }
            if(jMetaData.youTubeVideoId == null)
                jMetaData.youTubeVideoId = ""
            if(jMetaData.youTubeVideoId != "") {
                if(jMetaData.imageUrl != "")
                    addImage = jMetaData.imageUrl!!
                addVideoId = jMetaData.youTubeVideoId!!
                runOnUiThread {
                    val newYoutubePlayerView = YouTubePlayerView(this@EditVideoActivity)
                    val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                    setLayoutParams(layoutParams)
                    newYoutubePlayerView.layoutParams = layoutParams
                    editVideoConstraint.addView(newYoutubePlayerView)
                    editVideoConstraint.removeView(youtubePlayerView)
                    newYoutubePlayerView.play(addVideoId)
                }
            }
        }
        private fun getVideoIdFromUrl(url: String): JMetaData?{
            try{
                if(url.contains("v=")) {
                    val strArray = url.split("v=")
                    val metadata = JMetaData(url, null, strArray[1].subSequence(0, 11).toString())
                    val doc: Document = Jsoup.connect(url).ignoreContentType(true).get()
                    if(doc.select("meta[property=og:image]").size != 0)
                        metadata.imageUrl = doc.select("meta[property=og:image]").get(0).attr("content")
                    return metadata
                }
                else{
                    val strArray = url!!.split(".be/")
                    val metadata = JMetaData(url, null, strArray[1])
                    val doc: Document = Jsoup.connect(url).ignoreContentType(true).get()
                    if(doc.select("meta[property=og:image]").size != 0)
                        metadata.imageUrl = doc.select("meta[property=og:image]").get(0).attr("content")
                    return metadata
                }
            }catch (e: Exception){
                e.printStackTrace()
                return null
            }
        }
        private fun getRealDp(num: Int) : Int{
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, num.toFloat(), getResources().getDisplayMetrics()).toInt()
        }
        private fun setLayoutParams(layoutParams: ConstraintLayout.LayoutParams){
            layoutParams.marginStart = getRealDp(16)
            layoutParams.topMargin = getRealDp(8)
            layoutParams.marginEnd = getRealDp(16)
            layoutParams.bottomMargin = getRealDp(2)
            layoutParams.bottomToTop = lastDateVideoTextView.id
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.topToBottom = bodyVideoEditText.id
            layoutParams.horizontalBias = 0.78f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent.getLongExtra("id", -1L)
        setContentView(R.layout.activity_edit_video)
        setSupportActionBar(toolbarVideo)
        setViewFromRealm()
    }
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
    override fun onStop(){
        super.onStop()
        realm.removeAllChangeListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        val editMenu = menu!!.findItem(R.id.editMenuItem)
        deleteMenu = menu!!.findItem(R.id.deleteMenuItem)
        saveMenu = menu!!.findItem(R.id.saveMenuItem)
        youtubeUrlMenu = menu!!.findItem(R.id.youtubeUrlMenuItem)
        menu!!.findItem(R.id.cameraMenuItem).setVisible(false)
        menu!!.findItem(R.id.albumMenuItem).setVisible(false)
        menu!!.findItem(R.id.urlMenuItem).setVisible(false)

        if(id == -1L) {
            setEditable(true)
            editMenu.setVisible(false)
            youtubeUrlMenu!!.setVisible(true)
            val tmpDeleteMenuItem = deleteMenu as MenuItem
            tmpDeleteMenuItem.setVisible(false)
        }
        else{
            setEditable(false)
            for(menuItem in 0..menu!!.size() - 1){
                val currMenu = menu.getItem(menuItem)
                menuItems.add(currMenu)
                currMenu.setVisible(false)
            }
            editMenu.setVisible(true)
            val tmpDeleteMenuItem = deleteMenu as MenuItem
            tmpDeleteMenuItem.setVisible(true)
        }
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.editMenuItem ->{
                setEditable(true)
                if(id == -1L)
                    deleteMenu!!.setVisible(false)
                else
                    deleteMenu!!.setVisible(true)
                saveMenu!!.setVisible(true)
                youtubeUrlMenu!!.setVisible(true)
                item.setVisible(false)
            }
            R.id.youtubeUrlMenuItem ->{
                val youTubeDialogFragment = YouTubeDialogFragment.getInstance()
                youTubeDialogFragment.show(supportFragmentManager, YouTubeDialogFragment.INPUT_URL_FROM_DIALOG)
            }
            R.id.saveMenuItem ->{
                if(id == -1L)
                    insertMemo()
                else
                    updateMemo(id)
            }
            R.id.deleteMenuItem ->{
                val deleteDialogFragment = DeleteDialogFragment.getInstance()
                deleteDialogFragment.show(supportFragmentManager, DeleteDialogFragment.DELETE_DIALOG)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onYouTubeDialogFragmentInteraction(url: String) {
        if(!url.contains(YOUTUBE_URL) && !url.contains(YOUTUBE_MOBILE_URL)){
            alert("유효하지 않은 YOUTUBE 주소에요."){
                yesButton {}
            }.show()
            return
        }
        val jMetaDataTask = JMetadataTask(url)
        val workerThread = Thread(jMetaDataTask)
        workerThread.start()
    }

    private fun setViewFromRealm(){
        if(id == -1L) {
            return
        }
        val memo = realm.where<Memo>().equalTo("id", id).findFirstAsync()!!
        memo?.addChangeListener(RealmChangeListener { listener ->
            if(memo.isValid) {
                titleVideoEditText.setText(memo.title)
                bodyVideoEditText.setText(memo.body)
                if (id != -1L) {
                    lastDateVideoTextView.setText(
                        DateFormat.format(
                            "마지막 수정: yyyy년 MM월 dd일",
                            memo.lastDate
                        )
                    )
                    initDateVideoTextView.setText(
                        DateFormat.format(
                            "만든 날짜: yyyy년 MM월 dd일",
                            memo.initDate
                        )
                    )
                    lastDateVideoTextView.visibility = View.VISIBLE
                    initDateVideoTextView.visibility = View.VISIBLE
                }
                setVideoFromRealm(id)
            }
        })
    }
    private fun setVideoFromRealm(id: Long){
        val memo = realm.where<Memo>().equalTo("id", id).findFirstAsync()!!
        memo?.addChangeListener(RealmChangeListener { listener ->
            if(memo.isValid) {
                youtubePlayerView.visibility = View.VISIBLE
                youtubePlayerView.play(memo.youTubeVideoId)
            }
        })
    }

    private fun setMemo(memo: Memo){
        memo.title = titleVideoEditText.text.toString()
        memo.lastDate = calendar.timeInMillis
        if(memo.initDate == 0L)
            memo.initDate = memo.lastDate
        memo.body = bodyVideoEditText.text.toString()
        if(addVideoId != "")
            memo.youTubeVideoId = addVideoId
        if(addImage != "") {
            if(memo.images.size == 0)
                memo.images.add(addImage)
            else
                memo.images[0] = addImage
        }
    }
    private fun insertMemo(){
        if(addVideoId == ""){
            toast("입력한 영상이 없어 메모를 저장하지 않았어요.")
            finish()
            return
        }
        realm.executeTransaction { realmTransaction ->
            val newMemo = realmTransaction.createObject<Memo>(nextId())
            setMemo(newMemo)
            alert("메모가 추가되었어요."){
                yesButton { finish() }
            }.show().setCancelable(false)
        }
    }
    private fun updateMemo(id: Long){
        realm.executeTransaction { realmTransaction ->
            val updateMemo = realmTransaction.where<Memo>().equalTo("id", id).findFirstAsync()!!
            setMemo(updateMemo)
            alert("메모가 변경되었어요."){
                yesButton { finish() }
            }.show().setCancelable(false)
            widgetUpdate()
        }
    }

    fun deleteMemo(id: Long){
        realm.executeTransaction { realmTransaction ->
            val deleteMemo = realmTransaction.where<Memo>().equalTo("id", id).findFirstAsync()!!
            deleteMemo.deleteFromRealm()
            alert("메모가 삭제되었어요."){
                yesButton { finish() }
            }.show().setCancelable(false)
        }
    }
    fun widgetUpdate() {
        val widgetIntent = Intent(this, JMemoAppWidget::class.java)
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        this.sendBroadcast(widgetIntent)
    }

    private fun nextId(): Int{
        val maxId = realm.where<Memo>().max("id")
        if(maxId != null)
            return maxId.toInt() + 1
        return 0
    }
    private fun setEditable(editable: Boolean){
        if(editable){
            titleVideoEditText.isEnabled = true
            bodyVideoEditText.isEnabled = true
            setViewFromRealm()
        }
        else{
            titleVideoEditText.isEnabled = false
            bodyVideoEditText.isEnabled = false
        }
    }
}