package fr.univlyon1.m1if.m1if03.classes;

/**
 * Utilisateur du chat.
 * Cette classe pourra être améliorée dans la suite de l'UE.
 */
public class User {
    private final String login;
    private String name;

    public User(String login, String name) {
        this.login = login;
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
