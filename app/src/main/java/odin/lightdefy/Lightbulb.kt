package odin.lightdefy

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.lightbulb_list_elem.view.*


class Lightbulb(var name: String, private var onOff: Boolean, private var connected: Boolean) {
    companion object {
        // Pair(onOff: bool, connected: bool)
        val imageMap: Map<Pair<Boolean, Boolean>, Int> = mapOf(
                Pair(true, true) to R.drawable.light_on,
                Pair(false, true) to R.drawable.light_off,
                Pair(true, false) to R.drawable.light_disconnected,
                Pair(false, false) to R.drawable.light_disconnected
                // TODO: add separate image for "last status off, but disconnected now"?
        )
    }

    fun flickLightSwitch(context: Context, view: View) {
        // TODO: make http request to light and call the rest of this function in a callback

        onOff = !onOff
        updateState(context, view)
    }

    fun updateState(context: Context, view: View) {
        view.lightSwitch.text =
                if (onOff)
                    context.getString(R.string.turn_off)
                else
                    context.getString(R.string.turn_on)

        val bulbImage = Lightbulb.imageMap[Pair(onOff, connected)]
        view.bulb_image.setImageResource(bulbImage!!)
        view.name.text = name
    }
}