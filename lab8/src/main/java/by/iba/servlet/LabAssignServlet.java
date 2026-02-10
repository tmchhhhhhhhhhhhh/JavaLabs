package by.iba.servlet;

import by.iba.model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/LabAssignServlet")
public class LabAssignServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String lab = request.getParameter("lab");
        ListService.assignLab(lab);

        response.sendRedirect(request.getContextPath()+"/GroupListServlet");
    }
}
