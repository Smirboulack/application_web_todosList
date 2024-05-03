package fr.univlyon1.m1if.m1if03.controllers;


import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.model.Todo;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import java.io.IOException;


/**
 *
 */
@WebServlet(name = "TodoBusinessController", urlPatterns = {"/todos/toggleStatus"})
public class TodoBusinessController extends HttpServlet {
    private TodoBusiness todoBusiness;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        TodoDao todoDao = (TodoDao) config.getServletContext().getAttribute("todoDao");
        todoBusiness = new TodoBusiness(todoDao);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getRequestURI().endsWith("toggleStatus")) {
            int hash = Integer.parseInt(request.getParameter("hash"));
            try {
                boolean newStatus = todoBusiness.todoToggleStatus(hash);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                // Vous pourriez également retourner le nouveau statut si nécessaire
                // response.getWriter().write("Status changed to: " + newStatus);
            } catch (IllegalArgumentException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (NameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            } catch (InvalidNameException ignored) {
                // Ne doit pas arriver
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private static class TodoBusiness {
        private final TodoDao todoDao;

        TodoBusiness(TodoDao todoDao) {
            this.todoDao = todoDao;
        }

        public boolean todoToggleStatus(@NotNull int hash) throws NameNotFoundException, InvalidNameException {
            Todo t = todoDao.findOne(hash);
            if (t == null) {
                throw new NameNotFoundException("Todo non trouvé avec le hash: " + hash);
            }
            t.setCompleted(!t.isCompleted());
            todoDao.update(todoDao.getId(t), t);
            return t.isCompleted();
        }

    }
}
