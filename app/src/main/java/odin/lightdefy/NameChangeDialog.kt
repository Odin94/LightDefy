package odin.lightdefy

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.EditText


fun getNameChangeDialog(context: Context, lightbulb: Lightbulb, lightbulbView: View): AlertDialog.Builder {
    val builder = AlertDialog.Builder(context)

    // set up input
    val input = EditText(context)
    builder.setView(input)
            .setTitle("Change name of ${lightbulb.name}")
            .setPositiveButton("OK", { dialog, _ ->
                dialog.dismiss()
                if (input.text.toString().trim().isNotEmpty()) {
                    lightbulb.name = input.text.toString()
                    lightbulb.updateState(lightbulbView)
                }
            })
            .setNegativeButton("Cancel", { dialog, _ ->
                dialog.cancel()
            })

    return builder
}