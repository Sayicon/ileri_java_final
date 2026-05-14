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

    private static final float CELL_SIZE = 48f;
    private static final float CELL_GAP  = 6f;

    private SeatGridModel grid;
    private SeatViewModel viewModel;

    private final Paint paintAvailable = makePaint(Color.rgb(76, 175, 80));
    private final Paint paintLocked    = makePaint(Color.rgb(255, 152, 0));
    private final Paint paintSold      = makePaint(Color.rgb(244, 67, 54));
    private final Paint paintSelected  = makePaint(Color.rgb(33, 150, 243));
    private final Paint paintText      = new Paint(Paint.ANTI_ALIAS_FLAG);

    private OnSeatClickListener listener;

    public interface OnSeatClickListener {
        void onSeatClick(SeatItem seat, boolean nowSelected);
    }

    public SeatMapView(Context context) { super(context); init(); }
    public SeatMapView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        paintText.setColor(Color.WHITE);
        paintText.setTextSize(28f);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setAntiAlias(true);
    }

    public void setGrid(SeatGridModel grid, SeatViewModel viewModel) {
        this.grid      = grid;
        this.viewModel = viewModel;
        invalidate();
    }

    public void setOnSeatClickListener(OnSeatClickListener l) { this.listener = l; }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int cols = grid != null ? grid.getColCount() : 10;
        int rows = grid != null ? grid.getRowCount() : 1;
        int w = (int) (cols * (CELL_SIZE + CELL_GAP) + CELL_GAP);
        int h = (int) (rows * (CELL_SIZE + CELL_GAP) + CELL_GAP);
        setMeasuredDimension(
                resolveSize(w, widthSpec),
                resolveSize(h, heightSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (grid == null) return;
        float step = CELL_SIZE + CELL_GAP;
        for (int r = 0; r < grid.getRowCount(); r++) {
            for (int c = 0; c < grid.getColCount(); c++) {
                SeatItem seat = grid.getSeatAt(r, c);
                if (seat == null) continue;

                float left = c * step + CELL_GAP;
                float top  = r * step + CELL_GAP;
                RectF rect = new RectF(left, top, left + CELL_SIZE, top + CELL_SIZE);

                Paint p = paintFor(seat);
                canvas.drawRoundRect(rect, 8f, 8f, p);
                canvas.drawText(String.valueOf(seat.getId()),
                        left + CELL_SIZE / 2f, top + CELL_SIZE / 2f + 9f, paintText);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP || grid == null) return true;
        float step = CELL_SIZE + CELL_GAP;
        SeatItem seat = grid.getSeatByPixel(
                event.getX() - CELL_GAP, event.getY() - CELL_GAP, step);
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
