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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

public class Translator {

    private final static String ID_TRANS_FILE = "/id_trans.data";
    
    private final static int[] code2ting = new int[0x010001];
    private final static int[] ting2code = new int[0x010001];
    
    private final static int min_object_code = 15001;
    private final static int max_object_code;
    
    public final static int MAX_MID = 9999;
    
    private final static Random rnd = new Random();
    
    static {
        for(int i = 0; i < code2ting.length; i++) {
            code2ting[i] = -1;
            ting2code[i] = -1;
        }
        int codeId = 0;
        int lastLowerTingId = -1;
        int lastHigherTingId = 4715;
        int lastCurrentCodeId = -1;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(Translator.class.getResourceAsStream(ID_TRANS_FILE)));
            String row;
            while((row = in.readLine()) != null) {
                row = row.trim();
                if((!row.isEmpty()) && (!row.startsWith("#"))) {
                    int currentCodeId = Integer.parseInt(row);
                    if(lastCurrentCodeId >= currentCodeId) {
                        throw new Error();
                    }
                    lastCurrentCodeId = currentCodeId;

                    for(int i = codeId; i < currentCodeId; i++) {
                        code2ting[i] = ++lastHigherTingId;
                    }
                    code2ting[currentCodeId] = ++lastLowerTingId;
                    codeId = currentCodeId + 1;
                }
            }
            for(int c = 0; c < 0x010000; c++) {
                if(code2ting[c] != -1) {
                    if(ting2code[code2ting[c]] == -1) {
                        ting2code[code2ting[c]] = c;
                    } else {
                        throw new Error();
                    }
                }
            }
            int moc = 0;
            for(int t = min_object_code; t <= 0x010000; t++) {
                if(ting2code[t] == -1) {
                    moc = t - 1;
                    break;
                }
            }
            max_object_code = moc;
            
        } catch(Exception e) {
            throw new Error(e);
        }
    }
    
    public static int getMinObjectCode() {
        return(min_object_code);
    }
    
    public static int getMaxObjectCode() {
        return(max_object_code);
    }
    
    public static int code2ting(int c) {
        return(code2ting[c]);
    }
    
    public static int ting2code(int t) {
        return(ting2code[t]);
    }
    
    public static boolean isKnownTingID(int t) {
        return(ting2code[t] >= 0);
    }
    
    public static int getRandomBookCode() {
        int min = 8001;
        int max = 8500;
        max = Math.min(getMaxTingCodeFrom(min), max);
        return(rnd.nextInt(max - min + 1) + min);
    }
    
    
    
    public static void main(String[] args) {
        int c = 0;
        System.out.println("^ Ting-ID ^ Code-ID ^ Ting-ID ^ Code-ID ^ Ting-ID ^ Code-ID ^ Ting-ID ^ Code-ID ^ Ting-ID ^ Code-ID ^");
        for(int i = 0; i < 0x10000; i++) {
            if(ting2code[i] >= 0) {
                System.out.print("| " + i + " | " + ting2code[i] + " ");
                c++;
                if(c == 5) {
                    c = 0;
                    System.out.println("|");
                }
            }
        }
    }

    public static int getMaxTingCodeFrom(int start) {
        while(start < 0x10000) {
            if(ting2code[start] == -1) {
                return(start - 1);
            }
            start++;
        }
        return(0xffff);
    }
}
