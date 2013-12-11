CREATE TABLE Circles (
	circleId integer primary key autoincrement,
	description text
);

CREATE TABLE CircleContacts(
	circleId integer,
	contactId integer
);

CREATE TABLE CircleNotificationRules(
	circleId integer,
	notificationRuleId integer
);

CREATE TABLE ContactNotificationRules(
	contactId integer,
	notificationRuleId integer
);

CREATE TABLE NotificationRules (
	notificationRuleId integer primary key autoincrement,
	description text,
	interval integer,				/* interval constant value */
	intervalIncrement integer,		/* interval increment value */
	startDate integer NULL			/* unix timestamp */
);

CREATE TABLE NotificationOccurrences (
	notificationOccurrenceId integer primary key autoincrement,
	notificationRuleId integer,
	contactId integer,
	date integer,					/* unix timestamp */
	action integer					/* action constant value */
);