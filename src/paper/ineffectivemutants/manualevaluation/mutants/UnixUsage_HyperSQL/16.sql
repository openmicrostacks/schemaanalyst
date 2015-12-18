-- 16
-- FKCColumnPairE
-- ListElementExchanger with ChainedSupplier with ForeignKeyConstraintSupplier and ForeignKeyColumnPairWithAlternativesSupplier - Exchanged Pair(OFFICE_ID, OFFICE_ID) with Pair(RACE, OFFICE_ID)

CREATE TABLE "DEPT_INFO" (
	"DEPT_ID"	INT	PRIMARY KEY	NOT NULL,
	"DEPT_NAME"	VARCHAR(50)
)

CREATE TABLE "COURSE_INFO" (
	"COURSE_ID"	INT	PRIMARY KEY	NOT NULL,
	"COURSE_NAME"	VARCHAR(50),
	"OFFERED_DEPT"	INT	 REFERENCES "DEPT_INFO" ("DEPT_ID"),
	"GRADUATE_LEVEL"	SMALLINT
)

CREATE TABLE "OFFICE_INFO" (
	"OFFICE_ID"	INT	PRIMARY KEY	NOT NULL,
	"OFFICE_NAME"	VARCHAR(50),
	"HAS_PRINTER"	SMALLINT
)

CREATE TABLE "RACE_INFO" (
	"RACE_CODE"	INT	PRIMARY KEY	NOT NULL,
	"RACE"	VARCHAR(50)
)

CREATE TABLE "USER_INFO" (
	"USER_ID"	VARCHAR(50)	PRIMARY KEY	NOT NULL,
	"FIRST_NAME"	VARCHAR(50),
	"LAST_NAME"	VARCHAR(50),
	"SEX"	VARCHAR(1),
	"DEPT_ID"	INT	 REFERENCES "DEPT_INFO" ("DEPT_ID"),
	"OFFICE_ID"	INT,
	"GRADUATE"	SMALLINT,
	"RACE"	INT	 REFERENCES "RACE_INFO" ("RACE_CODE")	 REFERENCES "OFFICE_INFO" ("OFFICE_ID"),
	"PASSWORD"	VARCHAR(50)	NOT NULL,
	"YEARS_USING_UNIX"	INT,
	"ENROLL_DATE"	DATE
)

CREATE TABLE "TRANSCRIPT" (
	"USER_ID"	VARCHAR(50)	 REFERENCES "USER_INFO" ("USER_ID")	NOT NULL,
	"COURSE_ID"	INT	 REFERENCES "COURSE_INFO" ("COURSE_ID")	NOT NULL,
	"SCORE"	INT,
	PRIMARY KEY ("USER_ID", "COURSE_ID")
)

CREATE TABLE "UNIX_COMMAND" (
	"UNIX_COMMAND"	VARCHAR(50)	PRIMARY KEY	NOT NULL,
	"CATEGORY"	VARCHAR(50)
)

CREATE TABLE "USAGE_HISTORY" (
	"USER_ID"	VARCHAR(50)	 REFERENCES "USER_INFO" ("USER_ID")	NOT NULL,
	"SESSION_ID"	INT,
	"LINE_NO"	INT,
	"COMMAND_SEQ"	INT,
	"COMMAND"	VARCHAR(50)
)

