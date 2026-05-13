package com.tbl324.desktop.model;

import java.awt.Color;

public class SeatColorMapper {

    public static Color colorFor(SeatStatus status) {
        return switch (status) {
            case AVAILABLE -> new Color(0x4C, 0xAF, 0x50); // yeşil
            case LOCKED    -> new Color(0xFF, 0x98, 0x00); // turuncu
            case SOLD      -> new Color(0xF4, 0x43, 0x36); // kırmızı
            case SELECTED  -> new Color(0x21, 0x96, 0xF3); // mavi
        };
    }

    public static javafx.scene.paint.Color fxColorFor(SeatStatus status) {
        Color c = colorFor(status);
        return javafx.scene.paint.Color.rgb(c.getRed(), c.getGreen(), c.getBlue());
    }
}
