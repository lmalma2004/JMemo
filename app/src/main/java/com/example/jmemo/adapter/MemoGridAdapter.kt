package com.example.jmemo.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.marginStart
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.jmemo.R
import com.example.jmemo.database.Memo
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter

class MemoGridAdapter(realmResult: OrderedRealmCollection<Memo>)
    : RealmBaseAdapter<Memo>(realmResult){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vh: ViewHolderOfGridView
        val view: View

        if(convertView == null){
            view = LayoutInflater.from(parent?.context).inflate(R.layout.item_memo, parent, false)
            vh = ViewHolderOfGridView(view)
            view.tag = vh
        }
        else{
            view = convertView
            vh = view.tag as ViewHolderOfGridView
        }

        if(adapterData != null){
            val memo = adapterData!![position]
            vh.titleTextView.text = memo.title
            vh.dateTextView.text = DateFormat.format("yyyy년 MM월 dd일", memo.lastDate)
            vh.bodyTextView.text = memo.body
            if(memo.images.size != 0){
                val multiOption = MultiTransformation(CenterCrop(), RoundedCorners(30))
                Glide.with(view).load(memo.images.first())
                    .placeholder(R.drawable.ic_sync_black_24dp)
                    .error(R.drawable.ic_error)
                    .apply(RequestOptions.bitmapTransform(multiOption))
                    .into(vh.realmImageView)
            }
        }
        return view
    }

    override fun getItemId(position: Int): Long {
        if(adapterData != null)
            return adapterData!![position].id
        return super.getItemId(position)
    }
}

class ViewHolderOfGridView(view: View){
    val titleTextView: TextView = view.findViewById(R.id.titleTextView)
    val dateTextView: TextView = view.findViewById(R.id.dateTextView)
    val bodyTextView: TextView = view.findViewById(R.id.bodyTextView)
    val realmImageView: ImageView = view.findViewById(R.id.realmImageView)
}