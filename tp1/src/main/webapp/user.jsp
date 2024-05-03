<%@ page import="java.util.Map" %>
    <%@ page contentType="text/html;charset=UTF-8" %>

        <!DOCTYPE html>


        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <title>Modifier le nom d'utilisateur</title>
            <link rel="stylesheet" href="css/style.css">
        </head>

        <body>
            <jsp:useBean id="user" beanName="user" scope="session" type="fr.univlyon1.m1if.m1if03.classes.User" />
            <header>
                <h1 class="header-titre">Profil</h1>
                <p class="header-user">Bonjour <strong>
                        <%= user.getName() %>
                    </strong>,</p>
            </header>

            <article class="contenu">
                <h2>Modifier le nom d'utilisateur :</h2>
                <form method="post" action="todos" target="list">
                    <p>
                        <label>
                            Login : <input type="text" name="login" value="<%= user.getLogin() %>" readonly>
                        </label>
                        <label>
                            Nouveau nom : <input type="text" name="name" value="<%= user.getName() %>">
                        </label>
                        <input type="submit" value="Enregistrer">
                    </p>
                </form>
            </article>


            <footer>
                <div>Licence : <a rel="license" href="https://creativecommons.org/licenses/by-nc-sa/3.0/fr/"><img
                            alt="Licence Creative Commons" style="border-width:0; vertical-align:middle;"
                            src="https://i.creativecommons.org/l/by-nc-sa/3.0/fr/88x31.png" /></a></div>
            </footer>
        </body>

        </html>