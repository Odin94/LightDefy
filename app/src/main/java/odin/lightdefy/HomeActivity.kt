package odin.lightdefy

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.lightbulb_list_elem.view.*
import kotlin.properties.Delegates


class HomeActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "HomeActivity"
    }

    // automatically update view when lightbulbs is changed
    private var lightbulbs: MutableList<Lightbulb> by Delegates.observable(mutableListOf()) { property, oldValue, newValue ->
        this.updateLightbulbsView()
    }

    fun updateLightbulbsView(newLightbulbs: MutableList<Lightbulb>? = null) {
        if (newLightbulbs != null)
            this.lightbulbs = newLightbulbs
        else
            runOnUiThread {
                val lightbulbsListView = findViewById<ListView>(R.id.lightbulbListView)
                lightbulbsListView.adapter = LightbulbAdapter(this, lightbulbs)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupPermissions()

        runOnUiThread {
            val lightbulbsListView = findViewById<ListView>(R.id.lightbulbListView)
            lightbulbsListView.adapter = LightbulbAdapter(this, lightbulbs)
        }

        LightifyAccess.getTokens({
            this.getDevices()

            if (LightifyAccess.tokens == null) {
                LightifyAccess.authorize(this)
            }
        })
    }

    private fun getDevices() {
        LightifyAccess.getDevices({
            val newLightbulbs = mutableListOf<Lightbulb>()
            it.forEach { value: Map<String, String> ->
                Log.wtf(HomeActivity.TAG + " value", value::class.simpleName + " " + value.toString())
                val newLightbulb = Lightbulb.fromMap(value)

                if (newLightbulb != null) newLightbulbs.add(newLightbulb)
            }

            this.lightbulbs = newLightbulbs
        }, {
            // TODO: display disconnected-symbol or sth
            Log.wtf(HomeActivity.TAG + " getDevices failed!", it.toString())
            Handler().postDelayed({
                this.getDevices()
            }, 3000)
        })
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.e(HomeActivity.TAG, "Permission to access internet denied")
        }
    }

    private class LightbulbAdapter(val context: Context, val lightbulbs: MutableList<Lightbulb>) : BaseAdapter() {
        override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
            val lightbulb = getItem(position)
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.lightbulb_list_elem, null)

            val nameTextView = view.findViewById(R.id.name) as TextView
            nameTextView.paintFlags = nameTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            view.name.setOnLongClickListener {
                getNameChangeDialog(context, lightbulb).show()
                true
            }

            view.lightSwitch.setOnClickListener {
                lightbulb.flickLightSwitch(context, view, {
                    (context as? HomeActivity)?.updateLightbulbsView()
                })
            }

            view.lightSwitch.text =
                    if (lightbulb.onOff == "on")
                        context.getString(R.string.turn_off)
                    else
                        context.getString(R.string.turn_on)

            val bulbImage = Lightbulb.imageMap[Pair(lightbulb.onOff, lightbulb.online)]
            view.bulb_image.setImageResource(bulbImage!!)
            view.name.text = lightbulb.name

            return view
        }

        override fun getItem(position: Int) = lightbulbs[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = lightbulbs.size

    }
}
