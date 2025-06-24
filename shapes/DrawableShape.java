package shapes;

import java.awt.*;

public abstract class DrawableShape {
    int x1, y1, x2, y2;
    Color color;
    int stroke;

    public DrawableShape(int x1, int y1, int x2, int y2, Color color, int stroke) {
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.color = color;
        this.stroke = stroke;
    }

    public abstract void draw(Graphics2D g2);
}
