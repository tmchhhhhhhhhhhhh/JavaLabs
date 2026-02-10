package by.iba.servlet;

import by.iba.model.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/GroupListServlet")
public class GroupListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("group", ListService.retrieveList());
        request.getRequestDispatcher("/WEB-INF/views/welcome.jsp")
                .forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nname = request.getParameter("nname");
        String nphone = request.getParameter("nphone");
        String nemail = request.getParameter("nemail");

        if(nname.isEmpty()||nphone.isEmpty()||nemail.isEmpty()){
            request.setAttribute("errorMessage","Заполните все поля");
        } else {
            ListService.addPerson(new Person(nname,nphone,nemail));
        }

        request.setAttribute("group", ListService.retrieveList());
        request.getRequestDispatcher("/WEB-INF/views/welcome.jsp")
                .forward(request,response);
    }
}
