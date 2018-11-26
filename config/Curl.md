Admin:
1. Get all: curl http://localhost:8080/topjava/rest/admin/users
2. Get one: curl http://localhost:8080/topjava/rest/admin/users/100000
3. Post new user: curl -H "Content-Type: application/json; charset=utf-8" -X POST -d '{"name": "New2", "email": "new2@yandex.ru", "password": "passwordNew", "roles": ["ROLE_USER"]}' http://localhost:8080/topjava/rest/admin/users
4. Delete: curl -X DELETE http://localhost:8080/topjava/rest/admin/users/100000
5. Put: curl -H "Content-Type: application/json; charset=utf-8" -X PUT -d '{"name": "UserUpdatedByAdmin", "email": "user@yandex.ru", "password": "passwordNew", "roles": ["ROLE_USER"]}' http://localhost:8080/topjava/rest/admin/users/100000
6. Get by email: curl http://localhost:8080/topjava/rest/admin/users/by?email=user@yandex.ru

User:
1. Get: curl http://localhost:8080/topjava/rest/profile
2. Delete: curl -X DELETE http://localhost:8080/topjava/rest/profile
3. Put: curl -H "Content-Type: application/json; charset=utf-8" -X PUT -d '{"name": "UserUpdated", "email": "user@yandex.ru", "password": "passwordNew", "roles": ["ROLE_USER"]}' http://localhost:8080/topjava/rest/profile

Meal:
1. Get all: curl http://localhost:8080/topjava/rest/meals
2. Get between dates: curl http://localhost:8080/topjava/rest/meals?startDate=2015-05-31
3. Get between dates and time: curl http://localhost:8080/topjava/rest/meals?startDate=2015-05-31&endTime=14:20
4. Post new meal: curl -H "Content-Type: application/json; charset=utf-8" -X POST -d '{"dateTime": "2018-11-01T18:27:00", "description": "Созданный ужин", "calories": 980}' http://localhost:8080/topjava/rest/meals

Meal 100002:
1. Get: curl http://localhost:8080/topjava/rest/meals/100002
2. Put: curl -H "Content-Type: application/json; charset=utf-8" -X PUT -d '{"id":100002,"dateTime":"2015-05-30T10:00:00","description":"Обновленный завтрак","calories":500}' http://localhost:8080/topjava/rest/meals/100002
3. Delete: curl -X DELETE http://localhost:8080/topjava/rest/meals/100002
