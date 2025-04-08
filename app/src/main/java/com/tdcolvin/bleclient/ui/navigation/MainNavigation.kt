package com.tdcolvin.bleclient.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tdcolvin.bleclient.ui.screens.DeviceScreen
import com.tdcolvin.bleclient.ui.screens.PermissionsRequiredScreen
import com.tdcolvin.bleclient.ui.screens.ScanningScreen
import com.tdcolvin.bleclient.ui.screens.haveAllPermissions
import com.tdcolvin.bleclient.ui.viewmodel.BLEClientViewModel

@SuppressLint("MissingPermission")
@Composable
fun MainNavigation(
    modifier: Modifier = Modifier,
    viewModel: BLEClientViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var allPermissionsGranted by remember {
        mutableStateOf (haveAllPermissions(context))
    }

    if (!allPermissionsGranted) {
        PermissionsRequiredScreen { allPermissionsGranted = true }
    }
    else if (uiState.activeDevice == null) {
        ScanningScreen(
            modifier = modifier,
            isScanning = uiState.isScanning,
            foundDevices = uiState.foundDevices,
            startScanning = viewModel::startScanning,
            stopScanning = viewModel::stopScanning,
            selectDevice = { device ->
                viewModel.stopScanning()
                viewModel.setActiveDevice(device)
            }
        )
    }
    else {
        DeviceScreen(
            modifier = modifier,
            unselectDevice = {
                viewModel.disconnectActiveDevice()
                viewModel.setActiveDevice(null)
            },
            isDeviceConnected = uiState.isDeviceConnected,
            discoveredCharacteristics = uiState.discoveredCharacteristics,
            flag1 = uiState.flag1,
            nameWrittenTimes = uiState.nameWrittenTimes,
            flag2 = uiState.flag2Value,
            connect = viewModel::connectActiveDevice,
            discoverServices = viewModel::discoverActiveDeviceServices,
            readFlag1 = viewModel::readFlag1FromActiveDevice,
            writeName = viewModel::writeNameToActiveDevice,
            startNotifyFlag2 = viewModel::startNotifyForFlag2,
            stopNotifyFlag2 = viewModel::stopNotifyForFlag2
        )
    }
}