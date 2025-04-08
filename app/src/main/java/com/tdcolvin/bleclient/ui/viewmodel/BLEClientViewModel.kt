package com.tdcolvin.bleclient.ui.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattService
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tdcolvin.bleclient.ble.BLEScanner
import com.tdcolvin.bleclient.ble.BLEDeviceConnection
import com.tdcolvin.bleclient.ble.PERMISSION_BLUETOOTH_CONNECT
import com.tdcolvin.bleclient.ble.PERMISSION_BLUETOOTH_SCAN
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class BLEClientViewModel(private val application: Application): AndroidViewModel(application) {
    private val bleScanner = BLEScanner(application)
    private var activeConnection = MutableStateFlow<BLEDeviceConnection?>(null)

    private val isDeviceConnected = activeConnection.flatMapLatest { it?.isConnected ?: flowOf(false) }
    private val activeDeviceServices = activeConnection.flatMapLatest {
        it?.services ?: flowOf(emptyList())
    }
    private val activeDeviceFlag1 = activeConnection.flatMapLatest {
        it?.flag1Read ?: flowOf(null)
    }
    private val activeDeviceNameWrittenTimes = activeConnection.flatMapLatest {
        it?.successfulNameWrites ?: flowOf(0)
    }
    private val activeDeviceFlag2Value = activeConnection.flatMapLatest {
        it?.flag2Value ?: flowOf(null)
    }

    private val _uiState = MutableStateFlow(BLEClientUIState())
    val uiState = combine(
        _uiState,
        isDeviceConnected,
        activeDeviceServices,
        activeDeviceFlag1,
        activeDeviceNameWrittenTimes,
        activeDeviceFlag2Value
    ) { values ->
        // Limitation of Kotlin here! Would be better to use combine()'s fixed format, but that only
        // supports up to 5 args! Same as array destructuring.
        val state = values[0] as BLEClientUIState
        val isDeviceConnected = values[1] as Boolean
        @Suppress("UNCHECKED_CAST")
        val services = values[2] as List<BluetoothGattService>
        val flag1 = values[3] as String?
        val nameWrittenTimes = values[4] as Int
        val flag2Value = values[5] as String?

        state.copy(
            isDeviceConnected = isDeviceConnected,
            discoveredCharacteristics = services.associate { service -> Pair(service.uuid.toString(), service.characteristics.map { it.uuid.toString() }) },
            flag1 = flag1,
            nameWrittenTimes = nameWrittenTimes,
            flag2Value = flag2Value
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BLEClientUIState())

    init {
        viewModelScope.launch {
            bleScanner.foundDevices.collect { devices ->
                _uiState.update { it.copy(foundDevices = devices) }
            }
        }
        viewModelScope.launch {
            bleScanner.isScanning.collect { isScanning ->
                _uiState.update { it.copy(isScanning = isScanning) }
            }
        }
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_SCAN)
    fun startScanning() {
        bleScanner.startScanning()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_SCAN)
    fun stopScanning() {
        bleScanner.stopScanning()
    }

    @SuppressLint("MissingPermission")
    @RequiresPermission(allOf = [PERMISSION_BLUETOOTH_CONNECT, PERMISSION_BLUETOOTH_SCAN])
    fun setActiveDevice(device: BluetoothDevice?) {
        activeConnection.value = device?.run { BLEDeviceConnection(application, device) }
        _uiState.update { it.copy(activeDevice = device) }
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun connectActiveDevice() {
        activeConnection.value?.connect()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun disconnectActiveDevice() {
        activeConnection.value?.disconnect()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun discoverActiveDeviceServices() {
        activeConnection.value?.discoverServices()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun readFlag1FromActiveDevice() {
        activeConnection.value?.readFlag1()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun writeNameToActiveDevice() {
        activeConnection.value?.writeName()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun startNotifyForFlag2() {
        activeConnection.value?.startNotifyForFlag2()
    }

    @RequiresPermission(PERMISSION_BLUETOOTH_CONNECT)
    fun stopNotifyForFlag2() {
        activeConnection.value?.stopNotifyForFlag2()
    }

    override fun onCleared() {
        super.onCleared()

        //when the ViewModel dies, shut down the BLE client with it
        if (bleScanner.isScanning.value) {
            if (ActivityCompat.checkSelfPermission(
                    getApplication(),
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                bleScanner.stopScanning()
            }
        }
    }
}

data class BLEClientUIState(
    val isScanning: Boolean = false,
    val foundDevices: List<BluetoothDevice> = emptyList(),
    val activeDevice: BluetoothDevice? = null,
    val isDeviceConnected: Boolean = false,
    val discoveredCharacteristics: Map<String, List<String>> = emptyMap(),
    val flag1: String? = null,
    val nameWrittenTimes: Int = 0,
    val flag2Value: String? = null
)