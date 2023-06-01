import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class DrawingProgram extends JFrame {

    private int width;
    private int height;
    private Color[] colors;
    private Color color;
    private int blockSize;

    public DrawingProgram() {
        setTitle("Drawing Program");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Draw");
        JMenuItem customItem = new JMenuItem("Custom");
        customItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showCustomDialog();
            }
        });
        JMenuItem mosaicItem = new JMenuItem("Mosaic");
        mosaicItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMosaicDialog();
            }
        });

        menu.add(customItem);
        menu.add(mosaicItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Draw mosaic
        if (colors != null && blockSize > 0) {
            Random random = new Random();

            for (int y = centerY - (height / 2); y < centerY + (height / 2); y += blockSize) {
                for (int x = centerX - (width / 2); x < centerX + (width / 2); x += blockSize) {
                    Color color = colors[random.nextInt(colors.length)];
                    g2d.setColor(color);
                    g2d.fillRect(x, y, blockSize, blockSize);
                }
            }
        } else { g2d.setColor(color);
        g2d.fillRect(centerX - (width / 2), centerY - (height / 2), width, height);}
    }

    private void showMosaicDialog() {
        JTextField widthField = new JTextField(5);
        JTextField heightField = new JTextField(5);
        JComboBox<String> blockSizeCombo = new JComboBox<>(new String[]{"2x2", "4x4", "8x8"});

        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new GridLayout(2, 2));
        JButton[] colorButtons = new JButton[4];
        colors = new Color[4]; // Initialize the colors array
        for (int i = 0; i < colorButtons.length; i++) {
            colorButtons[i] = new JButton("Choose Color");
            int index = i; // to access the correct color button index inside the action listener
            colorButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Color color = JColorChooser.showDialog(null, "Choose Color", colors[index]);
                    if (color != null) {
                        colors[index] = color;
                        colorButtons[index].setBackground(color);
                    }
                }
            });
            colorPanel.add(colorButtons[i]);
        }

        JPanel panel = new JPanel();
        panel.add(new JLabel("Width:"));
        panel.add(widthField);
        panel.add(new JLabel("Height:"));
        panel.add(heightField);
        panel.add(new JLabel("Block Size:"));
        panel.add(blockSizeCombo);
        panel.add(new JLabel("Colors:"));
        panel.add(colorPanel);

        int result = JOptionPane.showConfirmDialog(null, panel, "Custom Parameters", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                width = Integer.parseInt(widthField.getText());
                height = Integer.parseInt(heightField.getText());
                blockSize = getBlockSizeFromCombo(blockSizeCombo);
                repaint();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input! Please enter numeric values for width, height, and block size.", "Error", JOptionPane.ERROR_MESSAGE);
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
        JTextField widthField = new JTextField(5);
        JTextField heightField = new JTextField(5);
        JButton colorButton = new JButton("Choose Color");
        colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                color = JColorChooser.showDialog(null, "Choose Color", color);
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Width:"));
        panel.add(widthField);
        panel.add(new JLabel("Height:"));
        panel.add(heightField);
        panel.add(colorButton);

        int result = JOptionPane.showConfirmDialog(null, panel, "Custom Parameters", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                width = Integer.parseInt(widthField.getText());
                height = Integer.parseInt(heightField.getText());
                repaint();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input! Please enter numeric values for width and height.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DrawingProgram();
        });
    }

}