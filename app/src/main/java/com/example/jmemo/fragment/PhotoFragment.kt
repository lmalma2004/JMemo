package com.example.jmemo.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.jmemo.R
import com.example.jmemo.activity.EditActivity
import kotlinx.android.synthetic.main.fragment_photo.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_IMAGE = "image"
private const val ARG_ID = "id"
private const val ARG_DELETEBUTTON_VISIBLE = "delete_button_visible"

class PhotoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var image: String? = null
    private var id: Long? = null
    private var deleteButtonVisible: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            image = it.getString(ARG_IMAGE)
            id = it.getLong(ARG_ID)
            deleteButtonVisible = it.getBoolean(ARG_DELETEBUTTON_VISIBLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this).load(image)
            .placeholder(R.drawable.ic_sync_black_24dp)
            .error(R.drawable.ic_error).into(imageView)

        if(deleteButtonVisible != null && !deleteButtonVisible!!) {
            imageDeleteButton.visibility = View.GONE
            deleteButtonVisible = true
        }

        imageDeleteButton.setOnClickListener {
            view.visibility = View.GONE
            val editActivity = activity as EditActivity
            editActivity.deleteImage(image!!)
        }
    }
    companion object {
        fun newInstance(image: String, id: Long, deleteButtonVisible: Boolean) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE, image)
                    putLong(ARG_ID, id)
                    putBoolean(ARG_DELETEBUTTON_VISIBLE, deleteButtonVisible)
                }
            }
        fun newInstance(image: String, id: Long) =
            PhotoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_IMAGE, image)
                    putLong(ARG_ID, id)
                }
            }
    }
}
