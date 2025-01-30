# Project Management System
<hr/>

Spring Boot-based Project Management System to manage users, projects, and tasks. The system should include authentication, role-based access control, and RESTful APIs for managing resources.

### How to run the project
1. Clone the repository
2. Open the project in your favorite IDE, recommending IntelliJ IDEA
3. Make sure your Docker engine is running
4. Run the project (Spring boot application will start and 2 docker file containers will be created - REDIS and PostgreSQL server)
5. Open the browser and navigate to `http://localhost:8080/swagger-ui.html`
6. You can test the APIs from the Swagger UI
7. Or test by importing the Postman collection from `postman` folder

## Features
<hr/>

### Authentication
- User registration `POST /v1/api/auth/register`
- User login `POST /v1/api/auth/login`
- User logout
### ProjectAPI
- Create a project `POST /v1/api/projects`
- Update a project `PUT /v1/api/projects/{projectId}`
- Delete a project `DELETE /v1/api/projects/{projectId}`
- Get a project `GET /v1/api/projects/{projectId}`
- Get all projects `GET /v1/api/projects`
- Get all tasks from project `GET /v1/api/projects/{projectId}/tasks`
### TaskAPI
- Create a task `POST /v1/api/{projectId}/tasks`
- Update a task `PUT /v1/api/tasks/{taskId}`
- Get a task `GET /v1/api/tasks/{taskId}`
- Update a task `PUT /v1/api/tasks/{taskId}`


## Project Structure
<hr />

Follows spring boot best practices and project structure is as follows:
- `controller` - Rest controllers
- `service` - Service layer
- `repository` - Repository layer
- `model` - Entity classes
- `config` - Configuration classes

Also, more domain oriented and build for scale where each API is treated as a separate module.
<br />    

api/ <br/>
|__ Authentication <br />
|__ User <br />
|__ Project <br />
|__ Communication (incomplete)


## Technologies
<hr/>

- Java 21
- Spring Boot 3.x.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- Redis
- Docker
- Swagger