CREATE TABLE `students` ( `regno` , `CourseCode` , `CourseTitle` , `slot` , `facid` , `sem` , 'ccode' text)

alter table students add column 'ccode' text
update students set ccode = CourseCode || "-" ||CourseTitle