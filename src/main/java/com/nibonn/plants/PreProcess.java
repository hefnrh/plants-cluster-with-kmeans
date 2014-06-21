package com.nibonn.plants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * format data set to <code>name,0/1,0/1,...,0/1</code>
 */
public class PreProcess extends LineProcess {

    private Map<String, Integer> map;
    private Pattern p;

    /**
     * @param data data file name
     * @param initial initial character list file
     * @throws IOException
     */
    public PreProcess(String data, String initial) throws IOException {
        super(data);
        BufferedReader br = new BufferedReader(new FileReader(initial));
        map = new HashMap<>();
        String line;
        int i = 0;
        while ((line = br.readLine()) != null) {
            map.put(line, i++);
        }
        br.close();
        p = Pattern.compile("[,]");
    }

    @Override
    protected String processLine(String line) {
        List<String> tokens = Arrays.asList(p.split(line));
        int[] result = new int[map.size()];
        map.forEach((k, v) -> result[v] = tokens.contains(k) ? 1 : 0);
        StringBuilder sb = new StringBuilder(tokens.get(0));
        Arrays.stream(result).forEach(i -> {
            sb.append(',');
            sb.append(i);
        });
        return sb.toString();
    }
}
