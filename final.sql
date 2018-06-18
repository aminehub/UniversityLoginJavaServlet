SET DATESTYLE TO PostgreSQL,European;

CREATE TABLE Users (
	id_users INT(6) NOT NULL,
	name varchar(50) NOT NULL,
	surname varchar(50) NOT NULL,
	username varchar(50) NOT NULL,
	hash varchar(50) NOT NULL,
	salt varchar(50) NOT NULL,
	department varchar(50) NOT NULL,
	PRIMARY KEY (id_users)
);
CREATE TABLE Students (
	id_students INT(8) NOT NULL,
	semester INT NOT NULL,
	PRIMARY KEY (id_students)
);

CREATE TABLE Courses (
	id_courses INT(6) NOT NULL,
	course_name varchar(50) NOT NULL,
	dept_name varchar(50) NOT NULL,
	semester INT NOT NULL,
	id_professors varchar(50) NOT NULL,
	PRIMARY KEY (id_courses)
);

CREATE TABLE Grades (
	id_courses INT NOT NULL,
	id_students  varchar(50) NOT NULL,
	grade_num FLOAT, 
	PRIMARY KEY (id_students, id_courses)
);

CREATE TABLE Professors (
	id_professors INT(6) NOT NULL,
	officeNum INT NOT NULL,
	salary FLOAT NOT NULL,
	PRIMARY KEY (id_professors)
);

CREATE TABLE Secretaries (
	id_secretaries INT(6) NOT NULL,
	officeNum INT NOT NULL,
	salary FLOAT NOT NULL,
	PRIMARY KEY (id_secretaries)
);

--and now moving on in the foreign keys insertion

ALTER TABLE Students ADD CONSTRAINT Students_fk0 FOREIGN KEY (id_students) REFERENCES Users(id_users);

ALTER TABLE Grades ADD CONSTRAINT Grades_fk0 FOREIGN KEY (id_courses) REFERENCES Courses(id_courses);

ALTER TABLE Grades ADD CONSTRAINT Grades_fk1 FOREIGN KEY (id_students) REFERENCES Students(id_students);

ALTER TABLE Secretaries ADD CONSTRAINT Secretaries_fk2 FOREIGN KEY (id_secretaries) REFERENCES Users(id_users);

ALTER TABLE Professors ADD CONSTRAINT Professors_fk1 FOREIGN KEY (id_professors) REFERENCES Users(id_users);

ALTER TABLE Courses ADD CONSTRAINT Courses_fk0 FOREIGN KEY (id_professors) REFERENCES Professors(id_professors);