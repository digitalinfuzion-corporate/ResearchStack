package org.researchstack.backbone.step.active;

import android.content.Context;

import org.researchstack.backbone.result.Result;
import org.researchstack.backbone.step.Step;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;

/**
 * Created by TheMDP on 2/5/17.
 *
 * A recorder is the runtime companion to an `RecorderConfiguration` object, and is
 * usually generated by one.
 *
 * During active tasks, it is often useful to collect one or more pieces of data
 * from sensors on the device. In research tasks, it's not always
 * necessary to display that data, but it's important to record it in a controlled manner.
 *
 * An active step (`ActiveStep`) has an array of recorder configurations
 * (`RecorderConfiguration`) that identify the types of data it needs to record
 * for the duration of the step. When a step starts, the active step layout
 * instantiates a recorder for each of the step's recorder configurations.
 * The step layout starts the recorder when the active step is started, and stops the
 * recorder when the active step is finished.
 *
 * The results of recording are typically written to a file specified by the value of the `outputDirectory` property.
 *
 * Usually, the `ActiveStepLayout` object is the recorder's delegate, and it
 * receives callbacks when errors occur or when recording is complete.
 */

public abstract class Recorder implements Serializable {
    /**
     * A short string that uniquely identifies the recorder (usually assigned by the recorder configuration).
     *
     * The identifier is reproduced in the results of a recorder created from this configuration.
     * In fact, the only way to link a result
     * (an `FileResult` object) to the recorder that generated it is to look at the value of
     * `identifier`. To accurately identify recorder results, you need to ensure that recorder identifiers
     * are unique within each step.
     *
     * In some cases, it can be useful to link the recorder identifier to a unique identifier in a
     * database; in other cases, it can make sense to make the identifier human
     * readable.
     */
    private String identifier;

    /**
     * The step that produced this recorder, configured during initialization.
     */
    private Step step;

    /**
     * The configuration that produced this recorder.
     */
    private RecorderConfig config;

    /**
     * The file URL of the output directory configured during initialization.
     *
     * Typically, you set the `outputDirectory` property for the `ViewTaskActivity` object
     * before presenting the task.
     */
    private File outputDirectory;

    /**
     * A Boolean value indicating whether the recorder is currently recording.
     * @return `true` if the recorder is recording; otherwise, `false`.
     */
    private boolean isRecording;
    
    /**
     * Used to communicate with the listener if the recording completed successfully or failed
     */
    private RecorderListener recorderListener;

    /** Default constructor for serialization/deserialization */
    Recorder() {
        super();
    }

    Recorder(String identifier, Step step, File outputDirectory) {
        super();
        setIdentifier(identifier);
        setStep(step);
        setOutputDirectory(outputDirectory);
    }

    /**
     * Starts data recording.
     *
     * If an error occurs when recording starts, it is returned through the delegate.
     *
     * @param context can be app or activity, used for starting sensor
     */
    public abstract void start(Context context);

    /**
     * Stops data recording, which generally triggers the return of results.
     *
     * If an error occurs when stopping the recorder, it is returned through the delegate.
     * Subclasses should call `finishRecordingWithError:` rather than calling super.
     */
    public abstract void stop();

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isRecording() {
        return isRecording;
    }

    protected void setRecording(boolean recording) {
        isRecording = recording;
    }

    public RecorderConfig getConfig() {
        return config;
    }

    protected void setConfig(RecorderConfig config) {
        this.config = config;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public RecorderListener getRecorderListener() {
        return recorderListener;
    }

    public void setRecorderListener(RecorderListener recorderListener) {
        this.recorderListener = recorderListener;
    }

    protected void onRecorderCompleted(Result result) {
        if (recorderListener != null) {
            recorderListener.onComplete(this, result);
        }
    }

    protected void onRecorderFailed(String error) {
        if (recorderListener != null) {
            recorderListener.onFail(this, new Throwable(error));
        }
    }

    protected void onRecorderFailed(Throwable throwable) {
        if (recorderListener != null) {
            recorderListener.onFail(this, throwable);
        }
    }

    protected void openFileOutputStream() {
        try {
            FileOutputStream fOut = new FileOutputStream(outputDirectory);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
