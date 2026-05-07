-- V4__Add_Feedback_Schema.sql
-- Phase 11: Turkish NLP Feedback Loop - Database schema for user feedback and sentiment analysis

-- Add missing columns to user_feedback if it exists, otherwise create the table
ALTER TABLE IF EXISTS user_feedback
  ADD COLUMN IF NOT EXISTS feedback_processed BOOLEAN DEFAULT FALSE,
  ADD COLUMN IF NOT EXISTS sentiment_score DECIMAL(4, 3),
  ADD COLUMN IF NOT EXISTS mapped_reward_score DECIMAL(4, 3);

-- Create feedback_themes table if it doesn't exist
CREATE TABLE IF NOT EXISTS feedback_themes (
    feedback_id BIGINT NOT NULL REFERENCES user_feedback(id) ON DELETE CASCADE,
    theme VARCHAR(50) NOT NULL,  -- Enum: ROUTE_QUALITY, CROWDING, BUDGET_EXCEEDED, etc.
    PRIMARY KEY (feedback_id, theme)
);
