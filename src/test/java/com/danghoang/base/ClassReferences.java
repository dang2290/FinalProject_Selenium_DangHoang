package com.danghoang.base;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

/**
 * Một file .class đã gọi những gì, lấy từ bảng hằng của bytecode. Không quét chữ trong source vì dễ lọt
 * (method reference {@code WebElement::click}, lệnh xuống dòng, tên biến khác).
 *
 * @param classes class được dùng, VD {@code org.openqa.selenium.By}
 * @param methods hàm được gọi, VD {@code org.openqa.selenium.WebElement#click}
 * @param strings chuỗi trong class, VD đoạn JavaScript đưa cho executeJS
 */
public record ClassReferences(Set<String> classes, Set<String> methods, Set<String> strings) {

    private static final int CLASS_MAGIC = 0xCAFEBABE;

    public static ClassReferences of(Path classFile) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(classFile)))) {
            if (in.readInt() != CLASS_MAGIC) {
                throw new IOException("Không phải file .class: " + classFile);
            }
            in.readUnsignedShort();
            in.readUnsignedShort();
            int size = in.readUnsignedShort();
            int[] tags = new int[size];
            int[] first = new int[size];
            int[] second = new int[size];
            String[] text = new String[size];
            for (int i = 1; i < size; i++) {
                int tag = in.readUnsignedByte();
                tags[i] = tag;
                switch (tag) {
                    case 1 -> text[i] = in.readUTF();
                    case 7, 8, 16, 19, 20 -> first[i] = in.readUnsignedShort();
                    case 9, 10, 11, 12, 17, 18 -> {
                        first[i] = in.readUnsignedShort();
                        second[i] = in.readUnsignedShort();
                    }
                    case 3, 4 -> in.readInt();
                    case 5, 6 -> {
                        in.readLong();
                        i++; // long và double chiếm 2 ô
                    }
                    case 15 -> {
                        in.readUnsignedByte();
                        in.readUnsignedShort();
                    }
                    default -> throw new IOException("Ô hằng lạ " + tag + " trong " + classFile);
                }
            }

            Set<String> classes = new TreeSet<>();
            Set<String> methods = new TreeSet<>();
            Set<String> strings = new TreeSet<>();
            for (int i = 1; i < size; i++) {
                switch (tags[i]) {
                    case 1 -> strings.add(text[i]);
                    case 7 -> classes.add(className(text[first[i]]));
                    case 10, 11 -> methods.add(className(text[first[first[i]]]) + "#" + text[first[second[i]]]);
                    default -> { }
                }
            }
            return new ClassReferences(classes, methods, strings);
        }
    }

    /** {@code [Lorg/openqa/selenium/By;} → {@code org.openqa.selenium.By}. */
    private static String className(String internalName) {
        return internalName.replaceAll("^\\[+L?|;$", "").replace('/', '.');
    }
}
