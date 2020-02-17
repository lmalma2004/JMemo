package com.example.jmemo

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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
            vh.dateTextView.text = DateFormat.format("yyyy/MM/dd", memo.date)
            vh.bodyTextView.text = memo.body
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
}