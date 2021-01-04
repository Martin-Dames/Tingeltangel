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
package tingeltangel.cli.cmds;

import tingeltangel.core.ReadYamlFile;
import tingeltangel.cli.CliCommand;
import tingeltangel.cli.CliSwitch;
import tingeltangel.core.Book;
import tingeltangel.core.Codes;
import tingeltangel.core.Translator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class OidCode extends CliCommand {
    @Override
    public String getName() {
        return "oid-code";
    }

    @Override
    public String getDescription() {
        return "Erstellt zu einer yaml-Datei passende OID-Codes.";
    }

    @Override
    public Map<String, CliSwitch> getSwitches() {
        CliSwitch[] list = {
                new CliSwitch() {
                    @Override
                    public String getName() {
                        return ("i");
                    }

                    @Override
                    public String getDescription() {
                        return ("setzt den Namen der Eingabedatei");
                    }

                    @Override
                    public boolean hasArgument() {
                        return (true);
                    }

                    @Override
                    public boolean isOptional() {
                        return (false);
                    }

                    @Override
                    public String getLabel() {
                        return ("Eingabedatei");
                    }

                    @Override
                    public String getDefault() {
                        return (null);
                    }

                    @Override
                    public boolean acceptValue(String value) {
                        return (!value.isEmpty());
                    }
                }
        };


        return list2map(list);
    }

    @Override
    public void execute(Map<String, String> args) throws Exception {
        File inputFile = new File(args.get("i"));

        if (inputFile.canRead()) {

            Codes.setResolution(Codes.DPI1200);

            ReadYamlFile ryf = new ReadYamlFile();
            Book book = ryf.read(inputFile, null);

            // Write files for the used OIDs.
            generateImage(book.getID(), book, "START");
            for (Integer oid : ryf.getUsedOidAndIdentifiers().keySet()) {
                String identifier = ryf.getUsedOidAndIdentifiers().get(oid);
                if (identifier == null) {
                    generateImage(oid, book, oid.toString());
                } else {
                    generateImage(oid, book, identifier);
                }
            }
        } else {
            System.err.println("Fehler beim Lesen der Eingabedatei.");
        }
    }

    private void generateImage(final int oid, final  Book book, final String name) throws IOException {
        final int codeID = Translator.ting2code(oid);

        String filename = "oid-" + book.getID() + "-" + name + ".png";
        System.out.println("Writing " + filename + "...");

        OutputStream out = new FileOutputStream(filename);
        Codes.drawPng(codeID, 102, 102, out);
        out.close();
    }
}
