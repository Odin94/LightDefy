package odin.lightdefy

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.lightbulb_list_elem.view.*


class HomeActivity : AppCompatActivity() {

    private val lightbulbs = mutableListOf<Lightbulb>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        lightbulbs.add(Lightbulb("Lightbulb 1 (on, con)", true, true))
        lightbulbs.add(Lightbulb("Lightbulb 2 (on, disc)", true, false))
        lightbulbs.add(Lightbulb("Lightbulb 3 (off, con)", false, true))


        val lightbulbsListView = findViewById<ListView>(R.id.lightbulbListView)
        lightbulbsListView.adapter = LightbulbAdapter(this, lightbulbs)
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
                lightbulb.flickLightSwitch(view)
            }
            lightbulb.updateState(view)

            return view
        }

        override fun getItem(position: Int) = lightbulbs[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = lightbulbs.size

    }
}
