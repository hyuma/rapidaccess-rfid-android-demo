package com.chammarit.rapidaccessdemo.model

class RapidScanRFID(serviceBinder:RapidScanRFIDConnectionService.LocalBinder){
    val sArduinoBinder : RapidScanRFIDConnectionService.LocalBinder = serviceBinder

    fun isAndroidConnected():Boolean{
        return sArduinoBinder.isConnected()
    }
    fun isOpen():Boolean{
        return sArduinoBinder.isOpen()
    }
    fun isLocked():Boolean{
        return sArduinoBinder.isLocked()
    }
    fun lock(){
        sArduinoBinder.lockDoor()
    }
    fun unlock(){
        sArduinoBinder.unlockDoor()
    }
}