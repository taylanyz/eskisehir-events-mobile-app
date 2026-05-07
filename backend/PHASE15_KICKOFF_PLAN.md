# Phase 15 - Implementation Readiness & Optimization

**Date**: May 7, 2026  
**Status**: Phase 14 Complete, Phase 15 Kickoff  
**Target Completion**: May 31, 2026  
**Critical Deadline**: May 18 User Study Start  

---

## Phase 15 Strategic Overview

Phase 15 unblocks two critical paths:
1. **Test Infrastructure Completion**: Re-enable 25 deferred tests (Mockito + Docker)
2. **User Study Readiness**: Ensure system stable and deployable by May 18

---

## Immediate Action Items (Week 1: May 7-13)

### ✅ DONE - Phase 14 Completion
- ✅ 63 tests passing (100% active rate)
- ✅ Performance baseline established
- ✅ Test infrastructure documented
- ✅ NO COMMITS - Waiting for approval

### 📋 TODO - Phase 15 Step 1: Test Infrastructure Unblocking

#### 1.1 Mockito Java 25 Compatibility Check
**Action**: Assess Mockito upgrade options
**Timeline**: 1-2 days
**Tasks**:
- [ ] Check Mockito 6.x release status for Java 25 support
- [ ] Verify Maven Central for latest stable Mockito version
- [ ] Document compatibility findings
- [ ] If available: Plan upgrade and test re-enablement

**Current Status**:
- Spring Boot 3.4.4 (parent) → transitive Mockito 5.8.0
- Java: 25.0.2
- Issue: Mockito inline-mock-maker bytecode modification fails on Java 25
- Affected: 28+ tests (14 PoiControllerTest + 14+ other controller tests)

**Mockito Compatibility Matrix**:
| Version | Java 25 Support | Status |
|---------|-----------------|--------|
| 5.8.0 | ❌ Not supported | Current |
| 6.0.x | 🔍 Under review | Monitoring |
| 6.1.x | 🔍 In development | Waiting |

**Action Point**: Monitor Mockito GitHub releases daily. Subscribe to release notifications.

---

#### 1.2 Docker Desktop Setup Check
**Action**: Verify Docker availability for TestContainers
**Timeline**: 1 day
**Tasks**:
- [ ] Check Docker Desktop installation status
- [ ] Verify Docker daemon running: `docker ps`
- [ ] Test basic container: `docker run hello-world`
- [ ] If unavailable: Document local PostgreSQL alternative

**Current Status**:
- Docker Desktop: NOT running (found during Phase 14)
- Impact: 11 integration tests deferred (POIIntegrationTestWithTestcontainers)
- TestContainers version: 1.19.8 (compatible)

**Options**:
1. **Option A**: Install Docker Desktop (recommended)
   - Enables TestContainers integration
   - Enables future CI/CD Docker agents
   - Timeline: 30 min setup + 15 min validation

2. **Option B**: Local PostgreSQL Instance
   - Set up standalone PostgreSQL 15
   - Configure manual connection
   - Timeline: 1 hour setup
   - Alternative to TestContainers

3. **Option C**: Skip Integration Tests
   - Defer to Phase 16
   - Keep H2 baseline tests
   - Not recommended for production readiness

**Recommendation**: Option A (Docker Desktop) for full CI/CD capability

---

#### 1.3 System Environment Validation
**Action**: Verify all dependencies ready
**Timeline**: 30 min
**Tasks**:
- [ ] Java 25.0.2 installed: ✅ `C:\Program Files\Java\jdk-25.0.2`
- [ ] Maven 3.9.6 installed: ✅ (verified)
- [ ] PostgreSQL 15 available (prod readiness check)
- [ ] Docker Desktop ready (if Option A chosen)

**Current**:
```
Java: 25.0.2 ✅
Maven: 3.9.6 ✅
Spring Boot: 3.4.4 ✅
PostgreSQL: Not running (dev)
Docker: Not running (dev)
```

---

### 📋 TODO - Phase 15 Step 2: Performance Optimization Prep

#### 2.1 Baseline Analysis
**Action**: Review Phase 14 performance metrics
**Timeline**: 30 min
**Deliverable**: Performance optimization strategy document
**Status**: Ready (RecommendationPerformanceBaselineTest completed)

**Baseline Metrics** (from Phase 14):
```
Query Performance (H2, 100 POIs):
- Query all POIs: <100ms ✓
- Category filter: ~134ms (was <50ms target)
- District filter: <200ms ✓
- Accessibility filter: <200ms ✓
- Batch operations: <200ms ✓
```

**Phase 15 Optimization Goals**:
- Target: 30-40% latency reduction
- Focus: Category/district filtering (identified bottleneck)
- Method: Database indexing + query optimization

**Index Strategy** (to implement):
```sql
-- POI table indexes (already defined, verify implementation)
CREATE INDEX idx_poi_district ON poi(district);
CREATE INDEX idx_poi_category ON poi(category);
CREATE INDEX idx_poi_location ON poi(latitude, longitude);
CREATE INDEX idx_poi_popularity ON poi(popularity_score DESC);

-- Additional compound index for frequent queries
CREATE INDEX idx_poi_category_district ON poi(category, district);
```

---

#### 2.2 Query Profiling Tasks
**Action**: Identify slow queries for optimization
**Timeline**: 2-3 days
**Tasks**:
- [ ] Run performance tests with PostgreSQL (not just H2)
- [ ] Compare H2 vs PostgreSQL latency
- [ ] Identify N+1 query problems
- [ ] Profile JPQL queries with `spring.jpa.properties.hibernate.generate_statistics=true`

---

### 📋 TODO - Phase 15 Step 3: CI/CD Pipeline Prep

#### 3.1 Automated Testing Setup
**Action**: Prepare GitHub Actions or Azure DevOps pipeline
**Timeline**: 2-3 days
**Tasks**:
- [ ] Create `.github/workflows/test.yml` (or Azure DevOps yaml)
- [ ] Configure: `mvn test` on PR
- [ ] Set merge gate: All tests must pass
- [ ] Performance regression detection

**Pipeline Template** (GitHub Actions):
```yaml
name: Test Suite
on: [pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
      - run: mvn test
```

---

### 📋 TODO - Phase 15 Step 4: User Study Readiness

#### 4.1 System Deployment Validation
**Action**: Ensure system deployable by May 18
**Timeline**: 1 week (May 8-15)
**Critical**: Must complete before recruitment deadline (May 15)

**Deployment Checklist**:
- [ ] Build passes: `mvn clean install`
- [ ] All 63 Phase 14 tests PASS
- [ ] Docker deployment working (if using containers)
- [ ] PostgreSQL production schema ready
- [ ] Seed data loaded successfully
- [ ] API endpoints responding
- [ ] Mobile app connects and receives data

**Expected Deployment Timeline**:
- May 14: Final testing
- May 15: Deploy to staging
- May 16: UAT (User Acceptance Testing)
- May 17: Deploy to production
- May 18: User study begins

---

## Weekly Milestone Timeline

| Week | Milestone | Status | Owner |
|------|-----------|--------|-------|
| W1 (May 7-13) | Phase 15 kickoff + dependency check | 🔄 In Progress | You |
| W1 (May 7-13) | Mockito/Docker assessment | 📋 Pending | You |
| W2 (May 14-20) | Performance optimization | 📋 Pending | You |
| W2 (May 14-20) | Test infrastructure unblocking | 📋 Pending | You |
| W2 (May 14-20) | CI/CD pipeline setup | 📋 Pending | You |
| W2 (May 14-20) | **User study begins (May 18)** | 📋 Pending | System |
| W3-W4 (May 21-Jun 4) | User study baseline period | 📋 Pending | Participants |
| W4-W5 (Jun 5-18) | User study intervention period | 📋 Pending | Participants |
| W5-W6 (Jun 19-Jul 2) | Results analysis | 📋 Pending | You |

---

## Phase 15 Success Criteria

### Testing Infrastructure
- ✅ 63 core tests passing
- ⏳ 14 PoiControllerTest tests re-enabled (Mockito fix)
- ⏳ 11 integration tests re-enabled (Docker setup)
- 🎯 Target: 88/88 tests passing

### Performance
- 🎯 Category filter latency: Reduce from 134ms to <85ms (target 30% reduction)
- 🎯 Overall system: All queries <200ms
- 🎯 Batch operations: <150ms

### Deployment
- 🎯 Build passes consistently
- 🎯 All environments: dev, staging, prod
- 🎯 Zero failing integration tests

### User Study Readiness
- 🎯 System stable for 4-week study period
- 🎯 All APIs responding within SLAs
- 🎯 Database operations reliable
- 🎯 Monitoring and alerting configured

---

## Known Blockers & Risks

### 1. Mockito Java 25 Incompatibility
**Impact**: 14+ controller tests cannot run
**Status**: Waiting for Mockito 6.x release
**Mitigation**: Daily monitoring, escalate if no release by May 20
**Fallback**: Use @TestPropertySource workarounds for non-critical tests

### 2. Docker Not Available
**Impact**: 11 integration tests deferred
**Status**: Can be resolved with Docker Desktop installation
**Timeline**: 30 minutes to fix
**Alternative**: Local PostgreSQL setup

### 3. Timeline Pressure
**Impact**: May 18 user study start date
**Status**: Tight but achievable
**Mitigation**: Parallel workstreams, prioritize critical path
**Critical Path**: 
1. Phase 14 tests stable ✅
2. Docker/Mockito setup (May 7-13) 📋
3. Performance baseline (May 14-15) 📋
4. Final deployment (May 16-17) 📋
5. User study ready (May 18) 🎯

---

## Phase 15 Action Sequence

```
Day 1 (May 7):
  ✅ Complete Phase 14 documentation
  📋 Check Mockito releases
  📋 Check Docker Desktop status

Days 2-3 (May 8-9):
  📋 If Docker needed: Install Docker Desktop
  📋 If Mockito needed: Plan upgrade
  📋 Setup CI/CD pipeline
  
Days 4-5 (May 10-11):
  📋 Performance baseline analysis
  📋 Database indexing verification
  📋 Query optimization planning

Days 6-7 (May 12-13):
  📋 Run performance tests with PostgreSQL
  📋 Implement quick wins
  📋 Update performance doc

Days 8-10 (May 14-16):
  📋 Final testing
  📋 UAT (staging deployment)
  📋 Production deployment

Day 11 (May 17):
  🎯 System ready
  🎯 All tests passing
  🎯 Ready for May 18 user study start
```

---

## Deliverables - Phase 15

### Code Changes
- [ ] Mockito upgrade (pom.xml) - if available
- [ ] Docker configuration - if setup needed
- [ ] Database indexes verification
- [ ] Query optimizations
- [ ] Re-enabled tests (if blockers resolved)

### Documentation
- [ ] Performance optimization strategy
- [ ] Deployment runbook
- [ ] System SLA documentation
- [ ] User study system readiness report

### Tests
- [ ] All 63 Phase 14 tests passing ✅
- [ ] 14 PoiControllerTest tests passing (goal)
- [ ] 11 integration tests passing (goal)
- [ ] CI/CD pipeline passing

### Infrastructure
- [ ] Docker Desktop running (if Option A)
- [ ] PostgreSQL 15 production schema
- [ ] Staging environment validated
- [ ] Production environment ready

---

## Next Immediate Step

**Right Now (May 7, End of Day)**:
1. Assess Mockito 6.x availability
2. Determine Docker Desktop approach (install vs. local PostgreSQL)
3. Document findings
4. Plan optimization strategy
5. **DO NOT COMMIT** (per user directive)

**Tomorrow (May 8, Start of Day)**:
1. Begin Phase 15 Step 1 execution
2. Report findings to user
3. Request approval for any major changes

---

## User Approval Points

**Before Proceeding**, require user approval for:
1. ✅ Phase 14 completion and documentation (DONE - no approval needed)
2. 📋 Docker Desktop installation (if needed)
3. 📋 Mockito upgrade (if available)
4. 📋 Performance optimization focus areas
5. 📋 Any breaking changes to current code

---

**Document Version**: Phase 15 Kickoff  
**Status**: Ready for execution  
**Next Review**: May 8, 2026 morning  
**User Directive Reminder**: No commits without explicit approval
