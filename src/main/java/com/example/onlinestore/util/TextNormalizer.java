package com.example.onlinestore.util;

public class TextNormalizer {
    public static String normalize(String input) {
        if (input == null) return null;
        return input
                .toLowerCase()
                .replace('o', 'о')
                .replace('a', 'а')
                .replace('e', 'е')
                .replace('p', 'р')
                .replace('c', 'с')
                .replace('x', 'х')
                .replace('y', 'у')
                .replace('i', 'і');
    }
}
