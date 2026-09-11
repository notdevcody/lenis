package org.lwjgl.opengl;

import org.lwjgl.LWJGLException;

import static org.lwjgl.sdl.SDLError.SDL_GetError;
import static org.lwjgl.sdl.SDLVideo.*;
import static org.lwjgl.system.MemoryUtil.memFree;

@SuppressWarnings("unused")
public class SharedDrawable implements Drawable {
    private final long window;
    private long context;
    private GLCapabilities capabilities;

    public SharedDrawable(final Drawable drawable) throws LWJGLException {
        long previousWindow = SDL_GL_GetCurrentWindow();
        long previousContext = SDL_GL_GetCurrentContext();
        GLCapabilities previousCapabilities = previousContext == 0 ? null : GL.getCapabilities();
        try {
            drawable.makeCurrent();
            window = SDL_GL_GetCurrentWindow();
            check(SDL_GL_SetAttribute(SDL_GL_SHARE_WITH_CURRENT_CONTEXT, 1));
            try {
                context = SDL_GL_CreateContext(window);
                check(context != 0);
            } finally {
                SDL_GL_SetAttribute(SDL_GL_SHARE_WITH_CURRENT_CONTEXT, 0);
            }
            capabilities = GL.createCapabilities(org.lwjgl.system.MemoryUtil::memCallocPointer);
        } catch (LWJGLException | RuntimeException | Error e) {
            destroy();
            throw e;
        } finally {
            check(SDL_GL_MakeCurrent(previousWindow, previousContext));
            GL.setCapabilities(previousCapabilities);
        }
    }

    @Override
    public boolean isCurrent() throws LWJGLException {
        return context != 0 && SDL_GL_GetCurrentContext() == context;
    }

    @Override
    public void makeCurrent() throws LWJGLException {
        if (context == 0) {
            throw new LWJGLException("Shared context has been destroyed");
        }
        check(SDL_GL_MakeCurrent(window, context));
        GL.setCapabilities(capabilities);
    }

    @Override
    public void releaseContext() throws LWJGLException {
        if (isCurrent()) {
            check(SDL_GL_MakeCurrent(window, 0));
            GL.setCapabilities(null);
        }
    }

    @Override
    public void destroy() {
        if (context != 0) {
            if (SDL_GL_GetCurrentContext() == context) {
                SDL_GL_MakeCurrent(window, 0);
                GL.setCapabilities(null);
            }
            SDL_GL_DestroyContext(context);
            context = 0;
        }
        if (capabilities != null) {
            memFree(capabilities.getAddressBuffer());
            capabilities = null;
        }
    }

    private static void check(boolean success) throws LWJGLException {
        if (!success) {
            throw new LWJGLException("SDL error encountered: " + SDL_GetError());
        }
    }
}
