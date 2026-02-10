<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<h2>Welcome ${sessionScope.name}</h2>

<table border="1">
<tr><th>Name</th><th>Phone</th><th>Email</th></tr>
<c:forEach items="${group}" var="p">
<tr>
<td>${p.name}</td>
<td>${p.phone}</td>
<td>${p.email}</td>
</tr>
</c:forEach>
</table>

<form method="POST" action="GroupListServlet">
Name:<input name="nname">
Phone:<input name="nphone">
Email:<input name="nemail">
<input type="submit">
</form>

<form method="POST" action="LabAssignServlet">
Lab: <input name="lab">
<input type="submit" value="Assign to all">
</form>
