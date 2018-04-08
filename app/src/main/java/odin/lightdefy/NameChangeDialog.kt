package odin.lightdefy

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText


fun getNameChangeDialog(context: Context, lightbulb: Lightbulb): AlertDialog.Builder {
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
                    (context as? HomeActivity)?.updateLightbulbsView()
                }
            })
            .setNegativeButton(context.getString(R.string.cancel), { dialog, _ ->
                dialog.cancel()
            })

    return builder
}