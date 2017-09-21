package com.personal.vamsi.wayto

import android.app.Dialog
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.text.Html
import android.view.View
import android.widget.TextView

class BottomSheetActivity : BottomSheetDialogFragment() {
    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun setupDialog(dialog: Dialog, style: Int) {
        val instructions: String
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.activity_bottom_sheet, null)
        dialog.setContentView(contentView)
        val setInstructions = contentView.findViewById<TextView>(R.id.instructions)
        setInstructions.text = Html.fromHtml(arguments.getString("key"), Html.FROM_HTML_MODE_COMPACT)
    }
}