CREATE TABLE publication_schedule (
    id BIGSERIAL PRIMARY KEY,
    platform VARCHAR(50) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    scheduled_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_publication_schedule_platform CHECK (platform IN ('TELEGRAM', 'YOUTUBE', 'ARTICLE')),
    CONSTRAINT chk_publication_schedule_status CHECK (status IN ('SCHEDULED', 'IN_PRODUCTION', 'PUBLISHED'))
);

CREATE INDEX idx_publication_schedule_platform ON publication_schedule(platform);
CREATE INDEX idx_publication_schedule_status ON publication_schedule(status);
