\# Smart-Campus Service Platform



A web-based campus service request management platform developed as an individual academic project for the Master DevOps \& Cloud program at ISET Tozeur.



\## 1. Project Overview



Smart-Campus Service Platform aims to centralize campus service requests and provide a structured process for submitting, tracking, and processing requests.



The project also demonstrates the application of DevOps and DevSecOps practices throughout the software delivery lifecycle, including containerization, continuous integration, security scanning, Kubernetes deployment, GitOps, and observability.



\*\*Project status:\*\* Initial repository setup and requirements planning.



\## 2. Objectives



\- Provide a centralized interface for campus service requests.

\- Implement backend REST APIs and persistent data storage.

\- Automate build and test activities through continuous integration.

\- Package application components using Docker.

\- Scan container images for known vulnerabilities.

\- Deploy the application to a local Kubernetes cluster.

\- Demonstrate GitOps-based deployment management.

\- Monitor application and infrastructure metrics.

\- Document the architecture, setup, testing, and deployment procedures.



\## 3. Planned Technology Stack



| Component | Technology |

|---|---|

| Frontend | React |

| Backend | Java, Spring Boot |

| Database | PostgreSQL |

| API communication | REST |

| Containerization | Docker |

| Local integration | Docker Compose |

| Version control | Git, GitHub |

| Continuous integration | GitHub Actions |

| Container security | Trivy |

| Orchestration | Kubernetes, KIND |

| GitOps | Argo CD |

| Monitoring | Prometheus |

| Visualization | Grafana |

| Project management | Jira Software |



The technology stack and service boundaries may be refined during implementation to maintain a feasible six-week scope.



\## 4. Planned Architecture



The initial architecture consists of:



\- A React frontend for students and authorized staff.

\- Spring Boot backend services for authentication and service request management.

\- PostgreSQL for persistent application data.

\- Docker Compose for local development and integration.

\- GitHub Actions for automated build and verification.

\- Trivy for container image vulnerability scanning.

\- KIND for local Kubernetes deployment.

\- Argo CD for GitOps-based synchronization.

\- Prometheus and Grafana for monitoring and visualization.



The notification service is a planned extension and will be implemented according to the available time.



\## 5. Planned Repository Structure



```text

smart-campus-platform/

├── .github/

│   └── workflows/

├── backend/

├── database/

├── deployment/

├── docs/

├── frontend/

├── .gitignore

└── README.md

```



The directory structure will evolve as application code and deployment configuration are added.



\## 6. Core Functional Scope



The minimum viable application is expected to support:



\- Submitting campus service requests.

\- Viewing submitted requests and their details.

\- Tracking request status.

\- Allowing authorized staff to update request status.

\- Persisting request data in PostgreSQL.

\- Enforcing the defined access permissions.



Additional features, such as category administration, notifications, and request statistics, will depend on implementation progress.



\## 7. DevOps Workflow



The planned delivery workflow is:



1\. Manage source code with Git and GitHub.

2\. Build the frontend and backend.

3\. Run automated tests through GitHub Actions.

4\. Build container images.

5\. Scan images using Trivy.

6\. Publish images to a container registry according to the configured policy.

7\. Deploy the application to Kubernetes using KIND.

8\. Manage deployment configuration through Argo CD.

9\. Monitor the system with Prometheus and Grafana.



These steps will be implemented and validated incrementally.



\## 8. Project Management



The project is managed individually using a Jira Scrum board.



The planned implementation period is six weeks:



| Week | Focus |

|---|---|

| 1 | Planning and foundation |

| 2 | Backend microservices |

| 3 | Frontend, integration, and CI |

| 4 | Security scanning and Kubernetes |

| 5 | GitOps and monitoring |

| 6 | Testing, documentation, and final demonstration |



The backlog and sprint assignments are maintained in Jira.



\## 9. Getting Started



Application setup instructions will be added as the implementation progresses.



The initial repository contains the project structure and version-control configuration. Application execution instructions will be documented after the required services and configuration files have been implemented.



\## 10. Security Practices



\- Do not commit passwords, API tokens, private keys, or production credentials.

\- Store local environment variables in untracked `.env` files.

\- Provide example configuration files without real secrets when needed.

\- Scan container images for known vulnerabilities.

\- Document security-related configuration and verification results.



\## 11. Documentation



Project documentation will be maintained in the `docs/` directory and will include:



\- Requirements and architecture.

\- API documentation.

\- Local development setup.

\- CI workflow.

\- Containerization and deployment.

\- Security scanning.

\- Kubernetes and GitOps configuration.

\- Monitoring and testing.

\- Final evaluation and known limitations.



\## 12. Academic Context



\*\*Program:\*\* Master DevOps \& Cloud  

\*\*Institution:\*\* ISET Tozeur  

\*\*Project type:\*\* Individual academic project  

\*\*Duration:\*\* Six weeks  

\*\*Status:\*\* In progress



\## 13. License



No license has been selected yet. The repository's licensing terms will be defined if required for publication or distribution.

