package com.jmemo.engine.adapter

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.jmemo.engine.R
import com.jmemo.engine.activity.EditActivity
import com.jmemo.engine.activity.EditVideoActivity
import com.jmemo.engine.database.Memo
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import org.jetbrains.anko.startActivity


class MemoGridRecyclerAdapter(realmResult: OrderedRealmCollection<Memo>, context: Context)
    : RealmRecyclerViewAdapter<Memo, MemoGridRecyclerAdapter.ViewHolderOfGridRecycleView>(realmResult, false){
    var context : Context? = null
    var realmResult : OrderedRealmCollection<Memo>? = null
    init {
        this.context = context
        this.realmResult = realmResult
    }
    override fun getItem(index: Int): Memo? {
        return super.getItem(index)
    }

    //아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOfGridRecycleView {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_memo_stagger_grid, parent, false)
        return ViewHolderOfGridRecycleView(view!!)
    }
    //position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    override fun onBindViewHolder(holder: ViewHolderOfGridRecycleView, position: Int) {
        val memo = realmResult?.get(position)
        holder.titleTextView.text = memo!!.title
        holder.dateTextView.text = DateFormat.format("yyyy년 MM월 dd일", memo.lastDate)
        holder.bodyTextView.text = memo.body
        if(memo.images.size != 0){
            holder.imageView.visibility = View.VISIBLE
            val multiOption = MultiTransformation(CenterCrop(), RoundedCorners(30))
            Glide.with(holder.view!!).load(memo.images.first())
                .placeholder(R.drawable.ic_sync_black_24dp)
                .error(R.drawable.ic_error)
                .apply(RequestOptions.bitmapTransform(multiOption))
                .into(holder.imageView)
            holder.imageView.background = holder.view!!.resources.getDrawable(R.drawable.border_layout, null)
        }
        else{
            holder.imageView.visibility = View.GONE
        }
        if(memo.youTubeVideoId != ""){
            holder.view!!.setOnClickListener {
                context!!.startActivity<EditVideoActivity>("id" to memo.id)
            }
        }
        else{
            holder.view!!.setOnClickListener {
                context!!.startActivity<EditActivity>("id" to memo.id)
            }
        }
    }

    class ViewHolderOfGridRecycleView(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val bodyTextView: TextView = itemView.findViewById(R.id.bodyTextView)
        val imageView: ImageView = itemView.findViewById(R.id.mainImageView)
        val view = itemView
    }
}
