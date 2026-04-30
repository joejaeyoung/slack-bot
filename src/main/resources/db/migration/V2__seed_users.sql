-- 실제 값으로 교체 필요:
--   slack_user_id      : Slack 워크스페이스 → 프로필 → "Member ID" (U로 시작)
--   jira_account_id    : Jira 프로필 URL의 accountId 파라미터
--   google_calendar_id : Google Calendar 설정 → 캘린더 ID (보통 이메일 주소)
--   google_refresh_token: Google OAuth 1회성 CLI로 발급 (Phase 3에서 진행)

INSERT INTO users (display_name, slack_user_id, jira_account_id, google_calendar_id, google_refresh_token) VALUES
    ('조재영', 'U_SLACK_JJY',  'JIRA_ACCOUNT_JJY',  'jjy@gmail.com',  'REFRESH_TOKEN_JJY'),
    ('권태화', 'U_SLACK_KTH',  'JIRA_ACCOUNT_KTH',  'kth@gmail.com',  'REFRESH_TOKEN_KTH'),
    ('안수빈', 'U_SLACK_ASB',  'JIRA_ACCOUNT_ASB',  'asb@gmail.com',  'REFRESH_TOKEN_ASB'),
    ('이제우', 'U_SLACK_LJW',  'JIRA_ACCOUNT_LJW',  'ljw@gmail.com',  'REFRESH_TOKEN_LJW');
