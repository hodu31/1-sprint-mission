-- binary_contents 테이블 (먼저 생성)
CREATE TABLE binary_contents (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    file_name VARCHAR(255),
    size BIGINT,
    content_type VARCHAR(100),
    bytes BYTEA
);

-- users 테이블 (binary_contents가 이미 존재하므로 profile_id와 외래 키를 함께 정의)
CREATE TABLE users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    profile_id UUID,
    FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE CASCADE
);

-- 나머지 테이블들 (기존 쿼리 유지)
CREATE TABLE user_statuses (
    id UUID PRIMARY KEY,
    user_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    last_active_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE channels (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(10) NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

CREATE TABLE read_statuses (
    id UUID PRIMARY KEY,
    user_id UUID,
    channel_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    last_read_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE
);

CREATE TABLE messages (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    content TEXT NOT NULL,
    channel_id UUID,
    author_id UUID,
    FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE message_attachments (
    id UUID PRIMARY KEY,
    message_id UUID,
    attachment_id UUID,
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
);