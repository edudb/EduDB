/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.authentication;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private static final int WORKLOAD = 12;

    /**
     * Hashes a password using BCrypt.
     *
     * @param password The password to be hashed.
     * @return The hashed password.
     * @auther Ahmed Nasser Gaafar
     */
    public static String hashPassword(String password) {
        String salt = BCrypt.gensalt(WORKLOAD);
        String hashedPassword = BCrypt.hashpw(password, salt);

        return hashedPassword;
    }

    /**
     * Verifies a password using a hash.
     *
     * @param password
     * @param hashedPassword
     * @return True if the password is correct, false otherwise.
     * @auther Ahmed Nasser Gaafar
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
