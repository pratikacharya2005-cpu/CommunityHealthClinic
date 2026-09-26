package util;

import java.util.Comparator;
import java.util.List;

/** Hand-written sorting algorithms (bubble, insertion, quick). */
public final class SortAlgorithms {

    private SortAlgorithms() { }

    public static <T> void bubbleSort(List<T> list, Comparator<T> comparator) {
        int size = list.size();
        for (int pass = 0; pass < size - 1; pass++) {
            boolean swapped = false;
            for (int i = 0; i < size - 1 - pass; i++) {
                if (comparator.compare(list.get(i), list.get(i + 1)) > 0) {
                    T temp = list.get(i);
                    list.set(i, list.get(i + 1));
                    list.set(i + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
    }

    public static <T> void insertionSort(List<T> list, Comparator<T> comparator) {
        for (int i = 1; i < list.size(); i++) {
            T key = list.get(i);
            int j = i - 1;
            while (j >= 0 && comparator.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

    public static <T> void quickSort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() < 2) return;
        quickSort(list, comparator, 0, list.size() - 1);
    }

    private static <T> void quickSort(List<T> list, Comparator<T> comparator,
                                      int low, int high) {
        if (low >= high) return;
        int pivotIndex = partition(list, comparator, low, high);
        quickSort(list, comparator, low, pivotIndex - 1);
        quickSort(list, comparator, pivotIndex + 1, high);
    }

    private static <T> int partition(List<T> list, Comparator<T> comparator,
                                     int low, int high) {
        T pivot = list.get(high);
        int boundary = low - 1;
        for (int i = low; i < high; i++) {
            if (comparator.compare(list.get(i), pivot) <= 0) {
                boundary++;
                T temp = list.get(boundary);
                list.set(boundary, list.get(i));
                list.set(i, temp);
            }
        }
        T temp = list.get(boundary + 1);
        list.set(boundary + 1, list.get(high));
        list.set(high, temp);
        return boundary + 1;
    }
}