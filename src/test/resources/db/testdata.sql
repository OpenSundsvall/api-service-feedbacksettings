-------------------------------------
-- FeedbackSettingsRepositoryTest.*
-- CreateFeedbackSettingsTest.*
-- ReadFeedbackSettingsTest.*
-- UpdateFeedbackSettingsTest.*
-- DeleteFeedbackSettingsTest.*
-------------------------------------
-- Private person 1
INSERT INTO feedbacksettings.feedback_settings(id, person_id, created)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e1', '49a974ea-9137-419b-bcb9-ad74c81a1d1f', '2022-01-10 10:00:00.000');

INSERT INTO feedbacksettings.feedback_channels(setting_id, contact_method, destination, send_feedback)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e1', 'SMS', '0706100001', true);

-- Private person 2
INSERT INTO feedbacksettings.feedback_settings(id, person_id, created)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e2', '49a974ea-9137-419b-bcb9-ad74c81a1d2f', '2022-01-10 10:00:00.000');

INSERT INTO feedbacksettings.feedback_channels(setting_id, contact_method, destination, send_feedback)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e2', 'SMS', '0706100002', true),
       ('9a24743c-5c19-4774-954e-a3ad67a734e2', 'EMAIL', 'person.2@company.com', true);

-- Private person 3
INSERT INTO feedbacksettings.feedback_settings(id, person_id, created)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e3', '49a974ea-9137-419b-bcb9-ad74c81a1d3f', '2022-01-10 10:00:00.000');

INSERT INTO feedbacksettings.feedback_channels(setting_id, contact_method, destination, send_feedback)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e3', 'SMS', '0706100003', true),
       ('9a24743c-5c19-4774-954e-a3ad67a734e3', 'SMS', '0736100003', true),
       ('9a24743c-5c19-4774-954e-a3ad67a734e3', 'EMAIL', 'person.3@company.com', true);

-- Organizational representative 1 (person 3 has both private and representative settings)
INSERT INTO feedbacksettings.feedback_settings(id, person_id, organization_id, created)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e4', '49a974ea-9137-419b-bcb9-ad74c81a1d3f', '15aee472-46ab-4f03-9605-68bd64ebc84a', '2022-01-10 10:00:00.000');

INSERT INTO feedbacksettings.feedback_channels(setting_id, contact_method, destination, send_feedback)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e4', 'SMS', '0706100004', true),
       ('9a24743c-5c19-4774-954e-a3ad67a734e4', 'EMAIL', 'representative.1@company.com', false);

-- Organizational representative 2 (has only representative settings for same company as representative 1)
INSERT INTO feedbacksettings.feedback_settings(id, person_id, organization_id, created)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e5', '49a974ea-9137-419b-bcb9-ad74c81a1d4f', '15aee472-46ab-4f03-9605-68bd64ebc84a', '2022-01-10 10:00:00.000');

INSERT INTO feedbacksettings.feedback_channels(setting_id, contact_method, destination, send_feedback)
VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e5', 'SMS', '0706100005', true),
       ('9a24743c-5c19-4774-954e-a3ad67a734e5', 'SMS', '0736100005', true),
       ('9a24743c-5c19-4774-954e-a3ad67a734e5', 'EMAIL', 'representative.2@company.com', true);
