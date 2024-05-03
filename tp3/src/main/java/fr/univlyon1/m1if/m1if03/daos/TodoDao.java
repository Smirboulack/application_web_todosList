package fr.univlyon1.m1if.m1if03.daos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.univlyon1.m1if.m1if03.classes.Todo;

/**
 * Cette classe est un DAO pour les todos.
 * @author Benziane Abdeldjallil,Temirboulatov Koureich
 */
public class TodoDao extends AbstractListDao<Todo> {

    public int getIndex(Todo todo) {
        return collection.indexOf(todo);
    }

    public List<Todo> findTodosByUser(String login) {
        return collection.stream()
                .filter(todo -> todo.getassigneeUser() != null && todo.getassigneeUser().equals(login))
                .collect(Collectors.toList());
    }

    public List<Todo> findAll() {
        List<Todo> result = new ArrayList<>();
        for (Todo todo : collection) {
            if (todo != null) {
                result.add(todo);
            }
        }
        return result;
    }

}
