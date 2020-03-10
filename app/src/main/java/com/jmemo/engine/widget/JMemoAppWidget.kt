package com.jmemo.engine.widget

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.View
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.AppWidgetTarget
import com.jmemo.engine.R
import com.jmemo.engine.activity.EditActivity
import com.jmemo.engine.database.Memo

/**
 * Implementation of App Widget functionality.
 */

val memoMap = hashMapOf(Pair<Int, Memo?>(-1, null))

class JMemoAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val memo = memoMap.get(appWidgetId)
            if(memo != null) {
                updateReceiveAppWidget(
                    context,
                    appWidgetManager,
                    appWidgetId,
                    memo
                )
            }
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val myWidget = ComponentName(context!!.packageName, JMemoAppWidget::class.java.name)
        val widgetIds = appWidgetManager.getAppWidgetIds(myWidget)
        val action = intent?.action

        if(action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
            if(widgetIds != null && widgetIds.size != 0){
                this.onUpdate(context!!, AppWidgetManager.getInstance(context), widgetIds)
            }
        }
    }
}
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    memo: Memo?
){
    updateWidget(context, appWidgetManager, appWidgetId, memo)
    val resultValue = Intent().apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }
    val jMemoAppWidgetSettingActivity = context as JMemoAppWidgetSettingActivity
    jMemoAppWidgetSettingActivity.setResult(Activity.RESULT_OK, resultValue)
    jMemoAppWidgetSettingActivity.finish()
}

internal fun updateReceiveAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    memo: Memo?
){
    updateWidget(context, appWidgetManager, appWidgetId, memo)
    val resultValue = Intent().apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }
}

fun updateWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    memo: Memo?
){
    memoMap.put(appWidgetId, memo)
    val views = RemoteViews(context?.packageName, R.layout.j_memo_app_widget)
    val appWidgetTarget = AppWidgetTarget(context!!.applicationContext, R.id.imageViewOfWidget, views, appWidgetId)
    views.setTextViewText(R.id.titleTextViewOfWidget, memo?.title)
    views.setTextViewText(R.id.bodyTextViewOfWidget, memo?.body)
    views.setTextViewText(R.id.dateTextViewOfWidget, DateFormat.format("yyyy년 MM월 dd일", memo!!.lastDate))
    if(memo?.images.size != 0) {
        views.setImageViewResource(R.id.imageViewOfWidget, View.VISIBLE)
        val multiOption = MultiTransformation(CenterCrop())
        Glide.with(context!!.applicationContext)
            .asBitmap()
            .load(memo?.images?.first())
            .placeholder(R.drawable.ic_sync_black_24dp)
            .error(R.drawable.ic_error)
            .override(1500)
            .apply(RequestOptions.bitmapTransform(multiOption))
            .into(appWidgetTarget)
    }
    else{
        views.setImageViewResource(R.id.imageViewOfWidget, View.GONE)
    }

    val intent = Intent(context, EditActivity::class.java)
    intent.putExtra("id", memo.id)
    val pi = PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    views.setOnClickPendingIntent(R.id.widgetView, pi)

    views.also {remoteViews ->
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }
}