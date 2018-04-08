package odin.lightdefy

import android.content.Context
import android.view.View


class Lightbulb(val id: String, var name: String, var onOff: String, var online: Boolean,
                val brightness: Int? = null, var colorTemperature: Int? = null, colorRGBW: RGBW? = null,
                var colorHSV: HSV? = null, var mode: String? = null, var deviceModel: String? = null,
                val firmwareVersion: String? = null) {
    companion object {
        // Pair(onOff: bool, online: bool)
        val imageMap: Map<Pair<String, Boolean>, Int> = mapOf(
                Pair("on", true) to R.drawable.light_on,
                Pair("off", true) to R.drawable.light_off,
                Pair("on", false) to R.drawable.light_disconnected,
                Pair("off", false) to R.drawable.light_disconnected
                // TODO: add separate image for "last status off, but disconnected now"?
        )

        fun fromMap(map: Map<String, String>): Lightbulb? {
            // TODO: Parse map  (if map[type] == "LIGHT")
            val id = map["id"]
            val name = map["name"]
            val onOff = map["onOff"]
            val online = map["online"] as Boolean?

            // TODO: read & add other properties
            if (id != null && name != null && onOff != null && online != null) {
                return Lightbulb(id, name, onOff, online)
            }
            return null
        }
    }

    fun flickLightSwitch(context: Context, view: View, onSuccess: (() -> Unit)? = null) {
        LightifyAccess.switchLight(this.id, this.invertOnOff(this.onOff), {
            this.onOff = this.invertOnOff(onOff)
            if (onSuccess != null) onSuccess()
        })
    }

    private fun invertOnOff(onOff: String): String {
        if (onOff == "on") return "off"
        return "on"
    }
}

data class RGBW(var red: Int, var green: Int, var blue: Int, var white: Int)
data class HSV(var hue: Int, var saturation: Double, var brightness: Int)