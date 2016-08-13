package view.elements;

import javax.swing.*;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * Created by oleh on 02.07.15.
 */
public class ScrollBarStyle {
    public ScrollBarStyle() {}

    /* Classical scrollbar replaced with another style */
    public ScrollBarUI scrollUI = new BasicScrollBarUI() {

        @Override
        protected JButton createDecreaseButton(int orientation) {
            JButton button = super.createDecreaseButton(orientation);
            button.setBackground(new Color(71, 77, 64));
            return button;
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            JButton button = super.createIncreaseButton(orientation);
            button.setBackground(new Color(52, 52, 51));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            g.translate(thumbBounds.x, thumbBounds.y);
            g.setColor( new Color(77, 104, 105));
            g.drawRect( 0, 0, thumbBounds.width - 2, thumbBounds.height - 1 );
            g.translate( -thumbBounds.x, -thumbBounds.y );
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.translate(trackBounds.x, trackBounds.y);
            g.translate( -trackBounds.x, -trackBounds.y );
        }
    };

}
