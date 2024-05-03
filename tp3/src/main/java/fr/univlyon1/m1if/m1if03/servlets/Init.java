package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.classes.Todo;
import fr.univlyon1.m1if.m1if03.classes.User;
import fr.univlyon1.m1if.m1if03.daos.Dao;
import fr.univlyon1.m1if.m1if03.daos.TodoDao;
import fr.univlyon1.m1if.m1if03.daos.UserDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

/**
 * Cette servlet uniquement chargée de l'initialisation des DAOs.
 *
 * @author Benziane Abdeldjallil,Temirboulatov Koureich
 */
@WebServlet(name = "Init", urlPatterns = { "/init" }, loadOnStartup = 1)
public class Init extends HttpServlet {

    private final Dao<User> users = new UserDao();
    private final Dao<Todo> todos = new TodoDao();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        context.setAttribute("users", users);
        context.setAttribute("todos", todos);
    }
}
