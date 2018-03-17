package odin.lightdefy

import android.view.View
import kotlinx.android.synthetic.main.lightbulb_list_elem.view.*

// TODO: put image assets in here? Maybe a map of status -> image?

class Lightbulb(var name: String, var on: Boolean, var connected: Boolean) {
    companion object {
        // Pair(onOff, connected)
        val imageMap: Map<Pair<Boolean, Boolean>, Int> = mapOf(
                Pair(true, true) to R.drawable.light_on,
                Pair(false, true) to R.drawable.light_off,
                Pair(true, false) to R.drawable.light_disconnected,
                Pair(false, false) to R.drawable.light_disconnected
                // TODO: add separate image for "last status off, but disconnected now"?
        )
    }

    fun flickLightSwitch(view: View) {
        // TODO: make http request to light and call the rest of this function in a callback

        on = !on
        updateState(view)
    }

    fun updateState(view: View) {
        view.lightSwitch.text = if (on) "Turn Off" else "Turn On"
        val bulbImage = Lightbulb.imageMap[Pair(on, connected)]
        view.bulb_image.setImageResource(bulbImage!!)
        view.name.text = name
    }
}