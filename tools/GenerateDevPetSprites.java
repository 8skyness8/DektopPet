import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/** Generates the deliberately simple placeholder artwork used by the DevPet mascot. */
public final class GenerateDevPetSprites {
    private static final int SPRITE_COUNT = 46;
    private static final int SIZE = 128;

    private GenerateDevPetSprites() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected the output directory as the only argument");
        }

        Path outputDirectory = Path.of(args[0]);
        Files.createDirectories(outputDirectory);

        for (int frame = 1; frame <= SPRITE_COUNT; frame++) {
            BufferedImage sprite = drawSprite(frame);
            Path output = outputDirectory.resolve("shime" + frame + ".png");
            if (!ImageIO.write(sprite, "png", output.toFile())) {
                throw new IOException("No PNG writer is available for " + output);
            }
        }
    }

    private static BufferedImage drawSprite(int frame) {
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // A little movement keeps the temporary art useful with the existing animations.
        double bob = Math.sin((frame - 1) * Math.PI / 4.0) * 3.0;
        double lean = Math.sin((frame - 1) * Math.PI / 8.0) * 0.08;
        AffineTransform originalTransform = graphics.getTransform();
        graphics.rotate(lean, SIZE / 2.0, 74 + bob);

        Color outline = new Color(35, 61, 67);
        Color body = new Color(91, 205, 190);
        graphics.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Path2D ears = new Path2D.Double();
        ears.moveTo(37, 43 + bob);
        ears.lineTo(42, 20 + bob);
        ears.lineTo(57, 38 + bob);
        ears.moveTo(71, 38 + bob);
        ears.lineTo(87, 20 + bob);
        ears.lineTo(91, 45 + bob);
        graphics.setColor(body);
        graphics.fill(ears);
        graphics.setColor(outline);
        graphics.draw(ears);

        RoundRectangle2D bodyShape = new RoundRectangle2D.Double(31, 34 + bob, 66, 73, 30, 30);
        graphics.setColor(body);
        graphics.fill(bodyShape);
        graphics.setColor(outline);
        graphics.draw(bodyShape);

        double armSwing = Math.sin((frame - 1) * Math.PI / 3.0) * 9.0;
        graphics.drawLine(34, (int) (68 + bob), (int) (20 + armSwing), (int) (84 + bob));
        graphics.drawLine(94, (int) (68 + bob), (int) (108 - armSwing), (int) (84 + bob));
        graphics.drawLine(49, (int) (103 + bob), 43, (int) (116 + bob));
        graphics.drawLine(79, (int) (103 + bob), 85, (int) (116 + bob));

        graphics.setColor(outline);
        graphics.fill(new Ellipse2D.Double(47, 58 + bob, 7, 10));
        graphics.fill(new Ellipse2D.Double(74, 58 + bob, 7, 10));
        graphics.drawArc(55, (int) (68 + bob), 18, 12, 200, 140);

        graphics.setTransform(originalTransform);
        graphics.dispose();
        return image;
    }
}
