package com.chammarit.rapidaccessdemo.view.ui

import android.app.PendingIntent
import android.content.*
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.Toast
import com.chammarit.rapidaccessdemo.R
import com.chammarit.rapidaccessdemo.databinding.ContentMainBinding
import com.chammarit.rapidaccessdemo.model.RapidScanRFID
import com.chammarit.rapidaccessdemo.model.RapidScanRFIDConnectionService
import com.chammarit.rapidaccessdemo.viewModel.MainViewModel
import com.felhr.usbserial.UsbSerialDevice

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val TAG = "MainActivity"
    private val ACTION_USB_PERMISSION = "id.indoliquid.USB_PERMISSION"
    lateinit var rapidscan: RapidScanRFID
    private lateinit var usbManager: UsbManager
    private var rapidScanRFIDConnectionServiceBinder: RapidScanRFIDConnectionService.LocalBinder? = null
    private val rapidScanRFIDServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder){
            rapidScanRFIDConnectionServiceBinder = binder as RapidScanRFIDConnectionService.LocalBinder
            rapidscan = RapidScanRFID(requireNotNull(rapidScanRFIDConnectionServiceBinder))
            setUpRapidScan()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "ServiceDisconnected")
            rapidScanRFIDConnectionServiceBinder = null
        }

    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.apply {
                            //call method to set up device communication
                            requireNotNull(rapidScanRFIDConnectionServiceBinder).setRapidScanDevice(connectDevice(device))
                        }
                    } else {
                        Log.d(TAG, "permission denied for device $device")
                    }
                }
            }
        }
    }

    private fun connectDevice(device: UsbDevice): UsbSerialDevice {
        var usbConnection: UsbDeviceConnection = usbManager.openDevice(device)
        Toast.makeText(this, "RapidScan connected", Toast.LENGTH_SHORT).show()
        return UsbSerialDevice.createUsbSerialDevice(device, usbConnection)
    }


    fun setUpRapidScan() {
        usbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        var device: UsbDevice
        var usbDevices: HashMap<String, UsbDevice> = usbManager.getDeviceList()
        var foundArduino = false

        if (!usbDevices.isEmpty()) {
            for ((_, value) in usbDevices) {
                device = value
                val deviceVID = device.vendorId
                if ((deviceVID == 6790)||(device.deviceId == 1004))
                //Arduino Vendor ID
                {
                    val pi = PendingIntent.getBroadcast(
                        this, 0,
                        Intent(ACTION_USB_PERMISSION), 0
                    )
                    val filter = IntentFilter(ACTION_USB_PERMISSION)
                    registerReceiver(usbReceiver, filter)
                    usbManager.requestPermission(device, pi)

                    Toast.makeText(this, "Found Arduino: " + device.vendorId.toString() + ", "+ device.deviceId.toString(), Toast.LENGTH_SHORT).show()
                    foundArduino = true
                } else {
//                    usbConnection = null
//                    device = null
                }

                if (foundArduino)
                    break
            }
            if (!foundArduino){
                Toast.makeText(this, "No Arduino Found", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "No USB device connected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindService(intent, rapidScanRFIDServiceConnection, Context.BIND_AUTO_CREATE)

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container,  MainFragment(), TAG_F_MAIN)
                .commit()
        }
    }



    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
