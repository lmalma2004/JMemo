package com.example.jmemo.activity

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.bumptech.glide.Glide
import com.example.jmemo.*
import com.example.jmemo.R.id.deleteMenuItem
import com.example.jmemo.R.id.editConstraint
import com.example.jmemo.database.Memo
import com.example.jmemo.fragment.PhotoFragment
import com.example.jmemo.fragment.PhotoFragmentPagerAdapter
import com.example.jmemo.fragment.UrlDialogFragment
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.fragment_photo.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.io.File
import java.io.FileNotFoundException
import java.lang.Exception
import java.net.URL
import java.util.*

class EditActivity : UrlDialogFragment.OnUriDialogFragmentInteractionListener, AppCompatActivity() {

    private val realm = Realm.getDefaultInstance()
    private val calendar: Calendar = Calendar.getInstance()
    val addedImages: ArrayList<String> = arrayListOf()
    val deleteImages: ArrayList<String> = arrayListOf()
    private val REQUEST_IMAGE_CAPTURE = 2
    private val REQUEST_IMAGE_GALLERY = 1

    inner class JMetadataTask :AsyncTask<String, JMetaData, JMetaData>(){
        var url: String = ""
        var jMetaData :JMetaData? = JMetaData("", "")
        override fun doInBackground(vararg params: String?): JMetaData? {
            val jMetaData = getImageUrlFromUrl(url)
            return jMetaData
        }

        override fun onPostExecute(result: JMetaData?) {
            super.onPostExecute(result)
            if(result == null) {
                toast("URL 정보를 가져올 수 없습니다. 인터넷 연결을 확인하세요")
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setViewFromRealm()
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
                    toast("권한 거부 됨")
                return
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        val deleteMemoItem = menu!!.findItem(deleteMenuItem)
        val id = intent.getLongExtra("id", -1L)
        if(id == -1L)
            deleteMemoItem.setVisible(false)
        else
            deleteMemoItem.setVisible(true)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = intent.getLongExtra("id", -1L)

        when(item?.itemId){
            R.id.cameraMenuItem ->{
                if(!isPermissionCAMERA())
                    permissionCAMERA()
                else
                    sendTakePhotoIntent()
                return true
            }
            R.id.albumMenuItem ->{
                if(!isPermissionGALLERY())
                    permissionGALLERY()
                else
                    sendGalleyPhotoIntent()
                return true
            }
            R.id.urlMenuItem ->{
                val uriDialogFragment = UrlDialogFragment.getInstance()
                uriDialogFragment.show(supportFragmentManager, UrlDialogFragment.INPUT_URL_FROM_DIALOG)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val id = intent.getLongExtra("id", -1L)

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
                    try {
                        val imageOfBitmap: Bitmap = data!!.extras!!.get("data") as Bitmap
                        val filePath: File = ContextWrapper(applicationContext).getDir(
                            "Images",
                            Context.MODE_PRIVATE
                        )
                        val name = System.currentTimeMillis().toString() + ".jpg"
                        saveBitmapToJpeg(imageOfBitmap, filePath, name)
                        addImage(filePath.absolutePath + "/" + name)
                        setImageFromAddedImages(addedImages.size - 1)
                    } catch(e: Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    override fun onUriDialogFragmentInteraction(image: String) {
        //url 잘못된경우 처리
        if(!URLUtil.isValidUrl(image)){
            alert("유효하지 않은 URL 입니다."){
                yesButton {}
            }.show()
            return
        }
        val jMetaDataTask = JMetadataTask()
        jMetaDataTask.url = image
        jMetaDataTask.execute()

        //val preSize = addedImages.size
        //addImage(image)
        //val currSize = addedImages.size
        //if(preSize < currSize)
        //    setImageFromAddedImages(currSize - 1)
    }

    private fun sendTakePhotoIntent(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(takePictureIntent.resolveActivity(packageManager) != null)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
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

        alert(image + "의 URL이 추가되었습니다."){
            yesButton {}
        }.show()
    }
    private fun setMemo(memo: Memo){
        memo.title = titleEditText.text.toString()
        memo.date = calendar.timeInMillis
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
        realm.beginTransaction()
        val newMemo = realm.createObject<Memo>(nextId())
        setMemo(newMemo)
        realm.commitTransaction()

        alert("메모가 추가되었습니다."){
            yesButton { finish() }
        }.show()
    }
    private fun updateMemo(id: Long){
        realm.beginTransaction()
        val updateMemo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        setMemo(updateMemo)
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
            yesButton { finish () }
        }.show()
    }

    private fun setImageFromRealm(id: Long){
        val currMemo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        val images = currMemo.images
        val fragments = ArrayList<Fragment>()
        if(images.size != 0){
            for(image in 0..images.size - 1){
                fragments.add(PhotoFragment.newInstance(images[image]!!, id))
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
                PhotoFragment.newInstance(addedImages[image], -1L)
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
    private fun setViewFromRealm(){
        val id = intent.getLongExtra("id", -1L)
        if(id == -1L)
            return
        val memo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        titleEditText.setText(memo.title)
        bodyEditText.setText(memo.body)
        setImageFromRealm(id)
    }

    private fun nextId(): Int{
        val maxId = realm.where<Memo>().max("id")
        if(maxId != null)
            return maxId.toInt() + 1
        return 0
    }

    fun deleteImageFromAdded(image: String){
        deleteImages.add(image)
        val adapter = viewPager.adapter as PhotoFragmentPagerAdapter
        adapter.updateFragments()
        viewPager.adapter = adapter

        alert("사진이 삭제되었습니다."){
            yesButton { }
        }.show()
    }
}
