package com.jmemo.engine.activity

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
import io.realm.Realm
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
    private var deleteMenu: MenuItem? = null
    private var saveMenu: MenuItem? = null
    private var youtubeUrlMenu: MenuItem? = null
    private val menuItems: ArrayList<MenuItem> = arrayListOf()
    private var addImage: String = ""
    private var addVideoId: String = ""
    private val calendar: Calendar = Calendar.getInstance()
    private val YOUTUBE_URL = "https://www.youtube.com/watch?v="
    private val YOUTUBE_MOBILE_URL ="https://youtu.be/"
    var id: Long = -1L //PrimaryKey

    inner class JMetaData(url:String?, imageUrl:String?, youTubeVideoId: String?) {
        var url = url
        var imageUrl = imageUrl
        var youTubeVideoId = youTubeVideoId
    }
    inner class JMetadataTask : AsyncTask<String, JMetaData, JMetaData>(){
        var url: String = ""
        override fun doInBackground(vararg params: String?): JMetaData? {
            val jMetaData = getVideoIdFromUrl(url)
            return jMetaData
        }
        override fun onPostExecute(result: JMetaData?) {
            super.onPostExecute(result)
            if(result == null) {
                toast("URL 정보를 가져올 수 없어요. 인터넷 연결을 확인해주세요.")
                return
            }
            if(result.youTubeVideoId == null)
                result.youTubeVideoId = ""
            if(result.youTubeVideoId != "") {
                if(result.imageUrl != "")
                    addImage = result.imageUrl!!
                addVideoId = result.youTubeVideoId!!
                val newYoutubePlayerView = YouTubePlayerView(this@EditVideoActivity)
                val layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                setLayoutParams(layoutParams)
                newYoutubePlayerView.layoutParams = layoutParams

                editVideoConstraint.addView(newYoutubePlayerView)
                editVideoConstraint.removeView(youtubePlayerView)
                newYoutubePlayerView.play(addVideoId)
            }
        }
        private fun getVideoIdFromUrl(url: String?): JMetaData?{
            try{
                if(url!!.contains("=")) {
                    val strArray = url!!.split("=")
                    val metadata = JMetaData(url, null, strArray[1])
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
        setContentView(R.layout.activity_edit_video)
        setSupportActionBar(toolbarVideo)
        id = intent.getLongExtra("id", -1L)
        setViewFromRealm(false)
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
    private fun setEditable(editable: Boolean){
        if(editable){
            titleVideoEditText.isEnabled = true
            bodyVideoEditText.isEnabled = true
            setViewFromRealm(true)
        }
        else{
            titleVideoEditText.isEnabled = false
            bodyVideoEditText.isEnabled = false
        }
    }

    private fun setViewFromRealm(deleteButtonVisible: Boolean){
        if(id == -1L) {
            return
        }
        val memo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        titleVideoEditText.setText(memo.title)
        bodyVideoEditText.setText(memo.body)
        if(id != -1L){
            lastDateVideoTextView.setText(DateFormat.format("마지막 수정: yyyy년 MM월 dd일", memo.lastDate))
            initDateVideoTextView.setText(DateFormat.format("만든 날짜: yyyy년 MM월 dd일", memo.initDate))
            lastDateVideoTextView.visibility = View.VISIBLE
            initDateVideoTextView.visibility = View.VISIBLE
        }
        setVideoFromRealm(id, deleteButtonVisible)
    }

    private fun setVideoFromRealm(id: Long, deleteButtonVisible: Boolean){
        val memo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        youtubePlayerView.visibility = View.VISIBLE
        youtubePlayerView.play(memo.youTubeVideoId)
    }
    private fun insertMemo(){
        if(addVideoId == ""){
            toast("입력한 영상이 없어 메모를 저장하지 않았어요.")
            finish()
            return
        }
        realm.beginTransaction()
        val newMemo = realm.createObject<Memo>(nextId())
        setMemo(newMemo)
        realm.commitTransaction()

        alert("메모가 추가되었어요."){
            yesButton { finish() }
        }.show().setCancelable(false)
    }
    private fun updateMemo(id: Long){
        realm.beginTransaction()
        val updateMemo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        setMemo(updateMemo)
        realm.commitTransaction()

        alert("메모가 변경되었어요."){
            yesButton { finish() }
        }.show().setCancelable(false)
    }
    fun deleteMemo(id: Long){
        realm.beginTransaction()
        val deleteMemo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        deleteMemo.deleteFromRealm()
        realm.commitTransaction()

        alert("메모가 삭제되었어요."){
            yesButton { finish() }
        }.show().setCancelable(false)
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
    override fun onYouTubeDialogFragmentInteraction(url: String) {
        if(!url.contains(YOUTUBE_URL) && !url.contains(YOUTUBE_MOBILE_URL)){
            alert("유효하지 않은 YOUTUBE 주소에요."){
                yesButton {}
            }.show()
            return
        }
        val jMetaDataTask = JMetadataTask()
        jMetaDataTask.url = url
        jMetaDataTask.execute()
    }

    private fun nextId(): Int{
        val maxId = realm.where<Memo>().max("id")
        if(maxId != null)
            return maxId.toInt() + 1
        return 0
    }

}


/*
private var player : SimpleExoPlayer? = null
private var playWhenReady = true
private var currentWindow = 0
private var playbackPosition = 0L
private fun initializePlayer(){
    if(player == null){
        player = ExoPlayerFactory.newSimpleInstance(applicationContext)
        exoPlayerView.player = player
        //컨트롤러 없애기
        exoPlayerView.useController = true
        //사이즈 조절
        //or RESIZE_MODE_FILL
        exoPlayerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
    }
    val sample = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    val mediaSource = buildMediaSource(Uri.parse(sample))
    player!!.prepare(mediaSource, true, false)
    player!!.playWhenReady = playWhenReady
}
private fun buildMediaSource(uri : Uri): MediaSource{
    val userAgent = Util.getUserAgent(this, "blackJin")
    if(uri.lastPathSegment!!.contains("mp3") || uri.lastPathSegment!!.contains("mp4")){
        return ExtractorMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent)).createMediaSource(uri)
    }
    else if(uri.lastPathSegment!!.contains("m3u8")){
        //com.google.android.exoplayer:exoplayer-hls 확장 라이브러리를 빌드 해야 합니다.
        return HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent)).createMediaSource(uri)
    }
    else{
        return ExtractorMediaSource.Factory(DefaultDataSourceFactory(this, userAgent)).createMediaSource(uri)
    }
}
private fun releasePlayer(){
    if(player != null){
        playbackPosition = player!!.contentPosition
        currentWindow = player!!.currentWindowIndex
        playWhenReady = player!!.playWhenReady

        exoPlayerView.player = null
        player!!.release()
        player = null
    }
}
*/