package com.example.jmemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.jmemo.R.id.deleteMenuItem
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.lang.Exception
import java.util.*

class EditActivity : AppCompatActivity() {

    val realm = Realm.getDefaultInstance()
    val calendar: Calendar = Calendar.getInstance()
    private val REQUEST_IMAGE_CAPTURE = 2
    private val REQUEST_IMAGE_GALLERY = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        checkPermission()
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
                if(!isPermissionCAMERA()) {
                    permissionCAMERA()
                    //if(isPermissionCAMERA())
                    //    sendTakePhotoIntent()
                }
                else
                    sendTakePhotoIntent()
                return true
            }
            R.id.albumMenuItem ->{
                if(!isPermissionGALLERY()) {
                    permissionGALLERY()
                    //if(isPermissionGALLERY())
                    //    sendGalleyPhotoIntent()
                }
                else
                    sendGalleyPhotoIntent()
                return true
            }
            R.id.urlMenuItem ->{
                val udf = UrlDialogFragment.getInstance()
                udf.show(supportFragmentManager, UrlDialogFragment.INPUT_URL_DIALOG)
                //arrayList<String> uris 에 있는 uri들을 imageView에 뿌려주기
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
        if(resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_GALLERY){
            when(requestCode){
                REQUEST_IMAGE_GALLERY ->{
                    try{
                        val uri: Uri = data!!.data as Uri
                        val inputStream = contentResolver.openInputStream(uri)
                        val image = BitmapFactory.decodeStream(inputStream)
                        if(inputStream != null)
                            inputStream.close()
                        //imageView.setImageBitmap(image)
                    } catch (e: Exception){

                    }
                    return
                }
                REQUEST_IMAGE_CAPTURE ->{
                    val extras: Bundle = data!!.extras as Bundle
                    val image: Bitmap = extras.get("data") as Bitmap
                    //imageView.setImageBitmap(image)
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
        updateMemo.body = bodyEditText.text.toString()
        //updateMemo.images =

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
