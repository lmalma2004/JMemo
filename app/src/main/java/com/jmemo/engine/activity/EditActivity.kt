package com.jmemo.engine.activity

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.URLUtil
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.jmemo.engine.*
import com.jmemo.engine.R.id.deleteMenuItem
import com.jmemo.engine.R.id.editMenuItem
import com.jmemo.engine.widget.JMemoAppWidget
import com.jmemo.engine.database.Memo
import com.jmemo.engine.fragment.PhotoFragment
import com.jmemo.engine.adapter.PhotoFragmentPagerAdapter
import com.jmemo.engine.fragment.DeleteDialogFragment
import com.jmemo.engine.fragment.UrlDialogFragment
import com.jmemo.engine.fragment.YouTubeDialogFragment
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Exception
import java.util.*

class EditActivity : UrlDialogFragment.OnUriDialogFragmentInteractionListener, AppCompatActivity() {

    private val REQUEST_IMAGE_CAPTURE = 2
    private val REQUEST_IMAGE_GALLERY = 1
    private val realm = Realm.getDefaultInstance()
    private val calendar: Calendar = Calendar.getInstance()
    private val addedImages: ArrayList<String> = arrayListOf()
    private val deleteImages: ArrayList<String> = arrayListOf()
    private val menuItems: ArrayList<MenuItem> = arrayListOf()
    private var deleteMenu: MenuItem? = null
    private var captureImagePath: String? = null
    var id: Long = -1L //PrimaryKey

    inner class JMetaData(url: String?, imageUrl: String?) {
        var url = url
        var imageUrl = imageUrl
    }
    inner class JMetadataTask :AsyncTask<String, JMetaData, JMetaData>(){
        var url: String = ""
        override fun doInBackground(vararg params: String?): JMetaData? {
            val jMetaData = getImageUrlFromUrl(url)
            return jMetaData
        }
        override fun onPostExecute(result: JMetaData?) {
            super.onPostExecute(result)
            if(result == null) {
                toast("URL 정보를 가져올 수 없어요. 인터넷 연결을 확인해주세요.")
                return
            }
            val preSize = addedImages.size
            if(result.imageUrl == "")
                addImage(result!!.url!!)
            else
                addImage(result!!.imageUrl!!)
            val currSize = addedImages.size
            if(preSize < currSize)
                setImageFromAddedImages(currSize - 1)
        }
        private fun getImageUrlFromUrl(url: String?): JMetaData?{
            try{
                val metadata = JMetaData(url, "")
                val doc: Document = Jsoup.connect(url).ignoreContentType(true).get()
                if(doc.select("meta[property=og:image]").size != 0)
                    metadata.imageUrl = doc.select("meta[property=og:image]").get(0).attr("content")
                return metadata
            }catch (e: Exception){
                e.printStackTrace()
                return null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent.getLongExtra("id", -1L)
        setContentView(R.layout.activity_edit)
        setSupportActionBar(toolbar2)
        setViewFromRealm(false)
        setViewPagerMarginPadding()
    }
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_IMAGE_GALLERY->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED)
                    toast("권한이 거부 되었어요.")
            }
            REQUEST_IMAGE_CAPTURE->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED)
                    toast("권한이 거부 되었어요.")
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        val editMenu = menu!!.findItem(editMenuItem)
        deleteMenu = menu!!.findItem(deleteMenuItem)
        if(id == -1L) {
            setEditable(true)
            editMenu.setVisible(false)
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
                for(menuItem in 0..menuItems.size - 1){
                    menuItems[menuItem].setVisible(true)
                }
                if(id == -1L)
                    deleteMenu!!.setVisible(false)
                item.setVisible(false)
            }
            R.id.cameraMenuItem ->{
                if(!isPermissionCAMERA())
                    permissionCAMERA()
                else
                    sendTakePhotoIntent()
            }
            R.id.albumMenuItem ->{
                if(!isPermissionGALLERY())
                    permissionGALLERY()
                else
                    sendGalleyPhotoIntent()
            }
            R.id.urlMenuItem ->{
                val uriDialogFragment = UrlDialogFragment.getInstance()
                uriDialogFragment.show(supportFragmentManager, UrlDialogFragment.INPUT_URL_FROM_DIALOG)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_OK){
            when(requestCode){
                REQUEST_IMAGE_GALLERY ->{
                    try{
                        addImage(data!!.data.toString())
                        setImageFromAddedImages(addedImages.size - 1)
                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                    return
                }
                REQUEST_IMAGE_CAPTURE ->{
                    val file = File(captureImagePath)
                    var image: Uri? = null
                    try{
                        image = Uri.fromFile(file)
                    } catch (e: FileNotFoundException){
                        e.printStackTrace()
                    } catch (e: IOException){
                        e.printStackTrace()
                    }
                    addImage(image.toString())
                    setImageFromAddedImages(addedImages.size - 1)
                }
            }
        }
    }
    override fun onUriDialogFragmentInteraction(url: String) {
        if(!URLUtil.isValidUrl(url)){
            alert("유효하지 않은 URL 이에요."){
                yesButton {}
            }.show()
            return
        }
        val jMetaDataTask = JMetadataTask()
        jMetaDataTask.url = url
        jMetaDataTask.execute()
    }

    private fun sendTakePhotoIntent(){
        val name = System.currentTimeMillis().toString() + ".jpg"
        val filePath: File = ContextWrapper(applicationContext).getDir(
            "Images",
            Context.MODE_PRIVATE
        )
        captureImagePath = filePath.absolutePath + name

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uri = FileProvider.getUriForFile(this, "com.example.jmemo.fileprovider", File(captureImagePath))
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        if(takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }
    private fun sendGalleyPhotoIntent(){
        val uri: Uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val intent: Intent = Intent(Intent.ACTION_PICK, uri)
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    private fun isPermissionGALLERY(): Boolean {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED)
            return true
        return false
    }
    private fun isPermissionCAMERA(): Boolean {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED)
            return true
        return false
    }
    private fun permissionGALLERY(){
        ActivityCompat.requestPermissions(this@EditActivity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_IMAGE_GALLERY)
    }
    private fun permissionCAMERA(){
        ActivityCompat.requestPermissions(this@EditActivity,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_IMAGE_CAPTURE)
    }

    private fun addImage(image: String){
        if(image == "")
            return
        addedImages.add(image)

        alert(image + "의 사진이 추가되었어요."){
            yesButton {}
        }.show()
    }
    fun deleteImage(image: String){
        deleteImages.add(image)
        val adapter = viewPager.adapter as PhotoFragmentPagerAdapter
        adapter.updateFragments()
        viewPager.adapter = adapter

        alert("사진이 삭제되었어요."){
            yesButton { }
        }.show()
    }

    private fun setMemo(memo: Memo){
        memo.title = titleEditText.text.toString()
        memo.lastDate = calendar.timeInMillis
        if(memo.initDate == 0L)
            memo.initDate = memo.lastDate
        memo.body = bodyEditText.text.toString()
        for(image in 0..addedImages.size - 1){
            memo.images.add(addedImages[image])
        }
        for(deleteImage in 0..deleteImages.size - 1){
            for(image in 0..memo.images.size - 1){
                if(memo.images[image] == deleteImages[deleteImage]) {
                    memo.images.removeAt(image)
                    break
                }
            }
        }
    }
    private fun insertMemo(){
        if(titleEditText.text.toString() == "" &&
                bodyEditText.text.toString() == "" &&
                addedImages.size == 0 &&
                deleteImages.size ==0){
            toast("입력한 내용이 없어 메모를 저장하지 않았어요.")
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
        widgetUpdate()
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
    fun widgetUpdate(){
        val widgetIntent = Intent(this, JMemoAppWidget::class.java)
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        this.sendBroadcast(widgetIntent)
    }

    private fun setViewFromRealm(deleteButtonVisible: Boolean){
        if(id == -1L)
            return
        val memo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        titleEditText.setText(memo.title)
        bodyEditText.setText(memo.body)
        if(id != -1L){
            lastDateTextView.setText(DateFormat.format("마지막 수정: yyyy년 MM월 dd일", memo.lastDate))
            initDateTextView.setText(DateFormat.format("만든 날짜: yyyy년 MM월 dd일", memo.initDate))
            lastDateTextView.visibility = View.VISIBLE
            initDateTextView.visibility = View.VISIBLE
        }
        setImageFromRealm(id, deleteButtonVisible)
    }
    private fun setImageFromRealm(id: Long, deleteButtonVisible: Boolean){
        val currMemo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        val images = currMemo.images
        val fragments = ArrayList<Fragment>()
        if(images.size != 0){
            for(image in 0..images.size - 1){
                fragments.add(PhotoFragment.newInstance(images[image]!!, id, deleteButtonVisible))
            }
        }
        val adapter = PhotoFragmentPagerAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        adapter.insertFragments(fragments)
        viewPager.adapter = adapter
    }
    private fun setImageFromAddedImages(image: Int){
        val fragments = ArrayList<Fragment>()
        if(addedImages.size != 0){
            fragments.add(
                PhotoFragment.newInstance(addedImages[image], image-1L, true)
            )
        }
        //이미 사진들이 있는 경우
        if(viewPager.adapter != null) {
            val adapter = viewPager.adapter as PhotoFragmentPagerAdapter
            adapter.insertFragments(fragments)
            viewPager.adapter = adapter
        }
        else{
            val adapter = PhotoFragmentPagerAdapter(
                supportFragmentManager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            )
            adapter.insertFragments(fragments)
            viewPager.adapter = adapter
        }
    }

    private fun nextId(): Int{
        val maxId = realm.where<Memo>().max("id")
        if(maxId != null)
            return maxId.toInt() + 1
        return 0
    }
    private fun setViewPagerMarginPadding(){
        val dpValue = 54
        val d = resources.displayMetrics.density
        val margin = dpValue * d
        viewPager.setPadding(margin.toInt(), 0, margin.toInt(), 0)
        viewPager.pageMargin = margin.toInt() / 2
    }
    private fun setEditable(editable: Boolean){
        if(editable){
            titleEditText.isEnabled = true
            bodyEditText.isEnabled = true
            setViewFromRealm(true)
        }
        else{
            titleEditText.isEnabled = false
            bodyEditText.isEnabled = false
        }
    }

}

