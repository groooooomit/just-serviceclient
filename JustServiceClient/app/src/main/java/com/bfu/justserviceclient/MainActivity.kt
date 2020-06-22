package com.bfu.justserviceclient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val demoClient = DemoClient(this)
    private val aidlClient = AidlClient(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txt_hello.setOnClickListener {
            demoClient.binder?.saySomething("Hello world!")
            aidlClient.binder?.saySomething("Nice to meet you!")
        }
    }

    override fun onStart() {
        super.onStart()
        demoClient.bind()
        aidlClient.bind()
    }

    override fun onStop() {
        demoClient.unBind()
        aidlClient.unBind()
        super.onStop()
    }
}