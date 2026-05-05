# CS611 Final Project

Simple grading portal for courses, students, assignments, and grade scales.

## How to compile and run
---------------------------------------------------------------------------
1. From project root, run: `./run.sh`
2. To load saved course data on start: `./run.sh StoredData.csv`

## Files
---------------------------------------------------------------------------
- `src/main/Main.java`: starts the app
<br>

- `src/gui/Portal.java`: main Swing window (menus, table, stats, chart panels, edit dialogs, save button)
- `src/gui/FileHandler.java`: handles file chooser plus loading/saving course/weight csv files
- `src/gui/LetterGradeBarChartPanel.java`: draws the letter-grade bar chart with plain Swing shape painting
- `src/gui/StatsByStudentChartPanel.java`: chart panel for per-student stats visualization
- `src/gui/StatsByAssignmentChartPanel.java`: chart panel for per-assignment stats visualization
<br>

- `src/core/CourseManager.java`: keeps track of all courses, lookup/removal, and loading prior courses
- `src/core/Course.java`: course model (id/name, assignments, students, grade scale)
- `src/core/Assignment.java`: assignment model (name, weight, max points, optional note)
- `src/core/Student.java`: student model (identity, active flag, scores map, note, final %, letter)
- `src/core/Grader.java`: does weighted final percent math, letter assignment, and curving to top student final percent logic
- `src/core/GradeScale.java`: stores letter boundaries, validation, shifts/resets, and observer notifications
- `src/core/GradeRange.java`: one grade bucket (letter + min/max percent)
- `src/core/GradeScaleObserver.java`: observer interface for reacting when grade boundaries change
- `src/core/ScaleLetterRefresh.java`: observer that reassigns letter grades after scale updates
- `src/core/Stats.java`: computes course overview numbers and assignment-level stats
- `src/core/StudentImportCsv.java`: merges roster/score rows from a student-grade csv into a course
- `src/core/CourseResultsCsv.java`: exports one course’s roster + computed results to a csv
- `src/core/Factory.java`: factory interface for building courses
- `src/core/CourseFactory.java`: concrete factory for blank courses or copied assignment setups
- `src/core/Gradebook.java`: gradebook data holder class used by core models

## Notes
---------------------------------------------------------------------------
1. Grade scale changes use an observer so letter grades refresh automatically after boundary updates
2. Course stats are active student focused, and the top section includes both text summary + chart views
3. Grade scale changes are validated as a batch as opposed to our initial approach of letter grade by letter grade to avoid old ranges from causing incorrect invalidity