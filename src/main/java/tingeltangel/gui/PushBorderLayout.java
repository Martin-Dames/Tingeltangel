/*
    Copyright (C) 2016   Martin Dames <martin@bastionbytes.de>
  
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
  
*/
package tingeltangel.gui;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 *
 */
public class PushBorderLayout implements LayoutManager2 {

    // Make the enum values accessible using PushBorderLayout.NAME
    public static final Position LINE_START = Position.LINE_START;
    public static final Position LINE_END = Position.LINE_END;
    public static final Position PAGE_START = Position.PAGE_START;
    public static final Position PAGE_END = Position.PAGE_END;
    public static final Position CENTER = Position.CENTER;

    public static enum Position {
        LINE_START, LINE_END,
        PAGE_START, PAGE_END,
        CENTER;

        private Position asLeftToRight(boolean leftToRight) {
            if (leftToRight) return this;
            switch (this) {
                case LINE_START: return PAGE_START;
                case LINE_END: return PAGE_END;
                case PAGE_START: return LINE_START;
                case PAGE_END: return LINE_END;
                case CENTER: return CENTER;
                default: throw new RuntimeException("Should not be possible");
            }

        }
    }
    private static class Element {
        Component component;
        Position position;

        public Element(Component component, Position position) {
            this.component = component;
            this.position = position;
        }
    }
    private boolean closed() {
        return !elements.isEmpty() && elements.get(elements.size() - 1).position == Position.CENTER;
    }


    private final ArrayList<Element> elements = new ArrayList<Element>();

    public static Component pad(int size) {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(size, size));
        return p;
    }
    public void addLayoutComponent(Component comp, Object constraints) {
        synchronized (comp.getTreeLock()) {
            if (constraints == null) {
                constraints = Position.CENTER;
            }
            if (closed()) {
                throw new IllegalStateException("Cannot add more components to a " + this.getClass().getName() + " after having added something in " + Position.CENTER.name() + " (or without parameter)");
            }
            if (!(constraints instanceof Position)) {
                throw new IllegalArgumentException("Only " + Position.class.getCanonicalName() + " constraints are allowed, found " + constraints.getClass().getCanonicalName());
            }
            for (Element e : elements) {
                if (e.component == comp) {
                    throw new IllegalArgumentException("Cannot add a given component twice, check your code.");
                }
            }
            elements.add(new Element(comp, (Position) constraints));
        }
    }

    public void removeLayoutComponent(Component comp) {
        synchronized (comp.getTreeLock()) {
            for (Element e : elements) {
                if (e.component == comp) {
                    elements.remove(e);
                    break;
                }
            }
        }
    }

    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            Rectangle available = new Rectangle(
                    insets.left,
                    insets.top,
                    target.getWidth() - insets.right - insets.left,
                    target.getHeight() - insets.bottom - insets.top);
            boolean ltr = target.getComponentOrientation().isLeftToRight();
            for (Element e : elements) {
                final Component c = e.component;
                Dimension pref;
                switch (e.position.asLeftToRight(ltr)) {
                    case LINE_START: // left
                        c.setSize(c.getWidth(), available.height);
                        pref = c.getPreferredSize();
                        c.setBounds(available.x, available.y, pref.width, available.height);
                        available.x += pref.width;
                        available.width -= pref.width;
                        break;
                    case LINE_END: // right
                        c.setSize(c.getWidth(), available.height);
                        pref = c.getPreferredSize();
                        c.setBounds(available.x + available.width - pref.width, available.y, pref.width, available.height);
                        available.width -= pref.width;
                        break;
                    case PAGE_START: // top
                        c.setSize(available.width, c.getHeight());
                        pref = c.getPreferredSize();
                        c.setBounds(available.x, available.y, available.width, pref.height);
                        available.y += pref.height;
                        available.height -= pref.height;
                        break;
                    case PAGE_END: // down
                        c.setSize(available.width, c.getHeight());
                        pref = c.getPreferredSize();
                        c.setBounds(available.x, available.y + available.height - pref.height, available.width, pref.height);
                        available.height -= pref.height;
                        break;
                    case CENTER:
                        c.setBounds(available.x, available.y, available.width, available.height);
                        break;
                }
            }
        }
    }

    public Dimension minimumLayoutSize(Container target) {
        return new Dimension(0, 0);
    }

    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Dimension usedSize = new Dimension();
            Dimension minimalInsideSize = new Dimension();

            boolean sanityCheckStopped = false;
            boolean ltr = target.getComponentOrientation().isLeftToRight();
            for (Element e : elements) {
                if (sanityCheckStopped) {
                    throw new IllegalStateException("Internal illegal state, wrong components order.");
                }
                final Component c = e.component;
                Dimension cDim = c.getPreferredSize();
                switch (e.position.asLeftToRight(ltr)) {
                    case CENTER:
                        sanityCheckStopped = true;
                        usedSize.width += cDim.width;
                        usedSize.height += cDim.height;
                        break;
                    case LINE_START:
                    case LINE_END:
                        usedSize.width += cDim.width;
                        minimalInsideSize.width = Math.max(0, minimalInsideSize.width - cDim.width);
                        minimalInsideSize.height = Math.max(minimalInsideSize.height, cDim.height);
                        break;
                    case PAGE_START:
                    case PAGE_END:
                        usedSize.height += cDim.height;
                        minimalInsideSize.width = Math.max(minimalInsideSize.width, cDim.width);
                        minimalInsideSize.height = Math.max(0, minimalInsideSize.height - cDim.height);
                        break;
                }
            }

            usedSize.width += minimalInsideSize.width;
            usedSize.height += minimalInsideSize.height;

            Insets insets = target.getInsets();
            usedSize.width += insets.left + insets.right;
            usedSize.height += insets.top + insets.bottom;
            return usedSize;
        }
    }

    public Dimension maximumLayoutSize(Container target) {
	return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    public void invalidateLayout(Container target) {
    }

    public void addLayoutComponent(String name, Component comp) {
        throw new UnsupportedOperationException();
    }

}