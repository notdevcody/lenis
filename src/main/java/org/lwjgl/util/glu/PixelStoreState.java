package org.lwjgl.util.glu;

import static org.lwjgl.opengl.GL11.*;

class PixelStoreState extends Util {
    public int unpackRowLength;
    public int unpackAlignment;
    public int unpackSkipRows;
    public int unpackSkipPixels;
    public int packRowLength;
    public int packAlignment;
    public int packSkipRows;
    public int packSkipPixels;

    PixelStoreState() {
        this.load();
    }

    public void load() {
        this.unpackRowLength = glGetIntegerv(GL_UNPACK_ROW_LENGTH);
        this.unpackAlignment = glGetIntegerv(GL_UNPACK_ALIGNMENT);
        this.unpackSkipRows = glGetIntegerv(GL_UNPACK_SKIP_ROWS);
        this.unpackSkipPixels = glGetIntegerv(GL_UNPACK_SKIP_PIXELS);
        this.packRowLength = glGetIntegerv(GL_PACK_ROW_LENGTH);
        this.packAlignment = glGetIntegerv(GL_PACK_ALIGNMENT);
        this.packSkipRows = glGetIntegerv(GL_PACK_SKIP_ROWS);
        this.packSkipPixels = glGetIntegerv(GL_PACK_SKIP_PIXELS);
    }

    public void save() {
        glPixelStorei(GL_UNPACK_ROW_LENGTH, this.unpackRowLength);
        glPixelStorei(GL_UNPACK_ALIGNMENT, this.unpackAlignment);
        glPixelStorei(GL_UNPACK_SKIP_ROWS, this.unpackSkipRows);
        glPixelStorei(GL_UNPACK_SKIP_PIXELS, this.unpackSkipPixels);
        glPixelStorei(GL_PACK_ROW_LENGTH, this.packRowLength);
        glPixelStorei(GL_PACK_ALIGNMENT, this.packAlignment);
        glPixelStorei(GL_PACK_SKIP_ROWS, this.packSkipRows);
        glPixelStorei(GL_PACK_SKIP_PIXELS, this.packSkipPixels);
    }
}
