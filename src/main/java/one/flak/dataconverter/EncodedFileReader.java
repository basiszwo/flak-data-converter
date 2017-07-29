// Licensed under the GNU GENERAL PUBLIC LICENSE Version 3.
// See LICENSE file in the project root for full license information.

package one.flak.dataconverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class EncodedFileReader {

    public String readFile(Path filePath) {
        StringBuilder base64Encoded = new StringBuilder();

        try (BufferedReader fileReader = Files.newBufferedReader(filePath)) {
            String line;

            while ((line = fileReader.readLine()) != null) {
                base64Encoded.append(line);
            }

        } catch (IOException e) {
            // @Todo Proper exception handling (maybe send of to sentry)
            e.printStackTrace();
            return "";
        }

        byte[] base64Decoded = Base64.getDecoder().decode(base64Encoded.toString());

        Inflater inflater = new Inflater();
        inflater.setInput(base64Decoded);

        byte[] buffer = new byte[4096];

        StringBuilder fileContent = new StringBuilder();

        int readCount = 0;

        try {
            while((readCount = inflater.inflate(buffer, 0, buffer.length)) > 0) {
                fileContent.append(new String(buffer, 0, readCount));
            }
        } catch (DataFormatException e) {
            e.printStackTrace();
            return "";
        }

        return fileContent.toString();
    }

}
