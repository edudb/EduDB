/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.executors;

import net.edudb.Request;
import net.edudb.Response;
import net.edudb.ResponseStatus;
import net.edudb.engine.DatabaseEngine;
import net.edudb.engine.Utility;
import net.edudb.engine.authentication.JwtUtil;
import net.edudb.engine.authentication.UserRole;
import net.edudb.exception.InvalidRoleException;
import net.edudb.exception.UserAlreadyExistException;
import net.edudb.exception.WorkspaceAlreadyExistException;
import net.edudb.exception.WorkspaceNotFoundException;

import java.util.regex.Matcher;

public class CreateUserExecutor implements ConsoleExecutorChain {
    private ConsoleExecutorChain nextElement;
    private static final String REGEX = "\\A(?i)create\\s+user\\s+(\\w+)\\s+with\\s+password\\s*=\\s*\"([^\"]*)\"" +
            "\\s*(?:as\\s+(\\w+))?\\s*(?:in\\s+workspace\\s*=\\s*\"([^\"]*)\")?\\s*;?\\z";

    @Override
    public void setNextElementInChain(ConsoleExecutorChain chainElement) {
        this.nextElement = chainElement;
    }


    @Override
    public Response execute(Request request) {
        String command = request.getCommand();
        Matcher matcher = Utility.getMatcher(command, REGEX);

        if (!matcher.matches()) {
            return nextElement.execute(request);
        }

        String username = matcher.group(1).toLowerCase();
        String password = matcher.group(2).toLowerCase();
        String workspaceName = matcher.group(4);
        UserRole role;

        try {
            role = UserRole.fromString(matcher.group(3));
        } catch (InvalidRoleException e) {
            System.err.println(e.getMessage());
            return new Response(e.getMessage(), ResponseStatus.ERROR);
        }

        if (role == UserRole.ADMIN) {
            return new Response("Admins can not be assigned to workspaces, try workspace_admin", ResponseStatus.UNAUTHORIZED);
        }

        UserRole requesterRole = JwtUtil.getUserRole(request.getAuthToken());
        String requesterWorkspace = JwtUtil.getWorkspaceName(request.getAuthToken());

        if (requesterRole == UserRole.ADMIN) {
            if (workspaceName == null) {
                return new Response("As admin you must specify the workspace", ResponseStatus.ERROR);
            }
        } else if (requesterRole == UserRole.WORKSPACE_ADMIN) {
            if (workspaceName == null) {
                workspaceName = requesterWorkspace;
            } else if (!workspaceName.equals(requesterWorkspace)) {
                return new Response("Only admins can create users in other workspaces", ResponseStatus.UNAUTHORIZED);
            }
        } else {
            return new Response("Only admins and workspace_admins can create users", ResponseStatus.UNAUTHORIZED);
        }

        try {
            DatabaseEngine engine = DatabaseEngine.getInstance();

            if (!engine.isWorkspaceExist(workspaceName)) {
                engine.createWorkspace(workspaceName);
            }

            engine.createUser(username, password, role, workspaceName);
            return new Response(String.format("User %s created successfully", username), ResponseStatus.OK);
        } catch (UserAlreadyExistException | WorkspaceNotFoundException | WorkspaceAlreadyExistException e) {
            return new Response(e.getMessage(), ResponseStatus.ERROR);
        }

    }
}
