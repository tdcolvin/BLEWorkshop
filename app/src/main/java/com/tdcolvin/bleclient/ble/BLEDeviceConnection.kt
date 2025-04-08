package com.tdcolvin.bleclient.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

val DEVFEST_SERVICE_UUID: UUID = UUID.fromString("8c380000-10bd-4fdb-ba21-1922d6cf860d")
val PASSWORD_CHARACTERISTIC_UUID: UUID = UUID.fromString("8c380001-10bd-4fdb-ba21-1922d6cf860d")
val NAME_CHARACTERISTIC_UUID: UUID = UUID.fromString("8c380002-10bd-4fdb-ba21-1922d6cf860d")
val FLAG_2_CHARACTERISTIC_UUID: UUID = UUID.fromString("8c380003-10bd-4fdb-ba21-1922d6cf860d")

@Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
class BLEDeviceConnection @RequiresPermission("PERMISSION_BLUETOOTH_CONNECT") constructor(
    private val context: Context,
    private val bluetoothDevice: BluetoothDevice
) {
    val isConnected = MutableStateFlow(false)
    val passwordRead = MutableStateFlow<String?>(null)
    val successfulNameWrites = MutableStateFlow(0)
    val flag2Value = MutableStateFlow<String?>(null)
    val services = MutableStateFlow<List<BluetoothGattService>>(emptyList())

    private var gatt: BluetoothGatt? = null


    /*
    TASK 1: Connect to the BLE device
    =================================
    The connect() function is called whenever the user asks to connect to the BLE device they
    selected.
    To make this work, you need to:
        1. Write code in the connect() function to connect to the BLE device:
           gatt = bluetoothDevice.connectGatt(context, false, callback)

        2. Override onConnectionStateChange(...) in the BluetoothGattCallback object. This will be
           called whenever your phone notices the connection has been established or lost.
           Set isConnected.value to whether the device is connected or not, which you can get from
           seeing whether newState == BluetoothGatt.STATE_CONNECTED.

     Now you should be able to run the app, scan for BLE devices, select a device, and connect to
     it.
     */


    /*
    TASK 2: Discover services on the BLE device
    ===========================================
    The discoverServices() function is called whenever the user asks to see a list of BLE services
    in the device they've selected and connected to.
        1. In the discoverServices() function, call discoverServices() on the GATT object
           created in task 1.
        2. Override onServicesDiscovered. This is called when the device sends us its list of
           BLE services.
           Set services.value to the GATT's services. You can get these from `gatt.services`.

    Run and test: Now you should be able to run the app, scan and connect, and then list the
    available services on your chosen device.
    */


    /*
    TASK 3: Read the password characteristic
    ========================================
    The readPassword() function is called whenever the user asks to read the password characteristic
    (that's the characteristic with the UUID PASSWORD_CHARACTERISTIC_UUID defined above).
        1. In the readPassword() function, get the Devfest service:
           val service = gatt?.getService(DEVFEST_SERVICE_UUID)
        2. Get the password characteristic:
           val characteristic = service?.getCharacteristic(PASSWORD_CHARACTERISTIC_UUID)
        3. If succeeded (i.e. if `characteristic` is non-null), ask the GATT to read from it:
           gatt?.readCharacteristic(...)
        4. Implement onCharacteristicRead in the BluetoothGattCallback:
           override fun onCharacteristicRead(
               gatt: BluetoothGatt,
               characteristic: BluetoothGattCharacteristic,
               status: Int
           ) { ... }
           This function should set passwordRead.value to the characteristic's value.

     Run and test: Scan and connect to the Devfest device, and you should be able to read the
     password.
     */


    /*
    TASK 4: Write your name to the name characteristic
    ==================================================
    The writeName() function is called whenever the user asks to write their name to the BLE device.
        1. In the writeName() function, get the name characteristic. Follow the steps 1 & 2 in task
           3 above, but substituting NAME_CHARACTERISTIC_UUID
        2. If succeeded, ask the GATT to write to it:
           gatt?.writeCharacteristic(...)
        3. Implement onCharacteristicWrite in the BluetoothGattCallback. This is called when we
           receive confirmation that the write was successful or otherwise.
           Check status, and if good, increment successfulNameWrites:
           successfulNameWrites.update { it + 1 }
    */


    private val callback = object: BluetoothGattCallback() {
        /*
        TODO: Methods in this class are called whenever we hear back from the device we're connecting
              to.
              Add methods to this class as directed above, whenever you need do something as a result
              of what the device returns to us.
         */
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun connect() {
        //TODO: connect the GATT as directed in task 1 above
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun discoverServices() {
        //TODO: discover services as directed in task 2 above
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun readPassword() {
        //TODO: read from the password characteristic as directed in task 3 above.
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun writeName() {
        //TODO: write your name to the name characteristic as directed in task 4 above.
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun startNotifyForFlag2() {
        //TODO: request to start receiving notifications for flag 2, as directed in task 5 above
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun stopNotifyForFlag2() {
        //TODO: stop notifications for flag 2 as directed in task 5 above
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun disconnect() {
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }
}