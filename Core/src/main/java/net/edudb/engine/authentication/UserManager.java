/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.engine.authentication;

import net.edudb.engine.Config;
import net.edudb.engine.FileManager;
import net.edudb.engine.Utility;
import net.edudb.exception.InvalidRoleException;
import net.edudb.exception.UserAlreadyExistException;
import net.edudb.exception.UserNotFoundException;
import net.edudb.exception.WorkspaceNotFoundException;

import java.io.FileNotFoundException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.List;

public class UserManager {
    private static final UserManager instance = new UserManager();
    private static FileManager fileManager = FileManager.getInstance();
    private static String DEFAULT_ADMIN_USERNAME = System.getProperty("DEFAULT_ADMIN_USERNAME");
    private static String DEFAULT_ADMIN_PASSWORD = PasswordUtil.hashPassword(System.getProperty("DEFAULT_ADMIN_PASSWORD"));

    private UserManager() {

        //TODO: fix this, this is a temporary fix
        fileManager = FileManager.getInstance();
        DEFAULT_ADMIN_USERNAME = "admin";
        DEFAULT_ADMIN_PASSWORD = PasswordUtil.hashPassword("admin");

        createAdminsFile();
        createDefaultAdminUser();
    }

    public static UserManager getInstance() {
        return instance;
    }

    /**
     * Creates the users file if it doesn't exist.
     */
    private void createAdminsFile() {
        try {
            fileManager.createFile(Config.adminsPath());
        } catch (FileAlreadyExistsException e) {
            // Do nothing
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
            createAdmin(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD);
        } catch (UserAlreadyExistException e) {
            // Do nothing
        }
    }

    /**
     * creates a new user in the specified workspace
     *
     * @param username       the username of the user to create
     * @param hashedPassword the hashed password of the user
     * @param role           the role of the user to check (must be one of the values in {@link UserRole})
     * @param workspaceName  the name of the workspace to create the user in
     * @throws UserAlreadyExistException  if a user with the same username already exists
     * @throws InvalidRoleException       if the role is invalid
     * @throws WorkspaceNotFoundException if the workspace is not found
     * @author Ahmed Nasser Gaafar
     */
    public void createUser(String username, String hashedPassword, String role, String workspaceName) throws UserAlreadyExistException, InvalidRoleException, WorkspaceNotFoundException {
        if (isUserExists(workspaceName, username)) {
            throw new UserAlreadyExistException(String.format("User %s already exists in workspace %s", username, workspaceName));
        }

        UserRole userRole = UserRole.fromString(role); // throws InvalidRoleException if role is invalid

        if (userRole == UserRole.ADMIN) {
            throw new InvalidRoleException("Admins cannot be assigned to a workspace");
        }

        if (!fileManager.isWorkspaceExists(workspaceName)) {
            throw new WorkspaceNotFoundException(String.format("Workspace %s not found", workspaceName));
        }

        Path usersFilePath = Config.usersPath(workspaceName);
        String[] data = {username, hashedPassword, role};
        fileManager.appendToCSV(usersFilePath, data);
    }

    /**
     * @param username       the username of the admin to create
     * @param hashedPassword the hashed password of the admin
     * @throws UserAlreadyExistException if an admin with the same username already exists
     * @author Ahmed Nasser Gaafar
     */
    public void createAdmin(String username, String hashedPassword) throws UserAlreadyExistException {
        if (isAdminExists(username)) {
            throw new UserAlreadyExistException(String.format("Admin %s already exists", username));
        }

        Path adminsFilePath = Config.adminsPath();
        String[] data = {username, hashedPassword};
        fileManager.appendToCSV(adminsFilePath, data);
    }

    /**
     * @param workspace the name of the workspace to read the users from
     * @param username  the username of the user to read
     * @return the user as an array of strings (username, hashed password, role)
     * @throws WorkspaceNotFoundException if the workspace is not found
     * @throws UserNotFoundException      if the user does not exist
     * @author Ahmed Nasser Gaafar
     */
    public String[] readUser(String workspace, String username) throws WorkspaceNotFoundException, UserNotFoundException {
        List<String[]> users = readAllUsers(workspace);
        for (String[] user : users) {
            if (user[0].equals(username)) {
                return user;
            }
        }
        throw new UserNotFoundException(String.format("User %s not found", username));
    }

    /**
     * @param username the username of the admin to read
     * @return the admin as an array of strings (username, hashed password)
     * @throws UserNotFoundException if the admin does not exist
     * @author Ahmed Nasser Gaafar
     */
    public String[] readAdmin(String username) throws UserNotFoundException {
        List<String[]> admins = readAllAdmins();
        for (String[] admin : admins) {
            if (admin[0].equals(username)) {
                return admin;
            }
        }
        throw new UserNotFoundException(String.format("Admin %s not found", username));
    }

    /**
     * @param workspace the name of the workspace to read the users from
     * @return a list of users, each user is represented as an array of strings (username, hashed password, role)
     * @throws WorkspaceNotFoundException if the workspace is not found
     * @author Ahmed Nasser Gaafar
     */
    public List<String[]> readAllUsers(String workspace) throws WorkspaceNotFoundException {
        Path usersFilePath = Config.usersPath(workspace);

        try {
            return fileManager.readCSV(usersFilePath);
        } catch (FileNotFoundException e) {
            throw new WorkspaceNotFoundException(String.format("Workspace %s not found", workspace));
        }
    }

    /**
     * @return a list of admins, each admin is represented as an array of strings (username, hashed password)
     * @author Ahmed Nasser Gaafar
     */
    public List<String[]> readAllAdmins() {
        Path usersFilePath = Config.adminsPath();
        try {
            return fileManager.readCSV(usersFilePath);
        } catch (FileNotFoundException e) {
            Utility.handleDatabaseFileStructureCorruption(e);
        }
        return null; // unreachable
    }


    public boolean isUserExists(String workspace, String username) throws WorkspaceNotFoundException {
        try {
            readUser(workspace, username);
            return true;
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    public boolean isAdminExists(String username) {
        try {
            readAdmin(username);
            return true;
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    public void removeUser(String workspace, String username) throws WorkspaceNotFoundException, UserNotFoundException {
        List<String[]> users = readAllUsers(workspace);
        List<String[]> filteredUsers = users.stream().filter(user -> !user[0].equals(username)).toList();
        if (filteredUsers.size() == users.size()) {
            throw new UserNotFoundException(String.format("User %s not found", username));
        }
        overwriteUsers(workspace, filteredUsers);
    }

    public void removeAdmin(String username) throws UserNotFoundException {
        List<String[]> admins = readAllAdmins();
        List<String[]> filteredAdmins = admins.stream().filter(admin -> !admin[0].equals(username)).toList();
        if (filteredAdmins.size() == admins.size()) {
            throw new UserNotFoundException(String.format("Admin %s not found", username));
        }
        overwriteAdmins(filteredAdmins);
    }

    /**
     * Writes a list of users to disk. (Overwrites the existing users file)
     *
     * @param workspace the workspace to write the users to
     * @param users     a list of users, each user is represented as an array of strings (username, hashed password, role)
     * @author Ahmed Nasser Gaafar
     */
    private void overwriteUsers(String workspace, List<String[]> users) {
        Path usersFilePath = Config.usersPath(workspace);
        fileManager.writeCSV(usersFilePath, users);

    }

    private void overwriteAdmins(List<String[]> admins) {
        Path adminsFilePath = Config.adminsPath();
        fileManager.writeCSV(adminsFilePath, admins);
    }
}
