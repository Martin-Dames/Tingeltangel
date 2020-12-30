/*
 * Copyright 2019 mdames.
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
package tingeltangel.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 *
 * @author mdames
 */
public class MD5Sum {
    
    private static String getFileID(File file) {
        String id = file.getAbsolutePath() + "|" + file.length() + "|" + file.lastModified();
        return(id);
    }
    
    private static byte[] createChecksum(File file) throws IOException {
        InputStream fis =  new FileInputStream(file);

        byte[] buffer = new byte[1024];
        MessageDigest complete;
        try {
            complete = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            throw new Error(ex);
        }
        int numRead;

        do {
            numRead = fis.read(buffer);
            if(numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return(complete.digest());
    }

    private final static HashMap<String, String> CACHE = new HashMap<>();
    
    public static String md5sum(File file) throws IOException {
        
        String fileID = getFileID(file);
        String md5 = CACHE.get(fileID);
        if(md5 != null) {
            return(md5);
        }
        
        byte[] b = createChecksum(file);
        String result = "";

        for(int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        
        CACHE.put(fileID, result);
        System.out.println("md5(" + fileID + ")=" + result);
        
        return(result);
    }
}
