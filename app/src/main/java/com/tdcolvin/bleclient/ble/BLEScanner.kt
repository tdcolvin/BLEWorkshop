package com.tdcolvin.bleclient.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

//These fields are marked as API >= 31 in the Manifest class, so we can't use those without warning.
//So we create our own, which prevents over-suppression of the Linter
const val PERMISSION_BLUETOOTH_SCAN = "android.permission.BLUETOOTH_SCAN"
const val PERMISSION_BLUETOOTH_CONNECT = "android.permission.BLUETOOTH_CONNECT"

class BLEScanner(context: Context) {

    private val bluetooth = context.getSystemService(Context.BLUETOOTH_SERVICE)
        as? BluetoothManager
        ?: throw Exception("Bluetooth is not supported by this device")

    val isScanning = MutableStateFlow(false)

    val foundDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())

    private val scanner = bluetooth.adapter.bluetoothLeScanner

    private val scanCallback = object : ScanCallback() {
        /*
        TODO:
        1. Override method onScanResult - this is called when a new BLE device is found. Your code
           should call foundDevice() with the new device as a parameter.
        2. Override method onScanFailed - this is called when there was a problem which meant the
           scan couldn't start. Your code should set isScanning.value to false.
        */

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            foundDevice(result.device)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            isScanning.value = false
        }
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_SCAN)
    fun startScanning() {
        //TODO: Start the scanner. Use the 'scanner' and 'scanCallback' objects above.
        //      Hint: scanner.startScan(...)

        isScanning.value = true
        scanner.startScan(scanCallback)
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_SCAN)
    fun stopScanning() {
        //TODO: Start the scanner. Use the 'scanner' and 'scanCallback' objects above.
        //      Hint: scanner.stopScan(...)

        scanner.stopScan(scanCallback)
        isScanning.value = false
    }

    private fun foundDevice(device: BluetoothDevice) {
        if (!foundDevices.value.contains(device)) {
            foundDevices.update { it + device }
        }
    }
}