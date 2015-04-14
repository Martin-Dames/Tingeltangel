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

        return nTotalMS  ;
    }
}
