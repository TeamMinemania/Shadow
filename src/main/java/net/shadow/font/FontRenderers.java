/*
 * Copyright (c) Shadow client, Saturn5VFive and contributors 2022. All rights reserved.
 */

package net.shadow.font;

import net.shadow.font.adapter.impl.BaseAdapter;
import net.shadow.font.render.FontRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FontRenderers {
    private static final List<BaseAdapter> fontRenderers = new ArrayList<>();
    private static BaseAdapter normal;
    private static BaseAdapter mono;

    public static BaseAdapter getRenderer() {
        return normal;
    }

    public static void setRenderer(BaseAdapter normal) {
        FontRenderers.normal = normal;
    }

    public static BaseAdapter getMono() {
        if (mono == null) {
            int fsize = 18 * 2;
            try {
                mono = (new BaseAdapter(new FontRenderer(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(FontRenderers.class.getClassLoader().getResourceAsStream("Mono.ttf"))).deriveFont(Font.PLAIN, fsize), fsize)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return mono;
    }

    public static BaseAdapter getCustomSize(int size) {
        int size1 = size;
        size1 *= 2;
        for (BaseAdapter fontRenderer : fontRenderers) {
            if (fontRenderer.getSize() == size1) {
                return fontRenderer;
            }
        }
        int fsize = size1;
        try {
            BaseAdapter BaseAdapter = (new BaseAdapter(new FontRenderer(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(FontRenderers.class.getClassLoader().getResourceAsStream("Mono.ttf"))).deriveFont(Font.PLAIN, fsize), fsize)));
            fontRenderers.add(BaseAdapter);
            return BaseAdapter;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
