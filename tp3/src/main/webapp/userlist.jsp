<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ page contentType="text/html;charset=UTF-8" %>

        <!DOCTYPE html>
        <html lang="fr">

        <head>
            <meta charset="UTF-8">
            <title>TODOs</title>
            <link rel="stylesheet" href="css/style.css">
        </head>

        <body>
            <h2>Liste des utilisateurs connectés</h2>
            <jsp:useBean id="utilisateurs" scope="request"
                type="java.util.Collection<fr.univlyon1.m1if.m1if03.classes.User>" />
            <jsp:useBean id="nbusers" scope="request" type="java.lang.Integer" />
            <p>Il y a actuellement ${nbusers} utilisateur(s) connect&eacute;(s) :</p>
            <ul>
                <c:forEach items="${utilisateurs}" var="u">
                    <li>${u.login} : <strong><a href="user.jsp?user=${u.login}">${u.name}</a></strong></li>
                </c:forEach>
            </ul>
        </body>

        </html>