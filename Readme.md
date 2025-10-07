# Document Approval Workflow

A multi-step document approval system built with Spring Boot, PostgreSQL, JWT, and WebSockets. Designed for efficient document review, automated notifications, and audit tracking.

## Features

- Multi-step approval workflow with role-based access
- Automated notifications for pending approvals
- Real-time status updates using WebSockets
- REST APIs for document and task management
- Audit tracking for all actions

## Technology Stack

- Backend: Spring Boot
- Database: PostgreSQL
- Authentication: JWT
- Real-time updates: WebSockets
- Build Tool: Maven

## Installation

1. Clone the repository:
- git clone https://github.com/Nimish2098/DocApproval.git
- cd DocApproval



2. Configure PostgreSQL:
- Create a database and update `application.properties` with credentials.

3. Build and run the backend:
- mvn clean install
- mvn spring-boot:run

4. Access APIs and WebSocket endpoints as per documentation.

## Future Improvements

- Add document versioning and rollback
- Role-based dashboard with analytics
- Integration with email and Slack for notifications
- Frontend UI with React or Angular
