package test;

/**
 * Created by fbgrecojr on 8/16/15.
 */
public class TestSubstring {

    public static void main(String[] args){
        String test = "tEST";
        String test2 = "Test";

        System.out.println(test);

        test = test.substring(0,1).toUpperCase() + test.substring(1).toLowerCase();
        System.out.println(test);

        TestSubstring y = new TestSubstring();
        String[] array = {"abc", "abb", "abd"};

        TestSubstring.mergeSort(array);

        for(String item : array){
            System.out.println(item);
        }


    }

    public static void mergeSort(String[] names) {
        if (names.length >= 2) {
            String[] left = new String[names.length / 2];
            String[] right = new String[names.length - names.length / 2];

            for (int i = 0; i < left.length; i++) {
                left[i] = names[i];
            }

            for (int i = 0; i < right.length; i++) {
                right[i] = names[i + names.length / 2];
            }

            mergeSort(left);
            mergeSort(right);
            merge(names, left, right);
        }
    }

    public static void merge(String[] names, String[] left, String[] right) {
        int a = 0;
        int b = 0;
        for (int i = 0; i < names.length; i++) {
            if (b >= right.length || (a < left.length && left[a].compareToIgnoreCase(right[b]) < 0)) {
                names[i] = left[a];
                a++;
            } else {
                names[i] = right[b];
                b++;
            }
        }
    }
}
