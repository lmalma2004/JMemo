package com.example.jmemo

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter
import kotlinx.android.synthetic.main.fragment_photo.*

class MemoGridAdapter(realmResult: OrderedRealmCollection<Memo>)
    : RealmBaseAdapter<Memo>(realmResult){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val vh: ViewHolder
        val view: View

        if(convertView == null){
            view = LayoutInflater.from(parent?.context).inflate(R.layout.item_memo, parent, false)
            vh = ViewHolder(view)
            view.tag = vh
        }
        else{
            view = convertView
            vh = view.tag as ViewHolder
        }

        if(adapterData != null){
            val memo = adapterData!![position]
            vh.titleTextView.text = memo.title
            vh.dateTextView.text = DateFormat.format("yyyy/MM/dd", memo.date)
            vh.bodyTextView.text = memo.body
            if(memo.images.size != 0){
                if(memo.images[0]!!.urlOfImage == ""){
                    Glide.with(view).asBitmap().load(memo.images[0]!!.image).into(vh.realmImageView)
                }
                else{
                    Glide.with(view).load(memo.images[0]!!.urlOfImage).into(vh.realmImageView)
                }
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

class ViewHolder(view: View){
    val titleTextView: TextView = view.findViewById(R.id.titleTextView)
    val dateTextView: TextView = view.findViewById(R.id.dateTextView)
    val bodyTextView: TextView = view.findViewById(R.id.bodyTextView)
    val realmImageView: ImageView = view.findViewById(R.id.realmImageView)
}