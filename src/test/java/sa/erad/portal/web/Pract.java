package sa.erad.portal.web;

import java.util.HashMap;
import java.util.Set;

public class Pract {
    public static void main(String[] args) {
        String str = "framework";
        HashMap<Character, Integer> mp = new HashMap<>();
        for (int i = 0; i < str.length(); i++) {
            char mapChar = str.charAt(i);
            if (mp.containsKey(mapChar)) {
                mp.put(mapChar, mp.get(mapChar) + 1);
            } else {
                mp.put(mapChar, 1);
            }
        }

        Set<Character> keys = mp.keySet();
        for (char key : keys) {
            System.out.println(key + ":" + mp.get(key));
        }

    }
}
