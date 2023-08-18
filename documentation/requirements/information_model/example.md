# Student information system - Module Enrollments

The module Enrollments enables to manage enrollments of students in courses in semesters and enrollments in specific schedule tickets in a given semester according to the planned schedule

## Functional Requirements

Course Enrollment Management: The module should enable students to enroll in courses for a given semester and schedule. It should also ensure that a student cannot enroll in a course unless they have completed its prerequisites successfully. Additionally, it should allow a student to enroll in a course with a filled capacity by adding them to the waiting list.

Repeated Enrollments: The module should allow students to enroll in a course they have previously completed if the course is explicitly specified as allowing repeated enrollments.

Student List Management: The module should allow course guarantors and teachers to view the list of students enrolled in a particular course for a given semester and schedule. They should also be able to reallocate students between schedule sheets defined in the schedule for the given semester.

Email Communication: The module should allow course guarantors and teachers to send email messages to students enrolled in a particular course for a given semester and schedule.

Statistical Reports: The module should enable the creation of statistical reports on the number of students enrolled in subjects and timetables in individual semesters. Additionally, it should provide data on the teaching shares of teachers.

### User requirements

#### Students: ####
* View available courses and their prerequisite courses.
  - So that he can plan his studies accordingly.
* Restrict course enrollment based on the requirements.
  - Ensures students have necessary knowledge.
* Enroll in courses for a given semester and schedule, or discard the enrollment.
  - Allows students to plan study schedules.
* View their enrollment status.
  - Helps students track course enrollments.
* Can add/remove themselves to/from a waiting list for a scheduled ticket that is already full.
  - So that he doesn't have to check whether a space has freed up.
* Receive email notifications from course guarantors and teachers regarding the courses they are enrolled in.
  - It is important for students to stay informed about changes that may affect their enrollment status or course schedules.
* See whether a subject can be enrolled to repeatedly.
  - So they can plan their studies accordingly.
* See whether they have already completed a certain subject.
  - So that they can know, what subjects they have left.
* Filter the view of available courses according factors like name, students requirement, faculty.
  - Students will be able to find their wanted course easier and in shorter time.
* See the schedule of courses of current semester during the enrollment.
  - Students can easily decide which schedule ticket is the best for them.
* Can verify their current schedule if it has time conflicts.
  - Students can prevent unpleasant ascertainment early.

#### Course Guarantors and Teachers: ####
* View list of students enrolled in their courses for a given semester and schedule.
  - For better communication with students
* Send email messages to students enrolled in their course.
  - This is important for teachers to communicate with their students and provide them with timely feedback and support.
* Reallocate students between schedule sheets defined in the schedule for the given semester.
  - Helps teachers adjust the course schedule and ensure that all students have an equal opportunity to participate in the course.
* Modify course details, such as prerequisites and allowed repeated enrollments.
  - To adjust course requirements to meet evolving teaching materials and student needs
* Review and grade student assignments and exams.
  - It is important for providing students with feedback on their work.

#### Management: ####
* Create statistical reports on the number of students enrolled in subjects and timetables in individual semesters and on the teaching shares of teachers.

### System requirements

#### Actors

##### Student
A person who enrolls in courses for a given semester and schedule.

##### Course Guarantor/Teacher
A person who manages the courses, the enrollments, and the communication with the students.

##### Management
A person who creates statistical reports.

#### Use cases

Student Enrolls in Course
1. Student selects a course from the available courses list.
2. The system displays the prerequisites required for the selected course.
3. If the student has completed the prerequisites, they can enroll in the course.
4. If the course has a filled capacity, the student can choose to add themselves to the waiting list.
5. The system updates the enrollment status of the student.

Student Removes Enrollment in Course
1. Student selects a course from their enrolled courses list.
2. The system displays the details of the selected course.
3. The student can choose to remove their enrollment in the course.
4. The system updates the enrollment status of the student.

Student Views Available Courses and Prerequisites
1. Student accesses the list of available courses.
2. The system displays the available courses along with their prerequisites.

Student Views Enrollment Status
1. Student accesses their enrollment status.
2. The system displays the courses the student is currently enrolled in.

Student Views Repeated Enrollment Status for a Course
1. Student accesses the list of available courses.
2. The system displays whether the student is allowed to enroll in a course they have previously completed.

Course Guarantor/Teacher Views List of Enrolled Students
1. Course guarantor/teacher selects a course for a given semester and schedule.
2. The system displays the list of students enrolled in the selected course.
**
Course Guarantor/Teacher Sends Email Messages to Students
1. Course guarantor/teacher selects a course for a given semester and schedule.
2. The system displays the list of students enrolled in the selected course.
3. The course guarantor/teacher writes an email message and sends it to the selected students.**

Course Guarantor/Teacher Modifies Course Details
1. Course guarantor/teacher selects a course for a given semester and schedule.
2. The system displays the details of the selected course.
3. The course guarantor/teacher modifies the course details, such as prerequisites and allowed repeated enrollments.
4. The system updates the course details.

Course Guarantor/Teacher Reviews and Grades Student Assignments and Exams
1. Course guarantor/teacher selects a course for a given semester and schedule.
2. The system displays the list of students enrolled in the selected course.
3. The course guarantor/teacher selects a student and their assignment or exam to review.
4. The system displays the selected student's work.
5. The course guarantor/teacher provides a grade and feedback for the student's work.
6. The system updates the grade for the selected student's work.

Management Creates Statistical Reports on Enrollments
1. Management selects a semester to generate a statistical report.
2. The system generates a statistical report on the number of students enrolled in subjects and timetables for the selected semester.

##### Use case diagram

```plantuml
@startuml
left to right direction

actor Student as S
actor "Course Guarantor/Teacher" as T
actor Management as M

package "Student Use Cases" {
  usecase "Enrolls in Course" as UC1
  usecase "Removes Enrollment in Course" as UC2
  usecase "Views Available Courses and Prerequisites" as UC3
  usecase "Views Enrollment Status" as UC4
  usecase "Views Repeated Enrollment Status for a Course" as UC5
  usecase "Submit assignment" as SUC1
  usecase "View reviewed assignment" as SUC2
}

package "Course Guarantor/Teacher Use Cases" {
  usecase "Views List of Enrolled Students" as UC6
  usecase "Sends Email Messages to Students" as UC7
  usecase "Modifies Course Details" as UC8
  usecase "Reviews and Grades Student Assignments and Exams" as UC9
}

package "Management Use Cases" {
  usecase "Creates Statistical Reports on Enrollments" as UC10
  usecase "Creates Statistical Reports on Teaching Shares" as UC11
}

S --> UC1
S --> UC2
S --> UC3
S --> UC4
S --> UC5
S --> SUC1
S --> SUC2

T --> UC6
T --> UC7
T --> UC8
T --> UC9

M --> UC10
M --> UC11

UC1 <.. UC3 : <<include>>
UC2 <.. UC3 : <<include>>

UC1 <.. UC4 : <<extend>>
UC2 <.. UC4 : <<extend>>

@enduml
```

There are three main actors - the student, course guarantor/teacher and the management. The student uses the system for his study agenda, such as submitting and viewing assignments, enrolling into courses and viewing his enrollment status.
The teacher uses it mainly to modify/create course details, inform the enrolled students and view the enrolled students' list.
The management only uses the system for generating statistical reports that may be useful for them.


###### Review and grade student assignments and exams

Actors:
- Course Guarantor/Teacher
- System

Preconditions:
- The student has submitted assignments and/or exams for the course.

Flow of Events:

1. The course guarantor/teacher receives student assignments and exams.
2. The course guarantor/teacher reviews student assignments and exams.
3. The course guarantor/teacher provides feedback and grades.
4. The system records the feedback and grades.
5. The system notifies students of their grades and feedback.

```plantuml
@startuml
|Course Guarantor/Teacher|
start

:Receive student assignments and exams;
|Course Guarantor/Teacher|
:Review student assignments and exams;
:Provide feedback and grades;
|System|
:Record feedback and grades;
:Notify students of their grades and feedback;
stop
@enduml
```

###### Course enrollment


Actors:
- Student
- System

Preconditions:
- The student is logged into the system.
- The enrollment period is open.

Postconditions:
- The student is enrolled in the course if the requirements are met, and the schedule ticket is not full.
- The student is added to the waiting list if the schedule ticket is full.

Flow of Events:

1. The student views available courses and their prerequisites.
2. The student tries to enroll in course.
3. The system checks if the requirements are met.
4. If the requirements are met, the student enrolls in the course for a given semester and schedule.
   If the requirements are not met, the system informs the student that they cannot enroll in the course.
5. The student views their enrollment status.

```plantuml
@startuml
|Student|
start

:View available courses and prerequisites;
|System|
:Display courses and prerequisites;
|Student|
:Choose course to enroll in;
|System|
if (Requirements met?) then (yes)
  |System|
  :Enroll in course for a given semester and schedule;
  |System|
  if (Schedule ticket full?) then (yes)
    |System|
    :Add to waiting list;
  else (no)
    :Enroll;
  endif
else (no)
  :Cannot enroll in course;
endif

|System|
:Display enrollment status;
|Student|
:View enrollment status;
|Student|
stop
@enduml
```


###### Course Guarantor/Teacher Modifies Course Details

**Actors**
- Course Guarantor/Teacher
- System

**Preconditions**
- The Course Guarantor/Teacher is logged in

**Postconditions**
- The details of a certain course have been modified

**Flow of events**:

1. Course guarantor/teacher selects a course for a given semester and schedule.
2. The system displays the details of the selected course.
3. The course guarantor/teacher modifies the course details, such as prerequisites and allowed repeated enrollments.
4. The system updates the course details.


```plantuml
@startuml
|Course Guarantor/Teacher|
start

:Views the courses where he is the guarantor/teacher;
|Course Guarantor/Teacher|
:Chooses the course he wishes to modify;
|System|
:Displays the details of the selected course;
|Course Guarantor/Teacher|
:Modifies all the details he wans to;
if(Does he confirm the changes?) then (confirms)
    |System|
    :modifies and saves the course details;
    stop
else (cancels)
|System|
:Cancels the changes and returns to the courses overview page;
stop
@enduml
```

###### Sending Email Messages to Students

**Actors**:
- Course Guarantor/Teacher
- System

**Preconditions**:
- The Course Guarantor/Teacher is logged into the system.

**Postconditions**:
- Required e-mails are sent to students. 

**Flow of Events**:

1. The course guarantor/teacher views all courses, or all his courses.
2. The course guarantor/teacher selects one of the courses.
3. The course guarantor/teacher selects a course for a given semester and schedule.
5. The course guarantor/teacher views the list of students enrolled in the selected course.
6. The course guarantor/teacher selects set of students from the list.
7. The course guarantor/teacher writes an email message and sends it to the selected students.


```plantuml
@startuml
|The Course Guarantor/Teacher|
start

:View courses;
|System|
:Display courses;
|The Course Guarantor/Teacher|
:Choose course;
|System|
:Display semseters and schedules;
|The Course Guarantor/Teacher|
:Choose schedule;
|System|
if(Does The Course Guarantor/Teacher has access to this course) then (no)
    |System|
    :Display error message;
    |The Course Guarantor/Teacher|
    :View error message;
    stop
else (yes)
endif
|System|
:Display enrolled students;
|The Course Guarantor/Teacher|
:Choose set of students;
|System|
:Copy email addersses of selected students;
|The Course Guarantor/Teacher|
:Write email;
|The Course Guarantor/Teacher|
:Send email;
stop

@enduml
```

###### Student Removes Enrollment

**Actors**:
- Student
- System

**Preconditions**:
- The student is logged into the system.
- The enrollment period is open.

**Postconditions**:
- Student discarded his enrollment into some course or the student is informed why the action failed..
- A course lost a participant, its queue in waiting list has shifted and the peeked student filled the vacancy place.

**Flow of Events**:

1. Student selects a course from his enrolled courses list.
2. The system displays the details of the selected course.
3. The student can choose to remove their enrollment in the course.
4. The system updates the enrollment status of the student.
5. The system updates the waiting list.

```plantuml
@startuml
|Student|
start

:View his enrolled courses list;
|System|
:Display students enrolled courses list;
|Student|
:Choose course to discard enrollment;
|System|
if(is student enrolled in the course) then (yes)
    if (is course full?) then (yes)
        if (is Waiting list empty?) then (no)
            |System|
            :peek a student;
            |System|
            :enroll the peeked student to the course;
            |System|
            :send an inform email to the peeked student;
        else (yes)
        endif
    else (no)
    endif
else (no)
|System|
:Display error message;
|Student|
:View error message;
stop
endif

|System|
:Display enrollment status;
|Student|
:View enrollment status;
|Student|
stop
@enduml
```

## Information model


```plantuml
@startuml
class Student {
    -id: int
    -name: string
    -email: string
}

class Course {
    -courseCode: string
    -name: string
    -description: string
    -allowRepeatedEnrollment: bool
}

class Schedule {
    -id: int
    -semester: string
    -credits: int
}

class TimeSlot {
  - startTime: DateTime
  - endTime: DateTime
  - dayOfWeek: DayOfWeek
}

enum DayOfWeek {
  MONDAY
  TUESDAY
  WEDNESDAY
  THURSDAY
  FRIDAY
  SATURDAY
  SUNDAY
}

class Enrollment {
    -id: int
    -schedule: Schedule
    -status: string
}

class WaitingList {
    -id: int
    -students: Queue<Student>
}

class CourseGuarantor {
    -id: int
    -name: string
    -email: string
}

class Teacher {
    -id: int
    -name: string
    -email: string
}

class Management {
    -id: int
    -name: string
}

Student "1" -- "0...n" Enrollment : enrolls in >
Student "0...n" -- "0...n" WaitingList : is in >
Course "1" -- "0...n" Schedule : is offered in >
Course "0...n" -- "0...n" Course : prerequisity >
CourseGuarantor "1" -- "0...n" Course : guarantees >
Teacher "1...n" -- "0...n" Schedule : teaches >
Management "1...n" -- "0...n" Course : manages >
Schedule "1" -- "0...n" Enrollment : includes >
Course "1" -- "1" WaitingList : has >
Schedule "1...n" -- "1...n" TimeSlot : has >
@enduml

```


#### Class: Student
Represents a student in the system. The student class can have attributes such as name, ID, contact information, and enrollment status.

#### Class: Course
Represents a course offered in the system. Its number of credits can vary during different semesters.

#### Class: Schedule
Represents a schedule of courses offered in a particular semester. 

#### Class: TimeSlot
Represents a time slot for a particular course. The time slot class can have attributes such as start time, end time, day of the week, and location. 

#### Enum: DayOfWeek
Represents a day of the week. The DayOfWeek enum can have values such as Monday, Tuesday, Wednesday, etc. It may be used in conjunction with the TimeSlot class to specify the day of the week for a particular course.

#### Class: Enrollment
Represents a student's enrollment in a particular course schedule.

#### Class: WaitingList
Represents the waiting list for a particular course. The waiting list class can have attributes such as the course code and a list of students on the waiting list. 

#### Class: CourseGuarantor
Represents a guarantor for a particular course. The course guarantor class can have attributes such as name, contact information, and a list of courses for which they are the guarantor. 

#### Class: Teacher
Represents a teacher for a particular course. The teacher class can have attributes such as name, contact information, and a list of courses they are teaching.

#### Class: Management
Represents the management of the system. The management class can have attributes such as the system administrator's credentials.