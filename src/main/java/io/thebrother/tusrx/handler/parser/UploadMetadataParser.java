package io.thebrother.tusrx.handler.parser;

import java.util.*;

public class UploadMetadataParser {
    public static Map<String, byte[]> parse(String str) {
        Map<String, byte[]> uploadMetadata = new HashMap<>();
        Arrays.stream(str.split(","))
            .map(UploadMetadataParser::parseEntry)
            .forEach(p -> uploadMetadata.put(p.left, p.right));
        return uploadMetadata;
    }
    
    private static P parseEntry(String keyValue) {
        String[] keyValueArr = keyValue.split(" ");
        if (keyValueArr.length != 2) {
            throw new IllegalArgumentException("invalid data in upload-metadata header");
        }
        return new P(keyValueArr[0], Base64.getDecoder().decode(keyValueArr[1]));
    }
    
    private static class P {
        String left;
        byte[] right;
        
        P(String left, byte[] right) {
            this.left = left;
            this.right = right;
        }
    }
}
