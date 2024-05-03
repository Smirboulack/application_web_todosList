package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.classes.User;
import fr.univlyon1.m1if.m1if03.daos.UserDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;

/**
 * Cette servlet gère la liste des utilisateurs.
 * @author Benziane Abdeldjallil,Temirboulatov Koureich
 */
@WebServlet(name = "UserListController", value = {"/UserList"})
public class UserListController extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDao utilisateurs = (UserDao) getServletContext().getAttribute("users");
        Collection<User> allusers = utilisateurs.findAll();
        Integer nbusers = allusers.size();
        request.setAttribute("utilisateurs", allusers);
        request.setAttribute("nbusers", nbusers);
        request.getRequestDispatcher("/userlist.jsp").include(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    request.getRequestDispatcher("/interface.jsp").forward(request, response);

    }
}
