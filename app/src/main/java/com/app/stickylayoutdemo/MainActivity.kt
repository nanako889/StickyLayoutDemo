package com.app.stickylayoutdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qbw.l.L
import com.qbw.lib.StickyLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        L.GL.isEnabled = true
        L.GL.e("onCreate.........")
        var rcv = findViewById<RecyclerView>(R.id.rcv)
        val adapter = TestAdapter(this)
        rcv.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        for (i in 0 until (Math.random() * 50).toInt()) {
            adapter.addHeader(Header("header$i"))
        }
        for (i in 0 until (Math.random() * 50).toInt()) {
            adapter.addChild(Child("child$i"))
        }
        for (i in 0 until (Math.random() * 20).toInt()) {
            adapter.addGroup(Group("group$i"))
            for (j in 0 until (Math.random() * 20).toInt()) {
                adapter.addGroupChild(i, GroupChild("groupchild$j"))
            }
        }
        for (i in 0 until (Math.random() * 50).toInt()) {
            adapter.addFooter(Footer("footer$i"))
        }

        val stl = findViewById<StickyLayout>(R.id.stl)
        stl.init()
    }

    override fun onResume() {
        super.onResume()
        L.GL.e("resume")
    }

    override fun onPause() {
        super.onPause()
        L.GL.e("pause")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        L.GL.e("onNetINtent")
    }
}