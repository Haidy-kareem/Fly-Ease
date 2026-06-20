package oopflightbookingsystem.src;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Shared dark-theme visual toolkit for SkyBook.
 * Centralizes color palette, backgrounds, animated components and
 * hand-drawn icon usage so every screen in Gui.java looks consistent.
 */
public final class UI {
    private UI() {}
// ── Palette 
// Background
public static final Color BG_TOP        = new Color(38, 28, 24);  
public static final Color BG_BOTTOM     = new Color(50, 37, 31);
public static final Color BG_CARD       = new Color(66, 49, 41);   
public static final Color BG_CARD_HOVER = new Color(80, 60, 50);
public static final Color BG_FIELD      = new Color(74, 55, 46);
public static final Color BORDER        = new Color(145, 118, 99);
public static final Color BORDER_SOFT   = new Color(110, 90, 76);


// Accent Colors
public static final Color CYAN          = new Color(230, 210, 190); 
public static final Color CYAN_SOFT     = new Color(245, 228, 210);
public static final Color VIOLET        = new Color(205, 175, 150); 
public static final Color AMBER         = new Color(225, 180, 130); 
public static final Color MINT          = new Color(190, 160, 135); 
public static final Color ROSE          = new Color(215, 170, 145);
public static final Color GOLD          = new Color(245, 205, 150);


// Text
public static final Color TEXT_PRIMARY  = new Color(250, 245, 240);
public static final Color TEXT_MUTED    = new Color(205, 190, 175);
public static final Color TEXT_FAINT    = new Color(155, 135, 120);

    public static final Font FONT_TITLE   = new Font("Georgia", Font.BOLD, 46);
    public static final Font FONT_HEAD    = new Font("Segoe UI", Font.BOLD, 21);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BTN     = new Font("Segoe UI", Font.BOLD, 14);

    // ════════════════════════════════════════════════════════════
    //  BACKGROUND PANEL — gradient + faint watermark icons
    // ════════════════════════════════════════════════════════════
    public static class BackgroundPanel extends JPanel {
        private final List<Watermark> marks = new ArrayList<>();
        private float driftPhase = 0f;
        private final Timer driftTimer;

        private static class Watermark {
            Icons.Type type; float x, y, size, baseAlpha, rotation; Color color;
            Watermark(Icons.Type t, float x, float y, float size, float a, Color c) {
                this(t, x, y, size, a, c, 0f);
            }
            Watermark(Icons.Type t, float x, float y, float size, float a, Color c, float rotation) {
                this.type=t; this.x=x; this.y=y; this.size=size; this.baseAlpha=a; this.color=c; this.rotation=rotation;
            }
        }

        public BackgroundPanel() {
            setLayout(null);
            setOpaque(true);
            // Scattered faint plane watermarks only — no locks/keys/shields
            marks.add(new Watermark(Icons.Type.PLANE,   0.07f, 0.14f, 70,  0.07f,  CYAN,   -0.35f));
            marks.add(new Watermark(Icons.Type.PLANE,   0.92f, 0.18f, 55,  0.06f,  VIOLET,  0.5f));
            marks.add(new Watermark(Icons.Type.PLANE,   0.05f, 0.60f, 60,  0.06f,  MINT,    0.2f));
            marks.add(new Watermark(Icons.Type.PLANE,   0.93f, 0.64f, 75,  0.055f, CYAN,   -0.6f));
            marks.add(new Watermark(Icons.Type.PLANE,   0.20f, 0.88f, 50,  0.05f,  AMBER,   0.15f));
            marks.add(new Watermark(Icons.Type.PLANE,   0.83f, 0.90f, 58,  0.055f, ROSE,   -0.25f));
            marks.add(new Watermark(Icons.Type.PLANE,   0.50f, 0.06f, 46,  0.06f,  CYAN,    0.05f));
            marks.add(new Watermark(Icons.Type.PLANE,   0.38f, 0.94f, 42,  0.045f, VIOLET,  0.4f));

            driftTimer = new Timer(60, e -> { driftPhase += 0.012f; repaint(); });
            driftTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            GradientPaint gp = new GradientPaint(0, 0, BG_TOP, w * 0.6f, h, BG_BOTTOM);
            g2.setPaint(gp);
            g2.fillRect(0, 0, w, h);

            // subtle radial glow behind center-top
            RadialGradientPaint glow = new RadialGradientPaint(
                new Point2D.Float(w * 0.5f, h * 0.18f), Math.max(w, h) * 0.45f,
                new float[]{0f, 1f},
                new Color[]{new Color(CYAN.getRed(), CYAN.getGreen(), CYAN.getBlue(), 18), new Color(0,0,0,0)});
            g2.setPaint(glow);
            g2.fillRect(0, 0, w, h);

            for (Watermark m : marks) {
                float bob = (float) Math.sin(driftPhase + m.x * 6) * 6f;
                float x = m.x * w - m.size / 2f;
                float y = m.y * h - m.size / 2f + bob;
                Icons.paintAt(g2, m.type, withAlpha(m.color, m.baseAlpha), x, y, m.size, 2.5f, m.rotation);
            }
            g2.dispose();
        }
    }

    public static Color withAlpha(Color c, float a) {
        int alpha = Math.max(0, Math.min(255, (int) (a * 255)));
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    // ════════════════════════════════════════════════════════════
    //  GLOW TITLE — two-tone heading with soft glow behind it
    // ════════════════════════════════════════════════════════════
    public static class GlowTitle extends JComponent {
        private final String partA, partB;
        private final Color colorA;
        private final Font font;

        public GlowTitle(String partA, String partB, Color colorA, Font font) {
            this.partA = partA; this.partB = partB; this.colorA = colorA; this.font = font;
            setOpaque(false);
            FontMetrics fm = new JLabel().getFontMetrics(font);
            int totalW = fm.stringWidth(partA + " " + partB) + 40;
            setPreferredSize(new Dimension(totalW, fm.getHeight() + 40));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            int totalW = fm.stringWidth(partA + " " + partB);
            int x = (getWidth() - totalW) / 2;
            int y = getHeight() / 2 + fm.getAscent() / 2 - 4;

            // layered translucent offsets approximate a soft glow (no native blur in Swing)
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    g2.setColor(withAlpha(colorA, 0.035f));
                    g2.drawString(partA, x + dx, y + dy);
                }
            }
            g2.setColor(colorA);
            g2.drawString(partA, x, y);

            int xB = x + fm.stringWidth(partA + " ");
            g2.setColor(TEXT_PRIMARY);
            g2.drawString(partB, xB, y);
            g2.dispose();
        }
    }

    // ════════════════════════════════════════════════════════════
    //  ACTION CARD — icon + label, animated glow border on hover
    // ════════════════════════════════════════════════════════════
    public static class ActionCard extends JPanel {
        private float hoverProgress = 0f; // 0..1
        private Timer animTimer;
        private final Color accent;
        private boolean hovered = false;

        public ActionCard(Icons.Type iconType, String label, Color accent, ActionListener onClick) {
            this.accent = accent;
            setOpaque(false);
            setLayout(new GridBagLayout());
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(190, 130));

            JPanel inner = new JPanel(new GridBagLayout());
            inner.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 10, 0);

            Icons.IconPanel icon = Icons.make(iconType, accent, 34);
            inner.add(icon, gbc);

            gbc.gridy = 1; gbc.insets = new Insets(0,0,0,0);
            JLabel lbl = new JLabel(label, SwingConstants.CENTER);
            lbl.setFont(FONT_LABEL);
            lbl.setForeground(TEXT_PRIMARY);
            inner.add(lbl, gbc);

            add(inner);

            animTimer = new Timer(15, e -> {
                float target = hovered ? 1f : 0f;
                hoverProgress += (target - hoverProgress) * 0.22f;
                if (Math.abs(target - hoverProgress) < 0.01f) {
                    hoverProgress = target;
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            });

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true; restartAnim(); }
                @Override public void mouseExited(MouseEvent e)  { hovered = false; restartAnim(); }
                @Override public void mouseClicked(MouseEvent e) { if (onClick != null) onClick.actionPerformed(null); }
            });
        }

        private void restartAnim() {
            if (!animTimer.isRunning()) animTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int lift = (int) (hoverProgress * 3);

            RoundRectangle2D bg = new RoundRectangle2D.Float(2, 2 - lift, w - 4, h - 4, 16, 16);
            Color cardColor = blend(BG_CARD, BG_CARD_HOVER, hoverProgress);
            g2.setColor(cardColor);
            g2.fill(bg);

            // glow border that intensifies on hover
            float glowAlpha = 0.18f + hoverProgress * 0.55f;
            g2.setStroke(new BasicStroke(1.4f + hoverProgress * 0.8f));
            g2.setColor(withAlpha(accent, glowAlpha));
            g2.draw(bg);

            if (hoverProgress > 0.05f) {
                g2.setColor(withAlpha(accent, hoverProgress * 0.10f));
                g2.fill(new RoundRectangle2D.Float(2, 2 - lift, w - 4, h - 4, 16, 16));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static Color blend(Color a, Color b, float t) {
        t = Math.max(0, Math.min(1, t));
        int r = (int) (a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int gg= (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bb= (int) (a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(r, gg, bb);
    }

    // ════════════════════════════════════════════════════════════
    //  PRIMARY BUTTON — filled, animated hover brighten + press dip
    // ════════════════════════════════════════════════════════════
    public static class GlowButton extends JButton {
        private float hoverProgress = 0f;
        private Timer animTimer;
        private final Color base;
        private boolean outline;

        public GlowButton(String text, Color base) { this(text, base, false); }

        public GlowButton(String text, Color base, boolean outline) {
            super(text);
            this.base = base;
            this.outline = outline;
            setFont(FONT_BTN);
            setForeground(outline ? base : Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 22, 12, 22));

            animTimer = new Timer(15, e -> {
                boolean hovered = getModel().isRollover();
                float target = hovered ? 1f : 0f;
                hoverProgress += (target - hoverProgress) * 0.25f;
                if (Math.abs(target - hoverProgress) < 0.01f) {
                    hoverProgress = target;
                    ((Timer) e.getSource()).stop();
                }
                repaint();
            });
            addChangeListener(e -> { if (!animTimer.isRunning()) animTimer.start(); });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            RoundRectangle2D shape = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 12, 12);

            if (outline) {
                g2.setColor(withAlpha(base, 0.10f + hoverProgress * 0.12f));
                g2.fill(shape);
                g2.setStroke(new BasicStroke(1.4f + hoverProgress * 0.6f));
                g2.setColor(withAlpha(base, 0.55f + hoverProgress * 0.45f));
                g2.draw(shape);
            } else {
                Color top = blend(base, Color.WHITE, 0.12f * hoverProgress);
                Color bot = base;
                GradientPaint gp = new GradientPaint(0, 0, top, 0, h, bot);
                g2.setPaint(gp);
                g2.fill(shape);
                if (hoverProgress > 0.02f) {
                    g2.setStroke(new BasicStroke(1.2f));
                    g2.setColor(withAlpha(Color.WHITE, hoverProgress * 0.35f));
                    g2.draw(shape);
                }
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class LinkButton extends JButton {
        public LinkButton(String text, Color color) {
            super(text);
            setForeground(color);
            setFont(FONT_BODY);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addChangeListener(e -> setText(getModel().isRollover() ?
                "<html><u>" + text + "</u></html>" : text));
        }
    }

    // ════════════════════════════════════════════════════════════
    //  CARD CONTAINER — rounded panel w/ border, used for forms
    // ════════════════════════════════════════════════════════════
    public static class GlassCard extends JPanel {
        private Color borderColor = BORDER;
        public GlassCard() {
            setOpaque(false);
            setBorder(new EmptyBorder(26, 28, 26, 28));
        }
        public void setBorderColor(Color c) { this.borderColor = c; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D shape = new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 18, 18);
            g2.setColor(BG_CARD);
            g2.fill(shape);
            g2.setColor(withAlpha(borderColor, 0.5f));
            g2.setStroke(new BasicStroke(1.3f));
            g2.draw(shape);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ════════════════════════════════════════════════════════════
    //  FORM FIELDS
    // ════════════════════════════════════════════════════════════
    public static JTextField field() {
        JTextField f = new JTextField(16);
        styleField(f);
        return f;
    }

    public static JPasswordField passwordField() {
        JPasswordField f = new JPasswordField(16);
        styleField(f);
        return f;
    }

    private static void styleField(JTextField f) {
        f.setBackground(BG_FIELD);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(CYAN);
        f.setFont(FONT_BODY);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(9, 12, 9, 12)));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(new LineBorder(CYAN_SOFT, 1, true), new EmptyBorder(9, 12, 9, 12)));
            }
            @Override public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(new LineBorder(BORDER, 1, true), new EmptyBorder(9, 12, 9, 12)));
            }
        });
    }

    public static <T> JComboBox<T> combo(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        cb.setBackground(BG_FIELD);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        cb.setBorder(new LineBorder(BORDER, 1, true));
        cb.setFocusable(false);
        return cb;
    }

    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SMALL);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    public static JLabel sectionTitle(String text, Color accent) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(FONT_HEAD);
        l.setForeground(TEXT_PRIMARY);
        l.setBorder(new EmptyBorder(0, 0, 4, 0));
        return l;
    }

    // ════════════════════════════════════════════════════════════
    //  TABLE STYLING
    // ════════════════════════════════════════════════════════════
    public static void styleTable(JTable table) {
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(withAlpha(CYAN, 0.22f));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setFillsViewportHeight(true);
        table.setGridColor(BORDER_SOFT);

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_CARD_HOVER);
        header.setForeground(CYAN_SOFT);
        header.setFont(FONT_LABEL);
        header.setBorder(new MatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setBackground(sel ? withAlpha(CYAN, 0.18f) : (row % 2 == 0 ? BG_CARD : BG_CARD_HOVER));
                setForeground(TEXT_PRIMARY);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                return this;
            }
        });
    }
}
