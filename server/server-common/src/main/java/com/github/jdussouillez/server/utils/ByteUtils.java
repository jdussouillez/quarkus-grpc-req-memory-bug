package com.github.jdussouillez.server.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ByteUtils {

    public static List<byte[]> partition(final byte[] bytes, final int size) {
        var nbChunks = (int) Math.ceil((double) bytes.length / size);
        var chunks = new ArrayList<byte[]>(nbChunks);
        for (int i = 0; i < nbChunks; i++) {
            int startIdx = i * size;
            int endIdx = Math.min(startIdx + size, bytes.length);
            chunks.add(Arrays.copyOfRange(bytes, startIdx, endIdx));
        }
        return chunks;
    }
}
