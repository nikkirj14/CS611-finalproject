/*
 * GradeScaleObserver interface for when letter ranges change in a course's grade scale. It allows observers to be notified when the grade scale is updated, so that they can refresh any relevant displays or calculations.
 */

package core;

public interface GradeScaleObserver {

    // called whenever ranges shift or reset
    void gradeScaleChanged();
}
