/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.statistics;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import net.edudb.engine.Config;
import net.edudb.exception.WorkspaceAlreadyExistException;
import net.edudb.exception.WorkspaceNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;

public class SchemaTest {
    private static FileSystem fs; // in-memory file system for testing
    private static final String[] workspaces = {"workspace1", "workspace2", "workspace3"};

    @BeforeAll
    public static void setup() throws WorkspaceAlreadyExistException, IOException {
        fs = Jimfs.newFileSystem(Configuration.unix());
        Config.setAbsolutePath(fs.getPath("test"));


        for (String workspace : workspaces) {
            Files.createDirectories(Config.databasesPath(workspace));
        }
    }

    @AfterAll
    public static void tearDown() throws IOException {
        Config.setAbsolutePath(null);
        fs.close();
    }

    @Test
    public void test() throws WorkspaceNotFoundException {
        Schema generalSchema = Schema.getInstance();
        for (String workspace : workspaces) {
            Assertions.assertNotNull(generalSchema.getWorkspace(workspace));
            Assertions.assertEquals(workspace, generalSchema.getWorkspace(workspace).getWorkspaceName());
        }
    }

}
