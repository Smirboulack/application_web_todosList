package fr.univlyon1.m1if.m1if03.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Filtre pour vérifier que l'utilisateur connecté est bien celui qui est
 * demandé.
 * @author Benziane Abdeldjallil,Temirboulatov Koureich
 */
@WebFilter(filterName = "AuthorizationFilter", urlPatterns = { "/userlist.jsp" })
public class AuthorizationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (httpRequest.getMethod().equals("POST")) {
            String login = httpRequest.getParameter("login");
            HttpSession session = httpRequest.getSession(false);
            if (session == null || !login.equals(session.getAttribute("login"))) {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Vous n'avez pas le droit de modifier l'utilisateur " + login);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
