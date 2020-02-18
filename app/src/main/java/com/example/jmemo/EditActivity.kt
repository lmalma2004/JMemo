package com.example.jmemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.example.jmemo.R.id.deleteMenuItem
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.android.synthetic.main.fragment_photo.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.lang.Exception
import java.util.*

class EditActivity : UrlDialogFragment.OnFragmentInteractionListener, AppCompatActivity() {

    val realm = Realm.getDefaultInstance()
    val calendar: Calendar = Calendar.getInstance()
    public val addedImages: ArrayList<Image> = arrayListOf()
    private val REQUEST_IMAGE_CAPTURE = 2
    private val REQUEST_IMAGE_GALLERY = 1

    //사진 용량, 잘못된 url, 사진삭제

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setViewFromRealm()
        checkPermission()
    }

    fun setViewFromRealm(){
        val id = intent.getLongExtra("id", -1L)
        if(id == -1L)
            return
        val memo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        titleEditText.setText(memo.title)
        bodyEditText.setText(memo.body)
        setImageFromRealm(id)
    }
    fun sendTakePhotoIntent(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(takePictureIntent.resolveActivity(packageManager) != null)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }
    fun sendGalleyPhotoIntent(){
        val uri: Uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val intent: Intent = Intent(Intent.ACTION_PICK, uri)
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }
    fun isPermissionGALLERY(): Boolean {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        == PackageManager.PERMISSION_GRANTED)
            return true
        return false
    }
    fun isPermissionCAMERA(): Boolean {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED)
            return true
        return false
    }
    fun permissionGALLERY(){
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_IMAGE_GALLERY)
    }
    fun permissionCAMERA(){
        ActivityCompat.requestPermissions(this@EditActivity,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_IMAGE_CAPTURE)
    }
    fun checkPermission(){
        if(!isPermissionGALLERY()){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                //이전에 이미 권한이 거부되었을 때
                alert("사진 정보를 얻으려면 외부 저장소 권한이 필수로 필요합니다",
                    "권한이 필요한 이유"){
                    yesButton {
                        permissionGALLERY()
                    }
                }.show()
            }
            else{
                permissionGALLERY()
            }
        }
        if(!isPermissionCAMERA()){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
                //이전에 이미 권한이 거부되었을 때
                alert("사진 정보를 얻으려면 외부 저장소 권한이 필수로 필요합니다",
                    "권한이 필요한 이유"){
                    yesButton {
                        permissionCAMERA()
                    }
                }.show()
            }
            else{
                permissionCAMERA()
            }
        }
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
        //if(menu == null)
        //    return true;
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
                uriDialogFragment.show(supportFragmentManager, UrlDialogFragment.INPUT_URL_DIALOG)
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
                        val uri: Uri = data!!.data as Uri
                        val imageOfinputStream = contentResolver.openInputStream(uri)
                        if(imageOfinputStream == null)
                            return
                        val imageOfByteArray = inputStreamToByteArray(imageOfinputStream!!)
                        addImageByteArray(imageOfByteArray, id)
                        val newImage = addedImages.size - 1
                        setImageFromAddedImages(newImage)
                        imageOfinputStream.close()
                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                    return
                }
                REQUEST_IMAGE_CAPTURE ->{
                    val extras: Bundle = data!!.extras as Bundle
                    val imageOfBitmap: Bitmap = extras.get("data") as Bitmap
                    val imageOfByteArray = bitmapToByteArray(imageOfBitmap)
                    addImageByteArray(imageOfByteArray, id)
                    val newImage = addedImages.size - 1
                    setImageFromAddedImages(newImage)
                }
            }
        }
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
        for(image in 0..addedImages.size - 1){
            newMemo.images.add(addedImages[image])
        }

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
        updateMemo.body = bodyEditText.text.toString()
        for(image in 0..addedImages.size - 1){
            updateMemo.images.add(addedImages[image])
        }

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
    fun deleteImageFromRealm(image: Image, id: Long){
        realm.beginTransaction()
        val deleteMemo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        deleteMemo.images.remove(image)
        realm.commitTransaction()
        alert("사진이 삭제되었습니다."){
            yesButton { }
        }.show()
    }
    fun deleteImageFromAdded(image: Image){
        addedImages.remove(image)
        alert("사진이 삭제되었습니다."){
            yesButton { }
        }.show()
    }
    private fun setImageFromRealm(id: Long){
        val currMemo = realm.where<Memo>().equalTo("id", id).findFirst()!!
        val images = currMemo.images

        val fragments = ArrayList<Fragment>()
        if(images.size != 0){
            for(image in 0..images.size - 1){
                fragments.add(PhotoFragment.newInstance(images[image]!!.urlOfImage, images[image]!!.image, id, addedImages))
            }
        }
        val adapter = PagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        adapter.updateFragments(fragments)
        viewPager.adapter = adapter
    }
    private fun setImageFromAddedImages(image: Int){
        val fragments = ArrayList<Fragment>()
        if(addedImages.size != 0){
            fragments.add(PhotoFragment.newInstance(addedImages[image]!!.urlOfImage, addedImages[image]!!.image, -1L, addedImages))
        }
        if(viewPager.adapter != null) {
            val adapter = viewPager.adapter as PagerAdapter
            adapter.updateFragments(fragments)
            viewPager.adapter = adapter
        }
        else{
            val adapter = PagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
            adapter.updateFragments(fragments)
            viewPager.adapter = adapter
        }
    }
    private fun addImageByteArray(imageOfByteArray: ByteArray, id: Long){
        val newImage = Image(imageOfByteArray, "")
        addedImages.add(newImage)
        alert("사진이 추가되었습니다."){
            yesButton {}
        }.show()
    }
    private fun addImageUri(strOfUri: String){
        if(strOfUri == "")
            return
        val newImage = Image(byteArrayOf(), strOfUri)
        addedImages.add(newImage)
        alert(strOfUri + "의 URL이 추가되었습니다."){
            yesButton {}
        }.show()
    }
    private fun nextId(): Int{
        val maxId = realm.where<Memo>().max("id")
        if(maxId != null)
            return maxId.toInt() + 1
        return 0
    }
    override fun onFragmentInteraction(strOfUri: String) {
        val preSize = addedImages.size
        addImageUri(strOfUri)
        val currSize = addedImages.size
        if(preSize < currSize)
            setImageFromAddedImages(currSize - 1)
    }
}
