package com.example.jmemo


import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_photo.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_URI_IMAGE = "uri"
private const val ARG_BYTEARRAY_IMAGE = "byteArray"
/**
 * A simple [Fragment] subclass.
 * Use the [PhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    val realm = Realm.getDefaultInstance()
    private var uri: String? = null
    private var byteArray: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uri = it.getString(ARG_URI_IMAGE)
            byteArray = it.getByteArray(ARG_BYTEARRAY_IMAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(uri == ""){
            Glide.with(this).asBitmap().load(byteArray).into(imageView)
        }
        else{
            Glide.with(this).load(uri).into(imageView)
        }
        imageView.setOnClickListener {
            //imageView.setImageResource(0)
            //액티비티로 값을 줘서 EditActivity에서
            //addedImage 삭제, realm 삭제, fragment 다시그리기
            //imageView.visibility = View.GONE
            //액티비티에서 addedImage객체를 가져와서?
            view.visibility = View.GONE
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PhotoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(uri: String, byteArray: ByteArray, id:Long, addedImages: ArrayList<Image>) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URI_IMAGE, uri)
                    putByteArray(ARG_BYTEARRAY_IMAGE, byteArray)
                }
            }
    }
}
