package com.example.jmemo.fragment


import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.example.jmemo.R
import com.example.jmemo.activity.EditActivity
import kotlinx.android.synthetic.main.fragment_photo.*
import java.io.FileNotFoundException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_IMAGE = "image"
private const val ARG_ID = "id"
/**
 * A simple [Fragment] subclass.
 * Use the [PhotoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var image: String? = null
    private var id: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            image = it.getString(ARG_IMAGE)
            id = it.getLong(ARG_ID)
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
        //Glide.with(this).load(image).into(imageView)
        Glide.with(this).load(image)
            .placeholder(R.drawable.ic_sync_black_24dp)
            .error(R.drawable.ic_error).into(imageView)

        imageView.setOnClickListener {
            view.visibility = View.GONE
            val editActivity = activity as EditActivity
            editActivity.deleteImageFromAdded(image!!)
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
        fun newInstance(image: String, id:Long) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE, image)
                    putLong(ARG_ID, id)
                }
            }
    }
}
