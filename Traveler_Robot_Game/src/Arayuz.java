import javax.swing.*;
import java.awt.*;

public class Arayuz extends JFrame {

    public Arayuz() {
        setTitle("Gezgin Robot Projesi");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("background.png");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("Gezgin Robot Oyunu", JLabel.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(-200, 0, 0, 0);
        mainPanel.add(titleLabel, gbc);

        JLabel instructionLabel = new JLabel("Lütfen oyun modunu seçin", JLabel.CENTER);
        instructionLabel.setFont(new Font("Georgia", Font.PLAIN, 20));
        instructionLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        mainPanel.add(instructionLabel, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton problem1Button = createStyledButton("Problem 1");
        problem1Button.addActionListener(e -> {
            new Arayuz1();
            setVisible(false);
        });

        JButton problem2Button = createStyledButton("Problem 2");
        problem2Button.addActionListener(e -> {
            new Arayuz2();
            setVisible(false);
        });

        buttonPanel.add(problem1Button);
        buttonPanel.add(problem2Button);

        gbc.gridy = 2;
        gbc.insets = new Insets(30, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Georgia", Font.PLAIN, 20));
        button.setPreferredSize(new Dimension(200, 50));
        button.setBackground(new Color(50, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 2));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Arayuz());
    }
}