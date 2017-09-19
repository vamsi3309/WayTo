package com.personal.vamsi.wayto;

import android.app.Dialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class BottomSheetActivity extends BottomSheetDialogFragment {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        String instructions;
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.activity_bottom_sheet, null);
        dialog.setContentView(contentView);
        TextView setInstructions = contentView.findViewById(R.id.instructions);
        setInstructions.setText(Html.fromHtml(getArguments().getString("key"),Html.FROM_HTML_MODE_COMPACT));
    }
}