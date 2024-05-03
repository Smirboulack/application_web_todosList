package fr.univlyon1.m1if.m1if03.servlets;

import fr.univlyon1.m1if.m1if03.classes.Todo;
import fr.univlyon1.m1if.m1if03.daos.TodoDao;
import fr.univlyon1.m1if.m1if03.exceptions.MissingParameterException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Cette servlet gère la liste des TODOs.
 * Elle permet actuellement d'afficher la liste et de créer de nouveaux TODOs.
 * Elle devra aussi permettre de modifier l'état d'un TODO_.
 *
 * @author Lionel Médini,Benziane Abdeldjallil,Temirboulatov Koureich
 */
@WebServlet(name = "TodoList", value = "/todolist")
public class TodoList extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TodoDao todoService = (TodoDao) this.getServletContext().getAttribute("todos");
        List<Todo> todos = todoService.findAll();

        // Mettre la liste des todos dans un attribut de requête
        request.setAttribute("todos", todos);

        // Rediriger vers le JSP pour affichage
        RequestDispatcher dispatcher = request.getRequestDispatcher("/todolist.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            TodoDao todos = (TodoDao) this.getServletContext().getAttribute("todos");
            switch (request.getParameter("operation")) {
                case "add" -> {
                    if (request.getParameter("title") == null || request.getParameter("login") == null) {
                        throw new MissingParameterException("Paramètres du Todo insuffisamment spécifiés.");
                    }
                    Todo newTodo = new Todo(request.getParameter("title"), request.getParameter("login"));
                    todos.add(newTodo);
                }
                case "update" -> {
                    int index = Integer.parseInt(request.getParameter("index"));
                    Todo todo = todos.findOne(index);
                    if (todo == null) {
                        throw new StringIndexOutOfBoundsException("Pas de todo avec l'index " + index + ".");
                    }
                    if (request.getParameter("toggle") != null && !request.getParameter("toggle").isEmpty()) {
                        todo.setCompleted(Objects.equals(request.getParameter("toggle"), "Done!"));
                    } else {
                        if (request.getParameter("assign") != null && !request.getParameter("assign").isEmpty()) {
                            String login = (String) request.getSession().getAttribute("login");
                            todo.setassigneeUser(login); // Set assigneeId instead of User object
                        } else {
                            throw new MissingParameterException("Modification à réaliser non spécifiée.");
                        }
                    }
                    todos.update(index, todo);
                }
                default -> throw new UnsupportedOperationException("Opération à réaliser non prise en charge.");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de l'index du Todo incorrect.");
            return;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        doGet(request, response);
    }
}
