package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class UniqueIdentifier {
    private static List<Integer> ids = new ArrayList<>();

    private static int index = 0;

    private static final int RANGE = 10000;

    private UniqueIdentifier() {
    }

    static {
        for (int i = 0; i < RANGE; i++) {
            ids.add(i);
        }
        Collections.shuffle(ids);
    }

    public static int getIdentifier() {
        if (index > ids.size() - 1) {
            return 0;
        }
        return ids.get(index++);
    }

}
