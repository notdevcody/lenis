package org.lwjgl.util.glu;

import org.lwjgl.BufferUtils;
import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

public class MipMap extends Util {
    public static int gluBuild2DMipmaps(int target, int components, int width, int height, int format, int type, ByteBuffer data) {
        if (width < 1 || height < 1) {
            return GLU_INVALID_VALUE;
        }

        int bpp = bytesPerPixel(format, type);
        if (bpp == 0) {
            return GLU_INVALID_ENUM;
        }

        int maxSize = glGetIntegerv(GL_MAX_TEXTURE_SIZE);
        int w = nearestPower(width);
        if (w > maxSize) {
            w = maxSize;
        }

        int h = nearestPower(height);
        if (h > maxSize) {
            h = maxSize;
        }

        PixelStoreState pss = new PixelStoreState();
        glPixelStorei(GL_PACK_ROW_LENGTH, 0);
        glPixelStorei(GL_PACK_ALIGNMENT, 1);
        glPixelStorei(GL_PACK_SKIP_ROWS, 0);
        glPixelStorei(GL_PACK_SKIP_PIXELS, 0);

        int err = 0;
        boolean done = false;
        ByteBuffer image;
        if (w == width && h == height) {
            image = data;
        } else {
            image = BufferUtils.createByteBuffer((w + 4) * h * bpp);
            int error = gluScaleImage(format, width, height, type, data, w, h, type, image);
            if (error != 0) {
                err = error;
                done = true;
            }
        }

        ByteBuffer scratch = image != data ? image : null;

        for (int level = 0; !done; level++) {
            if (image != data) {
                glPixelStorei(GL_UNPACK_ROW_LENGTH, 0);
                glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
                glPixelStorei(GL_UNPACK_SKIP_ROWS, 0);
                glPixelStorei(GL_UNPACK_SKIP_PIXELS, 0);
            }

            glTexImage2D(target, level, components, w, h, 0, format, type, image);
            if (w == 1 && h == 1) {
                break;
            }

            int newW = Math.max(1, w >> 1);
            int newH = Math.max(1, h >> 1);
            if (scratch == null) {
                scratch = BufferUtils.createByteBuffer((newW + 4) * newH * bpp);
            }

            int error = gluScaleImage(format, w, h, type, image, newW, newH, type, scratch);
            if (error != 0) {
                err = error;
                break;
            }

            image = scratch;
            w = newW;
            h = newH;
        }

        pss.save();
        return err;
    }

    public static int gluScaleImage(int format, int widthIn, int heightIn, int typein, ByteBuffer dataIn, int widthOut, int heightOut, int typeOut, ByteBuffer dataOut) {
        int components = compPerPix(format);
        if (components == -1) {
            return GLU_INVALID_ENUM;
        }

        float[] tempIn = new float[widthIn * heightIn * components];
        float[] tempOut = new float[widthOut * heightOut * components];

        int sizein;
        switch (typein) {
            case GL_UNSIGNED_BYTE:
                sizein = 1;
                break;
            case GL_FLOAT:
                sizein = 4;
                break;
            default:
                return GL_INVALID_ENUM;
        }

        int sizeout;
        switch (typeOut) {
            case GL_UNSIGNED_BYTE:
                sizeout = 1;
                break;
            case GL_FLOAT:
                sizeout = 4;
                break;
            default:
                return GL_INVALID_ENUM;
        }

        PixelStoreState pss = new PixelStoreState();
        int rowlen;
        if (pss.unpackRowLength > 0) {
            rowlen = pss.unpackRowLength;
        } else {
            rowlen = widthIn;
        }

        int rowstride;
        if (sizein >= pss.unpackAlignment) {
            rowstride = components * rowlen;
        } else {
            rowstride = pss.unpackAlignment / sizein * ceil(components * rowlen * sizein, pss.unpackAlignment);
        }

        int temp = 0;
        switch (typein) {
            case GL_UNSIGNED_BYTE:
                dataIn.rewind();
                for (int i = 0; i < heightIn; i++ ) {
                    int ubptr = (i + pss.unpackSkipRows) * rowstride + pss.unpackSkipPixels * components;
                    for (int j = 0; j < widthIn * components; j++ ) {
                        tempIn[temp++] = dataIn.get(ubptr++) & 0xff;
                    }
                }
                break;
            case GL_FLOAT:
                dataIn.rewind();
                for (int i = 0; i < heightIn; i++ ) {
                    int fptr = 4 * ((i + pss.unpackSkipRows) * rowstride + pss.unpackSkipPixels * components);
                    for (int j = 0; j < widthIn * components; j++ ) {
                        tempIn[temp++] = dataIn.getFloat(fptr);
                        fptr += 4;
                    }
                }
                break;
        }

        float sx = (float)widthIn / widthOut;
        float sy = (float)heightIn / heightOut;
        float[] c = new float[components];

        for (int iy = 0; iy < heightOut; iy++) {
            for (int ix = 0; ix < widthOut; ix++) {
                int x0 = (int)(ix * sx);
                int x1 = (int)((ix + 1) * sx);
                int y0 = (int)(iy * sy);
                int y1 = (int)((iy + 1) * sy);
                int readPix = 0;

                Arrays.fill(c, 0.0F);

                for (int ix0 = x0; ix0 < x1; ix0++) {
                    for (int iy0 = y0; iy0 < y1; iy0++) {
                        int src = (iy0 * widthIn + ix0) * components;

                        for (int ic = 0; ic < components; ic++) {
                            c[ic] += tempIn[src + ic];
                        }

                        readPix++;
                    }
                }

                int dst = (iy * widthOut + ix) * components;
                if (readPix == 0) {
                    int src = (y0 * widthIn + x0) * components;

                    for (int ic = 0; ic < components; ic++) {
                        tempOut[dst++] = tempIn[src + ic];
                    }
                } else {
                    for (int i = 0; i < components; i++) {
                        tempOut[dst++] = c[i] / readPix;
                    }
                }
            }
        }

        if (pss.packRowLength > 0) {
            rowlen = pss.packRowLength;
        } else {
            rowlen = widthOut;
        }

        if (sizeout >= pss.packAlignment) {
            rowstride = components * rowlen;
        } else {
            rowstride = pss.packAlignment / sizeout * ceil(components * rowlen * sizeout, pss.packAlignment);
        }

        temp = 0;
        switch (typeOut) {
            case GL_UNSIGNED_BYTE:
                for (int i = 0; i < heightOut; i++) {
                    int ubptr = (i + pss.packSkipRows) * rowstride + pss.packSkipPixels * components;

                    for (int j = 0; j < widthOut * components; j++) {
                        dataOut.put(ubptr++, (byte)tempOut[temp++]);
                    }
                }
                break;
            case GL_FLOAT:
                for (int i = 0; i < heightOut; i++) {
                    int fptr = 4 * ((i + pss.unpackSkipRows) * rowstride + pss.unpackSkipPixels * components);

                    for (int j = 0; j < widthOut * components; j++) {
                        dataOut.putFloat(fptr, tempOut[temp++]);
                        fptr += 4;
                    }
                }
                break;
        }

        return 0;
    }
}
