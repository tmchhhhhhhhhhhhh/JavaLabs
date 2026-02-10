package by.iba.servlet;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name="LoginServlet", urlPatterns="/LoginServlet")
public class LoginServlet extends HttpServlet {

    public boolean validateUser(String user, String password) {
        return user.equalsIgnoreCase("admin") && password.equals("admin");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String password = request.getParameter("password");

        if (validateUser(name, password)) {
            request.getSession().setAttribute("name", name);
            response.sendRedirect(request.getContextPath()+"/GroupListServlet");
        } else {
            request.setAttribute("errorMessage","Invalid login or password");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp")
                    .forward(request,response);
        }
    }
}
