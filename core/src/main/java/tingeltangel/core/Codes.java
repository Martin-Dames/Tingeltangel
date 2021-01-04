/*
    Copyright (C) 2015   Martin Dames <martin@bastionbytes.de>
  
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

package tingeltangel.core;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import tingeltangel.gui.CodePreferences;


public class Codes {

    /*
    private final static float[] PS_DOT_SIZE = {0.12f, 0.06f};
    private final static float[] PS_DELTA_SIZE = {0.12f, 0.12f};
    private final static float[] PS_BLOCK_SIZE = {0.84f, 0.84f};
    */

    private static int[] PNG_DOT_SIZE = {2, 2};
    //private final static int[] PNG_DOT_SIZE = {1, 2};
    
    private static int[] PNG_DELTA_SIZE = {1, 2};
    private static int[] PNG_DELTA_X_SIZE = {1, 2};
    
    private static int[] PNG_BLOCK_SIZE = {8, 12};
    //private final static int[] PNG_BLOCK_SIZE = {6, 12};

    public static float[] PNG_PIXEL_PER_MM = {23.62205f, 47.24409f};

    public final static int DPI600 = 0;
    public final static int DPI1200 = 1;
    
    public final static int[] A4_WIDTH = {4880, 9760};
    public final static int[] A4_HEIGHT = {6680, 13360};

                
    private static int resolution = DPI1200;

    public static int getPatternSize600() {
        return(PNG_BLOCK_SIZE[DPI600]);
    }
    
    public static int getDeltaSize600() {
        return(PNG_DELTA_SIZE[DPI600]);
    }
    
    public static int getDotSize600() {
        return(PNG_DOT_SIZE[DPI600]);
    }
    
    public static int getPatternSize1200() {
        return(PNG_BLOCK_SIZE[DPI1200]);
    }
    
    public static int getDeltaSize1200() {
        return(PNG_DELTA_SIZE[DPI1200]);
    }
    
    public static int getDotSize1200() {
        return(PNG_DOT_SIZE[DPI1200]);
    }

    public static void setPatternSize600(int x) {
        PNG_BLOCK_SIZE[DPI600] = x;
    }
    
    public static void setPatternSize1200(int x) {
        PNG_BLOCK_SIZE[DPI1200] = x;
    }
    
    public static void setDotSize600(int x) {
        PNG_DOT_SIZE[DPI600] = x;
    }
    
    public static void setDotSize1200(int x) {
        PNG_DOT_SIZE[DPI1200] = x;
    }
    
    public static void setDeltaSize600(int x) {
        PNG_DELTA_SIZE[DPI600] = x;
        PNG_DELTA_X_SIZE[DPI600] = x;
    }
    
    public static void setDeltaSize1200(int x) {
        PNG_DELTA_SIZE[DPI1200] = x;
        PNG_DELTA_X_SIZE[DPI1200] = x;
    }
    
    
    public static void setResolution(int resolution) {
        Codes.resolution = resolution;
    }
    
    public static int getResolution() {
        return(resolution);
    }


    private static int[] getDotPosFromInt(int i) {
        int[] f = new int[2];
        switch (i) {
            case 0:
                f[0] = 1;
                f[1] = -1;
                return (f);
            case 1:
                f[0] = -1;
                f[1] = -1;
                return (f);
            case 2:
                f[0] = -1;
                f[1] = 1;
                return (f);
            case 3:
                f[0] = 1;
                f[1] = 1;
                return (f);
        }
        throw new Error();
    }

    private static int getPari(int w7, int w6, int w5, int w4, int w3, int w2, int w1, int w0) {
        int c1 = ((w1 ^ w4 ^ w6 ^ w7) & 0x01) << 1;
        int c2 = (w0 ^ w2 ^ w3 ^ w5) & 0x01;
        return (c1 | c2);
    }

    private static int[][][] getPatternFromInt(int i) {
        int w0 = i & 0x03;
        int w1 = (i >> 2) & 0x03;
        int w2 = (i >> 4) & 0x03;
        int w3 = (i >> 6) & 0x03;
        int w4 = (i >> 8) & 0x03;
        int w5 = (i >> 10) & 0x03;
        int w6 = (i >> 12) & 0x03;
        int w7 = (i >> 14) & 0x03;
        int[][][] pattern = {
                {{0, 0}, {0, 0}, {0, 0}, {0, 0}},
                {{0, 0}, getDotPosFromInt(getPari(w7, w6, w5, w4, w3, w2, w1, w0)), getDotPosFromInt(w7), getDotPosFromInt(w6)},
                {{+1, 0}, getDotPosFromInt(w5), getDotPosFromInt(w4), getDotPosFromInt(w3)},
                {{0, 0}, getDotPosFromInt(w2), getDotPosFromInt(w1), getDotPosFromInt(w0)}
        };
        return (pattern);
    }


    public static void drawPattern(int code, int x, int y, int width, int height, Graphics2D graphics) {
        int[][][] pattern = getPatternFromInt(code);
        for (int ix = 0; ix < width; ix++) {
            for (int iy = 0; iy < height; iy++) {
                for (int dx = 0; dx < 4; dx++) {
                    int mx = ix * 4 * PNG_BLOCK_SIZE[resolution] + dx * PNG_BLOCK_SIZE[resolution] + PNG_BLOCK_SIZE[resolution] / 2;
                    for (int dy = 0; dy < 4; dy++) {
                        int my = iy * 4 * PNG_BLOCK_SIZE[resolution] + dy * PNG_BLOCK_SIZE[resolution] + PNG_BLOCK_SIZE[resolution] / 2;

                        int px;
                        int py;

                        if ((pattern[dy][dx][1] == 0) && (pattern[dy][dx][0] > 0)) {
                            px = mx + pattern[dy][dx][0] * PNG_DELTA_X_SIZE[resolution];
                            py = my;
                        } else {
                            px = mx + pattern[dy][dx][0] * PNG_DELTA_SIZE[resolution];
                            py = my - pattern[dy][dx][1] * PNG_DELTA_SIZE[resolution];
                        }

                        graphics.fillRect(px + x, py + y, PNG_DOT_SIZE[resolution], PNG_DOT_SIZE[resolution]);

                    }
                }
            }
        }
    }

    public static void writePng(BufferedImage image, OutputStream out) throws IOException {
        
        // find an image writer for PNG
        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName("PNG"); iw.hasNext(); ) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }

            // add resolution information to image
            double dotsPerMilli = PNG_PIXEL_PER_MM[resolution];
            IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
            horiz.setAttribute("value", Double.toString(dotsPerMilli));
            IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
            vert.setAttribute("value", Double.toString(dotsPerMilli));
            IIOMetadataNode dim = new IIOMetadataNode("Dimension");
            dim.appendChild(horiz);
            dim.appendChild(vert);
            IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
            root.appendChild(dim);
            metadata.mergeTree("javax_imageio_1.0", root);

            final ImageOutputStream stream = ImageIO.createImageOutputStream(out);
            try {
                writer.setOutput(stream);
                writer.write(metadata, new IIOImage(image, null, metadata), writeParam);
            } finally {
                stream.close();
            }
            break;
        }
    }
    
    public static BufferedImage generateCodeImage(int code, int width, int height) {
        // convert width and height from mm to pixel
        width *= PNG_PIXEL_PER_MM[resolution];
        height *= PNG_PIXEL_PER_MM[resolution];


        width = (int) (width / (4 * PNG_BLOCK_SIZE[resolution]));
        height = (int) (height / (4 * PNG_BLOCK_SIZE[resolution]));

        int imageWidth = width * 4 * PNG_BLOCK_SIZE[resolution];
        int imageHeight = height * 4 * PNG_BLOCK_SIZE[resolution];

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.white);
        //graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, imageWidth, imageHeight);
        //graphics.setComposite(AlphaComposite.Src);
        graphics.setColor(Color.black);

        drawPattern(code, 0, 0, width, height, graphics);
        
        return(image);
    }
    
    /**
     * @param code   code-id (not ting-id)
     * @param width  width in mm
     * @param height height in mm
     * @param out    stream to which the image will be written
     * @throws IOException
     */
    public static void drawPng(int code, int width, int height, OutputStream out) throws IOException {
        writePng(generateCodeImage(code, width, height), out);
    }
    

    public static void drawPagePNG(int[] tingCodes, int patternWidthInMM, int patternHeightInMM, OutputStream out) throws IOException {
        
    
        int patternWidth = ((int) ((patternWidthInMM * PNG_PIXEL_PER_MM[resolution]) / (4 * PNG_BLOCK_SIZE[resolution])) * 4 * PNG_BLOCK_SIZE[resolution]);
        int patternHeight = ((int) ((patternHeightInMM * PNG_PIXEL_PER_MM[resolution]) / (4 * PNG_BLOCK_SIZE[resolution])) * 4 * PNG_BLOCK_SIZE[resolution]);

        int space = (int)(PNG_PIXEL_PER_MM[resolution] * 4); // 4mm Abstand
        int textSpace = (int)(PNG_PIXEL_PER_MM[resolution] * 2); // 4mm Abstand
        
        int cx = (A4_WIDTH[resolution] - space) / (patternWidth + space);
        int cy = (A4_HEIGHT[resolution] - space) / (patternHeight + space + textSpace);
        
        BufferedImage image = new BufferedImage(A4_WIDTH[resolution], A4_HEIGHT[resolution], BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, A4_WIDTH[resolution], A4_HEIGHT[resolution]);
        graphics.setColor(Color.black);
        graphics.setFont(graphics.getFont().deriveFont(35f));
        
        int p = 0;
        
        
        for (int y = 0; y < cy; y++) {
            for (int x = 0; x < cx; x++) {
                if(p < tingCodes.length) {
                    int tc = Translator.ting2code(tingCodes[p++]);
                    if(tc >= 0) {
                        
                        int px = x * (patternWidth + space) + space;
                        int py = y * (patternHeight + space + textSpace) + space;
                        
                        int pw = patternWidth / (PNG_BLOCK_SIZE[resolution] * 4);
                        int ph = patternHeight / (PNG_BLOCK_SIZE[resolution] * 4);
                        
                        drawPattern(tingCodes[p-1], px, py, pw, ph, graphics);
                        graphics.drawString(Integer.toString(tingCodes[p-1]), px, py + patternHeight + textSpace);
                    }
                }
                
            }
        }
        
        writePng(image, out);
    }
    
    public static void drawPagePNG(int start, OutputStream out) throws IOException {
        BufferedImage image = new BufferedImage(A4_WIDTH[resolution], A4_HEIGHT[resolution], BufferedImage.TYPE_INT_ARGB);

        int f = 1;
        if(resolution == Codes.DPI1200) {
            f = 2;
        }
        
        int cx = 25;
        int cy = 40;
        
        int dx = 350;
        int dy = 210;
        
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, A4_WIDTH[resolution], A4_HEIGHT[resolution]);
        graphics.setColor(Color.black);
        
        graphics.setFont(graphics.getFont().deriveFont(35f));
        graphics.drawString("Ting IDs ab " + Integer.toString(start), (5 + dx) * f, 150 * f);
        
        graphics.setFont(graphics.getFont().deriveFont(25f));

        for (int y = 0; y < cy; y++) {
            for (int x = 0; x < cx; x++) {
                if (start < 65536) {
                    int tc = Translator.ting2code(start++);
                    if(tc >= 0) {
                        drawPattern(tc, (x * 170 + dx) * f, (y * 155 + dy) * f, 4 * f, 4 * f, graphics);
                        graphics.drawString(Integer.toString(start - 1), (x * 170 + 5 + dx) * f, (y * 155 + 130 + dy) * f);
                    }
                }
            }
        }
        
        writePng(image, out);
        
    }

    public static void loadProperties() {
        setPatternSize600(Properties.getIntegerProperty(CodePreferences.PROPERTY_PATTERN_SIZE_600));
        setDotSize600(Properties.getIntegerProperty(CodePreferences.PROPERTY_DOT_SIZE_600));
        setDeltaSize600(Properties.getIntegerProperty(CodePreferences.PROPERTY_DELTA_SIZE_600));
        setPatternSize1200(Properties.getIntegerProperty(CodePreferences.PROPERTY_PATTERN_SIZE_1200));
        setDotSize1200(Properties.getIntegerProperty(CodePreferences.PROPERTY_DOT_SIZE_1200));
        setDeltaSize1200(Properties.getIntegerProperty(CodePreferences.PROPERTY_DELTA_SIZE_1200));
    }

    public static void saveProperties() {
        Properties.setProperty(CodePreferences.PROPERTY_PATTERN_SIZE_600, getPatternSize600());
        Properties.setProperty(CodePreferences.PROPERTY_DOT_SIZE_600, getDotSize600());
        Properties.setProperty(CodePreferences.PROPERTY_DELTA_SIZE_600, getDeltaSize600());
        Properties.setProperty(CodePreferences.PROPERTY_PATTERN_SIZE_1200, getPatternSize1200());
        Properties.setProperty(CodePreferences.PROPERTY_DOT_SIZE_1200, getDotSize1200());
        Properties.setProperty(CodePreferences.PROPERTY_DELTA_SIZE_1200, getDeltaSize1200());
    }
    
    

}
