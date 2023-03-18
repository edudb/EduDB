/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.authentication;

import net.edudb.engine.Config;
import net.edudb.exceptions.AuthenticationFailedException;
import net.edudb.exceptions.UserAlreadyExistException;
import net.edudb.exceptions.UserNotFoundException;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AuthenticationTest {
    private static final String USER_NAME = "test_username";
    private static final String PASSWORD = "password";
    private static final String WRONG_PASSWORD = "wrong_password";
    private static final UserRole ROLE = UserRole.USER;

    @BeforeAll
    public static void setup() throws IOException {
        new File(Config.workspacesPath()).mkdirs();
        new File(Config.usersPath()).createNewFile();
    }

    @AfterEach
    public void tearDown() {
        try {
            FileWriter writer = new FileWriter(Config.usersPath());
            writer.write("");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void cleanUp() {
        new File(Config.workspacesPath()).delete();
        new File(Config.usersPath()).delete();
        new File(Config.absolutePath()).delete();
    }

    @Test
    public void createUser() throws UserAlreadyExistException {
        Authentication.createUser(USER_NAME, PASSWORD, ROLE);
        List<String[]> users = UserManager.getInstance().readAllUsers();
        Assertions.assertEquals(1, users.size());
        Assertions.assertEquals(USER_NAME, users.get(0)[0]);
        Assertions.assertEquals(ROLE.toString(), users.get(0)[2]);
        Assertions.assertTrue(PasswordUtil.verifyPassword(PASSWORD, users.get(0)[1]));
    }

    @Test
    public void createDuplicatedUser() throws UserAlreadyExistException {
        Authentication.createUser(USER_NAME, PASSWORD, ROLE);
        Assertions.assertThrows(UserAlreadyExistException.class, () -> Authentication.createUser(USER_NAME, PASSWORD, ROLE));
    }

    @Test
    void loginWithCorrectPassword() throws AuthenticationFailedException, UserAlreadyExistException {
        Authentication.createUser(USER_NAME, PASSWORD, ROLE);
        String token = Authentication.login(USER_NAME, PASSWORD);
        Assertions.assertNotNull(token);
    }

    @Test
    void loginWithWrongPassword() throws UserAlreadyExistException {
        Authentication.createUser(USER_NAME, PASSWORD, ROLE);
        Assertions.assertThrows(AuthenticationFailedException.class, () -> Authentication.login(USER_NAME, WRONG_PASSWORD));

    }

    @Test
    void removeExistingUser() throws UserNotFoundException, UserAlreadyExistException {
        Authentication.createUser(USER_NAME, PASSWORD, ROLE);
        Authentication.removeUser(USER_NAME);
        List<String[]> users = UserManager.getInstance().readAllUsers();
        Assertions.assertEquals(0, users.size());
    }

    @Test
    void removeNonExistingUser() {
        Assertions.assertThrows(UserNotFoundException.class, () -> Authentication.removeUser(USER_NAME));
    }
}
