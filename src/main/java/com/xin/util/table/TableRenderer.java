package com.xin.util.table;

import java.io.OutputStream;
import java.io.Writer;

public interface TableRenderer {
    public void render(OutputStream ps, int indent);

    public void render(Writer w, int indent);
}
