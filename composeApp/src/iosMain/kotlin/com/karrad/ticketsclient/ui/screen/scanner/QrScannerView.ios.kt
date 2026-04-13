package com.karrad.ticketsclient.ui.screen.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.AVFoundation.AVMetadataObjectTypeQRCode
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSError
import platform.UIKit.UIView
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

private enum class PermissionState { NOT_DETERMINED, GRANTED, DENIED }

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun QrScannerView(onScanned: (String) -> Unit, modifier: Modifier) {
    var permissionState by remember {
        mutableStateOf(
            when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
                AVAuthorizationStatusAuthorized -> PermissionState.GRANTED
                AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> PermissionState.DENIED
                else -> PermissionState.NOT_DETERMINED
            }
        )
    }

    DisposableEffect(Unit) {
        if (permissionState == PermissionState.NOT_DETERMINED) {
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                dispatch_async(dispatch_get_main_queue()) {
                    permissionState = if (granted) PermissionState.GRANTED else PermissionState.DENIED
                }
            }
        }
        onDispose {}
    }

    when (permissionState) {
        PermissionState.GRANTED -> UIKitView(
            factory = { IosQrScannerView(onScanned) },
            modifier = modifier,
            onRelease = { view -> view.stopSession() }
        )
        PermissionState.DENIED -> Box(
            modifier = modifier.background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Нет доступа к камере",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Разрешите доступ в Настройках → Конфиденциальность",
                    color = Color.White.copy(alpha = 0.65f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        PermissionState.NOT_DETERMINED -> Box(modifier.background(Color.Black))
    }
}

// ─── UIView с AVFoundation ──────────────────────────────────────────────────

@OptIn(ExperimentalForeignApi::class)
class IosQrScannerView(private val onScanned: (String) -> Unit) : UIView(CGRectZero.readValue()) {

    private val session = AVCaptureSession()
    private var previewLayer: AVCaptureVideoPreviewLayer? = null
    private val metadataDelegate = QrMetadataDelegate { raw ->
        onScanned(raw)
    }

    init {
        buildSession()
    }

    private fun buildSession() {
        val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) ?: return

        val input: AVCaptureDeviceInput? = memScoped {
            val errorRef = alloc<ObjCObjectVar<NSError?>>()
            AVCaptureDeviceInput(device = device, error = errorRef.ptr)
        }
        if (input != null && session.canAddInput(input)) {
            session.addInput(input)
        }

        val output = AVCaptureMetadataOutput()
        if (session.canAddOutput(output)) {
            session.addOutput(output)
            output.setMetadataObjectsDelegate(metadataDelegate, queue = dispatch_get_main_queue())
            output.metadataObjectTypes = listOf(AVMetadataObjectTypeQRCode)
        }

        val layer = AVCaptureVideoPreviewLayer(session = session)
        layer.videoGravity = AVLayerVideoGravityResizeAspectFill
        this.layer.addSublayer(layer)
        previewLayer = layer

        session.startRunning()
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        previewLayer?.frame = bounds
    }

    fun stopSession() {
        if (session.isRunning()) session.stopRunning()
    }
}

// ─── Делегат AVCaptureMetadataOutput ───────────────────────────────────────

class QrMetadataDelegate(private val onScanned: (String) -> Unit) :
    platform.darwin.NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {

    private var scanned = false

    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputMetadataObjects: List<*>,
        fromConnection: platform.AVFoundation.AVCaptureConnection
    ) {
        if (scanned) return
        val qrObject = didOutputMetadataObjects
            .filterIsInstance<AVMetadataMachineReadableCodeObject>()
            .firstOrNull()
        val raw = qrObject?.stringValue ?: return
        scanned = true
        onScanned(raw)
    }
}
