<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Edit Meal</title>
</head>
<body>
<h3><a href="${pageContext.request.getServletContext().getContextPath()}${'/index.html'}">Home</a></h3>
<h2>Edit Meal</h2>
<form method="post" action="" enctype="application/x-www-form-urlencoded">
    <input name="id" type="hidden" value="${meal.id}">
    <dl>
        <dt>Время</dt>
        <input name="dateTimeString" type="datetime-local" value="${meal.dateTime}">
    </dl>
    <dl>
        <dt>Описание</dt>
        <textarea name="description" cols="30" rows="5">${meal.description}</textarea>
    </dl>
    <dl>
        <dt>Калории</dt>
        <input name="calories" type="number" value="${meal.calories}">
    </dl>
    <button type="submit">Сохранить</button>
    <button onabort="window.history.back()">Назад</button>
</form>
</body>
</html>
