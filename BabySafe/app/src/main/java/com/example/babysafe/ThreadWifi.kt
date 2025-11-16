package com.example.babysafe

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.lang.Exception
import java.net.URL
import java.util.Scanner


class ThreadWifi: Thread{
    private lateinit var activity : MainActivity
    private var result:String = ""

    constructor(activity: MainActivity ){
        this.activity = activity
    }

    override fun run() {
        super.run()
        try {
            var url:URL = URL(arduinoIP)
            var iStream: InputStream = url.openStream()
            var scan:Scanner = Scanner(iStream)
            while (scan.hasNext()){
                result+=scan.nextLine()
            }
            Log.w("MainActivity",result)
            var jsonobj = JSONObject(result)
            var temp = jsonobj.get("temp").toString()
            var quality = jsonobj.get("airquality").toString()
            Log.w("MainActivity",quality)
            activity.threadUpdate(temp,quality.toInt())

        }catch (e : Exception){
            Log.w("MainActivity", e.toString())
        }
    }

    companion object{
        const val arduinoIP = ""
    }
}