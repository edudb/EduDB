/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.engine.authentication;

import net.edudb.exception.AuthenticationFailedException;
import net.edudb.exception.InvalidRoleException;
import net.edudb.exception.UserAlreadyExistException;
import net.edudb.exception.UserNotFoundException;

public class Authentication {
    private static UserManager userManager = UserManager.getInstance();
    private static final int USERNAME_INDEX = 0;
    private static final int PASSWORD_INDEX = 1;
    private static final int ROLE_INDEX = 2;

    private Authentication() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Creates a new user with the given username, password and role.
     *
     * @param username Username of the user to be created.
     * @param password Password of the user to be created.
     * @param role     Role of the user to be created. limited to {@link UserRole} values.
     * @auther Ahmed Nasser Gaafar
     */
    public static void createUser(String username, String password, UserRole role) throws UserAlreadyExistException {
        String hashedPassword = PasswordUtil.hashPassword(password);
        try {
            userManager.writeUser(username, hashedPassword, role.toString());
        } catch (InvalidRoleException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a user with the given username.
     *
     * @param username Username of the user to be removed.
     * @throws UserNotFoundException If the user is not found.
     */
    public static void removeUser(String username) throws UserNotFoundException {
        userManager.removeUser(username);
    }

    /**
     * Authenticates a user with the given username and password.
     *
     * @param username
     * @param password
     * @return A JWT token if the user is authenticated.
     * @throws AuthenticationFailedException If the user is not authenticated.
     * @auther Ahmed Nasser Gaafar
     */
    public static String login(String username, String password) throws AuthenticationFailedException {
        try {
            String[] user = userManager.readUser(username);
            if (!PasswordUtil.verifyPassword(password, user[PASSWORD_INDEX])) {
                throw new AuthenticationFailedException("Password is incorrect");
            }
            UserRole role;
            try {
                role = UserRole.fromString(user[ROLE_INDEX]);
            } catch (InvalidRoleException e) {
                System.err.println(String.format("Data of user %s is corrupted", username));
                throw new RuntimeException(e);
            }

            return JwtUtil.generateToken(username, role);

        } catch (UserNotFoundException e) {
            throw new AuthenticationFailedException("Invalid username");
        }
    }
}
