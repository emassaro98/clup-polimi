package com.gheooinc.clup.Interfaces;

public interface Listener<T> {
    /**
     * Invoked when the AsyncTask has completed its execution.
     *
     * @param result The resulting object from the AsyncTask.
     */
    void onListening(T result);
}