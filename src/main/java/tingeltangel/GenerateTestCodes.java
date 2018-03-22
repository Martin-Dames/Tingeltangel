/*
 * Copyright 2017 mdames.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tingeltangel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import tingeltangel.core.Codes;
import static tingeltangel.core.Codes.drawPattern;
import tingeltangel.core.Translator;

/**
 *
 * @author mdames
 */
public class GenerateTestCodes {
    
    private final static int TING_ID = 9999;
    private final static int CODE_ID = Translator.ting2code(TING_ID);
    
    
    public static void main(String[] args) throws Exception {
        
        
        generate600dpi();
        generate1200dpi(1);
        generate1200dpi(2);
        generate1200dpi(3);
    }
    
    private static void generate1200dpi(int page) throws Exception {
        
        int patternMin = 12;
        int patternMax = 14;
        
        if(page == 2) {
            patternMin = 15;
            patternMax = 17;
        } else if(page == 3) {
            patternMin = 18;
            patternMax = 20;
        }
        
        int PATTERN_WIDTH = 10;
        int PATTERN_HEIGHT = 10;
        int PATTERN_DX = 10;
        int PATTERN_DY = 30;
        
        Codes.setResolution(Codes.DPI1200);
        float ppm = Codes.PNG_PIXEL_PER_MM[Codes.DPI1200];
        int imageWidth = Codes.A4_WIDTH[Codes.DPI1200];
        int imageHeight = Codes.A4_HEIGHT[Codes.DPI1200];
        BufferedImage image1200 = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics1200 = image1200.createGraphics();
        graphics1200.setColor(Color.white);
        //graphics.setComposite(AlphaComposite.Clear);
        graphics1200.fillRect(0, 0, imageWidth, imageHeight);
        //graphics.setComposite(AlphaComposite.Src);
        graphics1200.setColor(Color.black);

        graphics1200.setFont(new Font("TimesRoman", Font.PLAIN, 400)); 
        graphics1200.drawString("Ting Testcodes 1200dpi [Seite " + page + "/3] (code=" + TING_ID + ")", 400, 600);
        graphics1200.setFont(new Font("TimesRoman", Font.PLAIN, 300)); 
        graphics1200.drawString("  (ps=Rastergröße / ds=Punktgröße / de=Verschiebung)", 400, 900);
        graphics1200.setFont(new Font("TimesRoman", Font.PLAIN, 200)); 
        
        int dx = (int)((PATTERN_WIDTH + PATTERN_DX) * ppm);
        int dy = (int)((PATTERN_HEIGHT + PATTERN_DY) * ppm);
        
        int px = 200;
        int py = 1000;
        
        // 600dpi
        for(int patternSize = patternMin; patternSize <= patternMax; patternSize++) {
            Codes.setPatternSize1200(patternSize);
            for(int dotSize = 1; dotSize <= 4; dotSize++) {
                Codes.setDotSize1200(dotSize);
                for(int deltaSize = 1; deltaSize <= 4; deltaSize++) {
                    Codes.setDeltaSize1200(deltaSize);
                    
                    drawPattern(CODE_ID, px, py, PATTERN_WIDTH, PATTERN_HEIGHT, graphics1200);
                    graphics1200.drawString("ps = " + patternSize, px, py + (int)(PATTERN_HEIGHT * ppm) + 480);
                    graphics1200.drawString("ds = " + dotSize, px, py + (int)(PATTERN_HEIGHT * ppm) + 640);
                    graphics1200.drawString("de = " + deltaSize, px, py + (int)(PATTERN_HEIGHT * ppm) + 800);
                    
                    graphics1200.drawRect(px, py, (int)(PATTERN_WIDTH * ppm) + 420, (int)(PATTERN_HEIGHT * ppm) + 840);
                    
                    px += dx;
                    
                    if(px > imageWidth - 600) {
                        px = 200;
                        py += dy;
                    }
                }
            }
        }
        
        FileOutputStream out = new FileOutputStream("codetest/test1200dpi_" + page + ".png");
        
        Codes.writePng(image1200, out);
        
        out.close();
        
    }
    
    private static void generate600dpi() throws Exception {
        
        int PATTERN_WIDTH = 10;
        int PATTERN_HEIGHT = 10;
        int PATTERN_DX = 10;
        int PATTERN_DY = 30;
        
        Codes.setResolution(Codes.DPI600);
        float ppm = Codes.PNG_PIXEL_PER_MM[Codes.DPI600];
        int imageWidth = Codes.A4_WIDTH[Codes.DPI600];
        int imageHeight = Codes.A4_HEIGHT[Codes.DPI600];
        BufferedImage image600 = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics600 = image600.createGraphics();
        graphics600.setColor(Color.white);
        //graphics.setComposite(AlphaComposite.Clear);
        graphics600.fillRect(0, 0, imageWidth, imageHeight);
        //graphics.setComposite(AlphaComposite.Src);
        graphics600.setColor(Color.black);

        graphics600.setFont(new Font("TimesRoman", Font.PLAIN, 200)); 
        graphics600.drawString("Ting Testcodes 600dpi (code=" + TING_ID + ")", 200, 300);
        graphics600.setFont(new Font("TimesRoman", Font.PLAIN, 150)); 
        graphics600.drawString("  (ps=Rastergröße / ds=Punktgröße / de=Verschiebung)", 200, 450);
        graphics600.setFont(new Font("TimesRoman", Font.PLAIN, 100)); 
        
        int dx = (int)((PATTERN_WIDTH + PATTERN_DX) * ppm);
        int dy = (int)((PATTERN_HEIGHT + PATTERN_DY) * ppm);
        
        int px = 100;
        int py = 500;
        
        // 600dpi
        for(int patternSize = 6; patternSize <= 10; patternSize++) {
            Codes.setPatternSize600(patternSize);
            for(int dotSize = 1; dotSize <= 3; dotSize++) {
                Codes.setDotSize600(dotSize);
                for(int deltaSize = 1; deltaSize <= 4; deltaSize++) {
                    Codes.setDeltaSize600(deltaSize);
                    
                    drawPattern(CODE_ID, px, py, PATTERN_WIDTH, PATTERN_HEIGHT, graphics600);
                    graphics600.drawString("ps = " + patternSize, px, py + (int)(PATTERN_HEIGHT * ppm) + 240);
                    graphics600.drawString("ds = " + dotSize, px, py + (int)(PATTERN_HEIGHT * ppm) + 320);
                    graphics600.drawString("de = " + deltaSize, px, py + (int)(PATTERN_HEIGHT * ppm) + 400);
                    
                    graphics600.drawRect(px, py, (int)(PATTERN_WIDTH * ppm) + 210, (int)(PATTERN_HEIGHT * ppm) + 420);
                    
                    px += dx;
                    
                    if(px > imageWidth - 300) {
                        px = 100;
                        py += dy;
                    }
                }
            }
        }
        
        FileOutputStream out = new FileOutputStream("codetest/test600dpi.png");
        
        Codes.writePng(image600, out);
        
        out.close();
        
    }
    
}
