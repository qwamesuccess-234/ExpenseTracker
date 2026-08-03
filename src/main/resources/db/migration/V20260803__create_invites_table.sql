-- Flyway-style migration: create invites table

CREATE TABLE IF NOT EXISTS invites (
  id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  token VARCHAR(128) NOT NULL UNIQUE,
  invited_by INT NOT NULL,
  organization_id INT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  expires_at DATETIME NOT NULL,
  used TINYINT(1) DEFAULT 0,
  FOREIGN KEY (invited_by) REFERENCES users(id) ON DELETE CASCADE
);
