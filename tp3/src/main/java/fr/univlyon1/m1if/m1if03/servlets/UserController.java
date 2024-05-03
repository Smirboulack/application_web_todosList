package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.classes.User;
import fr.univlyon1.m1if.m1if03.daos.UserDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.naming.NameNotFoundException;

import java.io.IOException;

/**
 * Cette servlet gère l'utilisateur, notamment son affichage et sa modification.
 * @author Benziane Abdeldjallil,Temirboulatov Koureich
 */
@WebServlet(name = "UserController", value = { "/User" })
public class UserController extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserDao users = (UserDao) getServletContext().getAttribute("users");
        String login = request.getParameter("user");

        try {
            if (login != null && !login.isEmpty()) {
                User user = users.findByLogin(login);
                request.setAttribute("user", user);
                request.getRequestDispatcher("/user.jsp").forward(request, response);
            } else {
                response.sendRedirect("UserList");
            }
        } catch (NameNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur non trouvé");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserDao users = (UserDao) getServletContext().getAttribute("users");
        String login = request.getParameter("login");
        String name = request.getParameter("name");

        try {
            if (login != null && !login.isEmpty() && name != null) {
                User user = users.findByLogin(login);
                if (user != null) {
                    user.setName(name);
                }
            }
        } catch (NameNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utilisateur non trouvé");
            return;
        }

        response.sendRedirect("UserList");
    }
}
