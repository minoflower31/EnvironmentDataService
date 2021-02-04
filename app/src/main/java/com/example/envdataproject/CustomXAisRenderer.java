package com.example.envdataproject;

import android.graphics.Canvas;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

class CustomXAxisRenderer extends XAxisRenderer {
    CustomXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
        super(viewPortHandler, xAxis, trans);
    }

    @Override
    protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
        String[] line = formattedLabel.split("\n");
        Utils.drawXAxisValue(c, line[0], x, y, mAxisLabelPaint, anchor, angleDegrees);
        for (int i = 1; i < line.length; i++) { // we've already processed 1st line
            Utils.drawXAxisValue(c, line[i], x, y + mAxisLabelPaint.getTextSize() * i,
                    mAxisLabelPaint, anchor, angleDegrees);
        }
    }
}
