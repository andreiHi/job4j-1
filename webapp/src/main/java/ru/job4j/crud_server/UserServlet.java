package ru.job4j.crud_server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * junior.
 *
 * @author Igor Kovalkov
 * @version 0.1
 * @since 28.12.2017
 */
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        PrintWriter print = new PrintWriter(resp.getOutputStream());
        String login = req.getParameter("login");
        print.append(UserStoreDB.getUserStore().getUser(login).toString());
        print.flush();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (UserStoreDB.getUserStore().addUser(req.getParameter("login"),
                req.getParameter("name"), req.getParameter("email"))) {
            resp.sendError(201);
        } else {
            resp.sendError(501);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserStoreDB.getUserStore().deleteUser(req.getParameter("login"));
        resp.sendError(200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserStoreDB.getUserStore().updateUser(req.getParameter("login"), req.getParameter("name"), req.getParameter("email"));
        resp.sendError(200);
    }
}
