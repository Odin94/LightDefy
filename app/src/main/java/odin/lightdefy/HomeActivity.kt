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


class HomeActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "HomeActivity"
    }

    private var lightbulbs = mutableListOf<Lightbulb>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupPermissions()

        val lightbulbsListView = findViewById<ListView>(R.id.lightbulbListView)
        lightbulbsListView.adapter = LightbulbAdapter(this, lightbulbs)

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
            val lightbulbsListView = findViewById<ListView>(R.id.lightbulbListView)
            lightbulbsListView.adapter = LightbulbAdapter(this, lightbulbs)
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
                getNameChangeDialog(context, lightbulb, view).show()
                true
            }

            view.lightSwitch.setOnClickListener {
                lightbulb.flickLightSwitch(context, view)
            }
            lightbulb.updateState(context, view)

            return view
        }

        override fun getItem(position: Int) = lightbulbs[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = lightbulbs.size

    }
}
