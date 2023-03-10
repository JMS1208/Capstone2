package com.capstone.Capstone2Project.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SpeakerPhone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.theme.text_blue
import com.google.accompanist.permissions.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update




@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions(
    permissionState: MultiplePermissionsState
) {

    with(permissionState) {

        if(!allPermissionsGranted) {

            if(shouldShowRationale) {
                GoToSettingsForPermissionDialog(permissionState = permissionState)

            } else {
                PermissionRationaleDialog(permissionState)

            }

        }

    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun GoToSettingsForPermissionDialog(permissionState: MultiplePermissionsState) {

    val context = LocalContext.current

    DialogContent(
        permissionState = permissionState,
        buttonText = "??????"
    ) {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:" + context.packageName)
            context.startActivity(this)
        }
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionRationaleDialog(permissionState: MultiplePermissionsState) {

    DialogContent(
        permissionState = permissionState,
        buttonText = "??????"
    ) {
        permissionState.launchMultiplePermissionRequest()
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun DialogContent(
    permissionState: MultiplePermissionsState,
    buttonText: String,
    onClick: () -> Unit
) {
    val permissions: MutableList<String> = mutableListOf()

    permissionState.permissions.filter {
        !it.status.isGranted
    }.map {
        permissions.add(it.permission)
    }

    var showDialog by remember {
        mutableStateOf(true)
    }

    if (showDialog) {
        CompositionLocalProvider(
            LocalTextStyle provides TextStyle(
                fontFamily = CustomFont.nexonFont
            )
        ) {

            AlertDialog(
                onDismissRequest = {},
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "?????? ??????",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = CustomFont.nexonFont
                        )
                    }

                },
                shape = RoundedCornerShape(10.dp),
                text = {
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        PermissionListWithIcon(permissions = permissions)
                    }

                },
                dismissButton = null,
                confirmButton = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .wrapContentHeight()
                    ) {
                        IconButton(
                            onClick = {
                                showDialog = false
                                onClick()
                            },
                            modifier = Modifier
                                .background(color = Color.Transparent)
                                .align(Alignment.BottomEnd)
                        ) {

                            Text(
                                buttonText,
                                color = text_blue
                            )

                        }
                    }
                },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            )
        }
    }
}

@Composable
private fun PermissionListWithIcon(
    permissions: List<String>
) {
    val permissionsWithIcons = OurPermission.toOurPermissions(permissions)


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = CustomFont.nexonFont
        )
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("????????? ????????? ?????? ?????? ????????? ????????????")

            Spacer(modifier = Modifier.height(8.dp))

            permissionsWithIcons.forEach { permissionWithIcon ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = permissionWithIcon.iconResourceId),
                        contentDescription = null
                    )
                    Text(
                        text = permissionWithIcon.value
                    )
                }
            }
        }
    }

}


data class OurPermission(
    val value: String,
    @DrawableRes val iconResourceId: Int
) {

    companion object {
        private fun toOurPermission(permission: String): OurPermission? {
            return when (permission) {
                "android.permission.CAMERA" -> OurPermission("?????????", R.drawable.ic_camera)
                "android.permission.RECORD_AUDIO" -> OurPermission("??????", R.drawable.ic_record_audio)
                "android.permission.READ_EXTERNAL_STORAGE" -> OurPermission(
                    "??????????????? ??????",
                    R.drawable.ic_write_external_storage
                )
                "android.permission.WRITE_EXTERNAL_STORAGE" -> OurPermission(
                    "??????????????? ??????",
                    R.drawable.ic_read_external_storage
                )
                "android.permission.INTERNET" -> OurPermission(
                    "?????????",
                    R.drawable.ic_internet
                )
                else -> null
            }
        }

        fun toOurPermissions(permissions: List<String>): List<OurPermission> {

            val result = mutableListOf<OurPermission>()

            permissions.forEach { permission ->
                permission.toOurPermission()?.let {
                    result.add(it)
                }
            }

            return result
        }

        @JvmName("toOurPermission1")
        private fun String.toOurPermission(): OurPermission? {
            return Companion.toOurPermission(this)
        }
    }
}