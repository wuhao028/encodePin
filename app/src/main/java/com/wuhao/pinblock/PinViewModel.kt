package com.wuhao.pinblock

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import okhttp3.internal.and
import java.util.*
import kotlin.experimental.xor

class PinViewModel : ViewModel() {

    val pin = mutableStateOf("")
    val pinBlock = mutableStateOf("pinBlock")

    fun calculatePinBlock() {
        pinBlock.value = encodePin(pin.value, "1111222233334444")
    }

    private fun encodePin(pin: String, pan: String): String {
        var pinField: String = "3" + Integer.toHexString(pin.length) + pin
        val r = Random()
        for (i in 0 until 14 - pin.length) {
            pinField += Integer.toHexString(r.nextInt(16))
        }
        val panWithoutCheckDigit = pan.substring(0, pan.length - 1)

        val panField = if (panWithoutCheckDigit.length > 12) {
            "0000" + panWithoutCheckDigit
                .substring(
                    panWithoutCheckDigit.length - 12,
                    panWithoutCheckDigit.length
                )
        } else {
            "0000" + String.format(
                "%12s", panWithoutCheckDigit
            ).replace(' ', '0')
        }

        val pinFieldByteArray = h2b(pinField)
        val panFieldByteArray = h2b(panField)
        val pinBlockByteArray = ByteArray(8)
        for (i in 0..7) {
            pinBlockByteArray[i] = (pinFieldByteArray[i] xor panFieldByteArray[i])
        }
        return b2h(pinBlockByteArray).uppercase(Locale.getDefault())
    }

    private fun h2b(s: String?): ByteArray {
        s?.let {
            val len = s.length
            val data = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                        + Character.digit(s[i + 1], 16)).toByte()
                i += 2
            }
            return data
        }
        return ByteArray(0)
    }

    private val hexArray = charArrayOf(
        '0',
        '1',
        '2',
        '3',
        '4',
        '5',
        '6',
        '7',
        '8',
        '9',
        'A',
        'B',
        'C',
        'D',
        'E',
        'F'
    )

    private fun b2h(bytes: ByteArray): String {

        val hexChars = CharArray(bytes.size * 2)
        var v: Int
        for (j in bytes.indices) {
            v = bytes[j] and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

}