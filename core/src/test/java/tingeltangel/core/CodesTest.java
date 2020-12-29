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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CodesTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testDrawPng() throws IOException {
        // removed becase of problems with travis-ci test results (missing X11 environment)
        /*
        File result = folder.newFile();
        Codes.setResolution(Codes.DPI1200);
        OutputStream out = new FileOutputStream(result);
        Codes.drawPng(15001, 102, 51, out);
        out.close();

        BufferedImage image = ImageIO.read(result);
        assertEquals(4800, image.getWidth());
        assertEquals(2400, image.getHeight());
        */
    }
}
