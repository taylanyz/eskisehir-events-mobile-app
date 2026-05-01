-- V4__Add_Feedback_Schema.sql
-- Phase 11: Turkish NLP Feedback Loop - Database schema for user feedback and sentiment analysis

-- User feedback table (star rating + Turkish feedback text)
CREATE TABLE IF NOT EXISTS user_feedback (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    route_id BIGINT REFERENCES routes(id) ON DELETE SET NULL,
    
    -- Quantitative feedback
    star_rating INTEGER NOT NULL CHECK (star_rating >= 1 AND star_rating <= 5),
    
    -- Qualitative feedback (free-form Turkish text)
    feedback_text TEXT,  -- Max 500 chars enforced in DTO
    
    -- Sentiment analysis output
    sentiment_score DECIMAL(4, 3),  -- [-1, 1] from Turkish NLP
    
    -- Reward score for Thompson Sampling
    mapped_reward_score DECIMAL(4, 3),  -- [0, 1]
    
    -- Processing flags
    feedback_processed BOOLEAN DEFAULT FALSE,
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at_ms BIGINT,
    
    -- Legacy fields for backward compatibility
    rating INTEGER NOT NULL,
    comment_text TEXT,
    
    CONSTRAINT rating_consistency CHECK (rating = star_rating)
);

-- Complaint themes extracted from feedback text
CREATE TABLE IF NOT EXISTS feedback_themes (
    feedback_id BIGINT NOT NULL REFERENCES user_feedback(id) ON DELETE CASCADE,
    theme VARCHAR(50) NOT NULL,  -- Enum: ROUTE_QUALITY, CROWDING, BUDGET_EXCEEDED, etc.
    PRIMARY KEY (feedback_id, theme)
);

-- Indexes for query performance
CREATE INDEX IF NOT EXISTS idx_user_feedback_user_id ON user_feedback(user_id);
CREATE INDEX IF NOT EXISTS idx_user_feedback_route_id ON user_feedback(route_id);
CREATE INDEX IF NOT EXISTS idx_user_feedback_created_at ON user_feedback(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_user_feedback_processed ON user_feedback(feedback_processed);

-- Comment on tables
COMMENT ON TABLE user_feedback IS 'User feedback on completed routes: star ratings + Turkish NLP sentiment analysis (Phase 11)';
COMMENT ON COLUMN user_feedback.sentiment_score IS 'Sentiment score from Turkish NLP analyzer: -1 (very negative) to 1 (very positive)';
COMMENT ON COLUMN user_feedback.mapped_reward_score IS 'Combined reward score [0,1] for Thompson Sampling: 60% stars + 40% sentiment';
COMMENT ON TABLE feedback_themes IS 'Extracted complaint themes: helps identify systemic route quality issues';
