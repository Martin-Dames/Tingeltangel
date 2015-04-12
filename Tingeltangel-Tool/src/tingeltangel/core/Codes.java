
package tingeltangel.core;

import java.io.PrintWriter;


public class Codes {
    
        private final static float DOT_SIZE = 0.12f;
        private final static float DELTA_SIZE = DOT_SIZE;
        private final static float BLOCK_SIZE = 0.84f;
        
    
        private final static float BIG_DOT_SIZE = 1.8f;
        private final static float BIG_DELTA_SIZE = 1.2f;
        private final static float BIG_BLOCK_SIZE = 6.3f;
        
        
        private static float[] getDotPosFromInt(int i) {
                float[] f = new float[2];
                switch(i) {
                        case 0: 
                            f[0] = 1; f[1] = -1;
                            return(f);
                        case 1:
                            f[0] = -1; f[1] = -1;
                            return(f);
                        case 2:
                            f[0] = -1; f[1] = 1;
                            return(f);
                        case 3:
                            f[0] = 1; f[1] = 1;
                            return(f);
                }
                throw new Error();
        }

        private static int getPari(int w7, int w6, int w5, int w4, int w3, int w2, int w1, int w0) {
                int c1 = ((w1 ^ w4 ^ w6 ^ w7) & 0x01) << 1;
                int c2 = (w0 ^ w2 ^ w3 ^ w5) & 0x01;
                return(c1 | c2);
        }

        private static float[][][] getPatternFromInt(int i) {
                int w0 = i & 0x03;
                int w1 = (i >> 2) & 0x03;
                int w2 = (i >> 4) & 0x03;
                int w3 = (i >> 6) & 0x03;
                int w4 = (i >> 8) & 0x03;
                int w5 = (i >> 10) & 0x03;
                int w6 = (i >> 12) & 0x03;
                int w7 = (i >> 14) & 0x03;
                float[][][] pattern = {
                    {{ 0,  0}, { 0,  0}, { 0,  0}, { 0,  0}},
                    {{ 0,  0}, getDotPosFromInt(getPari(w7, w6, w5, w4, w3, w2, w1, w0)), getDotPosFromInt(w7), getDotPosFromInt(w6)},
                    {{+1,  0}, getDotPosFromInt(w5), getDotPosFromInt(w4), getDotPosFromInt(w3)},
                    {{ 0,  0}, getDotPosFromInt(w2), getDotPosFromInt(w1), getDotPosFromInt(w0)}
                };
                return(pattern);
        }

        private static void drawText(float x, float y, String text, PrintWriter out) {
                out.println("newpath");
                out.println(x + " " + y + " moveto");
                out.println("(" + text + ") show");
        }
        
        private static void drawDot(float x, float y, PrintWriter out) {
                out.println("newpath");
                out.println(x + " " + y + " moveto");
                out.println(x + " " + (y + DOT_SIZE) + " lineto");
                out.println((x + DOT_SIZE) + " " + (y + DOT_SIZE) + " lineto");
                out.println((x + DOT_SIZE) + " " + y + " lineto");
                out.println("fill");
        }
        
        private static void drawBigDot(float x, float y, PrintWriter out) {
                out.println("newpath");
                out.println(x + " " + y + " moveto");
                out.println(x + " " + (y + BIG_DOT_SIZE) + " lineto");
                out.println((x + BIG_DOT_SIZE) + " " + (y + BIG_DOT_SIZE) + " lineto");
                out.println((x + BIG_DOT_SIZE) + " " + y + " lineto");
                out.println("fill");
        }

        private static void drawPattern(int p, float x, float y, PrintWriter out) {
                float[][][] pattern = getPatternFromInt(p);
                for(int dx = 0; dx < 4; dx++) {
                        for(int dy = 0; dy < 4; dy++) {
                                float px = pattern[3 - dy][dx][0] * DELTA_SIZE;
                                float py = pattern[3 - dy][dx][1] * DELTA_SIZE;
                                drawDot(x + dx * BLOCK_SIZE + px, y + dy * BLOCK_SIZE + py, out);
                        }
                }
        }
        
        private static void drawBigPattern(int p, float x, float y, PrintWriter out) {
                float[][][] pattern = getPatternFromInt(p);
                for(int dx = 0; dx < 4; dx++) {
                        for(int dy = 0; dy < 4; dy++) {
                                float px = pattern[3 - dy][dx][0] * BIG_DELTA_SIZE;
                                float py = pattern[3 - dy][dx][1] * BIG_DELTA_SIZE;
                                drawBigDot(x + dx * BIG_BLOCK_SIZE + px, y + dy * BIG_BLOCK_SIZE + py, out);
                        }
                }
                out.println("newpath");
                out.println((x - 1 * BIG_BLOCK_SIZE) + " " + (y - 1 * BIG_BLOCK_SIZE) + " moveto");
                out.println((x - 1 * BIG_BLOCK_SIZE) + " " + (y + 4 * BIG_BLOCK_SIZE) + " lineto");
                out.println((x + 4 * BIG_BLOCK_SIZE) + " " + (y + 4 * BIG_BLOCK_SIZE) + " lineto");
                out.println((x + 4 * BIG_BLOCK_SIZE) + " " + (y - 1 * BIG_BLOCK_SIZE) + " lineto");
                out.println((x - 1 * BIG_BLOCK_SIZE) + " " + (y - 1 * BIG_BLOCK_SIZE) + " lineto");
                out.println("stroke");
                
        }

        private static void drawCarpet(int p, float x, float y, int w, int h, String label, PrintWriter out) {

                if(label != null) {
                    drawText(x, y - 4, label, out);
                }

                w = (int)(w / (4 * BLOCK_SIZE));
                h = (int)(h / (4 * BLOCK_SIZE));

                for(int ix = 0; ix < w; ix++) {
                        for(int iy = 0; iy < h; iy++) {
                                drawPattern(p, x + ix * 4 * BLOCK_SIZE, y + iy * 4 * BLOCK_SIZE, out);
                        }
                }
                out.flush();
        }

        private static void drawBigCarpet(int p, float x, float y, String label, PrintWriter out) {

                if(label != null) {
                    drawText(x, y - 15, label, out);
                }

                drawBigPattern(p, x, y, out);
                out.flush();
        }
        
        public static void drawEps(int code, int width, int height, PrintWriter out) {
            out.println("%!PS-Adobe-3.0 EPSF-3.0");
            out.println("%%BoundingBox: 0 0 " + width + " " + height);
            drawCarpet(code, 0, 0, width, height, null, out);
        }
        
        public static void drawPage(int start, PrintWriter out) {
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
                for(int y = cy - 1; y >= 0; y--) {
                        for(int x = 0; x < cx; x++) {
                                if(start < 65536) {
                                        drawCarpet(start, 50 + x * 20, 17 + y * 20, 15, 15, "" + (start++), out);
                                }
                        }
                }
                out.println("showpage");
                out.flush();
        }
        
        public static void drawBigPage(int[] index, String[] caption, PrintWriter out) {
                int cx = 8;
                int cy = 14;
                out.println("/Times-Roman findfont");
                out.println("8 scalefont");
                out.println("setfont");
                drawText(50, 815, "CODE IDS: " + caption[0] + " - " + caption[caption.length - 1], out);
                out.println("/Times-Roman findfont");
                out.println("8 scalefont");
                out.println("setfont");
                int p = 0;
                for(int y = cy - 1; y >= 0; y--) {
                    for(int x = 0; x < cx; x++) {
                        if(p < index.length) {
                            if(index[p] != -1) {
                                drawBigCarpet(index[p], 50 + x * 58, 18 + y * 57, caption[p], out);
                            }
                            p++;
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
                for(int y = cy - 1; y >= 0; y--) {
                    for(int x = 0; x < cx; x++) {
                        if(p < index.length) {
                            if(index[p] != -1) {
                                drawCarpet(index[p], 50 + x * 20, 17 + y * 20, 15, 15, caption[p], out);
                            }
                            p++;
                        }
                    }
                }
                out.println("showpage");
                out.flush();
        }

}
