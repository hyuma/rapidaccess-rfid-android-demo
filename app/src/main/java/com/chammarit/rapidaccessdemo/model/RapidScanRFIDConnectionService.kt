package com.chammarit.rapidaccessdemo.model

import android.app.Service
import android.os.IBinder
import android.content.Intent
import android.os.Binder
import android.util.Log
import android.widget.Toast
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import java.io.UnsupportedEncodingException

class RapidScanRFIDConnectionService : Service() {
    val TAG = "RapidScanRFIDConnectionService"
    var arduino: UsbSerialDevice? = null
    var health = false
    var isLocked = true
    var isOpen = true

    inner class LocalBinder : Binder() {
        fun setRapidScanDevice(dev:UsbSerialDevice){
            arduino = dev
            val arduinoSerial = requireNotNull(arduino)
            if (!arduinoSerial.isOpen) {
                arduinoSerial.open()
            }
            arduinoSerial.setBaudRate(9600)
            arduinoSerial.setDataBits(UsbSerialInterface.DATA_BITS_8)
            arduinoSerial.setParity(UsbSerialInterface.PARITY_ODD)
            arduinoSerial.setStopBits(UsbSerialInterface.STOP_BITS_1)
            arduinoSerial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
            arduinoSerial.read(mCallback)
            sendSignal("s")
        }

        fun isConnected():Boolean{

            return try{
                health && requireNotNull(arduino).isOpen
            } catch (e: Exception) {
                false
            }
        }

        fun unlockDoor(){
            sendSignal("u")
        }
        fun lockDoor(){
            sendSignal("l")
        }
        fun isLocked():Boolean{
            // TODO: wait until response
            sendSignal("s")
            Toast.makeText(applicationContext, "isLocked?", Toast.LENGTH_SHORT).show()
            return isLocked
        }
        fun isOpen():Boolean{
            // TODO: wait until response
            sendSignal("s")
            Toast.makeText(applicationContext, "isOpen?", Toast.LENGTH_SHORT).show()
            return isOpen
        }
    }

    var mCallback: UsbSerialInterface.UsbReadCallback = UsbSerialInterface.UsbReadCallback { arg0 ->
        //Defining a Callback which triggers whenever data is read.
        var data: String?
        health = true
        try {
            data = String(arg0, Charsets.US_ASCII)
            val prevIsOpen = isOpen
            when (data) {
                "0" -> {
                    isLocked = true
                    isOpen = false
                    health = true
                }
                "1" -> {
                    isLocked = true
                    isOpen = true
                    health = true
                }
                "2" -> {
                    isLocked = false
                    isOpen = false
                    health = true
                }
                "3" -> {
                    isLocked = false
                    isOpen = true
                    health = true
                }
            }
            if (prevIsOpen != isOpen) {
                val broadcast = Intent()
                if (isOpen) {
                    // Opend Now
                    broadcast.action = "DOOR_OPENED"
                    baseContext.sendBroadcast(broadcast)
                }else {
                    // Closed Now
                    broadcast.action = "DOOR_CLOSED"
                    baseContext.sendBroadcast(broadcast)
                }
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
    }

    // TODO: error handling for each functions
    private fun sendSignal(signal:String){
        if(arduino == null){
            Toast.makeText(baseContext, "Connect to Arduino First", Toast.LENGTH_SHORT).show()
            return
        }else{
            val arduinoSerial = arduino!!
            if (!arduinoSerial.isOpen) {
                arduinoSerial.open()
            }
            arduinoSerial.write(signal.toByteArray())
        }
    }



    private val binder = LocalBinder()
    override fun onBind(intent: Intent): IBinder? {
        // Return this instance of LocalService so clients can call public methods
        Log.d(TAG, "Bind")
        Toast.makeText(applicationContext, "Bind!", Toast.LENGTH_SHORT).show()
        return binder
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG, "ReBind")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "UnBind")
        try {
            requireNotNull(arduino).close()
        } finally {
            return true
        }
    }
//
//    private lateinit var usbManager: UsbManager
//    var arduino: UsbSerialDevice? = null
//
//    private fun connectUsbSerialButton(){
//        usbManager = activity!!.getSystemService(Context.USB_SERVICE) as UsbManager
//        var device: UsbDevice
//        var usbDevices: HashMap<String, UsbDevice> = usbManager.getDeviceList()
//        var foundArduino = false
//
//        if (!usbDevices.isEmpty()) {
//            for ((_, value) in usbDevices) {
//                device = value
//                val deviceVID = device.vendorId
//                if (deviceVID == 6790)
//                //Arduino Vendor ID
//                {
//                    val pi = PendingIntent.getBroadcast(
//                        activity, 0,
//                        Intent(ACTION_USB_PERMISSION), 0
//                    )
//                    usbManager.requestPermission(device, pi)
//                    Toast.makeText(activity!!.baseContext, "Found Arduino", Toast.LENGTH_SHORT).show()
//                    foundArduino = true
//                    connectDevice(device)
//                } else {
////                    usbConnection = null
////                    device = null
//                }
//
//                if (foundArduino)
//                    break
//            }
//        }
//        if (!foundArduino){
//            Toast.makeText(activity!!.baseContext, "No Arduino Founded", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun openDoor(){
//        if(arduino == null){
//            Toast.makeText(activity!!.baseContext, "Connect to Arduino First", Toast.LENGTH_SHORT).show()
//            return
//        }else{
//
//            val arduinoSerial = arduino!!
//            if (!arduinoSerial.isOpen) {
//                arduinoSerial.open()
//            }
//            arduinoSerial.setBaudRate(9600)
//            arduinoSerial.setDataBits(UsbSerialInterface.DATA_BITS_8)
//            arduinoSerial.setParity(UsbSerialInterface.PARITY_ODD)
//            arduinoSerial.setStopBits(UsbSerialInterface.STOP_BITS_1)
//            arduinoSerial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)
//            arduinoSerial.write("o".toByteArray())
//
//        }
//    }
}