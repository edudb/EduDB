/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.authentication;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import net.edudb.engine.Config;
import net.edudb.exceptions.InvalidRoleException;
import net.edudb.exceptions.UserAlreadyExistException;
import net.edudb.exceptions.UserNotFoundException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class UserManager {
    private static final UserManager instance = new UserManager();
    private static final String USERS_FILE = Config.usersPath();
    private static final String DEFAULT_ADMIN_USERNAME = System.getProperty("DEFAULT_ADMIN_USERNAME");
    private static final String DEFAULT_ADMIN_PASSWORD = PasswordUtil.hashPassword(System.getProperty("DEFAULT_ADMIN_PASSWORD"));
    private static final String DEFAULT_ADMIN_ROLE = "admin";

    private UserManager() {
    }

    static {
        instance.createUsersFile();
        instance.createDefaultAdminUser();
    }

    public static UserManager getInstance() {
        return instance;
    }

    /**
     * Creates the users file if it doesn't exist.
     */
    private void createUsersFile() {
        File users = new File(USERS_FILE);
        users.getParentFile().mkdirs();
        if (!users.exists()) {
            try {
                users.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates a default admin user if no users exist.
     * This method is called when the system starts.
     *
     * @author Ahmed Nasser Gaafar
     */
    private void createDefaultAdminUser() {
        try {
            writeUser(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD, DEFAULT_ADMIN_ROLE);
        } catch (UserAlreadyExistException e) {
            // Do nothing
        } catch (InvalidRoleException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Writes a user to disk. (Appends the user to the end of the users file)
     *
     * @param username       the username of the user to write
     * @param hashedPassword the hashed password of the user to write
     * @param role           the role of the user to write
     *                       (must be one of the values in {@link UserRole})
     * @throws UserAlreadyExistException if a user with the same username already exists
     * @throws InvalidRoleException      if the role is invalid
     * @author Ahmed Nasser Gaafar
     */
    public void writeUser(String username, String hashedPassword, String role) throws UserAlreadyExistException, InvalidRoleException {

        if (isUserExists(username)) {
            throw new UserAlreadyExistException(String.format("User %s already exists", username));
        }

        UserRole.fromString(role); // throws InvalidRoleException if role is invalid

        try (CSVWriter writer = new CSVWriter(new FileWriter(USERS_FILE, true))) {
            String[] data = {username, hashedPassword, role};
            writer.writeNext(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads a user from disk.
     *
     * @param username the username of the user to read
     * @return the user as an array of strings (username, hashed password, role)
     * @throws UserNotFoundException if the user does not exist
     * @author Ahmed Nasser Gaafar
     */
    public String[] readUser(String username) throws UserNotFoundException {
        List<String[]> users = readAllUsers();
        for (String[] user : users) {
            if (user[0].equals(username)) {
                return user;
            }
        }
        throw new UserNotFoundException(String.format("User %s not found", username));
    }

    /**
     * Reads all users from disk.
     *
     * @return a list of users, each user is represented as an array of strings (username, hashed password, role)
     * @author Ahmed Nasser Gaafar
     */
    List<String[]> readAllUsers() {
        try (CSVReader reader = new CSVReader(new FileReader(USERS_FILE))) {
            return reader.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a user exists in the users file.
     *
     * @param username the username to check
     * @return true if the user exists, false otherwise
     * @author Ahmed Nasser Gaafar
     */
    public boolean isUserExists(String username) {
        try {
            readUser(username);
            return true;
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    /**
     * Removes a user from the users file.
     *
     * @param username the username of the user to remove
     * @throws UserNotFoundException if the user does not exist
     * @author Ahmed Nasser Gaafar
     */
    public void removeUser(String username) throws UserNotFoundException {
        List<String[]> users = readAllUsers();
        List<String[]> filteredUsers = users.stream().filter(user -> !user[0].equals(username)).toList();
        if (filteredUsers.size() == users.size()) {
            throw new UserNotFoundException(String.format("User %s not found", username));
        }
        overwriteUsers(filteredUsers);
    }

    /**
     * Writes a list of users to disk. (Overwrites the existing users file)
     *
     * @param users a list of users, each user is represented as an array of strings (username, hashed password, role)
     * @author Ahmed Nasser Gaafar
     */
    private void overwriteUsers(List<String[]> users) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(USERS_FILE))) {
            writer.writeAll(users);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
