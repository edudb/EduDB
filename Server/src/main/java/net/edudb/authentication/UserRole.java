/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.authentication;

import net.edudb.exceptions.InvalidRoleException;

public enum UserRole {
    ADMIN,
    USER;


    public static final UserRole DEFAULT_ROLE = USER;

    /**
     * Converts a string to a UserRole. If the string is null, the default role is returned.
     *
     * @param role The role to be converted to a UserRole.
     * @return The UserRole.
     * @throws InvalidRoleException If the role is invalid.
     * @auther Ahmed Nasser Gaafar
     */
    public static UserRole fromString(String role) throws InvalidRoleException {
        if (role == null) {
            return DEFAULT_ROLE;
        }

        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidRoleException("Invalid role: " + role);
        }
    }
}
