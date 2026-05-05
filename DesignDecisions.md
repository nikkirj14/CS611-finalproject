# Design Decisions

This document complements `README.md` and `UML-Diagram.svg` by documenting some of our design decisions and thought process as well as the project's structure.

## Overall object model

The design is centered around a small core domain model:

- Each `Course` owns `Assignment` objects, `Student` objects, and one `GradeScale`
- `Grader` is the calculator for final percentages, letter assignment, and curve logic
- `CourseManager` holds all loaded/created courses
- `Portal` is the main GUI coordinator that renders state and calls core methods

We have a clear split:

- Core (`src/core`) = logic and data model
- GUI (`src/gui`) = user interaction, rendering, dialogs, charts, and table editing

### Benefit

Most grading rules and logic can be changed in core classes without redesigning the GUI widgets.

---

## Why grading logic is in `Grader`

Instead of putting all calculations inside `Student` or `Course`, we used `Grader` as a focused service:

- weighted final calculation
- letter assignment
- top student score curve application

### Tradeoff

- **Pro:** keeps model classes simpler and one place for grading math
- **Con:** GUI code must remember to call the right `Grader` methods after edits and adds file relationship complexity

### Benefit

It reduced duplication during imports, assignment edits, and boundary updates, because the same methods are reused everywhere so we decided that this was worth it.

---

## Grade scale updates use Observer design pattern

`GradeScale` supports observers through `GradeScaleObserver` and `ScaleLetterRefresh`.

- `ScaleLetterRefresh` is attached per course
- When ranges change (`reset`, `curve`, `boundary edit`), `GradeScale` notifies observers
- Observer callback reassigns letter grades

### Why this was chosen

Boundary changes can happen from different UI actions. Observer notifications avoid manual refresh in every call site.

### Tradeoff

- **Pro:** automatic consistency after scale changes
- **Con:** slightly more indirect control flow

---

## GUI and core relationship

`Portal` does not store duplicate grade logic. It:

- reads current model data to build tables/stats/charts
- sends user edits into core objects (`Student`, `GradeScale`, `Course`)
- asks `Grader`/`Stats` to recompute values

This keeps the GUI mostly as a controller/view layer and avoids embedding grading formulas in Swing event code.

### Tradeoff

- **Pro:** easier to test and reason about core rules independently
- **Con:** UI handlers still need careful sequencing (`recalculate -> reassign -> refresh view`)

---

## Stats and visualization choices

- `Stats` computes course overview (active count, mean/median/std, min/max, letter counts)
- GUI renders both text summary and custom Swing chart panels
- Chart rendering uses custom painting (`JPanel` + `Graphics2D`) to avoid external dependencies

### Benefit

Fast feedback for instructors (distribution + summary in one place) with no third-party chart library requirements.

---

## Factory design pattern for course creation

`Factory`/`CourseFactory` creating either:

- blank courses
- ported courses that copy assignment setup

### Benefit

It makes course-creation paths explicit and easier to extend (e.g. template-based creation) without changing every caller.

---

## Import/Export decisions

The project has multiple import/export interactions:

- course data save/load (`FileHandler`, `CourseManager`)
- grade results export (`CourseResultsCsv`)
- student score import (`StudentImportCsv`)

### Benefit

Import, save, and export concerns are separated so each flow can be developed independently (i.e. single responsibility principle).

---

## Boundary editing is applied as a batch

Originally, we implemented boundary updates and validation one row at a time. That caused a practical issue: users could type a valid overall set of ranges, but intermediate rows looked temporarily invalid and got rejected.

We changed this to batch apply:

- parse all edited min/max values first
- apply all values together
- validate once
- rollback if invalid

### Benefit

The boundary dialog now behaves how expected: if the final set is valid and nonoverlapping, it applies cleanly.

---

## Curve to top student behavior

For the top student curving, we wanted one click to shift boundaries appropriately, but our initial implementation caused repeated clicks on unchanged data keep shifting forever because it would always shift by the difference between 100 and the top student grade. The shifting would also cause the minimum of F (previously 0) to get shifted into a negative number.

Design choice:

- keep a ceiling based shift so repeated curve actions are stable on unchanged data
- keep the lower end (`F` min at 0) so ranges remain intuitive to users

### Tradeoff

- **Pro:** stable user experience and safer repeated actions
- **Con:** a little more logic complexity in `GradeScale` to handle edge cases

---

## Editable table cells vs modal dialogs

We intentionally support direct table edits (assignment scores and notes) instead of forcing all edits through popups.

### Why this was chosen

For instructor workflows, inline editing is much faster when correcting many rows.

### Tradeoff

- **Pro:** fewer clicks and faster updates for adjustments
- **Con:** table listeners need careful validation/recompute logic to avoid inconsistent state after invalid input
