DROP TABLE IF EXISTS USERTYPE CASCADE;
DROP TABLE IF EXISTS SPN CASCADE;
DROP TABLE IF EXISTS SPNREQUEST CASCADE;
DROP TABLE IF EXISTS PREREQUISITE CASCADE;
DROP TABLE IF EXISTS ENROLLED CASCADE;
DROP TABLE IF EXISTS STUDENT_INFO CASCADE;
DROP TABLE IF EXISTS COURSE CASCADE;
DROP TABLE IF EXISTS CLASSROOM CASCADE;
DROP TABLE IF EXISTS PROFESSOR CASCADE;
DROP TABLE IF EXISTS STUDENT_INFO CASCADE;
DROP TABLE IF EXISTS STUDENT CASCADE;
DROP TABLE IF EXISTS USERS CASCADE;


CREATE TABLE USERS(
						RU_ID       CHAR(9) NOT NULL,
						NET_ID      CHAR(12) NOT NULL,
						PASSWORD   CHAR(20) NOT NULL,
						PRIMARY KEY (RU_ID),
						UNIQUE KEY (NET_ID)
);

CREATE TABLE STUDENT (    
						RU_ID 	 CHAR(9) NOT NULL,
						NET_ID 		 CHAR(12) NOT NULL,
						PASSWORD	CHAR(20) NOT NULL,
						GRAD_YEAR		INTEGER,
						FIRSTNAME        CHAR(20),
						LASTNAME	CHAR(20),
						GPA REAL,
						MAJOR CHAR(10),
						CREDITS INTEGER,
						PRIMARY KEY(RU_ID),
						FOREIGN KEY (RU_ID) REFERENCES USERS (RU_ID)
						ON DELETE CASCADE,
						UNIQUE KEY (NET_ID)
					);

CREATE TABLE PROFESSOR (	RU_ID 	 CHAR(9),
							NET_ID 		 CHAR(12) NOT NULL,
							PASSWORD	CHAR(20) NOT NULL,
							FIRSTNAME  CHAR(20),
							LASTNAME	CHAR(20),
							DEPARTMENT	CHAR(20),  	#In case we add more departments
							PRIMARY KEY(RU_ID),
							FOREIGN KEY(RU_ID) REFERENCES USERS(RU_ID)
							ON DELETE CASCADE,
							UNIQUE KEY (NET_ID),
							UNIQUE KEY (LASTNAME)
						);

CREATE TABLE CLASSROOM (	SEAT_LIMIT		INTEGER,
							BUILDING_CODE	CHAR(10),
							ROOM			CHAR(10),
							PRIMARY KEY(BUILDING_CODE, ROOM)
						);

CREATE TABLE COURSE (		COURSE_ID		INTEGER,
							NET_ID			CHAR(12),
							NAME			CHAR(20),
							LASTNAME		CHAR(20), # professor
							SECTION		INTEGER,
							SPN_AMOUNT	INTEGER,
							BUILDING_CODE 	CHAR(10),
							ROOM CHAR(10),
							SEMESTER		CHAR(10),
							YEAR_DATE 			INTEGER,
							PRIMARY KEY (COURSE_ID, SECTION, SEMESTER, YEAR_DATE),
							FOREIGN KEY(NET_ID) REFERENCES PROFESSOR (NET_ID),
							FOREIGN KEY(BUILDING_CODE, ROOM) REFERENCES CLASSROOM	(BUILDING_CODE, ROOM)
				   );

CREATE TABLE STUDENT_INFO (	NET_ID		CHAR(12),
							COURSE_ID	INTEGER,
							GRADE		CHAR(3),
							PRIMARY KEY(NET_ID, COURSE_ID),
							FOREIGN KEY(NET_ID) REFERENCES STUDENT (NET_ID),
							FOREIGN KEY(COURSE_ID) REFERENCES COURSE (COURSE_ID)
							ON DELETE CASCADE
						);

CREATE TABLE ENROLLED (
							NET_ID        CHAR(9),
							SECTION 	INTEGER,
							COURSE_ID    INTEGER,
							SEMESTER	CHAR(10),
							YEAR_DATE INTEGER,
							GRADE  CHAR(2),
							PRIMARY KEY (NET_ID, COURSE_ID, SECTION, SEMESTER, YEAR_DATE),
							FOREIGN KEY (NET_ID) REFERENCES STUDENT (NET_ID),
							FOREIGN KEY (COURSE_ID, SECTION, SEMESTER, YEAR_DATE) REFERENCES COURSE (COURSE_ID, SECTION, SEMESTER, YEAR_DATE)

						);


CREATE TABLE PREREQUISITE (		NAME		CHAR(20),
								PID		INTEGER,
								SECTION INTEGER,
								SEMESTER	CHAR(10),
								PRE_COURSEID      CHAR(3), # alludding to the course it's needed for
								YEAR_DATE	INTEGER,
								COURSE_ID	INTEGER,
								PRIMARY KEY (PID,COURSE_ID, SEMESTER, YEAR_DATE, PRE_COURSEID),
								FOREIGN KEY (COURSE_ID, SECTION, SEMESTER, YEAR_DATE) REFERENCES COURSE (COURSE_ID, SECTION, SEMESTER, YEAR_DATE)
							);

CREATE TABLE SPNREQUEST (
								SP_REQUEST_ID INTEGER AUTO_INCREMENT,
								NET_ID       CHAR(9),
								MAJOR_ID    CHAR(3),
								COURSE_ID        INTEGER,
								SECTION       INTEGER,
								SEMESTER   CHAR(10),
								REQUIRED 	INTEGER,
								TIME_STAMP		CHAR(255),
								YEAR_DATE      INTEGER,
								STATUS			CHAR(20),
								RATING			REAL,
								NOTE_REQ   VARCHAR(256),
								NOTE_FV     VARCHAR(256), 
								PRIMARY KEY (SP_REQUEST_ID, NET_ID),
								FOREIGN KEY (NET_ID) REFERENCES STUDENT (NET_ID),
								FOREIGN KEY(COURSE_ID, SECTION, SEMESTER, YEAR_DATE) REFERENCES COURSE (COURSE_ID, SECTION, SEMESTER, YEAR_DATE)
						);

CREATE TABLE SPN (
					SPN_ID    INTEGER AUTO_INCREMENT,
					NET_ID		CHAR(20),
					PROF_ID		CHAR(20),
					MAJOR CHAR(3),
					COURSE_ID     INTEGER,
					SECTION     INTEGER,
					SEMESTER CHAR(10),
					YEAR_DATE     INTEGER,
					SP_REQUEST_ID  INTEGER,
					PRIMARY KEY (SPN_ID),
					FOREIGN KEY(NET_ID) REFERENCES STUDENT(NET_ID),
					FOREIGN KEY(PROF_ID) REFERENCES PROFESSOR(NET_ID),
					FOREIGN KEY (COURSE_ID, SECTION, SEMESTER, YEAR_DATE) REFERENCES COURSE (COURSE_ID, SECTION, SEMESTER, YEAR_DATE),
					FOREIGN KEY (SP_REQUEST_ID) REFERENCES SPNREQUEST (SP_REQUEST_ID)
				);

CREATE TABLE USERTYPE 	(
								TYPEID     INTEGER,
								TYPENAME    VARCHAR(20),
								PRIMARY KEY (TYPEID)
						);

# INSERTING VALUES

INSERT INTO USERTYPE (TYPEID, TYPENAME) VALUES
    (1, 'Student'), 
    (2, 'Faculty'), 
    (3, 'SysAdmin');

INSERT INTO CLASSROOM (SEAT_LIMIT, BUILDING_CODE, ROOM) VALUES
(50, 'ARC', 103),
(1, 'HILL', 115),
(50, 'HILL', 111),
(50, 'LSH', 'AUD'),
(50, 'TIL', 242),
(50, 'BE', 'AUD'),
(50, 'PH', 115),
(50, 'HILL', 116),
(50, 'TIL', 232),
(50, 'TIL', 257),
(50, 'TIL', 252),
(50, 'SEC', 205),
(50, 'ARC', 110),
(50, 'ARC', 111),
(1, 'ARC', 105),
(50, 'HILL', 120);

INSERT INTO COURSE (COURSE_ID, NAME, LASTNAME, SECTION, BUILDING_CODE, ROOM, SEMESTER, YEAR_DATE,SPN_AMOUNT) VALUES
(111, 'Intro Comp Sci', NULL, 01, 'ARC', 103, 'FALL', '2014',10),
(151, 'Calculus I', NULL, 01, 'ARC', 110, 'FALL', '2014',10),
(152, 'Calculus II', NULL, 01, 'ARC', 111, 'FALL', '2014',10),
(111, 'Intro Comp Sci', NULL, 02, 'ARC', 103, 'FALL', '2014',10),
(211, 'Comp Arch', NULL, 01, 'HILL', 111, 'FALL', '2014',10),
(211, 'Comp Arch', NULL, 02, 'HILL', 111, 'FALL', '2014',10),
(205, 'Discrete Math I', NULL, 01, 'LSH', 'AUD', 'FALL', '2014',10),
(205, 'Discrete Math I', NULL, 02, 'LSH', 'AUD', 'FALL', '2014',10),
(206, 'Discrete Math II', NULL, 01, 'TIL', '242', 'FALL', '2014',10),
(206, 'Discrete Math II', NULL, 02, 'TIL', '242', 'FALL', '2014',10),
(214, 'Systems Programming', NULL, 01, 'BE', 'AUD', 'FALL', '2014',10),
(214, 'Systems Programming', NULL, 02, 'BE', 'AUD', 'FALL', '2014',10),
(314, 'Princ. Programming', NULL, 01, 'PH', 115, 'FALL', '2014',10),
(314, 'Princ. Programming', NULL, 02, 'PH', 115, 'FALL', '2014',10),
(334, 'Imaging & Multimedia', NULL, 01, 'HILL', 116, 'FALL', '2014',10),
(334, 'Imaging & Multimedia', NULL, 02, 'HILL', 116, 'FALL', '2014',10),
(336, 'Info & Data Mgt.', NULL, 01, 'TIL', 232, 'FALL', '2014',10),
(336, 'Info & Data Mgt.', NULL, 02, 'TIL', 232, 'FALL', '2014',10),
(344, 'Algorithms', NULL, 01, 'TIL', 257, 'FALL', '2014',10),
(344, 'Algorithms', NULL, 02, 'TIL', 257, 'FALL', '2014',10),
(416, 'Operating Systems', NULL, 01, 'TIL', 252, 'FALL', '2014',10),
(431, 'Software Eng.', NULL, 01, 'SEC', 205, 'FALL', '2014',10);

INSERT INTO PREREQUISITE( NAME, PID, COURSE_ID) VALUES
('Calculus I', 151, 112),
('Calculus I', 151, 152),
('Into Comp Sci', 111, 112),
('Into Comp Sci', 111, 205),
('Calculus II', 152, 205),
('Calculus II', 152, 206),
('Discrete Math I', 205, 206),
('Data Structures', 112, 211),
('Data Structures', 112, 214),
('Comp Arch', 211, 214),
('Comp Arch', 211, 314),
('Discrete Math I', 205, 314),
('Data Structures', 112, 334),
('Discrete Math II', 206, 334),
('Linear Algebrea', 250, 334),
('Discrete Math I', 205, 336),
('Data Structures', 112, 336),
('Discrete Math II', 206, 344),
('Data Structures', 112, 344),
('Comp Arch', 211, 416),
('Systems Program...', 214, 416),
('Software Meth..', 213, 431),
('Principles Program.', 314, 431);

INSERT INTO USERS(RU_ID, NET_ID, PASSWORD) VALUES
(512468580, 'dsellar', 'dylan'),
(442826712, 'jennika', 'jennifer'),
(558025804, 'sethd', 'seth'),
(027743555, 'greg', 'greg'),
(1, 'admin', 'admin'),
(636056170, 'syoon', 'cs336');

INSERT INTO PROFESSOR(RU_ID, NET_ID, PASSWORD, FIRSTNAME, LASTNAME, DEPARTMENT) VALUES
(636056170, 'syoon', 'cs336', 'Sejong', 'Yoon', 'CS');

INSERT INTO COURSE (COURSE_ID, NAME, LASTNAME, SECTION, BUILDING_CODE, ROOM, SEMESTER, YEAR_DATE, NET_ID, SPN_AMOUNT) VALUES
(170, 'Comp Business', 'Yoon', 01, 'ARC', 105, 'FALL', '2014','syoon',10),
(112, 'Data Structures', 'Yoon', 01, 'HILL', 115, 'FALL', '2014', 'syoon',10),
(112, 'Data Structures', 'Yoon', 02, 'HILL',  115,'FALL', '2014','syoon',10);

INSERT INTO STUDENT(RU_ID, NET_ID, PASSWORD, GRAD_YEAR, FIRSTNAME, LASTNAME, GPA, MAJOR, CREDITS) VALUES
(512468580, 'dsellar', 'dylan', 2015, 'dylan' , 'sellar', 3.2, 'CS', 105),
(442826712, 'jennika', 'jennifer', 2014, 'jennifer' , 'borkowski', 3.5, 'CS', 108),
(558025804, 'sethd', 'seth', 2015, 'seth', 'deneroff', 2.0, 'CS', 111),
(027743555, 'greg', 'greg', 2015, 'greg' , 'grzymski', 4.0, 'CS', 114);

INSERT INTO STUDENT_INFO(NET_ID, COURSE_ID, GRADE) VALUES
('greg', 111, 'B'),
('greg', 112, 'C'),
('greg', 152, 'A'),
('greg', 211, 'C'),
('greg', 151, 'B'),
('greg', 205, 'D');