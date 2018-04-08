package odin.lightdefy

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.lightbulb_list_elem.view.*


class Lightbulb(val id: String, var name: String, private var onOff: String, private var online: Boolean,
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

    fun flickLightSwitch(context: Context, view: View) {
        // TODO: make http request to light and call the rest of this function in a callback

        this.onOff = this.invertOnOff(onOff)
        updateState(context, view)
    }

    fun updateState(context: Context, view: View) {
        view.lightSwitch.text =
                if (this.onOff == "on")
                    context.getString(R.string.turn_off)
                else
                    context.getString(R.string.turn_on)

        val bulbImage = Lightbulb.imageMap[Pair(this.onOff, this.online)]
        view.bulb_image.setImageResource(bulbImage!!)
        view.name.text = this.name
    }

    private fun invertOnOff(onOff: String): String {
        if (onOff == "on") return "off"
        return "on"
    }
}

data class RGBW(var red: Int, var green: Int, var blue: Int, var white: Int)
data class HSV(var hue: Int, var saturation: Double, var brightness: Int)