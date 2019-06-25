package com.chammarit.rapidaccessdemo.viewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.view.View
import android.widget.Toast
import com.chammarit.rapidaccessdemo.view.ui.MainActivity
import java.lang.Exception


class MainViewModel(application: Application) : AndroidViewModel(application) {

    val TAG = "MainViewModel"
    var launchStatus = MutableLiveData<String>()
    init {
        launchStatus.value = "Launching..."
    }

    fun connectAndroidOnClick(view:View) {
        val mainActivity = view.context
        if (mainActivity is MainActivity){
            if (mainActivity.rapidscan.isAndroidConnected() == false){
                launchStatus.value = "Connecting Arduino..."
                mainActivity.setUpRapidScan()
            } else {
                launchStatus.value = "Arduino connected!"
            }
        } else {
            throw Exception()
        }
    }

    fun getDoorStatusOnClick(view:View){
        val mainActivity = view.context
        if (mainActivity is MainActivity){
            if (mainActivity.rapidscan.isAndroidConnected() == false){
                launchStatus.value = "Arduino Not yet Connected"
            } else {
                val isLocked = mainActivity.rapidscan.isLocked()
                val isOpen = mainActivity.rapidscan.isOpen()
                Toast.makeText(mainActivity, "locked:"+isLocked.toString()
                        +"\nIsOpen:"+isOpen.toString(), Toast.LENGTH_SHORT).show()
            }
        } else {
            throw Exception()
        }
    }

    fun lockOnClick(view:View){
        val mainActivity = view.context
        if (mainActivity is MainActivity){
            if (mainActivity.rapidscan.isAndroidConnected() == false){
                launchStatus.value = "Arduino Not yet Connected"
            } else {
                mainActivity.rapidscan.lock()
            }
        } else {
            throw Exception()
        }
    }
    fun unlockOnClick(view:View){
        val mainActivity = view.context
        if (mainActivity is MainActivity){
            if (mainActivity.rapidscan.isAndroidConnected() == false){
                launchStatus.value = "Arduino Not yet Connected"
            } else {
                mainActivity.rapidscan.unlock()
            }
        } else {
            throw Exception()
        }
    }

}
