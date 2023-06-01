/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.engine.authentication;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import net.edudb.engine.Config;
import net.edudb.exception.AuthenticationFailedException;
import net.edudb.exception.UserAlreadyExistException;
import net.edudb.exception.UserNotFoundException;
import net.edudb.exception.WorkspaceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.List;

class AuthenticationTest {
    private static FileSystem fs; // in-memory file system for testing
    private static final String WORKSPACE = "test_workspace";
    private static final String USERNAME = "test_username";
    private static final String PASSWORD = "password";
    private static final String WRONG_PASSWORD = "wrong_password";
    private static final UserRole ROLE = UserRole.USER;

    @BeforeEach
    public void setup() throws IOException {
        fs = Jimfs.newFileSystem(Configuration.unix());
        Config.setAbsolutePath(fs.getPath("test"));

        Files.createDirectories(Config.workspacePath(WORKSPACE));
        Files.createFile(Config.adminsPath());
        Files.createFile(Config.usersPath(WORKSPACE));
    }

    @AfterEach
    void tearDown() throws IOException {
        Config.setAbsolutePath(null);
        fs.close();
    }

    @Test
    void createUser() throws UserAlreadyExistException, WorkspaceNotFoundException {
        Authentication.createUser(USERNAME, PASSWORD, ROLE, WORKSPACE);
        List<String[]> users = UserManager.getInstance().readAllUsers(WORKSPACE);
        for (String[] user : users) {
            if (user[0].equals(USERNAME)) {
                Assertions.assertEquals(USERNAME, user[0]);
                Assertions.assertTrue(PasswordUtil.verifyPassword(PASSWORD, user[1]));
                Assertions.assertEquals(ROLE.toString(), user[2]);
                return;
            }
        }
        Assertions.fail("User not found");
    }

    @Test
    void createDuplicatedUser() throws UserAlreadyExistException, WorkspaceNotFoundException {
        Authentication.createUser(USERNAME, PASSWORD, ROLE, WORKSPACE);
        Assertions.assertThrows(UserAlreadyExistException.class, () -> Authentication.createUser(USERNAME, PASSWORD, ROLE, WORKSPACE));
    }

    @Test
    void loginWithCorrectPassword() throws AuthenticationFailedException, UserAlreadyExistException, WorkspaceNotFoundException {
        Authentication.createUser(USERNAME, PASSWORD, ROLE, WORKSPACE);
        String token = Authentication.login(WORKSPACE, USERNAME, PASSWORD);
        Assertions.assertNotNull(token);
    }

    @Test
    void loginWithWrongPassword() throws UserAlreadyExistException, WorkspaceNotFoundException {
        Authentication.createUser(USERNAME, PASSWORD, ROLE, WORKSPACE);
        Assertions.assertThrows(AuthenticationFailedException.class, () -> Authentication.login(null, USERNAME, WRONG_PASSWORD));

    }

    @Test
    void removeExistingUser() throws UserNotFoundException, UserAlreadyExistException, WorkspaceNotFoundException {
        Authentication.createUser(USERNAME, PASSWORD, ROLE, WORKSPACE);
        Authentication.removeUser(WORKSPACE, USERNAME);
        List<String[]> users = UserManager.getInstance().readAllUsers(WORKSPACE);
        Assertions.assertEquals(0, users.size());
    }


    @Test
    void removeExistingUserWithCommonPrefix() throws UserNotFoundException, UserAlreadyExistException, WorkspaceNotFoundException {
        Authentication.createUser(USERNAME, PASSWORD, ROLE, WORKSPACE);
        Authentication.createUser(USERNAME + "_temp", PASSWORD, ROLE, WORKSPACE);
        Authentication.removeUser(WORKSPACE, USERNAME);
        List<String[]> users = UserManager.getInstance().readAllUsers(WORKSPACE);
        Assertions.assertEquals(1, users.size());
    }

    @Test
    void removeNonExistingUser() {
        Assertions.assertThrows(UserNotFoundException.class, () -> Authentication.removeUser(WORKSPACE, USERNAME));
    }
}
