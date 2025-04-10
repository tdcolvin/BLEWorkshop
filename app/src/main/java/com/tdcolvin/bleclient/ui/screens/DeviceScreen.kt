package com.tdcolvin.bleclient.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tdcolvin.bleclient.ble.CTF_SERVICE_UUID

@Composable
fun DeviceScreen(
    modifier: Modifier = Modifier,
    unselectDevice: () -> Unit,
    isDeviceConnected: Boolean,
    discoveredCharacteristics: Map<String, List<String>>,
    flag1: String?,
    nameWrittenTimes: Int,
    flag2: String?,
    connect: () -> Unit,
    discoverServices: () -> Unit,
    readFlag1: () -> Unit,
    writeName: () -> Unit,
    startNotifyFlag2: () -> Unit,
    stopNotifyFlag2: () -> Unit
) {
    val foundTargetService = discoveredCharacteristics.contains(CTF_SERVICE_UUID.toString())

    Column(
        modifier.verticalScroll(rememberScrollState())
    ) {
        Button(onClick = connect) {
            Text("1. Connect")
        }
        Text("Device connected: $isDeviceConnected")
        Button(onClick = discoverServices, enabled = isDeviceConnected) {
            Text("2. Discover Services")
        }
            discoveredCharacteristics.keys.sorted().forEach { serviceUuid ->
                Text(text = serviceUuid, fontWeight = FontWeight.Black)
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    discoveredCharacteristics[serviceUuid]?.forEach {
                        Text(it)
                    }
                }
            }
        Button(onClick = readFlag1, enabled = isDeviceConnected && foundTargetService) {
            Text("3. Read Flag #1")
        }
        if (flag1 != null) {
            Text("Found flag 1: $flag1")
        }
        Button(onClick = writeName, enabled = isDeviceConnected && foundTargetService) {
            Text("4. Write Your Name")
        }
        if (nameWrittenTimes > 0) {
            Text("Successful writes: $nameWrittenTimes")
        }

        Button(onClick = startNotifyFlag2, enabled = isDeviceConnected && foundTargetService) {
            Text("5. Get Notifications for Flag #2")
        }
        if (flag2 != null) {
            Text("Current flag 2 value: $flag2")

            Button(modifier = Modifier.padding(start = 20.dp), onClick = stopNotifyFlag2) {
                Text("Stop notifications")
            }
        }

        OutlinedButton(modifier = Modifier.padding(top = 40.dp),  onClick = unselectDevice) {
            Text("Disconnect")
        }
    }
}
