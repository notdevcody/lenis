package org.lwjgl.util.glu;

import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

@SuppressWarnings("unused")
public class Util {
    protected static int ceil(int a, int b) {
        return a % b == 0 ? a / b : a / b + 1;
    }

    protected static float[] normalize(float[] v) {
        float r = (float)Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
        if (r == 0.0) {
            return v;
        }

        r = 1.0F / r;
        v[0] *= r;
        v[1] *= r;
        v[2] *= r;
        return v;
    }

    protected static void cross(float[] v1, float[] v2, float[] result) {
        result[0] = v1[1] * v2[2] - v1[2] * v2[1];
        result[1] = v1[2] * v2[0] - v1[0] * v2[2];
        result[2] = v1[0] * v2[1] - v1[1] * v2[0];
    }

    protected static int compPerPix(int format) {
        switch (format) {
            case GL_COLOR_INDEX:
            case GL_STENCIL_INDEX:
            case GL_DEPTH_COMPONENT:
            case GL_RED:
            case GL_GREEN:
            case GL_BLUE:
            case GL_ALPHA:
            case GL_LUMINANCE:
                return 1;
            case GL_LUMINANCE_ALPHA:
                return 2;
            case GL_RGB:
            case GL_BGR:
                return 3;
            case GL_RGBA:
            case GL_BGRA:
                return 4;
            default:
                return -1;
        }
    }

    protected static int nearestPower(int value) {
        if (value == 0) {
            return -1;
        }

        int i = 1;
        for (;;) {
            if (value == 1) {
                return i;
            } else if (value == 3) {
                return i << 2;
            }
            value >>= 1;
            i <<= 1;
        }
    }

    protected static int bytesPerPixel(int format, int type) {
        int n = Math.max(compPerPix(format), 0);

        int m;
        switch (type) {
            case GL_UNSIGNED_BYTE:
            case GL_BITMAP:
            case GL_BYTE:
                m = 1;
                break;
            case GL_UNSIGNED_SHORT:
            case GL_SHORT:
                m = 2;
                break;
            case GL_UNSIGNED_INT:
            case GL_FLOAT:
            case GL_INT:
                m = 4;
                break;
            default:
                m = 0;
        }

        return n * m;
    }

    protected static int glGetIntegerv(int what) {
        return GL11.glGetInteger(what);
    }
}
