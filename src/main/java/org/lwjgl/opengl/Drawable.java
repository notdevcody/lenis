package org.lwjgl.opengl;

import org.lwjgl.LWJGLException;

@SuppressWarnings("unused")
public interface Drawable {
    boolean isCurrent() throws LWJGLException;
    void makeCurrent() throws LWJGLException;
    void releaseContext() throws LWJGLException;
    void destroy();
}
