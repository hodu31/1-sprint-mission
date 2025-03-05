-- users 테이블
CREATE TABLE users (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL
);

-- binary_contents 테이블
CREATE TABLE binary_contents (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    file_name VARCHAR(255),
    size BIGINT,
    content_type VARCHAR(100),
    bytes BYTEA
);

-- user_statuses 테이블
CREATE TABLE user_statuses (
    id UUID PRIMARY KEY,
    user_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    last_active_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- channels 테이블
CREATE TABLE channels (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    type VARCHAR(10) NOT NULL CHECK (type IN ('PUBLIC', 'PRIVATE'))
);

-- read_statuses 테이블
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

-- messages 테이블
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

-- message_attachments 테이블
CREATE TABLE message_attachments (
    id UUID PRIMARY KEY,
    message_id UUID,
    attachment_id UUID,
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
    FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
);

-- users_profile 테이블 (users와 binary_contents 간의 관계)
ALTER TABLE users
ADD COLUMN profile_id UUID,
ADD FOREIGN KEY (profile_id) REFERENCES binary_contents(id) ON DELETE CASCADE;