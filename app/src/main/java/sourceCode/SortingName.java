package sourceCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortingName {

    /**
     *
     * @param al
     * @param which 0 == sort by first; 1 == sort by last
     * @return
     */
    public static ArrayList<String> sortLast(ArrayList<String> al, final int which) {
        final int other = which == 1 ? 0 : 1;
        Collections.sort(al, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] split1 = o1.split(" ");
                String[] split2 = o2.split(" ");
                String lastName1 = split1[which];
                String lastName2 = split2[which];
                String firstName1 = split1[other];
                String firstName2 = split2[other];
                if (lastName1.compareTo(lastName2) > 0) {
                    return 1;
                } else if(lastName1.compareTo(lastName2) < 0){
                    return -1;
                }else{ //they are equal
                    int toReturn = 0;
                    if (firstName1.compareTo(firstName2) > 0) {
                        toReturn = 1;
                    } else if(firstName1.compareTo(firstName2) < 0) {
                        toReturn = -1;
                    }
                    return toReturn;
                }
            }
        });
        return al;
    }
}