package com.example.jmemo.database

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.jmemo.R
import io.realm.OrderedRealmCollection
import io.realm.RealmBaseAdapter

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
            vh.dateTextView.text = DateFormat.format("yyyy년 MM월 dd일", memo.date)
            vh.bodyTextView.text = memo.body
            if(vh.bodyTextView.lineCount > 4){

            }
            if(memo.images.size != 0){
                //Glide.with(view).load(memo.images.first()).into(vh.realmImageView)
                Glide.with(view).load(memo.images.first())
                    .placeholder(R.drawable.ic_sync_black_24dp)
                    .error(R.drawable.ic_error).into(vh.realmImageView)
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