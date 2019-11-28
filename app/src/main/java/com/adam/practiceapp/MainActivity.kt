package com.adam.practiceapp

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.Adapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.internal.schedulers.SchedulerMultiWorkerSupport
import io.reactivex.internal.util.HalfSerializer
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.util.*
import java.util.concurrent.TimeUnit

const val TAG = "MAIN_ACTIVITY"

class TextViewAdapter(private val data: List<Int>) : RecyclerView.Adapter<TextViewAdapter.TextViewHolder>() {
    init {
        Log.i("MAIN", "Creating textViewAdapter")
    }
    class TextViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        Log.i("MAIN", "Creating $viewType")
        val textView = TextView(parent.context)
        textView.textSize = 40F
        return TextViewHolder(textView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        val item = data[position]
        holder.view.text = "$item"
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        val myData = mutableListOf(1, 2, 3)
        val adapter = TextViewAdapter(myData)
        myRecycler.adapter = adapter
        myRecycler.layoutManager = LinearLayoutManager(this)

        val myList = listOf(1, 2, 3)

        myList.toObservable()
            .map {
                Log.e(TAG, "Mapping on thread: ${Thread.currentThread().name}")
                it + 3
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e(TAG, "Adding on ${Thread.currentThread().name}")
                myData.add(it)
                adapter.notifyDataSetChanged()
                textView.text = "Setting text"
            }, {
                Log.e("MAIN", it.message ?: "Error message not found")
                throw it
            })

        GlobalScope.async {
            val result = Service.makeRequest()
            runOnUiThread {
                myData.add(123)
                adapter.notifyDataSetChanged()
                textView.text = result.toString()
            }
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
