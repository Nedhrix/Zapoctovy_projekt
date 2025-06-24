package shapes;

import java.awt.*;

public class PenStrokeSegment extends DrawableShape {
    int toX, toY;

    public PenStrokeSegment(int x1, int y1, int x2, int y2, Color color, int stroke) {
        super(x1, y1, x1, y1, color, stroke);
        this.toX = x2;
        this.toY = y2;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(stroke));
        g2.drawLine(x1, y1, toX, toY);
    }
}
