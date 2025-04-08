package com.tdcolvin.bleclient.ui.screens

import androidx.compose.foundation.layout.Column
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
import com.tdcolvin.bleclient.ble.DEVFEST_SERVICE_UUID

@Composable
fun DeviceScreen(
    modifier: Modifier = Modifier,
    unselectDevice: () -> Unit,
    isDeviceConnected: Boolean,
    discoveredCharacteristics: Map<String, List<String>>,
    password: String?,
    nameWrittenTimes: Int,
    connect: () -> Unit,
    discoverServices: () -> Unit,
    readPassword: () -> Unit,
    writeName: () -> Unit
) {
    val foundTargetService = discoveredCharacteristics.contains(DEVFEST_SERVICE_UUID.toString())

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
        Button(onClick = readPassword, enabled = isDeviceConnected && foundTargetService) {
            Text("3. Read Password")
        }
        if (password != null) {
            Text("Found password: $password")
        }
        Button(onClick = writeName, enabled = isDeviceConnected && foundTargetService) {
            Text("4. Write Your Name")
        }
        if (nameWrittenTimes > 0) {
            Text("Successful writes: $nameWrittenTimes")
        }

        OutlinedButton(modifier = Modifier.padding(top = 40.dp),  onClick = unselectDevice) {
            Text("Disconnect")
        }
    }
}
