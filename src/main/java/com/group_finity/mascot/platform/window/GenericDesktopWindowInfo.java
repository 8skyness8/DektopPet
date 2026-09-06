package com.group_finity.mascot.platform.window;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.List;

/** Safe non-Windows implementation using only Java desktop APIs. */
final class GenericDesktopWindowInfo implements DesktopWindowInfo {
    @Override
    public Bounds getDesktopBounds() {
        try {
            Rectangle result = null;
            for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
                Rectangle deviceBounds = device.getDefaultConfiguration().getBounds();
                result = result == null ? new Rectangle(deviceBounds) : result.union(deviceBounds);
            }
            return toBounds(result);
        } catch (HeadlessException exception) {
            return Bounds.EMPTY;
        }
    }

    @Override
    public Bounds getWorkAreaBounds() {
        try {
            var configuration = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice().getDefaultConfiguration();
            Rectangle bounds = new Rectangle(configuration.getBounds());
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(configuration);
            return new Bounds(bounds.x + insets.left, bounds.y + insets.top,
                    Math.max(0, bounds.width - insets.left - insets.right),
                    Math.max(0, bounds.height - insets.top - insets.bottom));
        } catch (HeadlessException exception) {
            return Bounds.EMPTY;
        }
    }

    @Override
    public List<WindowSnapshot> getTopLevelWindows() {
        return List.of();
    }

    private static Bounds toBounds(Rectangle rectangle) {
        return rectangle == null ? Bounds.EMPTY
                : new Bounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
}
