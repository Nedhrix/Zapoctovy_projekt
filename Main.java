import shapes.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {

    enum ShapeType { PEN, RECT, OVAL, TRIANGLE }

    BufferedImage canvas;
    Graphics2D g2d;
    int startX, startY;
    Color currentColor = Color.BLACK;
    ShapeType currentShape = ShapeType.PEN;
    int strokeWidth = 2;

    List<DrawableShape> shapes = new ArrayList<>();

    public Main() {
        setTitle("Malování");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        canvas = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2d.setColor(currentColor);
        g2d.setStroke(new BasicStroke(strokeWidth));

        JPanel topPanel = new JPanel();
        JButton clearBtn = new JButton("Vymazat");
        JButton colorBtn = new JButton("Barva");
        JButton saveBtn = new JButton("Uložit jako JPG");

        JComboBox<String> shapeSelector = new JComboBox<>(new String[]{"Kreslit", "Čtverec", "Kolečko", "Trojúhelník"});
        JComboBox<Integer> strokeSelector = new JComboBox<>(new Integer[]{1, 2, 3, 5, 8, 12});
        strokeSelector.setSelectedItem(strokeWidth);

        strokeSelector.addActionListener(e -> {
            strokeWidth = (Integer) strokeSelector.getSelectedItem();
            g2d.setStroke(new BasicStroke(strokeWidth));
        });

        shapeSelector.addActionListener(e -> {
            switch (shapeSelector.getSelectedIndex()) {
                case 0 -> currentShape = ShapeType.PEN;
                case 1 -> currentShape = ShapeType.RECT;
                case 2 -> currentShape = ShapeType.OVAL;
                case 3 -> currentShape = ShapeType.TRIANGLE;
            }
        });

        colorBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Vyber barvu", currentColor);
            if (chosen != null) currentColor = chosen;
        });

        clearBtn.addActionListener(e -> {
            shapes.clear();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            repaint();
        });

        saveBtn.addActionListener(e -> ulozitJakoJPG());

        topPanel.add(colorBtn);
        topPanel.add(shapeSelector);
        topPanel.add(new JLabel("Tloušťka:"));
        topPanel.add(strokeSelector);
        topPanel.add(clearBtn);
        topPanel.add(saveBtn);
        add(topPanel, BorderLayout.NORTH);

        JPanel drawPanel = new JPanel() {
          protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(canvas, 0, 0, null);
                for (DrawableShape s : shapes) s.draw((Graphics2D) g);
            }
        };

        drawPanel.setBackground(Color.WHITE);

        drawPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }

            public void mouseReleased(MouseEvent e) {
                int endX = e.getX();
                int endY = e.getY();
                DrawableShape shape = switch (currentShape) {
                    case RECT -> new RectangleShape(startX, startY, endX, endY, currentColor, strokeWidth);
                    case OVAL -> new OvalShape(startX, startY, endX, endY, currentColor, strokeWidth);
                    case TRIANGLE -> new TriangleShape(startX, startY, endX, endY, currentColor, strokeWidth);
                    default -> null;
                };
                if (shape != null) shapes.add(shape);
                repaint();
            }
        });

        drawPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (currentShape == ShapeType.PEN) {
                    int x = e.getX();
                    int y = e.getY();
                    shapes.add(new PenStrokeSegment(startX, startY, x, y, currentColor, strokeWidth));
                    startX = x;
                    startY = y;
                    repaint();
                }
            }
        });

        add(drawPanel, BorderLayout.CENTER);
    }

    void ulozitJakoJPG() {
        BufferedImage output = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = output.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, output.getWidth(), output.getHeight());
        for (DrawableShape shape : shapes) shape.draw(g);
        g.dispose();

        try {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                var file = chooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".jpg")) {
                    file = new java.io.File(file.getAbsolutePath() + ".jpg");
                }
                javax.imageio.ImageIO.write(output, "jpg", file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
