package com.example.assignment1

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.content.SharedPreferences
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    enum class MethodConstants (var methodCounterID: Int){
        OnCreateCount(R.id.onCreateCount),
        OnRestartCount(R.id.onRestartCount),
        OnStartCount(R.id.onStartCount),
        OnPauseCount(R.id.onPauseCount),
        OnSaveInstanceStateCount(R.id.onSaveInstanceStateCount),
        OnRestoreInstanceStateCount(R.id.onRestoreInstanceStateCount),
        OnStopCount(R.id.onStopCount),
        OnDestroyCount(R.id.onDestroyCount),
        OnResumeCount(R.id.onResumeCount)
    }

    private val LOGTAG = "ActivityCounters"
    var resetValue = 0
    private var sharedPreferences: SharedPreferences? = null
    //private var sharedPreferencesEditor: SharedPreferences.Editor? = null

    private fun convertNullableToInt(value: Int?) : Int {
        if (value == null)
            return resetValue
        return value.toInt()
    }

    private fun syncCounters(methodName: String , viewToUpdate: TextView) {//methodCounterID: Int) {
        //update shared preferences
        var count = sharedPreferences?.getInt(methodName, resetValue)
        count = convertNullableToInt(count).inc()
        with(sharedPreferences?.edit()) {
            this?.putInt(methodName, count)
            this?.commit()}
        //update the view
        viewToUpdate.text = count.toString()
        //log the statement count update
        Log.i(LOGTAG, "$methodName is now updated to $count")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences(LOGTAG,Context.MODE_PRIVATE)

        enumValues<MethodConstants>().forEach {
            findViewById<TextView>(it.methodCounterID).text = sharedPreferences?.getInt(it.name, 0).toString() }
        syncCounters(MethodConstants.OnCreateCount.toString() , onCreateCount)

        printBundleValues(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        findViewById<Button>(R.id.Reset)?.setOnClickListener { onReset() }
        syncCounters(MethodConstants.OnStartCount.toString() , onStartCount)
    }

    override fun onRestart() {
        super.onRestart()
        syncCounters(MethodConstants.OnRestartCount.toString() , onRestartCount)
    }

    override fun onPause() {
        super.onPause()
        syncCounters(MethodConstants.OnPauseCount.toString() , onPauseCount)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        syncCounters(MethodConstants.OnSaveInstanceStateCount.toString() , onSaveInstanceStateCount)

        //update bundle with sharedpreferences
        enumValues<MethodConstants>().forEach { outState?.putInt(it.name,
            convertNullableToInt(sharedPreferences?.getInt(it.name, resetValue)) )}

        Log.i(LOGTAG, "Printing sharedpref values in onSaveInstanceState")
        enumValues<MethodConstants>().forEach {
            Log.i(LOGTAG, it.name + " : "+sharedPreferences?.getInt(it.name, resetValue).toString())
        }
    }

    private fun printBundleValues(savedInstanceState: Bundle?) {
        //get values from bundle
        Log.i(LOGTAG, "Printing bundle values")
        //savedInstanceState.apply { Log.i(LOGTAG, this.toString()) }
        enumValues<MethodConstants>().forEach {
            Log.i(LOGTAG, it.name + " : "+savedInstanceState?.getInt(it.name, resetValue).toString()) }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        printBundleValues(savedInstanceState)
        syncCounters(MethodConstants.OnRestoreInstanceStateCount.toString() , onRestoreInstanceStateCount)
    }

    override fun onStop() {
        super.onStop()
        syncCounters(MethodConstants.OnStopCount.toString() , onStopCount)
    }

    override fun onDestroy() {
        super.onDestroy()
        syncCounters(MethodConstants.OnDestroyCount.toString() , onDestroyCount)
    }

    override fun onResume() {
        super.onResume()
        syncCounters(MethodConstants.OnResumeCount.toString() , onResumeCount)
    }

    private fun onReset() {
        with(sharedPreferences?.edit()) {
            this?.clear()
            this?.commit()}
        enumValues<MethodConstants>().forEach { findViewById<TextView>(it.methodCounterID).text = resetValue.toString() }
    }
}