CREATE TABLE user_calendars (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id),
    calendar_id TEXT   NOT NULL,
    name_filter TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_user_calendars_user_id ON user_calendars(user_id);

-- 조재영: 개인 + 팀 + 학교(필터)
INSERT INTO user_calendars (user_id, calendar_id, name_filter)
SELECT id, 'wowhdud0303@gmail.com', NULL FROM users WHERE display_name = '조재영'
UNION ALL
SELECT id, '51b35141763c84bdc7eadceb57d1cb2c71fef012ff22e296a3495ec55ff0f9c9@group.calendar.google.com', NULL FROM users WHERE display_name = '조재영'
UNION ALL
SELECT id, '512bd5ada853563861aef066438ed9b28fa803ba71aeec518e31ad23ce94d4b5@group.calendar.google.com', '조재영 - ' FROM users WHERE display_name = '조재영';

-- 권태화: 개인 + 팀
INSERT INTO user_calendars (user_id, calendar_id, name_filter)
SELECT id, '906ec91237f64b1de1ca2c853e1a8b6cd3f4d47ddf445f8f947184862f5344c7@group.calendar.google.com', NULL FROM users WHERE display_name = '권태화'
UNION ALL
SELECT id, '51b35141763c84bdc7eadceb57d1cb2c71fef012ff22e296a3495ec55ff0f9c9@group.calendar.google.com', NULL FROM users WHERE display_name = '권태화';

-- 안수빈: 개인 + 팀 + 학교(필터)
INSERT INTO user_calendars (user_id, calendar_id, name_filter)
SELECT id, '8c7a0db5a64306e6934327468b19db53a93380bd9b16e0daf26c334f64a4fd19@group.calendar.google.com', NULL FROM users WHERE display_name = '안수빈'
UNION ALL
SELECT id, '51b35141763c84bdc7eadceb57d1cb2c71fef012ff22e296a3495ec55ff0f9c9@group.calendar.google.com', NULL FROM users WHERE display_name = '안수빈'
UNION ALL
SELECT id, '512bd5ada853563861aef066438ed9b28fa803ba71aeec518e31ad23ce94d4b5@group.calendar.google.com', '안수빈 - ' FROM users WHERE display_name = '안수빈';

-- 이제우: 팀 캘린더만
INSERT INTO user_calendars (user_id, calendar_id, name_filter)
SELECT id, '51b35141763c84bdc7eadceb57d1cb2c71fef012ff22e296a3495ec55ff0f9c9@group.calendar.google.com', NULL FROM users WHERE display_name = '이제우';

-- google_refresh_token 은 민감 정보이므로 마이그레이션에 포함하지 않음
-- 운영/로컬 DB에 직접 업데이트: UPDATE users SET google_refresh_token = '<token>';
