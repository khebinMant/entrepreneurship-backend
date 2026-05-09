-- Schema for shared-service database
-- This file can be used to manually create tables if needed

CREATE TABLE IF NOT EXISTS catalogue_type (
    catalogue_type_id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS catalogue_value (
    catalogue_value_id BIGSERIAL PRIMARY KEY,
    catalogue_type_id BIGINT NOT NULL REFERENCES catalogue_type(catalogue_type_id),
    code VARCHAR(50) NOT NULL,
    value VARCHAR(200) NOT NULL,
    description TEXT,
    display_order INTEGER,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(catalogue_type_id, code)
);

CREATE TABLE IF NOT EXISTS image_gallery (
    image_id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    image_url TEXT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    display_order INTEGER,
    alt_text VARCHAR(255),
    description TEXT,
    file_size_kb INTEGER,
    width_px INTEGER,
    height_px INTEGER,
    mime_type VARCHAR(50),
    uploaded_by_user_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for image_gallery
CREATE INDEX IF NOT EXISTS idx_image_gallery_entity ON image_gallery(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_image_gallery_order ON image_gallery(entity_type, entity_id, display_order);

-- Indexes for catalogue_value
CREATE INDEX IF NOT EXISTS idx_catalogue_value_type ON catalogue_value(catalogue_type_id);
CREATE INDEX IF NOT EXISTS idx_catalogue_value_active ON catalogue_value(is_active);

