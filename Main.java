import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Main extends javax.swing.JFrame {

    private enum Tvary {PEN, RECT, OVAL, TRIANGLE}

    private BufferedImage canvas;
    private Graphics2D g2d;
    private int startX, startY, endX, endY;
    private Color aktualiBarva = Color.BLACK;
    private Tvary aktualniTvar = Tvary.PEN;
    private int sirkaTahu = 2;

    private java.util.List<DrawableShape> shapes = new ArrayList<>();

    public Main() {
        setTitle("Malovaní");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        canvas = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 800, 600);
        g2d.setColor(aktualiBarva);
        g2d.setStroke(new BasicStroke(sirkaTahu));

        JPanel panel = new JPanel();

        JButton vymazBtn = new JButton("Vymazat");
        JButton barvaBtn = new JButton("Barva");
        JButton ulozitBtn = new JButton("Uložit jako JPG");

        String[] shapeOptions = {"Kreslit", "Čtverec", "Kolečko", "Trojúhelník"};
        JComboBox<String> shapeSelector = new JComboBox<>(shapeOptions);

        Integer[] strokeOptions = {1, 2, 3, 5, 8, 12};
        JComboBox<Integer> strokeSelector = new JComboBox<>(strokeOptions);
        strokeSelector.setSelectedItem(sirkaTahu);

        strokeSelector.addActionListener(e -> {
            sirkaTahu = (Integer) strokeSelector.getSelectedItem();
            g2d.setStroke(new BasicStroke(sirkaTahu));
        });

        vymazBtn.addActionListener(e -> {
            shapes.clear();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            repaint();
        });

        barvaBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Vyber barvu", aktualiBarva);
            if (newColor != null) {
                aktualiBarva = newColor;
            }
        });

        ulozitBtn.addActionListener(e -> ulozitJakoJPG());

        shapeSelector.addActionListener(e -> {
            switch (shapeSelector.getSelectedIndex()) {
                case 0 -> aktualniTvar = Tvary.PEN;
                case 1 -> aktualniTvar = Tvary.RECT;
                case 2 -> aktualniTvar = Tvary.OVAL;
                case 3 -> aktualniTvar = Tvary.TRIANGLE;
            }
        });

        panel.add(barvaBtn);
        panel.add(shapeSelector);
        panel.add(new JLabel("Tloušťka:"));
        panel.add(strokeSelector);
        panel.add(vymazBtn);
        panel.add(ulozitBtn);
        add(panel, BorderLayout.NORTH);

        JPanel drawPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(canvas, 0, 0, null);
                for (DrawableShape shape : shapes) {
                    shape.draw((Graphics2D) g);
                }
            }
        };

        drawPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }

            public void mouseReleased(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();
                shapes.add(new DrawableShape(aktualniTvar, startX, startY, endX, endY, aktualiBarva, sirkaTahu));
                repaint();
            }
        });

        drawPanel.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (aktualniTvar == Tvary.PEN) {
                    int x = e.getX();
                    int y = e.getY();
                    g2d.setColor(aktualiBarva);
                    g2d.setStroke(new BasicStroke(sirkaTahu));
                    g2d.drawLine(startX, startY, x, y);
                    startX = x;
                    startY = y;
                    repaint();
                }
            }
        });

        drawPanel.setBackground(Color.WHITE);
        add(drawPanel, BorderLayout.CENTER);
    }


    private void ulozitJakoJPG() {
        BufferedImage vystup = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = vystup.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, vystup.getWidth(), vystup.getHeight());
        g.drawImage(canvas, 0, 0, null);
        for (DrawableShape shape : shapes) {
            shape.draw(g);
        }
        g.dispose();

        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Uložit jako JPG");
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".jpg")) {
                    file = new java.io.File(file.getAbsolutePath() + ".jpg");
                }
                javax.imageio.ImageIO.write(vystup, "jpg", file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }

    static class DrawableShape {
        Tvary type;
        int x1, y1, x2, y2;
        Color color;
        int stroke;

        public DrawableShape(Tvary type, int x1, int y1, int x2, int y2, Color color, int stroke) {
            this.type = type;
            this.x1 = Math.min(x1, x2);
            this.y1 = Math.min(y1, y2);
            this.x2 = Math.max(x1, x2);
            this.y2 = Math.max(y1, y2);
            this.color = color;
            this.stroke = stroke;
        }

        public void draw(Graphics2D g2) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(stroke));
            int w = x2 - x1;
            int h = y2 - y1;

            switch (type) {
                case RECT -> g2.drawRect(x1, y1, w, h);
                case OVAL -> g2.drawOval(x1, y1, w, h);
                case TRIANGLE -> {
                    int[] xPoints = {x1 + w / 2, x1, x2};
                    int[] yPoints = {y1, y2, y2};
                    g2.drawPolygon(xPoints, yPoints, 3);
                }
            }
        }
    }
}
