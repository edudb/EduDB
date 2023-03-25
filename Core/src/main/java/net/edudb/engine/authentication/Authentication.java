/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.engine.authentication;

import net.edudb.exception.*;

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
    public static void createUser(String username, String password, UserRole role, String workspaceName) throws UserAlreadyExistException, WorkspaceNotFoundException {
        String hashedPassword = PasswordUtil.hashPassword(password);
        try {
            userManager.createUser(username, hashedPassword, role.toString(), workspaceName);
        } catch (InvalidRoleException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new user with the given username, password and role.
     *
     * @param username Username of the user to be created.
     * @param password Password of the user to be created.
     * @throws UserAlreadyExistException If the user already exists.
     * @auther Ahmed Nasser Gaafar
     */
    public static void createAdmin(String username, String password) throws UserAlreadyExistException {
        String hashedPassword = PasswordUtil.hashPassword(password);
        userManager.createAdmin(username, hashedPassword);
    }

    /**
     * Removes a user with the given username.
     *
     * @param workspace The workspace to remove the user from.
     * @param username  Username of the user to be removed.
     * @throws UserNotFoundException If the user is not found.
     * @auther Ahmed Nasser Gaafar
     */
    public static void removeUser(String workspace, String username) throws UserNotFoundException, WorkspaceNotFoundException {
        userManager.removeUser(workspace, username);
    }

    /**
     * @param username Username of the admin to be removed.
     * @throws UserNotFoundException If the user is not found.
     * @auther Ahmed Nasser Gaafar
     */
    public static void removeAdmin(String username) throws UserNotFoundException {
        userManager.removeAdmin(username);
    }

    /**
     * Authenticates a user with the given username and password and returns a JWT token.
     * If the workspace is null, the user is logged in as an admin. Otherwise, the user is logged in as a normal user.
     *
     * @param workspace The workspace to login to. If null, the user is logged in as an admin.
     * @param username  The username of the user to be authenticated.
     * @param password  The password of the user to be authenticated.
     * @return A JWT token if the user is authenticated.
     * @throws AuthenticationFailedException If the user is not authenticated.
     * @auther Ahmed Nasser Gaafar
     */
    public static String login(String workspace, String username, String password) throws AuthenticationFailedException {
        if (workspace == null || workspace.isEmpty()) {
            return adminLogin(username, password);
        } else {
            return normalLogin(workspace, username, password);
        }
    }

    private static String adminLogin(String username, String password) throws AuthenticationFailedException {
        try {
            String[] admin = userManager.readAdmin(username);
            if (!PasswordUtil.verifyPassword(password, admin[PASSWORD_INDEX])) {
                throw new AuthenticationFailedException("Password is incorrect");
            }
            return JwtUtil.generateToken(username, UserRole.ADMIN, null);
        } catch (UserNotFoundException e) {
            throw new AuthenticationFailedException("Invalid username");
        }
    }

    private static String normalLogin(String workspace, String username, String password) throws AuthenticationFailedException {
        try {
            String[] user = userManager.readUser(workspace, username);
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

            return JwtUtil.generateToken(username, role, workspace);
        } catch (UserNotFoundException e) {
            throw new AuthenticationFailedException("Invalid username");
        } catch (WorkspaceNotFoundException e) {
            throw new AuthenticationFailedException("Invalid workspace");
        }
    }

}
