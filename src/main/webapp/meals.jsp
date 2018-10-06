<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="${pageContext.request.getServletContext().getContextPath()}${'/index.html'}">Home</a></h3>
<h2>Meals</h2>
<c:if test="${mealsWithExceed.size() > 0}">
<table>
    <tbody>
    <tr>
        <th>Дата</th>
        <th>Описание</th>
        <th>Калории</th>
        <th></th>
        <th></th>
    </tr>
    <c:forEach items="${mealsWithExceed}" var="mealExceed">
        <jsp:useBean id="mealExceed" type="ru.javawebinar.topjava.model.MealWithExceed"/>
        <tr style="background-color: ${mealExceed.exceed ? '#FA8072' : '#00FF7F'}">
            <td>${TimeUtil.toString(mealExceed.dateTime)}</td>
            <td>${mealExceed.description}</td>
            <td>${mealExceed.calories}</td>
            <td><a href="?action=edit&id=${mealExceed.id}">Изменить</a></td>
            <td><a href="?action=delete&id=${mealExceed.id}">Удалить</a></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<button onclick="window.location.href='?action=add'">Добавить</button>
</body>
</c:if>
</html>
