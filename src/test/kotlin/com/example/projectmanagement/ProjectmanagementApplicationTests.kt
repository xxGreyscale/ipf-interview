package com.example.projectmanagement

import com.example.projectmanagement.api.project.domain.Project
import com.example.projectmanagement.api.project.domain.ProjectTask
import com.example.projectmanagement.api.project.domain.enumerations.TaskStatus
import com.example.projectmanagement.api.project.domain.primitives.ProjectId
import com.example.projectmanagement.api.project.domain.primitives.TaskDescription
import com.example.projectmanagement.api.project.domain.primitives.TaskId
import com.example.projectmanagement.api.project.domain.primitives.TaskName
import com.example.projectmanagement.api.project.io.*
import com.example.projectmanagement.api.project.io.mapper.*
import com.example.projectmanagement.api.project.services.ProjectService
import com.example.projectmanagement.api.project.services.TaskService
import com.example.projectmanagement.api.user.domain.User
import com.example.projectmanagement.api.user.domain.UserInfoDetails
import com.example.projectmanagement.common.domain.enumarations.UserRole
import com.example.projectmanagement.common.domain.primitives.Email
import com.example.projectmanagement.common.domain.primitives.UserId
import com.example.projectmanagement.common.domain.primitives.UserName
import com.example.projectmanagement.common.domain.primitives.UserPassword
import org.hibernate.query.sqm.tree.SqmNode.log
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.argThat
import org.mockito.InjectMocks
import org.mockito.Mock
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@SpringBootTest
class ProjectmanagementApplicationTests {
	@InjectMocks
	private lateinit var taskApi: TaskApi

	@Mock
	private lateinit var taskService: TaskService

	@Mock
	private lateinit var authentication: Authentication

	@Mock
	private lateinit var taskApiMapperResponse: TaskApiMapperResponse

	@InjectMocks
	private lateinit var projectsApi: ProjectsApi

	@Mock
	private lateinit var projectService: ProjectService

	@Mock
	private lateinit var projectApiResponseMapper: ProjectApiResponseMapper

	@Mock
	private lateinit var listAllProjectsApiResponse: ListAllProjectsApiResponse

	@Mock
	private lateinit var listAllTasksApiResponse: ListAllTasksApiResponse

	private val passwordEncoder = BCryptPasswordEncoder()

	// Helper function to create a UserInfoDetails object
	private fun createUserInfoDetails(roles: List<UserRole>): UserInfoDetails {
		val hashedPassword = passwordEncoder.encode("password")
		val user = User.create(UserName("testuser"), Email("test@example.com"), UserPassword(hashedPassword), roles)
		return UserInfoDetails(user)
	}

	private fun createTestProject(
		title: String = "Test Project",
		description: String = "Test Description",
		managerId: UserId = UserId.generate(), // Generate a test UserId
		startDate: Instant = Instant.now(),
		endDate: Instant = Instant.now().plusSeconds(3600),
		tasks: List<ProjectTask> = listOf()
	): Project {
		return Project.create(
			title = title,
			description = description,
			managerId = managerId,
			startDate = startDate,
			endDate = endDate
		).apply {
			this.projectTasks = tasks
		}
	}

	private fun createTestProjectWithTasks(
		title: String = "Test Project",
		description: String = "Test Description",
		managerId: UserId = UserId.generate(), // Generate a test UserId
		startDate: Instant = Instant.now(),
		endDate: Instant = Instant.now().plusSeconds(3600),
		newTask: TaskCreationRequest?
	): Project {
		return Project.create(
			title = title,
			description = description,
			managerId = managerId,
			startDate = startDate,
			endDate = endDate,
		).apply {
			// Add tasks to the project
			val task1 = ProjectTask.create("Task 1", "Task 1 Description", this.projectId, UserId.generate(), TaskStatus.PENDING)
			val task2 = ProjectTask.create("Task 2", "Task 2 Description", this.projectId, UserId.generate(), TaskStatus.PENDING)
			val tasks = mutableListOf(task1, task2)
			if (newTask != null) log.info("Nothing")
			if (newTask != null) tasks.add(
				ProjectTask.create(newTask.name, newTask.description, this.projectId, UserId(newTask.assigneeId), TaskStatus.PENDING)
			)
			this.projectTasks = tasks
		}
	}

	private fun createTestUser(
		name: String = "Test User",
		email: String = "test@email.com",
		password: String = "password",
		roles: List<UserRole> = listOf(UserRole.ROLE_USER)
	): User {
		return User(
			UserId.generate(),
			UserName(name),
			Email(email),
			UserPassword(password),
			roles,
			Instant.now(),
			Instant.now()
		)
	}


	@Test
	fun `createProject - success`() {
		val request = ProjectRequest("Project Name", "Project Description", Instant.now(), Instant.now().plusSeconds(3600))
		val userDetails = createUserInfoDetails(listOf(UserRole.ROLE_ADMIN)) // Or ROLE_MANAGER
		val mockProject = createTestProject()
		val mockResponse = ProjectResponse(
			mockProject.projectId.asValue(),
			mockProject.title.asValue(),
			mockProject.description.asValue(),
			mockProject.startDate.toString(),
			mockProject.endDate.toString(),
			listOf()
		)

		`when`(authentication.principal).thenReturn(userDetails)
		`when`(projectService.createProject(request, UserId(userDetails.getUserId()))).thenReturn(mockProject)
		`when`(projectApiResponseMapper.toResponse(mockProject)).thenReturn(mockResponse)

		val response = projectsApi.createProject(request, authentication)

		assertEquals(HttpStatus.OK, response.statusCode)
		assertNotNull(response.body)
		// The Id will be different Something to fix
		assertEquals(mockResponse.title, response.body?.title)
	}

	@Test
	fun `createProject - service throws exception emphasizing bad request`() {
		val request = ProjectRequest("Project Name", "Project Description", Instant.now(), Instant.now().plusSeconds(3600))
		val userDetails = createUserInfoDetails(listOf(UserRole.ROLE_ADMIN))

		`when`(authentication.principal).thenReturn(userDetails)
		`when`(projectService.createProject(request, UserId(userDetails.getUserId()))).thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Service Error"))

		assertThrows(ResponseStatusException::class.java) {
			projectsApi.createProject(request, authentication)
		}
	}

	@Test
	fun `viewProjects - success`() {
		val firstProject = createTestProject()
		val secondProject = createTestProject(title = "another one")
		val mockProjects = listOf(firstProject, secondProject)
		val mockResponses = mockProjects.map { ProjectResponse(
			it.projectId.asValue(),
			it.title.asValue(),
			it.description.asValue(),
			it.startDate.toString(),
			it.endDate.toString(),
			listOf()
		) }
		`when`(projectService.getProjects()).thenReturn(mockProjects)
		`when`(listAllProjectsApiResponse.toResponse(mockProjects)).thenReturn(mockResponses)

		val response = projectsApi.viewProjects()

		assertEquals(HttpStatus.OK, response.statusCode)
		assertNotNull(response.body)
		assertEquals(2, response.body?.size)
	}

	@Test
	fun `viewProject - success`() {
		val mockProject = createTestProject()
		val mockResponse = ProjectResponse(
			mockProject.projectId.asValue(),
			mockProject.title.asValue(),
			mockProject.description.asValue(),
			mockProject.startDate.toString(),
			mockProject.endDate.toString(),
			listOf()
		)

		`when`(projectService.getProjectWithId(mockProject.projectId)).thenReturn(mockProject)
		`when`(projectApiResponseMapper.toResponse(mockProject)).thenReturn(mockResponse)

		val response = projectsApi.viewProject(mockProject.projectId.asValue())

		assertEquals(HttpStatus.OK, response.statusCode)
		assertNotNull(response.body)
		assertEquals("1", response.body?.id)
	}

	@Test
	fun `updateProjectWithNoTask - success`() {
		val mockProject = createTestProject()
		val projectId = mockProject.projectId.asValue()
		val request = UpdateProjectRequest("New Name", "New Description", Instant.now(), Instant.now().plusSeconds(3600))
		val mockResponse = ProjectResponse(
			projectId,
			mockProject.title.asValue(),
			mockProject.description.asValue(),
			mockProject.startDate.toString(),
			mockProject.endDate.toString(),
			listOf()
		)

		`when`(projectService.updateProject(ProjectId(projectId), request)).thenReturn(mockProject)
		`when`(projectApiResponseMapper.toResponse(mockProject)).thenReturn(mockResponse)

		val response = projectsApi.updateProject(projectId, request)

		assertEquals(HttpStatus.OK, response.statusCode)
		assertNotNull(response.body)
		assertEquals("1", response.body?.id)
		assertEquals("New Name", response.body?.title)
	}

	@Test
	fun `deleteProject - success`() {
		val mockProject = createTestProject()
		val projectId = mockProject.projectId.asValue()

		`when`(projectService.deleteProject(ProjectId(projectId))).thenReturn(true)

		val response = projectsApi.deleteProject(projectId)

		assertEquals(HttpStatus.OK, response.statusCode)
		assertNotNull(response.body)
		assertEquals(true, response.body)
	}

	@Test
	fun `createTask - success`() {
		val userId = UserId.generate()
		val request = TaskCreationRequest("Task Name", "Task Description", assigneeId = userId.asValue())
		val mockProject = createTestProjectWithTasks(newTask = request)
		val projectId = mockProject.projectId
		val mockResponse = ProjectResponse(
			mockProject.projectId.asValue(),
			mockProject.title.asValue(),
			mockProject.description.asValue(),
			mockProject.startDate.toString(),
			mockProject.endDate.toString(),
			mockProject.projectTasks.map { ProjectTaskInResponse(
				it.taskId.asValue(),
				it.name.asValue(),
				it.description.asValue(),
				it.assigneeId.asValue(),
				it.status.toString(),
				it.createdAt.toString(),
				it.updatedAt.toString()
			) }
		)
		log.info(mockProject.projectTasks)
		`when`(projectService.createTask(request, projectId)).thenReturn(mockProject)
		`when`(projectApiResponseMapper.toResponse(mockProject)).thenReturn(mockResponse)

		val response = projectsApi.createTask(projectId.asValue(), request)

		assertEquals(HttpStatus.OK, response.statusCode)
		assertNotNull(response.body)
		assertEquals(3, response.body?.tasks?.size)
	}

	@Test
	fun `viewProjectTasks - success`() {
		val mockProject = createTestProjectWithTasks(newTask = null)
		val projectId = mockProject.projectId
		val mockTasks = mockProject.projectTasks
		val mockResponses = mockProject.projectTasks.map { ProjectTaskInResponse(
			it.taskId.asValue(),
			it.name.asValue(),
			it.description.asValue(),
			it.assigneeId.asValue(),
			it.status.toString(),
			it.createdAt.toString(),
			it.updatedAt.toString()
		) }

		`when`(projectService.getAllTasksByProjectId(projectId)).thenReturn(mockTasks)
		`when`(listAllTasksApiResponse.toResponse(mockTasks)).thenReturn(mockResponses)

		val response = projectsApi.viewProjectTasks(projectId.asValue())

		assertEquals(HttpStatus.OK, response.statusCode)
		assertNotNull(response.body)
		assertEquals(2, response.body?.size)
	}


 /**
  * Task test
  * */
 // TODO: Implement tests for TaskApi
 @Test
 fun `updateTask - success admin`() {
	 val userId = UserId.generate()
	 val projectId = ProjectId.generate()
	 val request = TaskUpdateRequest("Task Name", "Task Desc", userId.asValue(), TaskStatus.IN_PROGRESS)
	 val userDetails = createUserInfoDetails(listOf(UserRole.ROLE_ADMIN))
	 val authorities = listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
	 val projectTask = ProjectTask.create("Task Name", "Task Desc", projectId, userId, TaskStatus.IN_PROGRESS)
	 val taskId = projectTask.taskId
	 val mockResponse = ProjectTaskInResponse(taskId.asValue(), "Task Name", "Task Desc", userId.asValue(), TaskStatus.IN_PROGRESS.toString(), Instant.now().toString(), Instant.now().toString())
	 val roles = authorities.map { UserRole.valueOf(it.toString()) }
	 `when`(authentication.principal).thenReturn(userDetails)
	 `when`(authentication.authorities).thenReturn(authorities)
	 `when`(taskService.updateTaskWithId(taskId, request, roles , UserId(userDetails.getUserId()))).thenReturn(projectTask)
	 `when`(taskApiMapperResponse.toResponse(projectTask)).thenReturn(mockResponse)

	 val response = taskApi.updateTask(taskId.asValue(), request, authentication)

	 assertEquals(HttpStatus.OK, response.statusCode)
	 assertNotNull(response.body)
	 assertEquals(mockResponse.id, response.body?.id)
 }

	@Test
	fun `updateTask - successful manager assign another user to task`() {
		val userId = UserId.generate()
		val projectId = ProjectId.generate()
		val request = TaskUpdateRequest("Task Name", "Task Desc", userId.asValue(), TaskStatus.IN_PROGRESS)
		val userDetails = createUserInfoDetails(listOf(UserRole.ROLE_MANAGER))
		val authorities = listOf(SimpleGrantedAuthority("ROLE_MANAGER"))
		val projectTask = ProjectTask.create("Task Name", "Task Desc", projectId, UserId.generate(), TaskStatus.IN_PROGRESS)
		val taskId = projectTask.taskId
		val roles = authorities.map { UserRole.valueOf(it.toString()) }
		val mockResponse = ProjectTaskInResponse(taskId.asValue(), "Task Name", "Task Desc", userId.asValue(), TaskStatus.IN_PROGRESS.toString(), "createdAt", "updatedAt")

		`when`(authentication.principal).thenReturn(userDetails)
		`when`(authentication.authorities).thenReturn(authorities)
		`when`(taskService.updateTaskWithId(taskId, request, roles, userId)).thenReturn(projectTask)
		`when`(taskApiMapperResponse.toResponse(projectTask)).thenReturn(mockResponse)
		val response = taskApi.updateTask(taskId.asValue(), request, authentication)

		assertEquals(HttpStatus.OK, response.statusCode)
		assertNotNull(response.body)
		assertEquals(mockResponse.id, response.body?.id)
	}

	@Test
	fun `updateTask - forbidden user tries to change assignee`() {
		val taskId = TaskId.generate()
		val request = TaskUpdateRequest("Task Name", "Task Desc", "2", TaskStatus.IN_PROGRESS)
		val userDetails = createUserInfoDetails(listOf(UserRole.ROLE_USER))
		val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))

		`when`(authentication.principal).thenReturn(userDetails)
		`when`(authentication.authorities).thenReturn(authorities)

		assertThrows(HttpClientErrorException::class.java) {
			taskApi.updateTask(taskId.asValue(), request, authentication)
		}
	}

	@Test
	fun `updateTask - service throws exception`() {
		val taskId = TaskId.generate()
		val userId = UserId.generate()
		val request = TaskUpdateRequest("Task Name", "Task Desc", userId.toString(), TaskStatus.IN_PROGRESS)
		val userDetails = createUserInfoDetails(listOf(UserRole.ROLE_ADMIN))
		val authorities = listOf(SimpleGrantedAuthority("ROLE_ADMIN"))

		`when`(authentication.principal).thenReturn(userDetails)
		`when`(authentication.authorities).thenReturn(authorities)
		`when`(taskService.updateTaskWithId(eq(taskId), eq(request), anyList(), eq(userId))).thenThrow(HttpClientErrorException(HttpStatus.BAD_REQUEST, "Service Error"))

		assertThrows(ResponseStatusException::class.java) {
			taskApi.updateTask(taskId.asValue(), request, authentication)
		}
	}

	@Test
	fun `updateStatus - success`() {
		val taskId = TaskId.generate()
		val userId = UserId.generate()
		val projectId = ProjectId.generate()
		val request = TaskStatusUpdateRequest(TaskStatus.COMPLETED)
		val userDetails = createUserInfoDetails(listOf(UserRole.ROLE_USER))
		val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
		val projectTask = ProjectTask.create("Task Name", "Task Desc", projectId, userId, TaskStatus.IN_PROGRESS)
		val mockResponse = ProjectTaskInResponse(taskId.asValue(), "Task Name", "Task Desc", userId.asValue(), TaskStatus.COMPLETED.toString(), "createdAt", "updatedAt")

		`when`(authentication.principal).thenReturn(userDetails)
		`when`(authentication.authorities).thenReturn(authorities)
		`when`(taskService.updateTaskWithId(eq(taskId), argThat { it.status == request.status }, anyList(), eq(userId))).thenReturn(projectTask) // Use argThat for status
		`when`(taskApiMapperResponse.toResponse(projectTask)).thenReturn(mockResponse)

		val response = taskApi.updateStatus(taskId.asValue(), request, authentication)

		assertEquals(HttpStatus.OK, response.statusCode)
		assertNotNull(response.body)
		assertEquals(mockResponse.status, response.body?.status)
	}
}
