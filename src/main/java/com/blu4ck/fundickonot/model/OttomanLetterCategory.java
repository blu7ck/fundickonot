package com.blu4ck.fundickonot.model;

public enum OttomanLetterCategory {
    ELIF("ا"),
    BE("ب"),
    PE("پ"),
    TE("ت"),
    SE("ث"),
    CIM("ج"),
    ÇE("چ"),
    HA("ح"),
    KHA("خ"),
    DAL("د"),
    ZAL("ذ"),
    RE("ر"),
    ZE("ز"),
    JE("ژ"),
    SIN("س"),
    ŞIN("ش"),
    SAD("ص"),
    DAD("ض"),
    TTA("ط"),
    ZZA("ظ"),
    AYN("ع"),
    GHAYN("غ"),
    FE("ف"),
    QAF("ق"),
    KAF("ك"),
    GAF("گ"),
    NG("ڭ"),
    LAM("ل"),
    MIM("م"),
    NOON("ن"),
    HE("ه"),
    WAW("و"),
    YA("ی");

    private final String display;

    OttomanLetterCategory(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    @Override
    public String toString() {
        return display;
    }
}
