/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.engine.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.edudb.exception.InvalidRoleException;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "mysecretkey";

    /**
     * Generates a JWT token.
     *
     * @param username The username of the user.
     * @param userRole The role of the user.
     * @return A JWT token.
     * @auther Ahmed Nasser Gaafar
     */
    public static String generateToken(String username, UserRole userRole) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", userRole)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * Validates a JWT token.
     *
     * @param token The token to be validated.
     * @return True if the token is valid, false otherwise.
     * @auther Ahmed Nasser Gaafar
     */
    public static boolean isValidToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parses a JWT token and returns the username of the user.
     *
     * @param token The token to be parsed.
     * @return The username of the user.
     * @auther Ahmed Nasser Gaafar
     */
    public static String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Parses a JWT token and returns the role of the user.
     *
     * @param token The token to be parsed.
     * @return The role of the user.
     * @auther Ahmed Nasser Gaafar
     */
    public static UserRole getUserRole(String token) {
        try {
            return UserRole.fromString((String) Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role"));
        } catch (InvalidRoleException e) {
            e.printStackTrace();
            return null;
        }
    }
}
