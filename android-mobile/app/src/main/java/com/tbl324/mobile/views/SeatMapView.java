package com.tbl324.mobile.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tbl324.mobile.model.SeatGridModel;
import com.tbl324.mobile.model.SeatItem;
import com.tbl324.mobile.viewmodel.SeatViewModel;

public class SeatMapView extends View {

    private final float CELL_SIZE;
    private final float CELL_GAP;

    private SeatGridModel grid;
    private SeatViewModel viewModel;

    private Paint paintAvailable;
    private Paint paintLocked;
    private Paint paintSold;
    private Paint paintSelected;
    private final Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintLabel = new Paint(Paint.ANTI_ALIAS_FLAG);

    private OnSeatClickListener listener;

    public interface OnSeatClickListener {
        void onSeatClick(SeatItem seat, boolean nowSelected);
    }

    public SeatMapView(Context context) { super(context); CELL_SIZE = dp(context, 30); CELL_GAP = dp(context, 4); init(); }
    public SeatMapView(Context context, AttributeSet attrs) { super(context, attrs); CELL_SIZE = dp(context, 30); CELL_GAP = dp(context, 4); init(); }

    private static float dp(Context ctx, float v) {
        return v * ctx.getResources().getDisplayMetrics().density;
    }

    private void init() {
        paintAvailable = makePaint(Color.rgb(76, 175, 80));
        paintLocked    = makePaint(Color.rgb(255, 152, 0));
        paintSold      = makePaint(Color.rgb(244, 67, 54));
        paintSelected  = makePaint(Color.rgb(33, 150, 243));

        paintText.setColor(Color.WHITE);
        paintText.setTextSize(CELL_SIZE * 0.45f);
        paintText.setTextAlign(Paint.Align.CENTER);

        paintLabel.setColor(Color.DKGRAY);
        paintLabel.setTextSize(CELL_SIZE * 0.5f);
        paintLabel.setTextAlign(Paint.Align.RIGHT);
    }

    public void setGrid(SeatGridModel grid, SeatViewModel viewModel) {
        this.grid      = grid;
        this.viewModel = viewModel;
        requestLayout();
        invalidate();
    }

    public void setOnSeatClickListener(OnSeatClickListener l) { this.listener = l; }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int cols = grid != null ? grid.getColCount() : 0;
        int rows = grid != null ? grid.getRowCount() : 0;
        float step = CELL_SIZE + CELL_GAP;
        float labelW = CELL_SIZE * 1.2f;
        int w = (int) (labelW + cols * step + CELL_GAP);
        int h = (int) (rows * step + CELL_GAP);
        setMeasuredDimension(
                resolveSize(Math.max(w, getSuggestedMinimumWidth()), widthSpec),
                resolveSize(Math.max(h, getSuggestedMinimumHeight()), heightSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (grid == null || grid.getRowCount() == 0) return;
        float step = CELL_SIZE + CELL_GAP;
        float labelW = CELL_SIZE * 1.2f;

        for (int r = 0; r < grid.getRowCount(); r++) {
            // Row label (A, B, C...)
            char rowChar = (char) ('A' + r);
            float labelY = r * step + CELL_GAP + CELL_SIZE * 0.75f;
            canvas.drawText(String.valueOf(rowChar), labelW - CELL_GAP, labelY, paintLabel);

            for (int c = 0; c < grid.getColCount(); c++) {
                SeatItem seat = grid.getSeatAt(r, c);
                if (seat == null) continue;

                float left = labelW + c * step + CELL_GAP;
                float top  = r * step + CELL_GAP;
                RectF rect = new RectF(left, top, left + CELL_SIZE, top + CELL_SIZE);

                canvas.drawRoundRect(rect, 6f, 6f, paintFor(seat));
                canvas.drawText(
                        String.valueOf(seat.getSeatNumber()),
                        left + CELL_SIZE / 2f,
                        top + CELL_SIZE * 0.68f,
                        paintText);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP || grid == null) return true;
        float step = CELL_SIZE + CELL_GAP;
        float labelW = CELL_SIZE * 1.2f;
        float x = event.getX() - labelW - CELL_GAP;
        float y = event.getY() - CELL_GAP;
        if (x < 0 || y < 0) return true;
        int col = (int) (x / step);
        int row = (int) (y / step);
        SeatItem seat = grid.getSeatAt(row, col);
        if (seat != null && "AVAILABLE".equals(seat.getStatus())) {
            boolean nowSelected = viewModel.toggleSeat(seat);
            invalidate();
            if (listener != null) listener.onSeatClick(seat, nowSelected);
        }
        return true;
    }

    private Paint paintFor(SeatItem seat) {
        if (viewModel != null && viewModel.isSelected(seat)) return paintSelected;
        switch (seat.getStatus()) {
            case "LOCKED": return paintLocked;
            case "SOLD":   return paintSold;
            default:       return paintAvailable;
        }
    }

    private static Paint makePaint(int color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        p.setStyle(Paint.Style.FILL);
        return p;
    }
}
