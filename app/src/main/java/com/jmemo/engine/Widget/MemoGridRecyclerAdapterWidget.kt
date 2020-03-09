package com.jmemo.engine.Widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.AppWidgetTarget
import com.jmemo.engine.R
import com.jmemo.engine.database.Memo
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import java.lang.Exception

class MemoGridRecyclerAdapterWidget(realmResult: OrderedRealmCollection<Memo>, context: Context)
    : RealmRecyclerViewAdapter<Memo, MemoGridRecyclerAdapterWidget.ViewHolderOfGridRecycleView>(realmResult, false){
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
        holder.view!!.setOnClickListener {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val jMemoAppWidgetSettingActivity = context as JMemoAppWidgetSettingActivity
            val appWidgetId = jMemoAppWidgetSettingActivity.intent?.extras?.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
            val views = RemoteViews(context?.packageName, R.layout.j_memo_app_widget)
            val appWidgetTarget = AppWidgetTarget(context!!.applicationContext, R.id.imageViewOfWidget, views, appWidgetId)

            views.setTextViewText(R.id.titleTextViewOfWidget, memo.title)
            views.setTextViewText(R.id.bodyTextViewOfWidget, memo.body)
            views.setTextViewText(R.id.dateTextViewOfWidget, DateFormat.format("yyyy년 MM월 dd일", memo.lastDate))
            val multiOption = MultiTransformation(CenterCrop(), RoundedCorners(100))
            Glide.with(context!!.applicationContext)
                .asBitmap()
                .load(memo.images.first())
                .placeholder(R.drawable.ic_sync_black_24dp)
                .error(R.drawable.ic_error)
                .override(1500)
                .apply(RequestOptions.bitmapTransform(multiOption))
                .into(appWidgetTarget)

            views.also {remoteViews ->
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
            }
            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            jMemoAppWidgetSettingActivity.setResult(Activity.RESULT_OK, resultValue)
            jMemoAppWidgetSettingActivity.finish()
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
