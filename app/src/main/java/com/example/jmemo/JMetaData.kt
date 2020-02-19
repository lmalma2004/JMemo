package com.example.jmemo

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.Exception

class JMetaData(url: String?, imageUrl: String?) {
    var url = url
    var imageUrl = imageUrl
}

fun getImageUrlFromUrl(url: String?): JMetaData?{
    try{
        val metadata = JMetaData(url, "")
        val doc: Document = Jsoup.connect(url).ignoreContentType(true).get()
        if(doc.select("meta[property=og:image]").size != 0)
            metadata.imageUrl = doc.select("meta[property=og:image]").get(0).attr("content")
        return metadata
    }catch (e: Exception){
        e.printStackTrace()
        return null
    }
}