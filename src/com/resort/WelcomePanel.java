package com.resort;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

public class WelcomePanel extends ResortPanel {
    private final JButton enterButton = new JButton("Enter");
    private ActionListener enterButtonListener;
    private final ImageIcon logo = loadLogo("/assets/MtBullerResort_Logo.png", 150);

    WelcomePanel(MtBullerResort resort, ResortGUI gui) {
        super("Welcome", resort, gui);
        createComponents();
    }

    @Override
    protected void createComponents() {

        // ---- Title label formatting ---- //
        JLabel titleLabel = new JLabel("Welcome to Mt Buller Resort", logo, SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        titleLabel.setForeground(ResortGUI.getPrimaryColour());
        titleLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);

        // ---- Description label formatting ---- //
        JLabel descriptionLabel = new JLabel("Create custom travel bundles for customers and their families");
        descriptionLabel.setForeground(Color.GRAY);
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // ---- Enter button formatting ---- //
        enterButton.setBackground(ResortGUI.getPrimaryColour());
        enterButton.setForeground(Color.WHITE);
        enterButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        enterButton.setBorderPainted(false);
        enterButton.setOpaque(true);
        enterButton.setHorizontalAlignment(SwingConstants.CENTER);
        enterButton.setPreferredSize(new Dimension(180, 35));
        enterButton.addActionListener(e -> {
            if ((enterButtonListener != null)) {
                enterButtonListener.actionPerformed(e);
            }
        });

        // Positioning and constraints, adding to Panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 0, 10, 0);
        this.add(titleLabel, gbc);

        gbc.insets = new Insets(0, 0, 40, 0);
        gbc.gridy = 1;
        this.add(descriptionLabel, gbc);

        gbc.gridy = 2;
        this.add(enterButton, gbc);
    }

    private static ImageIcon loadLogo(String path, int width) {
        try {
            BufferedImage src = ImageIO.read(Objects.requireNonNull(
                    WelcomePanel.class.getResource(path), "Missing resource: " + path));
            int height = Math.round(width * src.getHeight() / (float) src.getWidth());

            double[] scales = {1.0, 1.25, 1.5, 2.0, 3.0};
            Image[] variants = new Image[scales.length];
            for (int i = 0; i < scales.length; i++) {
                variants[i] = scale(src, (int) (width * scales[i]), (int) (height * scales[i]));
            }
            return new ImageIcon(new BaseMultiResolutionImage(variants));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** High-quality downscale: halves repeatedly instead of shrinking in one big jump. */
    private static BufferedImage scale(BufferedImage src, int targetW, int targetH) {
        BufferedImage img = src;
        int w = src.getWidth(), h = src.getHeight();
        do {
            w = Math.max(targetW, w / 2);
            h = Math.max(targetH, h / 2);
            BufferedImage tmp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = tmp.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(img, 0, 0, w, h, null);
            g.dispose();
            img = tmp;
        } while (w != targetW || h != targetH);
        return img;
    }

    public ActionListener addEnterButtonListener(ActionListener listener) {
        this.enterButtonListener = listener;
        return listener;
    }
}
