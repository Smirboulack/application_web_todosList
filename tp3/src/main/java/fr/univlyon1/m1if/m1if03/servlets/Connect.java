package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.classes.User;
import fr.univlyon1.m1if.m1if03.daos.Dao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.naming.InvalidNameException;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import java.io.IOException;

/**
 * Cette servlet initialise les objets communs à toute l'application,
 * récupère les infos de l'utilisateur pour les placer dans sa session
 * et affiche l'interface du chat.
 *
 * @author Lionel Médini,Benziane Abdeldjallil,Temirboulatov Koureich
 */
@WebServlet(name = "Connect", urlPatterns = { "/connect" })
public class Connect extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String login = request.getParameter("login");
        String name = request.getParameter("name");

        if (login == null || login.isEmpty() || name == null || name.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Les paramètres de connexion sont incomplets.");
            return;
        }

        Dao<User> users = (Dao<User>) this.getServletContext().getAttribute("users");
        User user = new User(login, name);
        try {
            users.add(user);
            HttpSession session = request.getSession(true);
            session.setAttribute("login", user.getLogin());
            request.getRequestDispatcher("interface.jsp").forward(request, response);
        } catch (NameAlreadyBoundException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Un utilisateur avec le login " + user.getLogin() + " existe déjà.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String login = (String) session.getAttribute("login");
            Dao<User> users = (Dao<User>) this.getServletContext().getAttribute("users");
            try {
                users.deleteById(login);
                session.invalidate();
                response.sendRedirect("index.html");
            } catch (NameNotFoundException | InvalidNameException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Le login de l'utilisateur courant est erroné : " + login + ".");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Aucune session active.");
        }
    }
}
