package fr.univlyon1.m1if.m1if03.classes;

import java.util.Date;
import java.util.Objects;

/**
 * TODOs créés par les utilisateurs.
 * Un TODO_ comporte un titre et un statut (à faire ou fait).
 * Pour le créer, il faut indiquer l'id de l'utilisateur qui l'a créé.
 */
public class Todo {
    private final int hash;
    private String title;
    private boolean completed;

    /**
     * Création d'un TODO_
     * @param title Texte indiqué dans le TODO_
     * @param creator Login de l'utilisateur créateur
     */
    public Todo(String title, String creator) {
        this.title = title;
        this.completed = false;
        // On rassemble toutes les infos sur l'instance, pour permettre de les distinguer
        this.hash = Objects.hash(title, creator, (new Date()).getTime());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return this.hashCode() == todo.hashCode();
    }

    @Override
    public int hashCode() {
        return this.hash;
    }
}