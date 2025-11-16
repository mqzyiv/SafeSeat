package com.example.babysafe

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Timer
import androidx.core.graphics.withSave

class MainActivity : AppCompatActivity() {
    private lateinit var uibuild: UIBuild
    private lateinit var timer:TimerT
    private lateinit var thread:ThreadWifi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var width : Int = resources.displayMetrics.widthPixels
        var height : Int = resources.displayMetrics.heightPixels
        var y = height/3f
        var x = width/2f
        uibuild = UIBuild(this, width,height)
        setContentView(uibuild)
        thread = ThreadWifi(this)
        thread.start()
        var timerfrfr = Timer()
        timerfrfr.schedule( TimerT(this),0L,1000)
        createNotificationChannel()
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channelName = "My Notification Channel"
            val channelDescription = "Channel for app notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun showNotification() {
        val channelId = "my_channel_id"

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Your Child May Be In Danger!")
            .setContentText("Unsafe Environment Detected.")
            .setSmallIcon(R.drawable.icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("MainActivity","We notifying")
            notificationManager.notify(1, notification)
        }else{
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001
            )

        }
    }


    fun threadUpdate(temp:String,quality:Int){
        Log.w("MainActivity",temp+quality.toString())
        var issue = false
        if (temp!=null && quality!=null){
            var text = "Temperature: "+temp
            text+="\nAir Quality:"
            if(quality == 0){
                text+="VERY BAD"
                issue = true
            }else if (quality == 1){
                text+="HIGH POLLUTION"
                issue = true
            }else if(quality == 2){
                text+="LOW POLLUTION"
            }else if(quality==3){
                text+="Fresh Air"

            }else{
                text+="NA"
            }
            if (temp.toFloat()>28){
                issue = true
            }
            uibuild.setText(text)
            if (issue){
                uibuild.setColor(Color.RED)
                showNotification()
            }else{
                uibuild.setColor(Color.GREEN)
            }

        }
    }
    fun update(){
        uibuild.postInvalidate()
        thread = ThreadWifi(this)
        thread.start()
    }
    inner class UIBuild : View {
        private var paint : Paint = Paint()
        private var textPaint : TextPaint = TextPaint()
        private var width : Int = 0
        private var color = Color.LTGRAY
        private var text = "ERROR: DEVICE NOT FOUND"
        private var height : Int = 0


        constructor(context: Context,width: Int, height:Int) : super(context) {
            paint.strokeWidth = 60f
            paint.color = Color.RED
            paint.isAntiAlias = true
            this.width = width
            this.height = height
        }
        fun setText(text:String){
            this.text = text
        }
        fun setColor(color: Int){
            this.color = color

        }
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            paint.color = color
            var y = height/3f
            var x = width/2f
            canvas.drawCircle(x,y-10,(width/2 - 20f),paint)
            textPaint.textSize=80f
            var textLayout =   StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
                .setAlignment( Layout.Alignment.ALIGN_CENTER)
                .build()

            canvas.withSave {
                var textX = (width-textLayout.width).toFloat()
                var textY = height/3f - 50f
                translate(textX, textY);
                textLayout.draw(this);
            };
        }
    }

}