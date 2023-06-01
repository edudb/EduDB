/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.block;

import net.edudb.engine.Config;
import net.edudb.page.Page;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A block writer that writes binary page files to disk.
 *
 * @author Ahmed Abdul Badie
 */
public class BinaryBlockWriter implements BlockWriter {
    @Override
    public void write(String workspaceName, String databaseName, Page page) throws IOException {
        Path pagePath = Config.pagePath(workspaceName, databaseName, page.getName());
        try (OutputStream fileOut = Files.newOutputStream(pagePath)) {
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(page);
            out.close();
        }
    }
}
