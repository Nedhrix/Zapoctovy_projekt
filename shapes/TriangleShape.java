package shapes;

import java.awt.*;

public class TriangleShape extends DrawableShape {
    public TriangleShape(int x1, int y1, int x2, int y2, Color color, int stroke) {
        super(x1, y1, x2, y2, color, stroke);
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(stroke));
        int[] xPoints = {x1 + (x2 - x1) / 2, x1, x2};
        int[] yPoints = {y1, y2, y2};
        g2.drawPolygon(xPoints, yPoints, 3);
    }
}
