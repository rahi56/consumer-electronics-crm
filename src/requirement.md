Consumer Electronics After-Sales CRM
Project Implementation Blueprint & Development Roadmap
Project type: Modular-monolith web application
Backend: Java 21, Spring Boot 3.5.x, Spring Security, Spring Data
JPA, Hibernate
Database: MySQL
Frontend: React + TypeScript (planned; current development focus is
backend)
API style: REST/JSON
Authentication: JWT access-token authentication, BCrypt password
hashing
Status: Core authentication, branch management, branch-manager
management, and technician management have been implemented.
Branch-level authorization is the current checkpoint and must be
verified before moving on.
1. Project vision
   Build a centralized after-sales service CRM for a consumer-electronics
   company operating through multiple service branches.

The system should manage the complete service journey:
1. A customer registers and registers a purchased product.
2. The customer submits a complaint against that product.
3. The system validates warranty and product-support information.
4. The complaint is routed to the appropriate service branch.
5. A branch manager reviews the complaint and arranges an appointment
   or assigns a technician.
6. The technician diagnoses and repairs the product, recording work,
   parts, time, and safety checks.
7. The customer receives status updates and can confirm completion or
   raise a dispute.
8. The company can manage payments, refunds, service quality, policies,
   and audit history.
   The application must support multiple branches while protecting
   branch-specific data. Company administrators need company-wide
   visibility; branch managers should only operate within their assigned
   branch.
2. Goals and non-goals
   Goals
- Provide secure role-based access for customers, technicians, branch
  managers, and administrators.
- Enforce branch-level data isolation on the backend, not merely in
  the frontend.
- Track complaints from submission through resolution and closure.
- Maintain product, warranty, service-history, and support-lifecycle
  records.
- Support appointments, technician assignment, repair work, parts
  usage, and customer communication.
- Record important changes in an audit trail.
- Provide reliable validation, consistent API errors, and meaningful
  automated tests.
- Keep the first release understandable and maintainable as a modular
  monolith.
  Non-goals for the first release
- Microservices and distributed infrastructure.
- Native Android/iOS applications.
- AI-based diagnosis or predictive maintenance.
- Complex ERP/accounting integration.
- Real-time chat and advanced notification orchestration.
  These can be considered after the core service workflow is stable.
3. Users, roles, and permissions
   Role                    Main responsibilities   Data scope
   ADMIN                 Manage users, branches, All branches
   policies, products,
   service configuration,
   reports, and
   company-wide operations

BRANCH_MANAGER        Manage branch           Assigned branch only
technicians,
complaints,
appointments,
assignments, branch
inventory, and service
reviews

TECHNICIAN            View assigned jobs,     Own assignments and
diagnose, record work,  permitted branch
request/use parts,      resources
complete safety checks,
and submit repair
results                 
CUSTOMER              Manage own              Own records only
profile/products,
submit complaints, book
appointments, view
service history, pay
eligible charges, and
raise disputes
Authorization principles
- Every protected endpoint must authenticate the caller.
- Every operation must authorize the requested resource, not just the
  URL.
- Never trust a branchId, customerId, technicianId, or owner ID
  supplied by the client without checking it against the authenticated
  user.
- A branch manager's branch must be loaded from the database using the
  authenticated user's ID.
- A technician must only access jobs assigned to that technician.
- A customer must only access their own products, complaints,
  appointments, payments, and disputes.
- Reassignment of users or technicians between branches is an
  administrator-only operation unless a future policy explicitly says
  otherwise.
- Return 401 for unauthenticated requests and 403 for
  authenticated users who lack permission. Use 404 where appropriate
  to avoid revealing another user's resource.
- Enforce these rules in service-layer authorization checks and, where
  useful, Spring Security method security. Frontend route guards are
  not security controls.
4. Architecture and package organization
   Use a modular monolith: one Spring Boot application and one
   relational database, with clear domain boundaries.

Suggested package structure (adapt names to the existing codebase rather
than moving working code unnecessarily):
com.rahim.crm
├── config/
├── Security/
├── entity/
├── repository/
├── common/
│   ├── dto/
│   ├── exception/
│   ├── audit/
│   └── util/
├── auth/
├── user/
├── branch/
├── customer/
├── product/
├── warranty/
├── complaint/
├── appointment/
├── technician/
├── repair/
├── inventory/
├── payment/
├── dispute/
├── notification/
├── report/
└── audit/
Each business module should normally contain its own controller,
service, repository, entity, and dto packages as needed.
Layer responsibilities
- Controller: HTTP routing, request binding, validation trigger,
  response status. Keep business rules out.
- DTO: Explicit API input/output models. Do not expose JPA
  entities directly.
- Service: Business rules, authorization, transaction boundaries,
  workflow transitions.
- Repository: Database queries. Prefer purpose-built queries over
  loading large collections.
- Entity: Persistence model and relationships.
- Security: Authentication, token processing, password policy, and
  authorization configuration.
- Common: Shared error response, pagination conventions, audit
  support, and reusable infrastructure.
  Engineering conventions
- Use constructor injection.
- Use DTOs for all public API boundaries.
- Use @Transactional on service operations that change multiple
  related records.
- Use @Transactional(readOnly = true) for read operations where
  appropriate.
- Avoid Lombok if it causes IDE/compiler problems in this project.
- Avoid exposing lazy JPA relationships in JSON.
- Use database constraints and unique indexes in addition to
  application validation.
- Use pagination for potentially large list endpoints.
- Use UTC for stored timestamps and a consistent ISO-8601 format in
  APIs.
- Use database migrations (Flyway or Liquibase) before production; do
  not rely on ddl-auto=update for production schema management.
5. Current implementation status
   This section distinguishes implemented work from work that still needs
   verification.
   Implemented modules
   Area                    Implemented             Status
   functionality           
   Authentication          Registration, login,    Implemented; regression
   JWT authentication,     tests still required
   BCrypt password hashing

Password management     First-login password    Implemented; security
change and              hardening required
forgot-password OTP
flow

Admin bootstrap         Initial administrator   Implemented
provisioning through
environment
configuration

Branch management       Create, list, retrieve, Implemented and tested
update, activate,       through API
deactivate branches

Branch managers         Create, list by branch, Implemented and tested
retrieve, update,       through API
activate/deactivate,
reassign branch

Technicians             Create with generated   Implemented and tested
technician ID and       through API
temporary password,
list, retrieve, update,
activate/deactivate,
reassign                
Technician login        Technician can log in   Postman login tested
using the configured
email/password login
flow
Current checkpoint --- branch-level technician authorization
The latest development task is to enforce the following:
- Admin can manage technicians in every branch.
- A branch manager can manage technicians only in the manager's own
  branch.
- A branch manager cannot reassign a technician to another branch.
- Technicians cannot manage technician accounts.
  An updated TechnicianService was prepared to load the authenticated
  user from the database using the ID in CustomUserPrincipal, then
  compare branch ownership. The compile result has not yet been
  confirmed.

Before starting new modules:
1. Run ./mvnw clean compile.
2. Fix any compile errors.
3. Update SecurityConfig to permit both ADMIN and BRANCH_MANAGER
   to reach technician endpoints, only after the service-layer checks
   are in place.
4. Keep technician reassignment administrator-only in the service layer
   (and preferably method-level authorization too).
5. Test all positive and negative cases in Postman.
   Important security follow-up
- Review whether the forgot-password OTP is returned in the API
  response. It must not be returned in production.
- Store OTPs securely (prefer a hash), enforce expiry, attempt limits,
  resend limits, and one-time use.
- Temporary technician passwords should ultimately be delivered
  through a controlled email/SMS process, not exposed repeatedly.
- Review JWT expiry, signing-key management, refresh-token strategy
  (if needed), logout/revocation requirements, and account
  disablement.
- Ensure secrets are provided through environment variables or a
  secret manager, never committed to Git.
- Add rate limiting to login and OTP endpoints before production.
6. Functional requirements and implementation roadmap
   The sequence below is intentionally dependency-driven. Finish and test
   one module before starting the next.
   Phase 0 --- Stabilize the existing foundation
   Purpose: Make the current authentication and account-management
   foundation safe to build on.

Tasks:
- [ ] Confirm the current project compiles with the intended Java 21
  runtime.
- [ ] Confirm the application starts and connects to the crm MySQL
  database.
- [ ] Complete branch-level authorization for technician management.
- [ ] Review branch-manager endpoint permissions and scope all manager
  operations to their own branch.
- [ ] Confirm technician first-login password change works end to end.
- [ ] Confirm disabled users cannot authenticate and disabled accounts
  cannot use protected APIs.
- [ ] Review global exception handling and standardize error
  responses.
- [ ] Add validation annotations to request DTOs and handle validation
  errors consistently.
- [ ] Add pagination conventions for list APIs.
- [ ] Add baseline unit and integration tests for authentication and
  authorization.
- [ ] Document environment variables and safe local setup.
  Acceptance criteria:
- Admin can access all branches.
- A manager can access only their own branch's permitted resources.
- Cross-branch access is rejected even if the caller changes an ID in
  Postman.
- Disabled users cannot access protected resources.
- No password, OTP, or secret is written to logs or committed to
  source control.
  Phase 1 --- Customer management
  Purpose: Create and manage customer identities and profiles.

Features:
- Customer registration through the public registration flow.
- Customer profile retrieval and update.
- Customer activation/deactivation by authorized staff where policy
  permits.
- Customer contact details and preferred communication channel.
- Customer account ownership checks.
- Optional customer search for authorized branch staff, limited to
  service-related needs.
  Possible data:
- CustomerProfile linked one-to-one to User.
- Contact preferences and consent records should be modeled separately
  where appropriate.
  Possible APIs:
  Method                  Endpoint                Purpose
  GET                   /api/customers/me     Current customer's
  profile

  PUT                   /api/customers/me     Update own profile

  GET                   /api/customers/{id}   Authorized staff
  retrieve customer
  profile
  GET                   /api/customers        Admin/staff search with
  scope and pagination
  Acceptance criteria: A customer cannot read or update another
  customer's profile by changing the path ID.
  Phase 2 --- Product catalog and customer product registration
  Purpose: Know which physical product is being serviced.

Features:
- Admin manages product brands, categories, models, and product
  specifications.
- Each physical unit is identified by a serial number or other
  company-approved unique identifier.
- Customer registers a purchased product.
- Validate serial number format and prevent duplicate
  ownership/registration.
- Store purchase date, purchase channel, invoice reference, and
  proof-of-purchase metadata where required.
- Allow authorized staff to verify or correct product registration.
- Track product status (active, recalled, retired, etc.) where
  applicable.
  Suggested entities:
- Brand
- ProductCategory
- ProductModel
- RegisteredProduct (physical unit)
- PurchaseRecord or purchase details on registration
- ProductDocument metadata, if file uploads are introduced
  Possible APIs:
- GET /api/product-models
- Admin CRUD under /api/admin/product-models
- POST /api/customer-products
- GET /api/customer-products/me
- GET /api/customer-products/{id}
- PATCH /api/customer-products/{id}/verify
  Acceptance criteria: A customer can only see their registered
  products. Serial numbers are unique according to the business rule.
  Staff verification is auditable.
  Phase 3 --- Warranty and service eligibility
  Purpose: Determine whether a requested repair is covered.

Features:
- Warranty policy by product model/category and effective date.
- Warranty start/end date and coverage type for each registered
  product.
- Warranty validation at complaint creation and again before charge
  estimation/repair.
- Record warranty verification result and the reason.
- Support proof-of-purchase review and authorized warranty correction.
- Support extended warranty only if the business requires it.
- Define how warranty is calculated when purchase date or invoice is
  missing.
  Suggested entities:
- WarrantyPolicy
- ProductWarranty
- WarrantyVerification
- ServiceEligibility (optional derived record)
  Acceptance criteria: The system can explain why a repair is
  considered covered or not covered. A later policy change must not
  silently rewrite historical decisions.
  Phase 4 --- Complaint management and lifecycle
  Purpose: Capture the customer's issue and manage it through
  resolution.

Features:
- Customer submits complaint for one of their registered products.
- Capture issue category, description, symptoms, preferred contact
  method, and attachments.
- Generate a unique complaint/ticket number.
- Determine service branch using an explicit routing policy (customer
  location, service coverage, product category, or manual triage).
- Allow customer to view status and timeline.
- Allow authorized staff to classify, prioritize, accept, reject with
  reason, and update complaints.
- Support reopen requests under defined conditions.
- Record every status transition with actor, timestamp, and reason.
- Prevent invalid status transitions.
  Suggested statuses (finalize before implementation):

SUBMITTED → UNDER_REVIEW → ACCEPTED → APPOINTMENT_PENDING → ASSIGNED → IN_DIAGNOSIS → AWAITING_PARTS / AWAITING_CUSTOMER / IN_REPAIR → REPAIR_COMPLETED → CUSTOMER_CONFIRMATION → CLOSED

Additional terminal or exception states may include REJECTED,
CANCELLED, and REOPENED. Do not implement every status until the
transition rules are agreed.

Suggested entities:
- Complaint
- ComplaintAttachment
- ComplaintStatusHistory
- ComplaintComment
- ComplaintCategory
  Possible APIs:
- POST /api/complaints
- GET /api/complaints/me
- GET /api/complaints/{id}
- GET /api/branch/complaints
- PATCH /api/complaints/{id}/status
- POST /api/complaints/{id}/comments
- POST /api/complaints/{id}/reopen
  Acceptance criteria: Every status change is validated and recorded.
  Customers cannot change internal workflow states directly. Branch staff
  cannot access complaints outside their branch.
  Phase 5 --- Appointment and service-center booking
  Purpose: Let customers schedule service and help branches manage
  capacity.

Features:
- Branch operating hours and holidays.
- Appointment slot configuration and capacity.
- Customer books an available slot for a complaint.
- Reschedule and cancellation rules.
- Prevent double booking under concurrent requests.
- Waitlist or alternative-slot suggestion as a later enhancement.
- Appointment reminders through notification infrastructure.
  Suggested entities:
- BranchOperatingHour
- BranchHoliday
- AppointmentSlot (or capacity model)
- Appointment
- AppointmentHistory
  Possible APIs:
- GET /api/branches/{id}/available-slots
- POST /api/appointments
- GET /api/appointments/me
- PATCH /api/appointments/{id}/reschedule
- PATCH /api/appointments/{id}/cancel
- Branch-scoped appointment list for managers.
  Acceptance criteria: Two customers cannot consume the same exclusive
  slot. Appointment actions are checked against complaint ownership and
  branch scope.
  Phase 6 --- Technician assignment and work queue
  Purpose: Assign the right technician and expose only relevant jobs.

Features:
- Branch manager assigns an accepted complaint/repair job to an active
  technician in the same branch.
- Validate technician skill/category eligibility if skills are
  configured.
- Technician sees only their own assigned work.
- Manager sees work for their branch.
- Track assignment, reassignment, acceptance, rejection, and
  completion.
- Prevent assignment to inactive technicians.
- Record assignment history.
  Suggested entities:
- RepairJob
- TechnicianSkill (optional)
- TechnicianAssignment
- AssignmentHistory
  Possible APIs:
- POST /api/repair-jobs/{id}/assign
- PATCH /api/repair-jobs/{id}/reassign
- GET /api/technicians/me/jobs
- GET /api/branch/repair-jobs
- PATCH /api/repair-jobs/{id}/accept
  Acceptance criteria: A technician cannot retrieve or update another
  technician's job. Managers cannot assign work across branches.
  Phase 7 --- Diagnosis, repair workflow, and service history
  Purpose: Maintain a trustworthy technical record of each repair.

Features:
- Technician records diagnosis, fault codes, observations, and
  recommended action.
- Record repair start/end and labour time.
- Record parts used and replaced components.
- Record repair notes and test results.
- Require completion checklist before marking a repair complete.
- Record customer-visible and internal notes separately.
- Maintain a permanent service history for the registered product.
- Support repeat complaints and repair warranty policy.
  Suggested entities:
- Diagnosis
- RepairWorkLog
- RepairAction
- RepairTestResult
- ServiceHistory
- RepairCompletionChecklist
  Acceptance criteria: A repair cannot be completed without required
  diagnosis and completion data. Completed service records cannot be
  silently overwritten; corrections should be auditable.
  Phase 8 --- Spare parts and inventory
  Purpose: Track parts availability and prevent inaccurate stock
  usage.

Features:
- Admin maintains part catalog and compatibility with product models.
- Each branch tracks its own stock.
- Receive stock, adjust stock, transfer stock, reserve stock, and
  consume stock.
- Technician requests parts for a repair.
- Manager approves requests according to policy.
- Record part serial/batch/lot where needed.
- Prevent negative inventory and concurrent over-consumption.
- Record stock movement history.
  Suggested entities:
- Part
- PartCompatibility
- BranchInventory
- StockMovement
- PartRequest
- PartReservation
- RepairPartUsage
- Supplier and PurchaseOrder only if procurement is in scope.
  Acceptance criteria: Inventory changes are transactional and
  traceable. A repair cannot consume more stock than is
  available/reserved. Branch managers cannot modify another branch's
  stock.
  Phase 9 --- Service bulletins, repair procedures, and safety
  Purpose: Make repairs consistent, safe, and aligned with
  manufacturer guidance.

Features:
- Admin publishes technical service bulletins.
- Versioned repair procedures by product model.
- Technician can view applicable procedures and bulletins.
- Track acknowledgement of critical bulletins.
- Safety checklist and hazard reporting.
- Equipment calibration records if tools require calibration.
- Quarantine/hold process for unsafe or recalled products.
  Suggested entities:
- ServiceBulletin
- BulletinAcknowledgement
- RepairProcedure
- RepairProcedureVersion
- SafetyChecklist
- HazardReport
- Equipment
- CalibrationRecord
- ProductQuarantine
  Acceptance criteria: Critical safety steps cannot be skipped.
  Historical repairs retain the procedure/bulletin version used at the
  time.
  Phase 10 --- Payments, estimates, disputes, and refunds
  Purpose: Handle customer charges transparently.

Features:
- Generate repair estimate with labour, parts, tax, and fees.
- Identify warranty-covered and customer-payable items.
- Customer approves or rejects estimate.
- Record payment attempts and payment status.
- Integrate a payment gateway only after the core payment model is
  stable.
- Support refund requests and authorized approval.
- Customer raises a dispute and attaches evidence.
- Staff respond and resolve disputes with reasons.
- Keep payment provider references; never store raw card details.
  Suggested entities:
- RepairEstimate
- EstimateLine
- CustomerApproval
- Payment
- PaymentTransaction
- Refund
- Dispute
- DisputeMessage
  Acceptance criteria: Financial records are immutable or corrected
  through reversal/refund records. Payment state is verified server-side,
  not trusted from frontend redirects.
  Phase 11 --- Notifications and customer communication
  Purpose: Keep users informed without exposing sensitive information.

Features:
- Email/SMS notifications for registration, complaint receipt,
  appointment, assignment, estimate approval, repair progress,
  completion, and dispute updates.
- Notification templates and delivery status.
- Retry failed delivery.
- User communication preferences and consent.
- In-app notification inbox as a later enhancement.
  Suggested entities:
- Notification
- NotificationTemplate
- NotificationPreference
- ConsentRecord
  Acceptance criteria: Notification failure does not roll back a
  completed business transaction. Sensitive data and passwords/OTPs are
  never included in messages or logs.
  Phase 12 --- Reports, dashboards, and audit
  Purpose: Give each role the information it is allowed to see.

Admin dashboard:
- Complaint volume and resolution time by branch.
- Warranty vs non-warranty service.
- Technician workload and productivity.
- Repeat repair rate.
- Parts usage and stock alerts.
- Customer satisfaction and dispute trends.
  Branch manager dashboard:
- Open complaints in own branch.
- Appointment schedule.
- Technician workload.
- Pending assignments and parts requests.
- Overdue repairs and branch stock alerts.
  Technician dashboard:
- Own assigned jobs.
- Today's appointments/jobs.
- Pending diagnosis, parts, and completion tasks.
  Customer dashboard:
- Registered products.
- Active complaints and status timeline.
- Upcoming appointments.
- Estimates, payments, and service history.
  Audit requirements:
- Record actor, action, entity type/ID, timestamp, and relevant
  before/after values.
- Do not log passwords, tokens, OTPs, or payment secrets.
- Restrict audit-log access.
- Make audit events tamper-resistant as far as the deployment
  architecture permits.
  Suggested entities:
- AuditEvent
- DashboardSnapshot only if later performance needs justify it.
  Acceptance criteria: Every role sees only its authorized data.
  Important administrative and workflow actions have audit events.
  Phase 13 --- Production readiness and deployment
  Features:
- Automated unit, repository, service, controller, and security tests.
- Integration tests against a MySQL-compatible test environment.
- API documentation (OpenAPI/Swagger).
- Database migrations and backup/restore procedure.
- Structured logs with correlation/request IDs.
- Health and readiness endpoints.
- Secure CORS configuration for the actual frontend origin.
- HTTPS and secure deployment secrets.
- Rate limiting and abuse protection.
- File upload size/type validation and malware scanning if attachments
  are enabled.
- Monitoring, error reporting, and database performance review.
- CI pipeline for build and tests.
- Deployment and rollback documentation.
  Acceptance criteria: A clean environment can be provisioned from
  documented steps. Migrations run predictably. Critical workflows pass
  automated tests before release.
7. Core domain model --- initial conceptual map
   This is a planning model, not a final database schema. Confirm
   cardinalities, deletion rules, and constraints before creating each
   module.
   User
   ├── belongs to Branch (optional for some roles)
   ├── has Role
   ├── may have CustomerProfile
   └── may have TechnicianProfile

Branch
├── has BranchManagers (Users with BRANCH_MANAGER role)
├── has Technicians (Users with TECHNICIAN role)
├── has Appointments
├── has RepairJobs
└── has Inventory

CustomerProfile
└── owns RegisteredProducts

ProductModel
├── has WarrantyPolicy
├── has RepairProcedures
├── has CompatibleParts
└── may have ServiceBulletins

RegisteredProduct
├── belongs to CustomerProfile
├── references ProductModel
├── has ProductWarranty
├── has Complaints
└── has ServiceHistory

Complaint
├── belongs to RegisteredProduct / Customer
├── is routed to Branch
├── has StatusHistory and Comments
├── may have Appointment
└── may create RepairJob

RepairJob
├── belongs to Branch
├── references Complaint
├── has TechnicianAssignment
├── has Diagnosis and WorkLogs
├── has PartRequests / PartUsage
├── has Estimate
└── produces ServiceHistory

RepairEstimate
├── has EstimateLines
├── has CustomerApproval
└── may have Payments

Dispute
├── belongs to Customer and related service/transaction
└── has messages and resolution history
Data integrity rules to decide explicitly
- User email and username uniqueness and case-sensitivity.
- Technician ID uniqueness and generation strategy.
- Branch code uniqueness.
- Product serial-number uniqueness and whether serial numbers can ever
  be reused.
- Whether one registered product can have multiple warranty records
  over time.
- Whether a complaint can have multiple appointments or repair jobs.
- Whether a repair can be reopened after closure.
- Whether stock is reserved at assignment/request time or consumed
  only when used.
- How cancelled appointments release capacity.
- How refunds and payment reversals are represented.
- Whether customer accounts can be merged.
8. API design standards
   Use consistent REST conventions.
   Request/response rules
- Request DTOs use validation annotations such as @NotBlank,
  @Email, @Size, and custom validators.
- Responses use explicit response DTOs.
- Use correct HTTP statuses: 200, 201, 204, 400, 401, 403,
  404, 409, and 422 where the API convention requires it.
- Return a consistent error structure, for example:
  {
  "timestamp": "2026-11-22T03:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": ["Email must be valid"],
  "path": "/api/example"
  }
- Use 409 Conflict for duplicate unique values or conflicting state
  changes.
- Use pagination for list endpoints: page, size, and an
  allowlisted sort.
- Never return entity internals, password hashes, OTPs, or
  security-sensitive fields.
- Avoid accepting role names in public registration requests.
- Avoid accepting ownership or branch assignment from the client where
  the server can derive it from the authenticated user.
  API versioning
  For the first release, /api/... is acceptable. Introduce /api/v1/...
  before external consumers depend on the API, or establish a documented
  compatibility policy.
9. Security checklist
- [ ] BCrypt password hashing with an appropriate work factor.
- [ ] Strong, externalized JWT signing secret/key and explicit token
  expiry.
- [ ] Validate signature, expiry, issuer/audience if configured, and
  token structure.
- [ ] Load account state from the database or otherwise ensure
  disablement/revocation takes effect.
- [ ] Enforce first-login password change where required.
- [ ] OTP expiry, hashed storage, one-time use, attempt limit, and
  resend limit.
- [ ] Login and OTP rate limiting.
- [ ] No default or hard-coded production admin credentials.
- [ ] No secrets committed to Git.
- [ ] CORS restricted to trusted frontend origins.
- [ ] CSRF decision documented according to the chosen token storage
  approach.
- [ ] HTTPS in deployed environments.
- [ ] File upload validation and access control.
- [ ] SQL injection prevention through parameterized repository
  queries.
- [ ] Prevent mass assignment by using request DTOs.
- [ ] Branch, customer, and technician ownership checks in service
  methods.
- [ ] Audit privileged operations.
- [ ] Do not expose stack traces or internal exception details to
  clients.
10. Testing strategy
    Testing is part of each phase, not a final activity.
    Unit tests
- Service business rules and state transitions.
- Authorization helper methods.
- Password/OTP policy.
- Warranty calculations.
- Appointment availability and capacity rules.
- Inventory reservation and stock calculations.
  Integration tests
- Repository queries against MySQL-compatible test infrastructure.
- Transaction rollback when a multi-step operation fails.
- Authentication and authorization filter chain.
- DTO validation and exception response.
- Concurrent booking and stock deduction scenarios.
  Essential authorization test matrix
  Caller           Target resource                         Expected
  Admin            Any branch's technician                 Allow
  Branch manager   Technician in own branch                Allow
  Branch manager   Technician in another branch            Deny
  Branch manager   Reassign technician to another branch   Deny
  Technician       Manage another technician               Deny
  Customer         Another customer's complaint/product    Deny
  Technician       Another technician's repair job         Deny
  Branch manager   Complaint in another branch             Deny

For every module, test both allowed and denied cases. A successful
request alone is not sufficient evidence of correct authorization.
11. Suggested development workflow
    Use this cycle for every feature:
1. Agree on the business rule and acceptance criteria.
2. Inspect the existing project tree and related files.
3. Design the entity/relationship changes.
4. Create or update DTOs.
5. Add repository methods.
6. Implement service logic and authorization.
7. Add controller endpoints.
8. Add exception and validation handling.
9. Compile and run the application.
10. Test success, invalid input, unauthorized access, and edge cases in
    Postman.
11. Add automated tests.
12. Update this roadmap and API notes.
13. Commit the working feature to Git.
    Git workflow
- Keep main in a working state.
- Create a feature branch for each meaningful feature.
- Use small, descriptive commits.
- Review the diff before committing.
- Do not commit .env, credentials, JWT secrets, database passwords,
  or real customer data.
  Example:
  git switch main
  git pull
  git switch -c feature/technician-branch-authorization
  ./mvnw clean compile
  git status
  git diff
  git add .
  git commit -m "feat: enforce technician branch authorization"
12. Definition of done
    A feature is not complete just because its endpoint returns 200 OK.

A feature is complete when:
- Business rules are documented.
- DTO validation is implemented.
- Service logic is correct and transactional where necessary.
- Authentication and resource-level authorization are enforced.
- Positive and negative Postman tests pass.
- Automated tests cover critical behavior.
- API responses and error cases are consistent.
- No sensitive data is exposed.
- Database constraints support the business rules.
- The code compiles and the application starts.
- Documentation and this roadmap are updated.
13. Immediate next actions
    Work in this exact order:
1. Compile the updated TechnicianService.java.
   ./mvnw clean compile
2. If compilation succeeds, update SecurityConfig so
   /api/technicians/** allows ADMIN and BRANCH_MANAGER. Keep the
   service-layer branch checks and administrator-only reassignment
   check.
3. Test with Postman:
    - Admin accesses technicians in branch 1 and branch 2.
    - Branch 1 manager lists and manages branch 1 technicians.
    - Branch 1 manager attempts to access a branch 2 technician and is
      denied.
    - Branch 1 manager attempts technician reassignment and is denied.
    - Technician cannot manage technician accounts.
4. Review and secure the branch-manager endpoints with the same
   database-backed branch scoping.
5. Once the authorization foundation is stable, begin Customer
   Management.
14. Project status tracker
    Update this table as development progresses.
    Module                              Status
    Authentication and JWT              Implemented; hardening/tests
    pending

Password and OTP management         Implemented; hardening pending

Admin bootstrap                     Implemented

Branch management                   Implemented; regression tests
pending

Branch manager management           Implemented; scope/security review
pending

Technician management               Implemented; branch authorization
verification pending

Customer management                 Not started

Product catalog                     Not started

Customer product registration       Not started

Warranty                            Not started

Complaint lifecycle                 Not started

Appointments                        Not started

Technician assignment / repair jobs Not started

Diagnosis and repair work logs      Not started

Inventory and spare parts           Not started

Service bulletins and procedures    Not started

Safety and calibration              Not started

Estimates and payments              Not started

Disputes and refunds                Not started

Notifications                       Not started

Dashboards and reports              Not started

Audit trail                         Foundation/review pending

Automated test suite                In progress / needs expansion
Deployment and monitoring           Not started
Important: This document is the implementation plan. It is not a
claim that every listed feature already exists. Mark a feature complete
only after its acceptance criteria and tests pas