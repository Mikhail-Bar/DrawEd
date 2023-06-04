import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.awt.image.*;
import java.util.Random;
import javax.imageio.*;
import javax.swing.filechooser.FileFilter;

public class DrawingProgram {


    boolean loading = false;
    int width = 0;
    int height = 0;
    Color[] colors;
    Color color;
    int blockSize = 1;

    String filename;
    BufferedImage image;
    Color maincolor;
    DrawFrame drawFrame;
    DrawPanel drawPanel;
    Rectangle userRectangle;
    Line line;
    JButton clipButton,deleteButton,moveButton,lineButton,drawRectButton;

    public enum Mode {
        DRAW, MOVE, NONE, LINE
    }

    public Mode mode;


    class DrawFrame extends JFrame {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
        }

        public DrawFrame(String title) {
            super(title);
        }
    }

    class DrawPanel extends JPanel {
        private Graphics2D graphics;

        public DrawPanel() {
            setPreferredSize(new Dimension(1200, 1200));
            image = new BufferedImage(getPreferredSize().width, getPreferredSize().height, BufferedImage.TYPE_INT_RGB);
            graphics = image.createGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
            graphics.dispose();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image == null) {
                image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D d2 = (Graphics2D) image.createGraphics();
                d2.setColor(Color.white);
                d2.fillRect(0, 0, this.getWidth(), this.getHeight());
                d2.dispose();
            }
            graphics = (Graphics2D) g;
            g.drawImage(image, 0, 0, this);


        }
        private void drawLine(Graphics g) {
            if (line.isSet()) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.BLUE);
                g2d.drawLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
            }
        }

        private void drawUserRectangle(Graphics g) {
            if (userRectangle != null) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.GREEN);
                g2d.drawRect(userRectangle.x, userRectangle.y, userRectangle.width, userRectangle.height);
            }
        }
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g;

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            Graphics2D imageGraphics = image.createGraphics();
            super.paint(imageGraphics);

            if (colors != null && blockSize > 0) {
                Random random = new Random();

                for (int y = centerY - (height / 2); y < centerY + (height / 2); y += blockSize) {
                    for (int x = centerX - (width / 2); x < centerX + (width / 2); x += blockSize) {
                        Color color = colors[random.nextInt(colors.length)];
                        g2d.setColor(color);
                        g2d.fillRect(x, y, blockSize, blockSize);
                        imageGraphics.setColor(color);
                        imageGraphics.fillRect(x, y, blockSize, blockSize);
                    }
                }
            } else {
                drawLine(g);
                drawUserRectangle(g);
                g2d.setColor(color);
                g2d.fillRect(centerX - (width / 2), centerY - (height / 2), width, height);
                imageGraphics.setColor(color);
                imageGraphics.fillRect(centerX - (width / 2), centerY - (height / 2), width, height);

            }
            imageGraphics.dispose();

        }
    }

    class TextFileFilter extends FileFilter {
        private String ext;

        public TextFileFilter(String ext) {
            this.ext = ext;
        }

        public boolean accept(java.io.File file) {
            if (file.isDirectory()) return true;
            return (file.getName().endsWith(ext));
        }

        public String getDescription() {
            return "*" + ext;
        }
    }

    public DrawingProgram() {
        drawFrame = new DrawFrame("Приложение для рисования");
        drawFrame.setSize(1150, 1000);
        drawFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        maincolor = Color.BLACK;
        userRectangle = null;
        line = new Line();
        mode = Mode.NONE;

        JMenuBar menuBar = new JMenuBar();
        drawFrame.setJMenuBar(menuBar);
        menuBar.setBounds(0, 0, 1000, 30);
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        JMenu DrawMenu = new JMenu("Отрисовать");
        menuBar.add(DrawMenu);
        Action objectAction = new AbstractAction("Объект") {
            public void actionPerformed(ActionEvent event) {
                showCustomDialog();
            }
        };
        JMenuItem objectDraw = new JMenuItem(objectAction);
        DrawMenu.add(objectDraw);
        Action mosaicAction = new AbstractAction("Мозаика") {
            public void actionPerformed(ActionEvent event) {
                showMosaicDialog();
            }
        };
        JMenuItem mosaicDraw = new JMenuItem(mosaicAction);
        DrawMenu.add(mosaicDraw);

        Action BresenhamAction = new AbstractAction("Алгоритм Брезенхема") {
            public void actionPerformed(ActionEvent event) {
                showBresenhamDialog();
            }
        };
        JMenuItem BresenhamDraw = new JMenuItem(BresenhamAction);
        DrawMenu.add(BresenhamDraw);
        Action CohenSutherlandAction = new AbstractAction("Алгоритма Коэна-Сазерленда") {
            public void actionPerformed(ActionEvent event) {
                showCohenSutherland();
            }
        };
        JMenuItem CohenSutherlandDraw = new JMenuItem(CohenSutherlandAction);
        DrawMenu.add(CohenSutherlandDraw);
        Action loadAction = new AbstractAction("Загрузить") {
            public void actionPerformed(ActionEvent event) {
                JFileChooser jf = new JFileChooser();
                int result = jf.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    try {
                        filename = jf.getSelectedFile().getAbsolutePath();
                        File iF = new File(filename);
                        jf.addChoosableFileFilter(new TextFileFilter(".png"));
                        jf.addChoosableFileFilter(new TextFileFilter(".jpg"));
                        image = ImageIO.read(iF);
                        loading = true;
                        drawFrame.setSize(image.getWidth() + 40, image.getWidth() + 80);
                        drawPanel.setSize(image.getWidth(), image.getWidth());
                        drawPanel.repaint();
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(drawFrame, "Такого файла не существует");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(drawFrame, "Исключение ввода-вывода");
                    } catch (Exception ex) {
                    }
                }
            }
        };
        JMenuItem loadMenu = new JMenuItem(loadAction);
        fileMenu.add(loadMenu);

        Action saveAction = new AbstractAction("Сохранить") {
            public void actionPerformed(ActionEvent event) {
                try {
                    JFileChooser jf = new JFileChooser();
                    TextFileFilter pngFilter = new TextFileFilter(".png");
                    TextFileFilter jpgFilter = new TextFileFilter(".jpg");
                    if (filename == null) {
                        jf.addChoosableFileFilter(pngFilter);
                        jf.addChoosableFileFilter(jpgFilter);
                        int result = jf.showSaveDialog(null);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            filename = jf.getSelectedFile().getAbsolutePath();
                        }
                    }
                    if (jf.getFileFilter() == pngFilter) {
                        ImageIO.write(image, "png", new File(filename + ".png"));
                    } else {
                        ImageIO.write(image, "jpeg", new File(filename + ".jpg"));
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(drawFrame, "Ошибка ввода-вывода");
                }
            }
        };
        JMenuItem saveMenu = new JMenuItem(saveAction);
        fileMenu.add(saveMenu);

        Action saveasAction = new AbstractAction("Сохранить как...") {
            public void actionPerformed(ActionEvent event) {
                try {
                    JFileChooser jf = new JFileChooser();
                    TextFileFilter pngFilter = new TextFileFilter(".png");
                    TextFileFilter jpgFilter = new TextFileFilter(".jpg");
                    jf.addChoosableFileFilter(pngFilter);
                    jf.addChoosableFileFilter(jpgFilter);
                    int result = jf.showSaveDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        filename = jf.getSelectedFile().getAbsolutePath();
                    }
                    if (jf.getFileFilter() == pngFilter) {
                        ImageIO.write(image, "png", new File(filename + ".png"));
                    } else {
                        ImageIO.write(image, "jpeg", new File(filename + ".jpg"));
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(drawFrame, "Ошибка ввода-вывода");
                }
            }
        };
        JMenuItem saveasMenu = new JMenuItem(saveasAction);
        fileMenu.add(saveasMenu);

        drawPanel = new DrawPanel();
        drawPanel.setBounds(50, 40, 1000, 800);
        drawPanel.setBackground(Color.WHITE);
        drawPanel.setOpaque(true);
        drawFrame.add(drawPanel);
        drawFrame.setLayout(null);
        drawFrame.setVisible(true);

        JPanel buttonPanel = new JPanel();
        clipButton = new JButton("Отсечение отрезка");
        clipButton.setVisible(false);
        clipButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clipLine();
                drawPanel.repaint();
            }
        });

        deleteButton = new JButton("Удаление прямоугольника");
        deleteButton.setVisible(false);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRectangle();
                mode = Mode.NONE;
                drawPanel.repaint();
            }
        });
        drawRectButton = new JButton("Рисование прямоугольника");
        drawRectButton.setVisible(false);
        drawRectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = Mode.DRAW;
                drawPanel.repaint();
            }
        });
        moveButton = new JButton("Перемещение прямоугольника");
        moveButton.setVisible(false);
        moveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = Mode.MOVE;
                drawPanel.repaint();
            }
        });
        lineButton = new JButton("Рисование отрезка");
        lineButton.setVisible(false);
        lineButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mode = Mode.LINE;
                drawPanel.repaint();
            }
        });
        buttonPanel.add(drawRectButton);
        buttonPanel.add(moveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(lineButton);
        buttonPanel.add(clipButton);

        drawPanel.add(buttonPanel, BorderLayout.SOUTH);
        drawPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (mode == Mode.DRAW) {
                    userRectangle = new Rectangle(e.getX(), e.getY(), 0, 0);
                }else if (mode == Mode.LINE) {
                    line.setStartPoint(e.getX(), e.getY());
                }

                drawPanel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (mode == Mode.DRAW) {
                    if (userRectangle != null) {
                        int width = e.getX() - userRectangle.x;
                        int height = e.getY() - userRectangle.y;
                        userRectangle.width = Math.abs(width);
                        userRectangle.height = Math.abs(height);
                    }
                }else if (mode == Mode.LINE) {
                    line.setEndPoint(e.getX(), e.getY());
                }

                drawPanel.repaint();
            }
        });

        drawPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (mode == Mode.DRAW) {
                    if (userRectangle != null) {
                        int width = e.getX() - userRectangle.x;
                        int height = e.getY() - userRectangle.y;
                        userRectangle.width = Math.abs(width);
                        userRectangle.height = Math.abs(height);
                    }
                }else if (mode == Mode.MOVE) {
                    if (userRectangle != null && userRectangle.contains(e.getX(), e.getY())) {
                        userRectangle.x = e.getX() - userRectangle.width / 2;
                        userRectangle.y = e.getY() - userRectangle.height / 2;
                    }
                }else if (mode == Mode.LINE) {
                    line.setEndPoint(e.getX(), e.getY());
                }

                drawPanel.repaint();
            }
        });
    }
    private void showCohenSutherland(){
        clipButton.setVisible(true);
        deleteButton.setVisible(true);
        lineButton.setVisible(true);
        moveButton.setVisible(true);
        drawRectButton.setVisible(true);
        drawPanel.getGraphics().clearRect(0, 0, drawPanel.getWidth(), drawPanel.getHeight());

    }
    private void showMosaicDialog() {
        clipButton.setVisible(false);
        deleteButton.setVisible(false);
        lineButton.setVisible(false);
        moveButton.setVisible(false);
        drawRectButton.setVisible(false);

        JTextField widthField = new JTextField(5);
        JTextField heightField = new JTextField(5);
        JComboBox<String> blockSizeCombo = new JComboBox<>(new String[]{"2x2", "4x4", "8x8"});

        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new GridLayout(2, 2));
        JButton[] colorButtons = new JButton[4];
        colors = new Color[4];
        for (int i = 0; i < colorButtons.length; i++) {
            colorButtons[i] = new JButton("Выбор цвета");
            int index = i;
            colorButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Color color = JColorChooser.showDialog(null, "Выбор цвета", colors[index]);
                    if (color != null) {
                        colors[index] = color;
                        colorButtons[index].setBackground(color);
                    }
                }
            });
            colorPanel.add(colorButtons[i]);
        }

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ширина:"));
        panel.add(widthField);
        panel.add(new JLabel("Высота:"));
        panel.add(heightField);
        panel.add(new JLabel("Размер блока:"));
        panel.add(blockSizeCombo);
        panel.add(new JLabel("Цвет:"));
        panel.add(colorPanel);

        int result = JOptionPane.showConfirmDialog(drawPanel, panel, "Параметры рисования", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                drawPanel.getGraphics().clearRect(0, 0, drawPanel.getWidth(), drawPanel.getHeight());
                width = Integer.parseInt(widthField.getText());
                height = Integer.parseInt(heightField.getText());
                blockSize = getBlockSizeFromCombo(blockSizeCombo);
                drawPanel.repaint();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(drawPanel, "Ошибка ввода данных! Введите числовые значения ширины, высоты и размер блока.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getBlockSizeFromCombo(JComboBox<String> combo) {
        String selectedSize = (String) combo.getSelectedItem();
        switch (selectedSize) {
            case "2x2":
                return 2;
            case "4x4":
                return 4;
            case "8x8":
                return 8;
            default:
                return 1;
        }
    }

    private void showCustomDialog() {
        clipButton.setVisible(false);
        deleteButton.setVisible(false);
        lineButton.setVisible(false);
        moveButton.setVisible(false);
        drawRectButton.setVisible(false);

        JTextField widthField = new JTextField(5);
        JTextField heightField = new JTextField(5);
        JButton colorButton = new JButton("Выбор цвета");
        colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                color = JColorChooser.showDialog(null, "Выбор цвета", color);
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ширина:"));
        panel.add(widthField);
        panel.add(new JLabel("Высота:"));
        panel.add(heightField);
        panel.add(colorButton);

        int result = JOptionPane.showConfirmDialog(drawPanel, panel, "Параметры рисования", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                drawPanel.getGraphics().clearRect(0, 0, drawPanel.getWidth(), drawPanel.getHeight());
                width = Integer.parseInt(widthField.getText());
                height = Integer.parseInt(heightField.getText());
                drawPanel.repaint();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(drawPanel, "Ошибка ввода данных! Введите числовые значения ширины, высоты.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void showBresenhamDialog() {
        clipButton.setVisible(false);
        deleteButton.setVisible(false);
        lineButton.setVisible(false);
        moveButton.setVisible(false);
        drawRectButton.setVisible(false);

        JPanel panel = new JPanel();
        JLabel phi1Label = new JLabel("Начальный угол:");
        JTextField phi1Field = new JTextField("  0");
        JLabel phi2Label = new JLabel("Конечный угол:");
        JTextField phi2Field = new JTextField("360");
        JLabel deltaPhiLabel = new JLabel("Шаг угла:");
        JTextField deltaPhiField = new JTextField("0.01");
        panel.add(phi1Label);
        panel.add(phi1Field);
        panel.add(phi2Label);
        panel.add(phi2Field);
        panel.add(deltaPhiLabel);
        panel.add(deltaPhiField);

        int result = JOptionPane.showConfirmDialog(drawPanel, panel, "Алгоритм Брезенхема", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                double phi1 = Double.parseDouble(phi1Field.getText());
                double phi2 = Double.parseDouble(phi2Field.getText());
                double deltaPhi = Double.parseDouble(deltaPhiField.getText());

                drawPanel.getGraphics().clearRect(0, 0, drawPanel.getWidth(), drawPanel.getHeight());
                drawPrimitives(phi1, phi2, deltaPhi);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(drawPanel, "Ошибка ввода данных! Введите числовые значения ширины, высоты.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void drawLine(Graphics g) {
        if (line.isSet()) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLUE);
            g2d.drawLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
        }
    }

    private void drawUserRectangle(Graphics g) {
        if (userRectangle != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.GREEN);
            g2d.drawRect(userRectangle.x, userRectangle.y, userRectangle.width, userRectangle.height);
        }
    }

    private void clipLine() {
        if (line.isSet()) {
            int startX = line.getStartX();
            int startY = line.getStartY();
            int endX = line.getEndX();
            int endY = line.getEndY();

            int codeStart = calculateCode(startX, startY);
            int codeEnd = calculateCode(endX, endY);

            boolean isVisible = false;

            while (true) {
                if ((codeStart | codeEnd) == 0) {
                    isVisible = true;
                    break;
                } else if ((codeStart & codeEnd) != 0) {
                    break;
                } else {
                    int x = 0, y = 0;
                    int code = (codeStart != 0) ? codeStart : codeEnd;

                    if ((code & Rectangle.OUT_LEFT) != 0) {
                        x = userRectangle.x;
                        y = startY + (endY - startY) * (x - startX) / (endX - startX);
                    } else if ((code & Rectangle.OUT_RIGHT) != 0) {
                        x = userRectangle.x + userRectangle.width;
                        y = startY + (endY - startY) * (x - startX) / (endX - startX);
                    } else if ((code & Rectangle.OUT_BOTTOM) != 0) {
                        y = userRectangle.y + userRectangle.height;
                        x = startX + (endX - startX) * (y - startY) / (endY - startY);
                    } else if ((code & Rectangle.OUT_TOP) != 0) {
                        y = userRectangle.y;
                        x = startX + (endX - startX) * (y - startY) / (endY - startY);
                    }

                    if (code == codeStart) {
                        startX = x;
                        startY = y;
                        codeStart = calculateCode(startX, startY);
                    } else {
                        endX = x;
                        endY = y;
                        codeEnd = calculateCode(endX, endY);
                    }
                }
            }

            if (isVisible) {
                line.setStartPoint(startX, startY);
                line.setEndPoint(endX, endY);
            } else {
                line.reset();
            }
        }
    }

    private void deleteRectangle() {
        userRectangle = null;
        mode = Mode.NONE;
    }

    private int calculateCode(int x, int y) {
        int code = 0;

        if (x < userRectangle.x)
            code |= Rectangle.OUT_LEFT;
        else if (x > userRectangle.x + userRectangle.width)
            code |= Rectangle.OUT_RIGHT;

        if (y < userRectangle.y)
            code |= Rectangle.OUT_TOP;
        else if (y > userRectangle.y + userRectangle.height)
            code |= Rectangle.OUT_BOTTOM;

        return code;
    }
    private void drawPrimitives(double phi1, double phi2, double deltaPhi) {
        double centerX = drawPanel.getWidth() / 2.0;
        double centerY = drawPanel.getHeight() / 2.0;
        double fixedRadius = Math.min(drawPanel.getWidth(), drawPanel.getHeight()) / 2.0;

        Graphics2D g2d = (Graphics2D) drawPanel.getGraphics();
        g2d.setStroke(new BasicStroke(2.0f));

        double phi = phi1;

        while (phi <= phi2) {
            double radius1 = 1 - (2.0/3.0)*Math.pow(Math.sin(2*Math.cos(phi/2)),2);
            double radius2 = 1.0/2.0;

            double x1 = centerX + radius1 * Math.cos(Math.toRadians(phi)) * fixedRadius;
            double y1 = centerY - radius1 * Math.sin(Math.toRadians(phi)) * fixedRadius;

            double x2 = centerX + radius2 * Math.cos(Math.toRadians(phi)) * fixedRadius;
            double y2 = centerY - radius2 * Math.sin(Math.toRadians(phi)) * fixedRadius;

            float hue = (float) (phi / 360.0);
            float saturation = 1.0f;
            float brightness = 1.0f;
            float alpha = 0.5f;

            Color color = Color.getHSBColor(hue, saturation, brightness);
            Color transparentColor = new Color(
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    Math.round(alpha * 255)
            );

            g2d.setColor(transparentColor);

            drawBresenhamLine((int) centerX, (int) centerY, (int) x1, (int) y1, g2d);
            drawBresenhamLine((int) centerX, (int) centerY, (int) x2, (int) y2, g2d);

            phi += deltaPhi;
        }

        g2d.setColor(Color.BLACK);

        drawBresenhamCircle((int) centerX, (int) centerY, (int) fixedRadius, g2d);
        drawBresenhamCircle((int) centerX, (int) centerY, (int) (fixedRadius * (1.0 / 3.0)), g2d);

    }

    private void drawBresenhamLine(int x1, int y1, int x2, int y2, Graphics2D g2d) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (x1 != x2 || y1 != y2) {
            g2d.drawLine(x1, y1, x1, y1);
            int err2 = 2 * err;

            if (err2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (err2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    private void drawBresenhamCircle(int centerX, int centerY, int radius, Graphics2D g2d) {
        int x = 0;
        int y = radius;
        int delta = 1 - 2 * radius;
        int error = 0;

        while (y >= 0) {
            g2d.fillRect(centerX + x, centerY + y, 1, 1);
            g2d.fillRect(centerX + x, centerY - y, 1, 1);
            g2d.fillRect(centerX - x, centerY + y, 1, 1);
            g2d.fillRect(centerX - x, centerY - y, 1, 1);

            error = 2 * (delta + y) - 1;
            if ((delta < 0) && (error <= 0)) {
                delta += 2 * ++x + 1;
                continue;
            }
            if ((delta > 0) && (error > 0)) {
                delta -= 2 * --y + 1;
                continue;
            }
            delta += 2 * (++x - y--);
        }
    }
    class Line {
        private int startX, startY, endX, endY;
        private boolean isSet;

        public Line() {
            isSet = false;
        }

        public void setStartPoint(int x, int y) {
            startX = x;
            startY = y;
            isSet = true;
        }

        public void setEndPoint(int x, int y) {
            endX = x;
            endY = y;
        }

        public int getStartX() {
            return startX;
        }

        public int getStartY() {
            return startY;
        }

        public int getEndX() {
            return endX;
        }

        public int getEndY() {
            return endY;
        }

        public boolean isSet() {
            return isSet;
        }

        public void reset() {
            isSet = false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DrawingProgram();
            }
        });
    }

}
