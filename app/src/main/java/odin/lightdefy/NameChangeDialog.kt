package odin.lightdefy

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.EditText


fun getNameChangeDialog(context: Context, lightbulb: Lightbulb, lightbulbView: View): AlertDialog.Builder {
    val builder = AlertDialog.Builder(context)

    // set up input
    val input = EditText(context)
    input.setText(lightbulb.name)

    builder.setView(input)
            .setTitle(context.getString(R.string.change_name_of) + lightbulb.name)
            .setPositiveButton(context.getString(R.string.ok), { dialog, _ ->
                dialog.dismiss()
                if (input.text.toString().trim().isNotEmpty()) {
                    lightbulb.name = input.text.toString()
                    lightbulb.updateState(context, lightbulbView)
                }
            })
            .setNegativeButton(context.getString(R.string.cancel), { dialog, _ ->
                dialog.cancel()
            })

    return builder
}