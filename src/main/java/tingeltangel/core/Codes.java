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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;


public class Codes {

    /*
    private final static float[] PS_DOT_SIZE = {0.12f, 0.06f};
    private final static float[] PS_DELTA_SIZE = {0.12f, 0.12f};
    private final static float[] PS_BLOCK_SIZE = {0.84f, 0.84f};
    */

    private final static int[] PNG_DOT_SIZE = {1, 2};
    private final static int[] PNG_DELTA_SIZE = {1, 2};
    private final static int[] PNG_DELTA_X_SIZE = {1, 2};
    private final static int[] PNG_BLOCK_SIZE = {6, 12};

    private final static float[] PNG_PIXEL_PER_MM = {23.62205f, 47.24409f};

    public final static int DPI600 = 0;
    public final static int DPI1200 = 1;

    private static int resolution = DPI1200;


    public static void setResolution(int resolution) {
        Codes.resolution = resolution;
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

    /*
    private static void drawText(float x, float y, String text, PrintWriter out) {
        out.println("newpath");
        out.println(x + " " + y + " moveto");
        out.println("(" + text + ") show");
    }

    private static void drawDot(float x, float y, PrintWriter out) {
        out.println("newpath");
        out.println(x + " " + y + " moveto");
        out.println(x + " " + (y + PS_DOT_SIZE[resolution]) + " lineto");
        out.println((x + PS_DOT_SIZE[resolution]) + " " + (y + PS_DOT_SIZE[resolution]) + " lineto");
        out.println((x + PS_DOT_SIZE[resolution]) + " " + y + " lineto");
        out.println("fill");
    }

    private static void drawPattern(int p, float x, float y, PrintWriter out) {
        int[][][] pattern = getPatternFromInt(p);
        for (int dx = 0; dx < 4; dx++) {
            for (int dy = 0; dy < 4; dy++) {
                float px = pattern[3 - dy][dx][0] * PS_DELTA_SIZE[resolution];
                float py = pattern[3 - dy][dx][1] * PS_DELTA_SIZE[resolution];
                drawDot(x + dx * PS_BLOCK_SIZE[resolution] + px, y + dy * PS_BLOCK_SIZE[resolution] + py, out);
            }
        }
    }

    private static void drawCarpet(int p, float x, float y, int w, int h, String label, PrintWriter out) {

        if (label != null) {
            drawText(x, y - 4, label, out);
        }

        w = (int) (w / (4 * PS_BLOCK_SIZE[resolution]));
        h = (int) (h / (4 * PS_BLOCK_SIZE[resolution]));

        for (int ix = 0; ix < w; ix++) {
            for (int iy = 0; iy < h; iy++) {
                drawPattern(p, x + ix * 4 * PS_BLOCK_SIZE[resolution], y + iy * 4 * PS_BLOCK_SIZE[resolution], out);
            }
        }
        out.flush();
    }

    public static void drawEps(int code, int width, int height, PrintWriter out) {
        width = (int) ((width * 100.0) / 25.4);
        height = (int) ((height * 100.0) / 25.4);
        out.println("%!PS-Adobe-3.0 EPSF-3.0");
        out.println("%%BoundingBox: 0 0 " + width + " " + height);
        drawCarpet(code, 0, 0, width, height, null, out);
    }
    */

    private static void drawPattern(int code, int x, int y, int width, int height, Graphics2D graphics) {
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

    private static void writePng(BufferedImage image, OutputStream out) throws IOException {
        
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
    
    

    /*
    public static void drawBooklet(String title, int mid, List<Tupel<Integer, String>> booklet, PrintWriter out) {

        mid = Translator.ting2code(mid);

        int entriesPerPage = 30;
        int pages = booklet.size() / entriesPerPage;
        if (booklet.size() % entriesPerPage > 0) {
            pages++;
        }
        int page = 0;
        int entry = 0;

        out.println("%!PS-Adobe-2.0");
        out.println();
        out.println("%%Pages: " + pages);

        Iterator<Tupel<Integer, String>> entries = booklet.iterator();
        while (entries.hasNext()) {

            Tupel<Integer, String> tupel = entries.next();

            if (entry % entriesPerPage == 0) {
                if (entry != 0) {
                    out.println("showpage");
                    page++;
                }
                out.println("%%Page: " + (page + 1) + " " + (page + 1));
                out.println("/Times-Roman findfont");
                out.println("10 scalefont");
                out.println("setfont");
                drawText(80, 815, "Buch: " + title + " [Seite " + (page + 1) + " von " + pages + "]", out);
                out.println("/Times-Roman findfont");
                out.println("8 scalefont");
                out.println("setfont");
                if (mid >= 0) {
                    drawCarpet(mid, 50, 810, 20, 20, null, out);
                }
            }


            drawText(80, 780 - (entry % entriesPerPage) * 25, Integer.toString(tupel.a) + ": " + tupel.b, out);

            int cid = Translator.ting2code(tupel.a);
            if (cid >= 0) {
                drawCarpet(cid, 50, 775 - (entry % entriesPerPage) * 25, 20, 20, null, out);
            }

            entry++;
        }
        out.println("showpage");
        out.flush();
    }
    */

    public static void drawPagePNG(int start, OutputStream out) throws IOException {
        BufferedImage image = new BufferedImage(4960, 7015, BufferedImage.TYPE_INT_ARGB);

        setResolution(DPI600);
        
        int cx = 25;
        int cy = 40;
        
        int dx = 350;
        int dy = 140;
        
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, 4960, 7015);
        graphics.setColor(Color.black);
        
        graphics.setFont(graphics.getFont().deriveFont(35f));
        graphics.drawString("Ting IDs ab " + Integer.toString(start), 5 + dx, 100);
        
        graphics.setFont(graphics.getFont().deriveFont(25f));

        for (int y = 0; y < cy; y++) {
            for (int x = 0; x < cx; x++) {
                if (start < 65536) {
                    int tc = Translator.ting2code(start++);
                    if(tc >= 0) {
                        drawPattern(tc, x * 170 + dx, y * 170 + dy, 4, 4, graphics);
                        graphics.drawString(Integer.toString(start - 1), x * 170 + 5 + dx, y * 170 + 130 + dy);
                    }
                }
            }
        }
        
        writePng(image, out);
        
    }
    
    
    public static void main(String[] args) throws IOException {
        drawPagePNG(0, new FileOutputStream("c:\\test.png"));
    }
    
    /*
    public static void drawPagePS(int start, PrintWriter out) {
        int cx = 25;
        int cy = 40;
        int till = Math.min(65535, start + (cx * cy) - 1);
        out.println("/Times-Roman findfont");
        out.println("5 scalefont");
        out.println("setfont");
        drawText(50, 815, "CODE IDS: " + start + " - " + till, out);
        out.println("/Times-Roman findfont");
        out.println("4 scalefont");
        out.println("setfont");
        for (int y = cy - 1; y >= 0; y--) {
            for (int x = 0; x < cx; x++) {
                if (start < 65536) {
                    drawCarpet(start, 50 + x * 20, 17 + y * 20, 15, 15, "" + (start++), out);
                }
            }
        }
        out.println("showpage");
        out.flush();
    }

    public static void drawPage(int[] index, String[] caption, PrintWriter out) {
        int cx = 25;
        int cy = 40;
        out.println("/Times-Roman findfont");
        out.println("5 scalefont");
        out.println("setfont");
        drawText(50, 815, "TING IDS: " + caption[0] + " - " + caption[caption.length - 1], out);
        out.println("/Times-Roman findfont");
        out.println("4 scalefont");
        out.println("setfont");
        int p = 0;
        for (int y = cy - 1; y >= 0; y--) {
            for (int x = 0; x < cx; x++) {
                if (p < index.length) {
                    if (index[p] != -1) {
                        drawCarpet(index[p], 50 + x * 20, 17 + y * 20, 15, 15, caption[p], out);
                    }
                    p++;
                }
            }
        }
        out.println("showpage");
        out.flush();
    }
    */

}
