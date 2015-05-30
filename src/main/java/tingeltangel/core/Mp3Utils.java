/*
    Copyright (C) 2015   Jesper Zedlitz <jesper@zedlitz.de>
  
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Header;

public class Mp3Utils {

    public static int getDuration( File f ) throws IOException {
        Bitstream bitstream = new Bitstream( new FileInputStream(f));
        Header m_header = null;
        try {
            m_header = bitstream.readFrame();
        } catch (BitstreamException e) {
           throw new IOException(e) ;
        }

        int mediaLength = (int)f.length();

        int nTotalMS = 0;
        if ( m_header != null && mediaLength != AudioSystem.NOT_SPECIFIED) {
            nTotalMS = Math.round(m_header.total_ms(mediaLength));
        }

        try {
            bitstream.close();
        } catch(BitstreamException e) {
            throw new IOException(e);
        }
        
        return nTotalMS  ;
    }
}
