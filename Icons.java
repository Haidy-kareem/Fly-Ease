package oopflightbookingsystem.src;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Hand-drawn vector icon library.
 * Every icon is painted with Graphics2D stroke/fill paths — no emoji glyphs,
 * no bundled image assets. Each icon is a small JComponent so it can be
 * dropped straight into any Swing container and recolored per-instance.
 */
public final class Icons {

    private Icons() {}

    public enum Type {
        PLANE, TICKET, SEARCH, FOLDER, CANCEL, CARD, RECEIPT, LOGOUT,
        CLOCK, USERS, USER_PLUS, SHIELD, CHART, KEY, LOCK, MAIL,
        GLOBE, CALENDAR, CHEVRON_RIGHT, CHECK, ALERT, CLOUD, RADAR
    }

    /** A small canvas that paints one vector icon, stroke-based, themable. */
    public static class IconPanel extends JComponent {
        private final Type type;
        private Color color;
        private float strokeWidth = 2.2f;
        private float alpha = 1f;

        public IconPanel(Type type, Color color, int size) {
            this.type = type;
            this.color = color;
            Dimension d = new Dimension(size, size);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
            setOpaque(false);
        }

        public void setColor(Color c) { this.color = c; repaint(); }
        public void setAlpha(float a) { this.alpha = a; repaint(); }
        public Color getColor() { return color; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            if (alpha < 1f) g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            int w = getWidth(), h = getHeight();
            float s = Math.min(w, h) / 48f; // design grid is 48x48
            g2.translate((w - 48 * s) / 2.0, (h - 48 * s) / 2.0);
            g2.scale(s, s);
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(color);

            paintIcon(type, g2);
            g2.dispose();
        }
    }

    public static IconPanel make(Type type, Color color, int size) {
        return new IconPanel(type, color, size);
    }

    /** Paint directly onto an existing Graphics2D at a given offset/scale — used for watermarks & inline painting. */
    public static void paintAt(Graphics2D base, Type type, Color color, float x, float y, float size, float strokeWidth) {
        paintAt(base, type, color, x, y, size, strokeWidth, 0f);
    }

    /** Same as paintAt, with an additional rotation (radians) around the icon's own center. */
    public static void paintAt(Graphics2D base, Type type, Color color, float x, float y, float size, float strokeWidth, float rotationRad) {
        Graphics2D g2 = (Graphics2D) base.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        if (rotationRad != 0f) g2.rotate(rotationRad, size / 2.0, size / 2.0);
        g2.scale(size / 48f, size / 48f);
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(color);
        paintIcon(type, g2);
        g2.dispose();
    }

    // All icons drawn on a 48x48 grid.
    private static void paintIcon(Type type, Graphics2D g2) {
        switch (type) {
            case PLANE:        plane(g2); break;
            case TICKET:       ticket(g2); break;
            case SEARCH:       search(g2); break;
            case FOLDER:       folder(g2); break;
            case CANCEL:       cancel(g2); break;
            case CARD:         card(g2); break;
            case RECEIPT:      receipt(g2); break;
            case LOGOUT:       logout(g2); break;
            case CLOCK:        clock(g2); break;
            case USERS:        users(g2); break;
            case USER_PLUS:    userPlus(g2); break;
            case SHIELD:       shield(g2); break;
            case CHART:        chart(g2); break;
            case KEY:          key(g2); break;
            case LOCK:         lock(g2); break;
            case MAIL:         mail(g2); break;
            case GLOBE:        globe(g2); break;
            case CALENDAR:     calendar(g2); break;
            case CHEVRON_RIGHT:chevronRight(g2); break;
            case CHECK:        check(g2); break;
            case ALERT:        alert(g2); break;
            case CLOUD:        cloud(g2); break;
            case RADAR:        radar(g2); break;
        }
    }

    // ── Individual icon paths (48x48 grid, stroke-only unless noted) ──

    private static void plane(Graphics2D g) {
        GeneralPath p = new GeneralPath();
        p.moveTo(6, 28);
        p.lineTo(20, 24);
        p.lineTo(30, 8);
        p.curveTo(31, 6, 34, 6, 33.5, 9);
        p.lineTo(27, 24);
        p.lineTo(40, 22);
        p.curveTo(43, 21.5, 44, 24, 41.5, 25.5);
        p.lineTo(27, 30);
        p.lineTo(24, 41);
        p.lineTo(29, 41);
        p.lineTo(32, 36);
        p.moveTo(24, 41);
        p.lineTo(19, 41);
        p.lineTo(20, 35);
        p.lineTo(24, 36);
        p.closePath();
        g.draw(p);
        // small tail flair
        GeneralPath tail = new GeneralPath();
        tail.moveTo(6, 28); tail.lineTo(2, 30); tail.lineTo(7, 31);
        g.draw(tail);
    }

    private static void ticket(Graphics2D g) {
        RoundRectangle2D body = new RoundRectangle2D.Float(5, 13, 38, 22, 6, 6);
        g.draw(body);
        // perforation notches
        g.fill(new Ellipse2D.Float(28 - 2.5f, 13 - 2.5f, 5, 5));
        g.fill(new Ellipse2D.Float(28 - 2.5f, 35 - 2.5f, 5, 5));
        g.setColor(blend(g.getColor(), Color.BLACK, 0f)); // keep same color
        float[] dash = {3f, 3f};
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, dash, 0));
        g.draw(new Line2D.Float(28, 17, 28, 31));
        g.setStroke(old);
        // little plane glyph on the stub
        GeneralPath mini = new GeneralPath();
        mini.moveTo(12, 24); mini.lineTo(20, 22); mini.lineTo(24, 17);
        mini.moveTo(20, 22); mini.lineTo(23, 26);
        g.draw(mini);
    }

    private static void search(Graphics2D g) {
        g.draw(new Ellipse2D.Float(8, 8, 22, 22));
        g.draw(new Line2D.Float(26, 26, 39, 39));
        // small plane hint inside the lens
        GeneralPath p = new GeneralPath();
        p.moveTo(13, 21); p.lineTo(19, 19); p.lineTo(23, 13);
        g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(p);
    }

    private static void folder(Graphics2D g) {
        GeneralPath p = new GeneralPath();
        p.moveTo(6, 14);
        p.lineTo(18, 14);
        p.lineTo(21, 18);
        p.lineTo(42, 18);
        p.lineTo(42, 38);
        p.curveTo(42, 39.5, 40.5, 40, 39.5, 40);
        p.lineTo(8.5, 40);
        p.curveTo(7, 40, 6, 39, 6, 37.5);
        p.closePath();
        g.draw(p);
    }

    private static void cancel(Graphics2D g) {
        g.draw(new Ellipse2D.Float(7, 7, 34, 34));
        g.draw(new Line2D.Float(18, 18, 30, 30));
        g.draw(new Line2D.Float(30, 18, 18, 30));
    }

    private static void card(Graphics2D g) {
        RoundRectangle2D body = new RoundRectangle2D.Float(5, 11, 38, 26, 5, 5);
        g.draw(body);
        g.fill(new Rectangle2D.Float(5, 18, 38, 6));
        g.draw(new Line2D.Float(11, 31, 22, 31));
    }

    private static void receipt(Graphics2D g) {
        GeneralPath p = new GeneralPath();
        p.moveTo(12, 5);
        p.lineTo(36, 5);
        p.lineTo(36, 43);
        p.lineTo(31, 39);
        p.lineTo(26, 43);
        p.lineTo(21, 39);
        p.lineTo(16, 43);
        p.lineTo(12, 39);
        p.closePath();
        g.draw(p);
        g.draw(new Line2D.Float(17, 15, 31, 15));
        g.draw(new Line2D.Float(17, 22, 31, 22));
        g.draw(new Line2D.Float(17, 29, 26, 29));
    }

    private static void logout(Graphics2D g) {
        g.draw(new RoundRectangle2D.Float(8, 8, 18, 32, 4, 4));
        g.draw(new Line2D.Float(18, 24, 41, 24));
        GeneralPath arrow = new GeneralPath();
        arrow.moveTo(34, 17); arrow.lineTo(41, 24); arrow.lineTo(34, 31);
        g.draw(arrow);
    }

    private static void clock(Graphics2D g) {
        g.draw(new Ellipse2D.Float(6, 6, 36, 36));
        g.draw(new Line2D.Float(24, 24, 24, 13));
        g.draw(new Line2D.Float(24, 24, 32, 28));
    }

    private static void users(Graphics2D g) {
        g.draw(new Ellipse2D.Float(10, 8, 14, 14));
        GeneralPath body = new GeneralPath();
        body.moveTo(4, 40);
        body.curveTo(4, 30, 11, 25, 17, 25);
        body.curveTo(23, 25, 30, 30, 30, 40);
        g.draw(body);
        g.draw(new Ellipse2D.Float(28, 12, 11, 11));
        GeneralPath body2 = new GeneralPath();
        body2.moveTo(31, 26);
        body2.curveTo(38, 27, 44, 32, 44, 40);
        g.draw(body2);
    }

    private static void userPlus(Graphics2D g) {
        g.draw(new Ellipse2D.Float(8, 7, 18, 18));
        GeneralPath body = new GeneralPath();
        body.moveTo(4, 41);
        body.curveTo(4, 30, 11, 25, 17, 25);
        body.curveTo(23, 25, 28, 29, 29.5, 35);
        g.draw(body);
        g.draw(new Line2D.Float(37, 22, 37, 38));
        g.draw(new Line2D.Float(29, 30, 45, 30));
    }

    private static void shield(Graphics2D g) {
        GeneralPath p = new GeneralPath();
        p.moveTo(24, 5);
        p.lineTo(40, 11);
        p.lineTo(40, 23);
        p.curveTo(40, 35, 32, 41.5, 24, 44);
        p.curveTo(16, 41.5, 8, 35, 8, 23);
        p.lineTo(8, 11);
        p.closePath();
        g.draw(p);
        g.draw(new Ellipse2D.Float(20, 19, 8, 8));
        g.draw(new Line2D.Float(24, 27, 24, 32));
    }

    private static void chart(Graphics2D g) {
        g.draw(new Line2D.Float(8, 41, 8, 9));
        g.draw(new Line2D.Float(8, 41, 41, 41));
        g.fill(new RoundRectangle2D.Float(14, 26, 6, 15, 2, 2));
        g.fill(new RoundRectangle2D.Float(23, 18, 6, 23, 2, 2));
        g.fill(new RoundRectangle2D.Float(32, 11, 6, 30, 2, 2));
    }

    private static void key(Graphics2D g) {
        g.draw(new Ellipse2D.Float(5, 14, 20, 20));
        g.fill(new Ellipse2D.Float(11, 20, 8, 8));
        GeneralPath shaft = new GeneralPath();
        shaft.moveTo(23, 24);
        shaft.lineTo(43, 24);
        shaft.lineTo(43, 31);
        shaft.lineTo(38, 31);
        shaft.lineTo(38, 24);
        g.draw(shaft);
        g.draw(new Line2D.Float(31, 24, 31, 30));
    }

    private static void lock(Graphics2D g) {
        g.draw(new RoundRectangle2D.Float(10, 22, 28, 20, 5, 5));
        GeneralPath shackle = new GeneralPath();
        shackle.moveTo(16, 22);
        shackle.lineTo(16, 15);
        shackle.curveTo(16, 7, 32, 7, 32, 15);
        shackle.lineTo(32, 22);
        g.draw(shackle);
        g.fill(new Ellipse2D.Float(22, 29, 4, 4));
        g.draw(new Line2D.Float(24, 33, 24, 37));
    }

    private static void mail(Graphics2D g) {
        RoundRectangle2D body = new RoundRectangle2D.Float(5, 11, 38, 26, 5, 5);
        g.draw(body);
        GeneralPath flap = new GeneralPath();
        flap.moveTo(6, 14);
        flap.lineTo(24, 28);
        flap.lineTo(42, 14);
        g.draw(flap);
    }

    private static void globe(Graphics2D g) {
        g.draw(new Ellipse2D.Float(6, 6, 36, 36));
        g.draw(new Ellipse2D.Float(15, 6, 18, 36));
        g.draw(new Line2D.Float(6, 24, 42, 24));
        GeneralPath lat1 = new GeneralPath();
        lat1.moveTo(9, 15); lat1.curveTo(20, 11, 28, 11, 39, 15);
        g.draw(lat1);
        GeneralPath lat2 = new GeneralPath();
        lat2.moveTo(9, 33); lat2.curveTo(20, 37, 28, 37, 39, 33);
        g.draw(lat2);
    }

    private static void calendar(Graphics2D g) {
        g.draw(new RoundRectangle2D.Float(6, 10, 36, 32, 5, 5));
        g.draw(new Line2D.Float(6, 19, 42, 19));
        g.draw(new Line2D.Float(15, 5, 15, 13));
        g.draw(new Line2D.Float(33, 5, 33, 13));
        g.fill(new RoundRectangle2D.Float(13, 25, 6, 6, 2, 2));
    }

    private static void chevronRight(Graphics2D g) {
        GeneralPath p = new GeneralPath();
        p.moveTo(16, 8); p.lineTo(32, 24); p.lineTo(16, 40);
        g.draw(p);
    }

    private static void check(Graphics2D g) {
        GeneralPath p = new GeneralPath();
        p.moveTo(8, 25); p.lineTo(19, 36); p.lineTo(41, 12);
        g.draw(p);
    }

    private static void alert(Graphics2D g) {
        GeneralPath tri = new GeneralPath();
        tri.moveTo(24, 5);
        tri.lineTo(44, 41);
        tri.lineTo(4, 41);
        tri.closePath();
        g.draw(tri);
        g.draw(new Line2D.Float(24, 19, 24, 29));
        g.fill(new Ellipse2D.Float(22.5f, 33, 3, 3));
    }

    private static void cloud(Graphics2D g) {
        GeneralPath p = new GeneralPath();
        p.moveTo(13, 33);
        p.curveTo(6, 33, 4, 24, 12, 22.5);
        p.curveTo(12, 13, 27, 11, 30, 19);
        p.curveTo(40, 17, 44, 28, 36, 32);
        p.lineTo(13, 33);
        p.closePath();
        g.draw(p);
    }

    private static void radar(Graphics2D g) {
        g.draw(new Ellipse2D.Float(6, 6, 36, 36));
        g.draw(new Ellipse2D.Float(14, 14, 20, 20));
        g.draw(new Ellipse2D.Float(21, 21, 6, 6));
        GeneralPath sweep = new GeneralPath();
        sweep.moveTo(24, 24); sweep.lineTo(24, 6); sweep.lineTo(40, 12);
        sweep.closePath();
        g.fill(sweep);
    }

    private static Color blend(Color a, Color b, float t) {
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int gg = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bb = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, gg, bb);
    }
}
