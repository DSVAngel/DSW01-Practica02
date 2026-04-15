---

description: "Task list template for feature implementation"
---

# Tasks: [FEATURE NAME]

**Input**: Design documents from `/specs/[###-feature-name]/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Las pruebas backend (auth, persistencia, endpoints criticos) y frontend (unit/component + E2E critico cuando aplique) son OBLIGATORIAS por constitucion.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Monorepo Java + Angular (default)**: backend en `src/main/` + `test/java/`, frontend en `frontend/src/`
- **Single project (legacy)**: `src/`, `tests/` at repository root
- **Mobile**: `api/src/`, `ios/src/` or `android/src/`
- Paths shown below assume monorepo Java + Angular - adjust based on plan.md structure

<!--
  ============================================================================
  IMPORTANT: The tasks below are SAMPLE TASKS for illustration purposes only.

  The /speckit.tasks command MUST replace these with actual tasks based on:
  - User stories from spec.md (with their priorities P1, P2, P3...)
  - Feature requirements from plan.md
  - Entities from data-model.md
  - Endpoints from contracts/

  Tasks MUST be organized by user story so each story can be:
  - Implemented independently
  - Tested independently
  - Delivered as an MVP increment

  DO NOT keep these sample tasks in the generated tasks.md file.
  ============================================================================
-->

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create project structure per implementation plan
- [ ] T002 Initialize [language] project with [framework] dependencies
- [ ] T003 [P] Configure linting and formatting tools

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**CRITICAL**: No user story work can begin until this phase is complete

Examples of foundational tasks (adjust based on your project):

- [ ] T004 Setup database schema and migrations framework
- [ ] T005 [P] Implement authentication/authorization framework
- [ ] T006 [P] Setup API routing and middleware structure
- [ ] T007 Create base models/entities that all stories depend on
- [ ] T008 Configure error handling and logging infrastructure
- [ ] T009 Setup environment configuration management
- [ ] T010 [P] Provision local PostgreSQL with Docker (compose or equivalent)
- [ ] T011 [P] Configure OpenAPI/Swagger endpoint and access control
- [ ] T012 [P] Initialize Angular 21 workspace in frontend/ with build/test/lint scripts
- [ ] T013 [P] Define backend-frontend contract sync workflow (OpenAPI client or adapter strategy)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - [Title] (Priority: P1) MVP

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 1 (REQUIRED)

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [ ] T014 [P] [US1] Backend contract/integration tests for [endpoint/flow] in test/java/[path]
- [ ] T015 [P] [US1] Frontend unit/component test for [screen/component] in frontend/src/app/[path].spec.ts
- [ ] T016 [P] [US1] Frontend E2E critical flow for [journey] in frontend/e2e/[name].spec.ts

### Implementation for User Story 1

- [ ] T017 [P] [US1] Implement backend model/service changes in src/main/java/[path]
- [ ] T018 [US1] Implement backend endpoint/validation in src/main/java/[path]
- [ ] T019 [P] [US1] Implement frontend service/client updates in frontend/src/app/[path]
- [ ] T020 [US1] Implement frontend UI flow in frontend/src/app/[feature-path]
- [ ] T021 [US1] Add logging/telemetry for user story 1 operations

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - [Title] (Priority: P2)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 2 (REQUIRED)

- [ ] T022 [P] [US2] Backend contract/integration tests for [endpoint/flow] in test/java/[path]
- [ ] T023 [P] [US2] Frontend unit/component test for [screen/component] in frontend/src/app/[path].spec.ts

### Implementation for User Story 2

- [ ] T024 [P] [US2] Implement backend changes in src/main/java/[path]
- [ ] T025 [US2] Implement frontend changes in frontend/src/app/[path]
- [ ] T026 [US2] Integrate with User Story 1 components/contracts (if needed)

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - [Title] (Priority: P3)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 3 (REQUIRED)

- [ ] T027 [P] [US3] Backend contract/integration tests for [endpoint/flow] in test/java/[path]
- [ ] T028 [P] [US3] Frontend unit/component test for [screen/component] in frontend/src/app/[path].spec.ts

### Implementation for User Story 3

- [ ] T029 [P] [US3] Implement backend changes in src/main/java/[path]
- [ ] T030 [US3] Implement frontend changes in frontend/src/app/[path]
- [ ] T031 [US3] Verify backend-frontend compatibility for this story

**Checkpoint**: All user stories should now be independently functional

---

[Add more user story phases as needed, following the same pattern]

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] TXXX [P] Documentation updates in docs/
- [ ] TXXX Code cleanup and refactoring
- [ ] TXXX Performance optimization across all stories
- [ ] TXXX [P] Additional backend/frontend unit tests and flaky-test cleanup
- [ ] TXXX Security hardening
- [ ] TXXX Run quickstart.md validation

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 -> P2 -> P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Backend domain/services before endpoint adapters
- Frontend service/client updates before UI bindings
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- All tests for a user story marked [P] can run in parallel
- Backend and frontend tasks in the same story can run in parallel when they touch different files
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together (if tests requested):
Task: "Backend contract/integration tests in test/java/[path]"
Task: "Frontend component/E2E tests in frontend/src/app/ and frontend/e2e/"

# Launch backend and frontend implementation work together:
Task: "Implement backend domain changes in src/main/java/[path]"
Task: "Implement frontend UI/service changes in frontend/src/app/[path]"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational -> Foundation ready
2. Add User Story 1 -> Test independently -> Deploy/Demo (MVP)
3. Add User Story 2 -> Test independently -> Deploy/Demo
4. Add User Story 3 -> Test independently -> Deploy/Demo
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
