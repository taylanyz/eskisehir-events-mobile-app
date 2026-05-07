# Phase 14 - Thesis Experiment Outline & Design

**Document Purpose**: Complete experimental design for thesis evaluation  
**Audience**: Thesis advisor, evaluation committee, research team  
**Research Question**: How can AI-personalized recommendations with multi-objective route optimization improve tourist satisfaction in a mobile tourism application?

---

## 1. Research Problem & Context

### 1.1 Problem Statement (Akademik)

```
Traditional tourism recommendation systems typically employ:
- Non-personalized approaches (popularity-based, collaborative filtering)
- Greedy routing algorithms (nearest-neighbor, simple distance optimization)
- Single-objective optimization (minimize distance only)

These approaches fail to capture:
- Individual user preferences (budget, accessibility, sustainability values)
- Real-world routing complexity (multiple conflicting objectives)
- Cultural and local context (Eskişehir-specific POI characteristics)
- Contextual factors (weather, time, crowd patterns)

This thesis proposes:
- A personalized recommendation system using content-based filtering
  + Thompson Sampling contextual bandit for learning
  + Real-time user preference adaptation
- A multi-objective route optimization engine
  + Balances: distance, duration, budget, crowd, sustainability
  + Uses weighted linear scalarization + OR-Tools solver
- Integration with Turkish language NLP feedback loop
  + Sentiment analysis of user reviews
  + Continuous model improvement

Hypothesis:
"A system combining personalized recommendations with multi-objective 
route optimization significantly improves user satisfaction, task 
completion rates, and route acceptance compared to baseline approaches."
```

### 1.2 Scope & Boundaries

**In Scope**:
- Eskişehir city boundaries (MVP)
- 100 carefully curated POIs
- 5 primary user objectives (distance, budget, crowd, accessibility, sustainability)
- 70 user study participants
- 4-week learning period
- Mobile (Kotlin) + Backend (Java/Spring) implementation

**Out of Scope**:
- Real-time crowd sensing (using proxies instead)
- Multi-city expansion (Phase 15+)
- Advanced NLP (using basic sentiment analysis)
- Payment processing (preference collection only)
- Social features (personal routing only, no group)

---

## 2. Theoretical Framework

### 2.1 Recommendation System Theory

**Content-Based Filtering Foundation**
```
User Preference Vector: u = [category_pref, budget_level, accessibility_need, 
                               sustainability_value, crowd_tolerance, ...]

POI Feature Vector: p = [category, price_level, wheelchair_accessible,
                         sustainability_score, avg_crowd_time, ...]

Similarity Score: sim(u, p) = Σ(w_i * similarity(u_i, p_i))

Where:
- w_i: learned weight for feature i
- similarity: cosine similarity, euclidean distance, or categorical match
```

**Thompson Sampling (Contextual Bandit)**
```
Key Advantage: Principled exploration-exploitation tradeoff

For each recommendation request:
1. Get user context (time, weather, budget, etc.)
2. Maintain posterior distribution for each POI's reward
3. Sample from posterior to get optimistic estimate
4. Select POI with highest sampled reward
5. Observe reward (interaction type: view, click, save, visit, feedback)
6. Update posterior with observed reward
7. Return top-K POIs sampled this way

Expected Convergence:
- Week 1: High exploration (many different POIs recommended)
- Week 2: Balanced (50% exploration, 50% exploitation)
- Week 3-4: High exploitation (converging to best POIs)
```

**Reward Function Design**
```
Reward Signal Hierarchy:
- Passive view: 0.1 (POI shown to user)
- Click/expand: 0.2 (user interested enough to see details)
- Save to favorites: 0.4 (explicit positive signal)
- Add to route: 0.6 (serious intent)
- Actual visit: 0.8 (ground truth)
- Positive feedback (4-5 stars): 1.0 (strong positive)
- Negative feedback (<3 stars): -0.4 (strong negative)
- Skip/dislike: -0.1 (weak negative)

Probability of conversion (typical):
- View → Click: 30%
- Click → Save: 20%
- Save → Route: 40%
- Route → Visit: 60%
- Visit → Positive Feedback: 70%
```

### 2.2 Multi-Objective Optimization Theory

**Orienteering Problem Extension**
```
Classic Orienteering Problem:
- Select subset of POIs to visit
- Maximize total reward (score)
- Subject to time/distance constraint

Our Extension (Multi-Objective):
Maximize: f(route) = w_1*reward + w_2*inverse_distance + w_3*crowd_avoidance 
                     + w_4*sustainability + w_5*budget_fit - w_6*accessibility_gap

Subject to:
- Total time ≤ user_time_limit
- Total cost ≤ user_budget
- All must-see POIs included
- Accessibility requirements met
- No infeasible POI combinations

Weights (user-customizable):
- Default: w = [0.3, 0.2, 0.2, 0.15, 0.1, 0.05]
- Eco-focused: w = [0.2, 0.1, 0.1, 0.5, 0.1, 0.0]
- Budget-conscious: w = [0.25, 0.15, 0.15, 0.1, 0.35, 0.0]
```

**Solver Approach**
```
For Eskişehir MVP:
- Problem size: ~100 POIs, up to 15 in route
- Solvable by exact solver (OR-Tools)
- Time limit: 5 seconds per optimization
- Solution quality: within 15% of optimal

Fallback (if optimization timeout):
- Use greedy heuristic
- Sort POIs by blended score
- Add greedily until constraints violated
- Response time: <1 second
```

### 2.3 User Satisfaction Theory

**Expectation Confirmation Theory (ECT)**
```
User Satisfaction = f(Perceived Quality, Perceived Value, Expectation Confirmation)

In our context:
- Perceived Quality: Is recommended route actually good?
- Perceived Value: Are constraints respected? Is time well spent?
- Expectation Confirmation: Did system deliver what user expected?

Measurement: Post-experience survey
- Expectation met: 4.0+/5.0
- Quality of experience: 4.0+/5.0  
- Would recommend: 4.0+/5.0
- Aggregated SUS score: ≥75
```

---

## 3. Experimental Design

### 3.1 Study Type & Design

**Type**: Quasi-experimental study with control group
**Design**: Randomized Controlled Trial (RCT)
**Duration**: 4 weeks (2 weeks baseline + 2 weeks intervention)

```
                Week 1-2              Week 3-4
                Baseline              Intervention
                
Test Group:    [Baseline System] → [Our Full System]
Control Group: [Baseline System] → [Baseline System]

Baseline System = Popular POI list + nearest-neighbor routing
Our System = Content-based rec + Thompson Sampling + Multi-objective routing
```

### 3.2 Participant Recruitment

**Target Population**
```
Inclusion Criteria:
- Age 18-65
- Smartphone user (iOS or Android)
- Interested in tourism/travel
- Can commit 4 weeks
- Turkish speaker (preference) or English speaker

Exclusion Criteria:
- Prior participation in related studies
- Professional tour guides
- POI operators/business owners
- Cognitive impairments affecting study participation
```

**Recruitment Strategy**
```
Target: 70 participants (35 test, 35 control)

Channels:
- University student clubs (30 participants)
- Local tourism boards (20 participants)
- Social media ads (20 participants)

Incentives:
- ₺50 gift card per week (₺200 total)
- Raffle: 1 weekend package (₺500) for top participants

Timeline:
- Recruitment: Week -2 to Week 0
- Enrollment: Week 1
- Study: Week 1-4
- Post-study: Week 5
```

### 3.3 Randomization & Stratification

**Randomization**
```
Method: Computer-generated random numbers (block randomization, block size 4)

Stratification Variables (to ensure balance):
1. Gender: M/F balanced
2. Age group: <25 / 25-40 / >40 balanced
3. Tech familiarity: Low/Medium/High balanced
4. Experience with tourism apps: None/Some/Frequent balanced

Ensures both groups are demographically similar
```

### 3.4 Study Protocol

**Week 0: Pre-Study**
```
- [ ] Participant information session (30 min)
- [ ] Informed consent (signature)
- [ ] Demographic questionnaire
- [ ] Pre-study attitudes survey (SUS, recommendation preferences)
- [ ] App installation & account setup
```

**Week 1-2: Baseline Period**
```
Both test and control groups use BASELINE SYSTEM ONLY

Baseline System Capabilities:
- Non-personalized POI list (sorted by popularity + rating)
- Simple filter by category/district/budget
- Nearest-neighbor route (visits POIs in geographic order)
- No personalization or learning

Task Protocol:
1. Day 1-2: Free exploration
   - Browse POIs
   - Create 1-2 routes
   - Explore features
   - Goal: familiarize with app

2. Day 3-7: Assigned tasks (to establish baseline)
   - Generate recommendations (free browsing)
   - Create 3 routes with varying constraints
   - Provide feedback on recommendations

3. Day 8-14: Continued use
   - Free usage with assigned 1-2 tasks per day
   - Track all interactions
   - Collect implicit feedback (saves, visits, shares)
```

**Week 3-4: Intervention Period**
```
TEST GROUP: Upgraded to FULL SYSTEM
- Content-based recommendations (initial)
- Thompson Sampling learning (adapts recommendations)
- Multi-objective route optimization
- Contextual adaptation (weather, time, mood)
- Feedback collection (for learning)

CONTROL GROUP: Remains on BASELINE SYSTEM
- Continue with popularity-based recommendations
- Greedy routing
- No personalization

Task Protocol:
1. Day 1: System changeover briefing (test group only)
   - What's new in the system
   - How to provide feedback
   - Encourage preference fine-tuning

2. Day 2-14: Continued use (same tasks as Week 2)
   - Generate recommendations daily
   - Create/edit routes
   - Provide feedback on quality
   - System learns preferences
```

**Week 5: Post-Study**
```
- [ ] Post-study survey (SUS, recommendation quality, satisfaction)
- [ ] In-depth interview (30 min) - select 10 participants
- [ ] Data collection from app (interactions, routes, feedback)
- [ ] Comparative analysis between groups
```

### 3.5 Data Collection

**Quantitative Data** (automatic logging)

```
From Mobile App:
- All user interactions (view, click, save, route, feedback)
- Timestamps and durations
- Device & network info
- API response times
- Errors encountered

From Backend:
- Recommendations generated (content + scores)
- Routes optimized (POIs selected + metrics)
- User preferences learned
- Contextual bandit states

From Analytics:
- Session duration
- Feature usage frequency
- Screen flow analysis
- Crash/error reports
```

**Qualitative Data** (surveys & interviews)

```
Weekly Surveys (3 min):
- Recommendation satisfaction (1 question)
- Route quality (1 question)
- Overall app satisfaction (1 question)

Post-Study Survey (15 min):
- SUS score (10 questions)
- Recommendation quality (5 questions)
- Route optimization (5 questions)
- Feature satisfaction (8 questions)
- Open-ended feedback (3 questions)
- Comparative questions vs baseline (3 questions)

In-Depth Interview (30 min, selected 10 participants):
- User experience and pain points
- Feature utility
- Recommendation relevance
- Route quality assessment
- Suggestions for improvement
- Willingness to use long-term
```

---

## 4. Analysis Plan

### 4.1 Primary Analysis

**Primary Outcome**: User Satisfaction Score

```
Measured by: SUS Score (System Usability Scale)
- Standard 10-question instrument
- Calculated as (sum of scores) * 2.5
- Range: 0-100

Hypothesis Test:
H0: SUS_test = SUS_control
H1: SUS_test > SUS_control (one-tailed)

Test: Independent samples t-test
- Alpha: 0.05
- Expected SUS_test: 75-80
- Expected SUS_control: 65-70
- Expected difference: 8-10 points
- Effect size (Cohen's d): 0.80 (large)

Power Analysis:
- Power: 0.80 (standard)
- Alpha: 0.05
- Effect size: 0.80
- Required sample size: 64 (32 per group)
- Actual sample: 70 (35 per group) ✓ sufficient power
```

**Secondary Outcomes**

| Outcome | Measurement | Expected Test | Control | Statistical Test |
|---------|------------|---|---|---|
| Recommendation CTR | % of recommendations clicked | 0.22 | 0.10 | Chi-square |
| Route Acceptance | % routes user uses | 0.84 | 0.68 | Chi-square |
| Precision@10 | Relevant items in top 10 | 0.52 | 0.28 | t-test |
| Task Completion | % of tasks completed | 97% | 93% | Chi-square |
| NPS (Net Promoter Score) | 0-10 scale | 28 | 5 | Mann-Whitney U |

### 4.2 Secondary Analysis

**Learning Curve Analysis**
```
Question: Does recommendation quality improve over time?

Method:
- Calculate weekly NDCG@10 for each user
- Plot learning curve: NDCG vs week
- Fit exponential model: NDCG(t) = NDCG_∞ + (NDCG_0 - NDCG_∞) * e^(-λt)
- Extract: convergence time (λ) and final quality (NDCG_∞)

Expected:
- Test group: NDCG improves 15-25% over 2 weeks
- Control group: NDCG stable (no learning)
```

**Constraint Satisfaction Analysis**
```
For each user-generated route, measure:
- Budget compliance: (routes within budget) / (total routes)
- Time compliance: (routes within time) / (total routes)
- Accessibility compliance: (accessibility requirements met) / (total routes)

Compare:
- Test group: 95%+ compliance across all constraints
- Control group: 85%+ compliance (due to user awareness, not system optimization)
```

**Subgroup Analysis**
```
Does system benefit vary by user characteristics?

Stratify by:
1. Age group: <25, 25-40, >40
   - Hypothesis: Younger users benefit more from UI
   
2. Tech familiarity: Low, Medium, High
   - Hypothesis: Tech-familiar benefit more from features
   
3. Tourism experience: Occasional, Regular, Frequent
   - Hypothesis: Regular users benefit from learning curve
   
4. Budget consciousness: Low, Medium, High
   - Hypothesis: Budget-conscious benefit from constraint optimization

Statistical test: Interaction term in ANCOVA
```

### 4.3 Qualitative Analysis

**Interview Coding**
```
Deductive approach (predefined codes):
- Recommendation quality (+/-)
- Route optimality (+/-)
- Learning effectiveness (+/-)
- UI/UX satisfaction (+/-)
- Feature usefulness (+/-)

Coding process:
1. Audio transcription
2. Manual coding by 2 independent raters
3. Inter-rater reliability (Cohen's κ ≥ 0.70)
4. Thematic analysis by code
5. Direct quotes to support findings

Expected themes:
- (Positive) Recommendations became personalized over time
- (Positive) Routes saved time/money
- (Negative) Learning period felt long
- (Negative) Some constraints hard to input
- (Neutral) Turkish UI helpful
```

### 4.4 Statistical Assumptions & Validation

**Normality Testing**
```
Shapiro-Wilk test on all continuous outcomes
- If p < 0.05: use non-parametric alternative (Mann-Whitney U)
- Report Q-Q plots for visual inspection
```

**Homogeneity of Variance**
```
Levene's test for equal variances
- If p < 0.05: use Welch's t-test (doesn't assume equal variance)
```

**Multiple Comparisons Correction**
```
If running >3 statistical tests:
- Apply Bonferroni correction
- Adjusted alpha = 0.05 / number of tests
- Report both uncorrected and corrected p-values
```

---

## 5. Study Timeline & Milestones

```
Phase          | Date        | Activities
---            | ---         | ---
Preparation    | Apr 15-30   | [ ] Protocol finalization
               |             | [ ] Ethics approval
               |             | [ ] System setup & testing
               |             | [ ] Recruitment materials

Recruitment    | May 1-15    | [ ] Advertise study
               |             | [ ] Collect applications
               |             | [ ] Screen & enroll (35+35)
               |             | [ ] Baseline surveys

Baseline Week  | May 18-31   | [ ] Run baseline period
  1-2          |             | [ ] Collect interaction data
               |             | [ ] Monitor app stability

Intervention   | Jun 1-14    | [ ] Deploy full system to test group
  Week 1-2     |             | [ ] Monitor learning progress
               |             | [ ] Collect weekly surveys
               |             | [ ] Support participants

Post-Study     | Jun 15-30   | [ ] Administer final surveys
               |             | [ ] Conduct interviews
               |             | [ ] Data export & cleaning

Analysis       | Jul 1-15    | [ ] Descriptive statistics
               |             | [ ] Hypothesis tests
               |             | [ ] Qualitative coding
               |             | [ ] Visualization

Write-Up       | Jul 16-31   | [ ] Draft results chapter
               |             | [ ] Draft discussion
               |             | [ ] Compile appendices
               |             | [ ] Final review
```

---

## 6. Expected Results & Implications

### 6.1 Best Case Scenario

```
Primary Result:
- SUS: Test = 78, Control = 68, p < 0.01, d = 0.90 (large effect)
- Recommendation CTR: Test = 24%, Control = 10%, p < 0.01
- Route acceptance: Test = 86%, Control = 70%, p < 0.01

Interpretation:
"The proposed system significantly improves user satisfaction and 
engagement compared to traditional approaches."

Implications:
- Thesis contribution: STRONG
- Publication: 1st-tier HCI/AI conference
- Deployment: Ready for production
```

### 6.2 Expected Case Scenario

```
Primary Result:
- SUS: Test = 74, Control = 68, p = 0.04, d = 0.55 (medium effect)
- Recommendation CTR: Test = 18%, Control = 10%, p = 0.08 (marginal)
- Route acceptance: Test = 80%, Control = 72%, p = 0.06 (marginal)

Interpretation:
"The system shows promising results but with more modest improvements.
Statistical power marginal for some outcomes."

Implications:
- Thesis contribution: MODERATE
- Publication: 2nd-tier conference or journal
- Deployment: Recommend further refinement before production
```

### 6.3 Worst Case Scenario

```
Primary Result:
- SUS: Test = 72, Control = 70, p = 0.32 (not significant)
- Recommendation CTR: Test = 12%, Control = 11%, p = 0.87 (not significant)
- Route acceptance: Test = 76%, Control = 74%, p = 0.64 (not significant)

Interpretation:
"No significant difference between systems. Need to investigate why."

Possible Explanations:
- Study sample too small (underpowered)
- System not different enough from baseline
- Measurement issues
- Learning period too short

Next Steps:
- Investigate qualitative feedback
- Larger sample size in next iteration
- Extended learning period
- System refinement based on user feedback

Implications:
- Thesis contribution: LIMITED
- Publication: Workshop or smaller venue
- Deployment: Requires significant refinement
```

---

## 7. Ethical Considerations

### 7.1 Informed Consent

- [ ] All participants receive clear explanation of study
- [ ] Informed consent form signed before participation
- [ ] Right to withdraw without penalty explained
- [ ] Data privacy policy communicated

### 7.2 Data Privacy

- [ ] Personal data de-identified (coded as P001, P002, etc.)
- [ ] Location data aggregated (no individual tracking)
- [ ] Data encrypted at rest and in transit
- [ ] Data access restricted to research team only
- [ ] Retention: 3 years after study, then destroyed

### 7.3 Minimal Risk

- [ ] Study involves only routine app usage
- [ ] No invasive data collection
- [ ] Participants can opt-out anytime
- [ ] No safety concerns anticipated
- [ ] Risk level: MINIMAL

### 7.4 Benefit Distribution

- [ ] Equal chance of receiving intervention (randomization)
- [ ] Control group offered full system after study completion
- [ ] All participants receive compensation (gift card)
- [ ] Research results shared with all participants

---

## 8. Limitations & Future Work

### 8.1 Study Limitations

1. **Sample Size**: n=70 may be underpowered for detecting small effects
   - Mitigation: Recruited extra 10% to account for attrition

2. **Duration**: 4-week study may not capture long-term satisfaction
   - Mitigation: Analyze learning curves within available window
   - Future: Extended 3-month longitudinal study

3. **Single City**: Eskişehir-only MVP
   - Generalizability: Results may not apply to other cities
   - Future: Multi-city evaluation with different geographic characteristics

4. **Proxy Metrics**: Using proxy scores (not real crowd data)
   - Impact: Crowd optimization accuracy limited
   - Future: Integration with real crowd sensors (mobile phone density)

5. **Limited Baseline**: Comparing against simple greedy approach
   - Impact: May not reflect state-of-the-art recommenders
   - Future: Compare against collaborative filtering, neural baselines

### 8.2 Future Research Directions

1. **Extended Evaluation**
   - Longitudinal: 6-month study with retention metrics
   - Different demographics: International visitors, business travelers
   - Seasonal variation: Same study across different seasons

2. **System Improvements**
   - Advanced NLP: Full aspect-based sentiment analysis
   - Real-time crowd: Integration with mobile phone data
   - Social learning: Group recommendation and route planning
   - Multi-objective tuning: Learning user objective weights

3. **Comparative Research**
   - Bandit algorithms: Compare Thompson vs LinUCB vs UCB1
   - Optimization solvers: Exact vs heuristic vs reinforcement learning
   - Explanation methods: User preference for different explanations

4. **Application Extensions**
   - Multi-city expansion: Generalize to other Turkish cities
   - Cross-domain: Food recommendations, shopping routing
   - Integration: Calendar/budget sync, group scheduling

---

## 9. Thesis Chapter Outline

**Chapter 5: Evaluation**
```
5.1 Experimental Design
    5.1.1 Study Overview
    5.1.2 Participant Recruitment
    5.1.3 Randomization & Stratification
    5.1.4 Study Protocol
    5.1.5 Data Collection Methods

5.2 Analysis Plan
    5.2.1 Primary Hypothesis Test (SUS)
    5.2.2 Secondary Outcomes
    5.2.3 Subgroup Analysis
    5.2.4 Qualitative Analysis

5.3 Results
    5.3.1 Participant Demographics
    5.3.2 Primary Outcome Results
    5.3.3 Secondary Outcome Results
    5.3.4 Learning Curve Analysis
    5.3.5 Qualitative Findings

5.4 Discussion
    5.4.1 Interpretation of Findings
    5.4.2 Comparison to Literature
    5.4.3 Study Strengths
    5.4.4 Study Limitations
    5.4.5 Implications for Practice
    5.4.6 Future Research Directions

5.5 Conclusion
```

---

## 10. Appendices

- [ ] Appendix A: Informed Consent Form (Turkish + English)
- [ ] Appendix B: Demographic Questionnaire
- [ ] Appendix C: SUS Survey (Turkish + English)
- [ ] Appendix D: Recommendation Satisfaction Survey
- [ ] Appendix E: Route Quality Survey
- [ ] Appendix F: Interview Protocol
- [ ] Appendix G: Qualitative Coding Scheme
- [ ] Appendix H: IRB Approval Letter
- [ ] Appendix I: Complete Results Tables
- [ ] Appendix J: Statistical Output & Q-Q Plots

---

**Document Status**: Ready for Ethics Review  
**IRB Submission**: Pending  
**Expected Study Start**: May 18, 2026  
**Expected Study End**: July 31, 2026  
**Thesis Chapter Target**: August 15, 2026
