package fr.univlyon1.m1if.m1if03.controllers;

import fr.univlyon1.m1if.m1if03.dao.TodoDao;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoDtoMapper;
import fr.univlyon1.m1if.m1if03.dto.todo.TodoResponseDto;
import fr.univlyon1.m1if.m1if03.model.Todo;
import fr.univlyon1.m1if.m1if03.utils.UrlUtils;
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
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contrôleur de ressources "todos".<br>
 * Gère les CU liés aux opérations CRUD sur la collection de todos :
 * <ul>
 *     <li>Création / modification / suppression d'un todos : POST, PUT, DELETE</li>
 *     <li>Récupération de la liste de todos / d'un todo / d'une propriété d'un todo : GET</li>
 * </ul>
 * Cette servlet fait appel à une <i>nested class</i> <code>TodoResource</code> qui se charge des appels au DAO.
 * Les opérations métier spécifiques sont réalisées par la servlet <a href="TodoBusinessController.html"><code>TodoBusinessController</code></a>.
 *
 */
@WebServlet(name = "TodoResourceController", urlPatterns = {"/todos", "/todos/*"})
public class TodoResourceController extends HttpServlet {
    private TodoDtoMapper todoMapper;
    private TodoResource todoResource;

    //<editor-fold desc="Méthode de gestion du cycle de vie">
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        TodoDao todoDao = (TodoDao) config.getServletContext().getAttribute("todoDao");
        todoMapper = new TodoDtoMapper(config.getServletContext());
        todoResource = new TodoResource(todoDao);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] url = UrlUtils.getUrlParts(request);
        if (url.length == 1) {
                String title = request.getParameter("title");
                String creator = request.getParameter("creator");
                if (title != null && creator != null) {
                    try {
                        Todo todo = todoResource.create(title, creator);
                        response.setStatus(HttpServletResponse.SC_CREATED);
                        response.setHeader("Location", "todos/" + todo.hashCode());
                    } catch (IllegalArgumentException e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "parametres de la requete non acceptable.");
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
                }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String[] url = UrlUtils.getUrlParts(request);
        if (url.length == 1) {
            request.setAttribute("todos", todoResource.readAll());
            response.setStatus(HttpServletResponse.SC_OK);
            request.setAttribute("view", "todos");
            return;
        }
        try {
            Todo todo = todoResource.read(url[1]);
            TodoResponseDto todoDTO = todoMapper.toDto(todo);
            switch (url.length) {
                case 2 -> {
                    request.setAttribute("model", ((boolean) request.getAttribute("authorizedUser")) ?
                            todoDTO : new TodoResponseDto(todoDTO.getTitle(), todoDTO.getHash(), todoDTO.getAssignee(), todoDTO.getCompleted()));
                    request.setAttribute("view", "todo");
                }
                case 3 -> {
                    switch (url[2]) {
                        case "title"->{
                            request.setAttribute("model", new TodoResponseDto(todoDTO.getTitle(), null, null, null));
                            request.setAttribute("view", "todoProperty");
                        }
                        case "assignee" -> {
                            request.setAttribute("model", new TodoResponseDto(todoDTO.getTitle(), null, todoDTO.getAssignee(), null));
                            request.setAttribute("view", "todoProperty");
                        }
                        case "status" -> {
                            request.setAttribute("model", new TodoResponseDto(null, null, null, todoDTO.getCompleted()));
                            request.setAttribute("view", "todoProperty");
                        }
                        default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Propriété demandée erronée.");
                    }
                }
                default -> {
                    if (url[2].equals("assignee")) {
                        // Vérifier si l'URL a plus de 3 segments pour la redirection vers la sous-ressource
                            String urlEnd = UrlUtils.getUrlEnd(request, 3);
                            response.sendRedirect(request.getContextPath() + "/users/" + urlEnd);
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Trop de paramètres dans l'URI.");
                    }
                }
            }
        } catch (IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        } catch (NameNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo " + url[1] + " n'existe pas.");
        } catch (InvalidNameException ignored) {
            // Ne devrait pas arriver car les paramètres sont déjà des Strings
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] url = UrlUtils.getUrlParts(request);
        Integer hash = Integer.parseInt(url[1]);
        String title = request.getParameter("title");
        String creator = request.getParameter("creator");
        //TodoRequestDto requestDto = (TodoRequestDto) request.getAttribute("dto");
        if (url.length == 2) {
            try {
                todoResource.update(hash, title, creator);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (IllegalArgumentException ex) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            } catch (NameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo " + url[1] + " n'existe pas.");
            } catch (InvalidNameException ignored) {
                // Ne devrait pas arriver car les paramètres sont déjà des Strings
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] url = UrlUtils.getUrlParts(request);
        if (url.length == 2) {
            try {
                todoResource.delete(url[1]);
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres de la requête non acceptables (id syntaxiquement incorrect)");
            } catch (InvalidNameException | NameNotFoundException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Le todo " + url[1] + " n'existe pas.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
        }
    }
    //</editor-fold>

    /**
     * Classe interne pour la gestion des ressources todos.
     */
    private static class TodoResource {
        private final TodoDao todoDao;

        TodoResource(TodoDao todoDao) {
            this.todoDao = todoDao;
        }

        public Todo create(@NotNull String title, @NotNull String creator) throws IllegalArgumentException {
            Todo todo = new Todo(title, creator);
            todoDao.add(todo);
            return todo;
        }

        public Collection<Todo> readAll() {
            return todoDao.findAll();
        }

        public Todo read(@NotNull String hash) throws InvalidNameException, NameNotFoundException, NoSuchElementException {
            return todoDao.findByHash(Integer.parseInt(hash));
        }

        public void delete(@NotNull String hash) throws InvalidNameException, NameNotFoundException {
            todoDao.deleteById(todoDao.getId(todoDao.findByHash(Integer.parseInt(hash))));
        }

        public void update(@NotNull Integer hash, String title, String assignee) throws InvalidNameException, NameNotFoundException {
            Todo todo = todoDao.findByHash(hash);
            if (todo == null) {
                throw new IllegalArgumentException("Le todo n'a pas été trouvé");
            } else {
                if (assignee != null) {
                    todo.setAssignee(assignee);
                }
                if (title != null) {
                    todo.setTitle(title);
                }
                todoDao.update(todoDao.getId(todo), todo);
            }
        }

    }
}
